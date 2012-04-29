package resLife;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Rate servlet
 * Insert room ratings into database.
 * 
 * Kim Merrill & Richard Yannow
 */
public class Rate extends HttpServlet {
	
	private String dorm, floor, room, sem, year;
	
	/**
	 * Pass to doPost.
	 */
	protected void doGet(HttpServletRequest request,HttpServletResponse response)
			throws ServletException, IOException {
		doPost(request, response);
	}

	/**
	 * Dynamically generate HTML response, which is an informative message
	 * telling whether rating was successfully entered.
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");

		// Print header tags and navbar.
		PrintWriter out = response.getWriter();
		out.println(HtmlHead.getHeader("Rating"));
		
		// Insert rating, if valid.
		submitRating(request, out);
		
		// Print closing links and tags.
    	out.println(HtmlHead.getFooter("rate.html"));
		
	}
	
	/**
	 * Construct insert statement, process user errors, and insert
	 * tuple into Ratings if user input valid parameters.
	 */
	private void submitRating (HttpServletRequest request, PrintWriter out) {
		try {
			
			// Connect to database.
			Connection conn = Database.openConnection();
			
			// Check if room exists in database before inserting rating so
			// user can be given an informative message explaining why the
			// insert has failed.
			String query = "SELECT R.rnum FROM Rooms R WHERE R.rnum=? AND " +
					"R.fnum=? AND R.dname=?;";
			PreparedStatement stmt = conn.prepareStatement(query);
			
			// Get room number, floor number, and dorm name.
			room = request.getParameter("roomtens") + request.getParameter("roomones");
			floor = request.getParameter("floor");
			dorm = request.getParameter("dorm");
			
			stmt.clearParameters();
			stmt.setInt(1, Integer.parseInt(room));
			stmt.setInt(2, Integer.parseInt(floor));
			stmt.setString(3, dorm);
			
			ResultSet exists = stmt.executeQuery();
			// Throw exception if specified room does not exist in database.
			if (!exists.first()) {
				throw new NonexistentException();
			}
			
			Statement stmt2 = conn.createStatement();
			
			// Get remaining input info from form, all of which are
			// required to have values - semester, year, cell rating,
			// noise rating, view rating, and comment.
			sem = request.getParameter("semester");
			year = request.getParameter("year");
			String crate = request.getParameter("cell");
			String nrate = request.getParameter("noise");
			String vrate = request.getParameter("view");
			String comm = request.getParameter("comment");
			
			// If comment was left blank, field should be set to null.
			// Otherwise, add quotation marks so it is recognized as a String.
			if (comm=="") comm=null;
			else comm="'" + comm + "'";
			
			// Insert tuple with input values into Ratings.
			query = "INSERT INTO Ratings (rnum, dname, fnum, year, semester, " +
					"comment, noise, cell, view) VALUES " +
					"(" + room + ", '" + dorm + "', " + floor + ", " + year + ", '" +
					sem + "', " + comm + ", " + nrate + ", " + crate + ", " + vrate + ");";
			stmt2.executeUpdate(query);
			
			// Print informative message if tuple was successfully inserted.
			out.println("<h2>Thank you! Your review has been recorded.</h2>");
			
			// Close database connection.
			conn.close();
		}
		// If room does not exist in the database, the user should be asked to
		// double-check the room number, floor, and dorm he/she entered.
		catch (NonexistentException e) {
			out.println("<h2>The room you entered is not in our database.<br/>Are you sure you meant " +
					dorm + " " + floor + room + "?</h2>");
		}
		// If insert fails for a different reason, another rating with the given
		// key (rnum, fnum, dname, ryear, semester) must already exist in database
		// (unless there is a major error). Print informative message.
		catch (SQLException e) {
			out.println("<h2>Sorry, a review of " + dorm + " " + floor + room + 
					" already exists in our database for " + 
					sem + " " + year + ". Please only submit ratings for " +
					"semesters during which you lived in this room.</h2>");
		}
	}

}
