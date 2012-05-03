package resLife;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Student servlet.
 * Find students who match user's conditions and
 * print their descriptive attributes.
 * 
 * Kim Merrill & Richard Yannow
 */
public class Student extends HttpServlet {

	/**
	 * Pass to doPost.
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doPost(request, response);
	}

	/**
	 * Dynamically generate HTML response, which is a list of students
	 * matching user-input selection conditions from form.
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("text/html");

		// Print header tags and navbar.
		PrintWriter out = response.getWriter();
		out.println(HtmlHead.getHeader("Students"));
		
		// Find matching students and print results.
		findStudents(request, out);
		
		// Print closing links and tags.
    	out.println(HtmlHead.getFooter("student.html"));
	}
	
	/**
	 * Construct query and process user errors from form input.
	 */
	private void findStudents (HttpServletRequest request, PrintWriter out) {
		try {
			
			// Connect to database.
			Connection conn = Database.openConnection();
			Statement stmt = conn.createStatement();
			
			// Selection conditions to add to query.
			String addConditions = "";
			
			String residency = request.getParameter("residency");
			
			// If a Spogro was selected (identified as an option from the
			// residency field with 10 or more characters), add it to the
			// selection conditions.
			if (residency.length()>=10) {
				
				// Split passed value into its dorm, floor, and hname components.
				String[] spogro = residency.split("[\\s]+");
				String dorm = spogro[0];
				String floor = spogro[1];
				String hall=spogro[2];
				
				// Find the group number to which the given Spogro corresponds.
				String sgQuery = "SELECT S.gnum FROM Spogros S WHERE S.hname='" +
					hall + "' AND S.dname='" + dorm + "' AND S.fnum=" + floor + ";";
				ResultSet spogros = stmt.executeQuery(sgQuery);
				
				// Add the group, floor, and dorm to the selection conditions
				// so only rooms in the given Spogro are returned.
				if (spogros.next()) {
					int gnum = spogros.getInt("gnum");
					addConditions = " AND R.gnum=" + gnum + " AND R.dname='" +
							dorm + "' AND R.fnum=" + floor;
				}
				// If the given Spogro doesn't exist, throw exception.
				else {
					throw new NonexistentException();
				}
				
			}
			// If a Dorm was selected, add selection condition.
			else if (!(residency.equals("u"))){
				String dorm=residency;
				addConditions = " AND R.dname='" + dorm + "'";
			}
			
			// Get input from form.
			String name = request.getParameter("student");
			String sex = request.getParameter("sex");
			String year = request.getParameter("year");
			
			if (!(sex.equals("u"))) {
				addConditions = addConditions + " AND S.sex='" + sex + "'";
			}
			if (!(year.equals("u"))) {
				addConditions = addConditions + " AND S.year=" + year;
			}
			
			// Join Students and Rooms table and add selection conditions.
			String query = "SELECT S.name, S.year, S.rnum, R.dname," +
					" R.fnum FROM Students S, Rooms R" + 
					" WHERE S.name LIKE '%" + name +
					"%' AND S.rnum=R.rnum AND S.fnum=R.fnum AND " +
					"S.dname=R.dname" + addConditions;
				
			ResultSet result = stmt.executeQuery(query+";");
			
			boolean next = result.next();
			
			// If results were found, print information about each student.
			if (next) {
				// Print table header.
				out.println("<h2>Students found:</h2><table class='table table-striped'>" +
						"<tr><th>Name</th><th>Class year</th><th>Room</th></tr>");
				// Print row for each student found.
				while (next) {
					out.println("<tr><td>" + result.getString("name") +
						"</td><td>" + result.getInt("year") + "</td><td>" +
						result.getString("dname") + " " +
						((result.getInt("fnum") * 100) + result.getInt("rnum")) + 
						"</td></tr>");
					next = result.next();
				}
				out.println("</table>");
			}
			// If no results were found, throw exception.
			else {
				throw new NonexistentException();
			}
			conn.close();
		}
		// If no matching students (or spogros) found, print informative message.
		catch (NonexistentException e) {
			out.println("<h2>No matching students found.</h2>");
		}
		// Print informative message if query returned errors.
		catch (SQLException e) {
			out.println("<h2>Search returned errors.<br/>Please try again.</h2>");
		}
	}

}
