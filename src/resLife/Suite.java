package resLife;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Suite servlet
 * Find suites that match user's conditions and print the rooms they contain.
 * A suite is identified as a group with 2-6 rooms (size).
 * 
 * Kim Merrill & Richard Yannow
 */
public class Suite extends HttpServlet {

	/**
	 * Pass to doPost.
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doPost(request, response);
	}

	/**
	 * Dynamically generate HTML response, which is a list of suites
	 * matching user-input selection conditions from form.
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("text/html");

		// Print header tags and navbar.
		PrintWriter out = response.getWriter();
		out.println(HtmlHead.getHeader("Suites"));

		// Find matching suites and print results.
		findSuites(request, out);

		// Print closing links and tags.
		out.println(HtmlHead.getFooter("suite.html"));
	}

	/**
	 * Construct query and process user errors from form input.
	 */
	private void findSuites (HttpServletRequest request, PrintWriter out) {
		try {

			// Connect to database.
			Connection conn = Database.openConnection();
			Statement stmt = conn.createStatement();

			// Query should return the suite number, floor number,
			// dorm name, suite capacity, and size (number of rooms).
			String query = "SELECT G.gnum, G.fnum, G.dname, G.capacity " + 
					"FROM Groups G";

			// Conditions that must be added to the WHERE clause of the query.
			String addCondition = " WHERE G.capacity=" +
					request.getParameter("capacity");

			// If selected, determine the dorm and floor and
			// format the conditions to add to the query.
			String dorm = request.getParameter("dorm");
			String floor = request.getParameter("floor");
			if (!(dorm.equals("u"))) {
				addCondition = addCondition + " AND G.dname='" + dorm + "'";
			}
			if (!(floor.equals("u"))) {
				addCondition = addCondition + " AND G.fnum=" + floor;
			}

			// If selected, determine the size of suite (categorized by
			// number of rooms).
			String size = request.getParameter("size");
			if (!(size.equals("u"))) {
				addCondition = addCondition + " AND G.size=" + size;
			}
			else {
				// A suite is identified as a group with 2-6 rooms.
				addCondition = addCondition + " AND (G.size BETWEEN 2 AND 6)";
			}

			// If selected, determine the type of suite (categorized
			// by whether it has a private bath or air-conditioning)
			// and then join Groups with the appropriate table.
			String[] type = request.getParameterValues("type");
			if (type!=null) {
				for (String t:type) {
					if (t.equals("1")) {
						query = query + ", Bathrooms B";
						addCondition = addCondition + " AND G.gnum=B.gnum AND " +
						"G.fnum=B.fnum AND G.dname=B.dname";
					}
					if (t.equals("2")) {
						query = query + ", Dorms D";
						addCondition = addCondition + " AND D.ac=true AND " +
						"D.dname=G.dname";
					}
				}
			}

			// Execute query and format results.
			query = query + addCondition;
			ResultSet result = stmt.executeQuery(query + ";");
			printSuites(result, out, conn);

			// Close database connection.
			conn.close();
		}
		// Print informative message if no suites found matching input conditions.
		catch (NonexistentException e) {
			out.println("<h2>No matching suites found.<h2/>");
		}
		// Print informative message if query returns errors.
		catch (SQLException e) {
			out.println("<h2>Search returned errors.<br/>Please try again.</h2>");
		}
	}

	/**
	 * Print room numbers of all rooms in found suites.
	 */
	private void printSuites(ResultSet result, PrintWriter out, 
			Connection conn) throws SQLException, NonexistentException{

		boolean next = result.next();

		// If results were found, print information about each suite.
		if (next) {

			int gnum, floor, capacity;
			String dorm;

			// Print table header.
			out.println("<h2>Suites found:</h2><table class='table table-striped'>" +
					"<tr><th>Dorm</th><th>Capacity</th><th>Rooms</th></tr>");

			while (next) {

				// Get fields of each result tuple.
				gnum = result.getInt("gnum");
				dorm = result.getString("dname");
				floor = result.getInt("fnum");
				capacity = result.getInt("capacity");

				// Find all room numbers of all rooms in given suite.
				String query = "SELECT R.rnum FROM Rooms R, Groups G " +
						"WHERE R.gnum=G.gnum AND R.gnum=" + gnum + " AND R.fnum=" +
						floor + " AND R.dname='" + dorm + "';";

				Statement roomstmt = conn.createStatement();
				ResultSet rooms = roomstmt.executeQuery(query);

				// Print dorm, capacity, and room information.
				out.println("<tr><td>" + dorm + "</td><td>" + capacity
						+ "</td><td>");

				boolean nextRoom = rooms.next();
				if (nextRoom) {
					out.println((floor*100 + rooms.getInt("rnum")));
					nextRoom = rooms.next();
					while (nextRoom) {
						out.println(", " + (floor*100 + rooms.getInt("rnum")));
						nextRoom = rooms.next();
					}
				}
				out.println("</td></tr>");
				next = result.next();
			}
			out.println("</table>");
		}
		// If no results were found, print informative message.
		else {
			throw new NonexistentException();
		}
	}
}
