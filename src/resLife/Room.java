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
 * Room servlet.
 * Find rooms that match user's conditions and
 * print their descriptive attributes.
 * 
 * Kim Merrill & Richard Yannow
 */
public class Room extends HttpServlet {

	/**
	 * Pass to doPost.
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doPost(request, response);
	}

	/**
	 * Dynamically generate HTML response, which is a list of rooms
	 * matching user-input selection conditions from form.
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("text/html");

		// Print header tags and navbar.
		PrintWriter out = response.getWriter();
		out.println(HtmlHead.getHeader("Rooms"));

		// Find matching rooms and print results.
		findRooms(request, out);

		// Print closing links and tags.
		out.println(HtmlHead.getFooter("room.html"));
	}

	/**
	 * Construct query and process user errors from form input.
	 */
	private void findRooms (HttpServletRequest request, PrintWriter out) {
		try {
			
			// Connect to database.
			Connection conn = Database.openConnection();
			
			// Begin constructing query - rcapacity (whether single or double)
			// is a required field. If selected, add other conditions.
			Statement stmt = conn.createStatement();
			String type = request.getParameter("type");
			String query = "SELECT R.rnum, R.fnum, R.dname, R.sqft, R.rcapacity, " +
					"R.gnum, R.cell, R.noise, R.view FROM Rooms R WHERE R.rcapacity=" + type;
			query = addConditions(request, query);
			
			// Execute query and format results.
			ResultSet result = stmt.executeQuery(query + ";");
			printRooms(result, out);

			// Close database connection.
			conn.close();
		}
		// Print informative message if no rooms found matching input conditions.
		catch (NonexistentException e) {
			out.println("<h2>No matching rooms found.</h2>");
		}
		// Print informative message if query returns errors.
		catch (SQLException e) {
			out.println("<h2>Search returned errors.<br/>Please try again.</h2>");
		}
	}

	/**
	 * Process user input selection conditions from form and format query.
	 */
	private String addConditions(HttpServletRequest request, String query) {

		// Get all user input selection conditions.
		String roomtens = request.getParameter("roomtens");
		String roomones = request.getParameter("roomones");
		String dorm = request.getParameter("dorm");
		String floor = request.getParameter("floor");
		String cell = request.getParameter("cell");
		String noise = request.getParameter("noise");
		String view = request.getParameter("view");

		// If selected, add room number, floor, and dorm conditions to query.
		if (!(roomtens.equals("u") || roomones.equals("u"))) {
			query = query + " AND R.rnum=" + (roomtens+roomones);
		}
		if (!(floor.equals("u"))) {
			query = query + " AND R.fnum=" + floor;
		}
		if (!(dorm.equals("u"))) {
			query = query + " AND R.dname='" + dorm + "'";
		}

		// If selected, add rating range conditions to query.
		// Rooms for which no ratings have been submitted should also be returned.
		if (!(cell.equals("u"))) query = query + " AND (R.cell" + cell + " OR R.cell=0)";
		if (!(noise.equals("u"))) query = query + " AND (R.noise" + noise + " OR R.noise=0)";
		if (!(view.equals("u"))) query = query + " AND (R.view" + view + " OR R.view=0.0)";

		return query;
	}

	/**
	 * Print descriptive attributes of all found rooms.
	 */
	private void printRooms(ResultSet result, PrintWriter out)
			throws SQLException, NonexistentException{
		boolean next = result.next();

		// If results were found, print information about each room.
		if (next) {
			// Print table header.
			out.println("<h2>Rooms found:</h2><table class='table table-striped'>" +
					"<tr><th>Room</th><th>Capacity</th><th>Square feet</th>" +
					"<th>Cell service</th><th>Quietness</th><th>View</th></tr>");
			while (next) {

				String dorm = result.getString("dname");
				
				String floor = "" + result.getInt("fnum");
				String room = "" + result.getInt("rnum");
				if (room.length()==1) room = 0 + room;
				
				// If rcapacity=1, print "single" - otherwise,
				// print "double".
				int c = result.getInt("rcapacity");
				String cap;
				if (c==1) cap = "single";
				else cap = "double";
				
				// Print descriptive attributes of room.
				out.println("<td>" + dorm + " " +
						(floor + room) + 
						"</td><td>" + cap + "</td><td>" + result.getInt("sqft") +
						"</td><td>" + result.getFloat("cell") + "</td><td>" +
						result.getFloat("noise") + "</td><td>" +
						result.getFloat("view") + "</td></tr>");
				next = result.next();
			}
			out.println("</table>");
		}
		// If no rooms were found, throw exception.
		else {
			throw new NonexistentException();
		}
	}

}
