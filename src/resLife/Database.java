package resLife;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Connect to resLife database.
 * 
 * Kim Merrill & Richard Yannow
 */
public class Database {

	// Load the database driver once.
	static {
		try {
			Class.forName("com.mysql.jdbc.Driver");
		}
		catch (ClassNotFoundException cnfe) {
			throw new RuntimeException(cnfe);
		}
	}

	// Create connection to the database.
	public static Connection openConnection () throws SQLException {
		return DriverManager.getConnection("jdbc:mysql://localhost:3306/resLife",
				"dbproject", "merrillyannow");
	}

}