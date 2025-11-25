package database;

import java.sql.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import entityClasses.User;
import entityClasses.Post;
import entityClasses.Reply;
import entityClasses.Parameter;
import entityClasses.ParameterCategory;
import entityClasses.Thread;
import entityClasses.Request;

/*******
 * <p> Title: Database Class. </p>
 * 
 * <p> Description: This is an in-memory database built on H2.  Detailed documentation of H2 can
 * be found at https://www.h2database.com/html/main.html (Click on "PDF (2MP) for a PDF of 438 pages
 * on the H2 main page.)  This class leverages H2 and provides numerous special supporting methods.
 * </p>
 * 
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 * 
 * @author Lynn Robert Carter
 * 
 * @version 2.00		2025-04-29 Updated and expanded from the version produce by on a previous
 * 							version by Pravalika Mukkiri and Ishwarya Hidkimath Basavaraj
 */

/*
 * The Database class is responsible for establishing and managing the connection to the database,
 * and performing operations such as user registration, login validation, handling invitation 
 * codes, and numerous other database related functions.
 */
public class Database {

	// JDBC driver name and database URL 
	static final String JDBC_DRIVER = "org.h2.Driver";   
	static final String DB_URL = "jdbc:h2:~/FoundationDatabase";  

	//  Database credentials 
	static final String USER = "sa"; 
	static final String PASS = ""; 

	//  Shared variables used within this class
	private Connection connection = null;		// Singleton to access the database 
	private Statement statement = null;			// The H2 Statement is used to construct queries
	
	// These are the easily accessible attributes of the currently logged-in user
	// This is only useful for single user applications
	private String currentUsername;
	private String currentPassword;
	private String currentFirstName;
	private String currentMiddleName;
	private String currentLastName;
	private String currentPreferredFirstName;
	private String currentEmailAddress;
	private boolean currentAdminRole;
	private boolean currentNewStudent;
	private boolean currentNewStaff;

	/*******
	 * <p> Method: Database </p>
	 * 
	 * <p> Description: The default constructor used to establish this singleton object.</p>
	 * 
	 */
	
	public Database () {
		
	}
	
	
/*******
 * <p> Method: connectToDatabase </p>
 * 
 * <p> Description: Used to establish the in-memory instance of the H2 database from secondary
 *		storage.</p>
 *
 * @throws SQLException when the DriverManager is unable to establish a connection
 * 
 */
	public void connectToDatabase() throws SQLException {
		try {
			Class.forName(JDBC_DRIVER); // Load the JDBC driver
			connection = DriverManager.getConnection(DB_URL, USER, PASS);
			statement = connection.createStatement(); 
			// You can use this command to clear the database and restart from fresh.
			//statement.execute("DROP ALL OBJECTS");

			createTables();  // Create the necessary tables if they don't exist
		} catch (ClassNotFoundException e) {
			System.err.println("JDBC Driver not found: " + e.getMessage());
		}
	}

	
/*******
 * <p> Method: createTables </p>
 * 
 * <p> Description: Used to create new instances of the two database tables used by this class.</p>
 * 
 */
	private void createTables() throws SQLException {
		// Create the user database
		String userTable = "CREATE TABLE IF NOT EXISTS userDB ("
				+ "id INT AUTO_INCREMENT PRIMARY KEY, "
				+ "userName VARCHAR(255) UNIQUE, "
				+ "password VARCHAR(255), "
				+ "firstName VARCHAR(255), "
				+ "middleName VARCHAR(255), "
				+ "lastName VARCHAR (255), "
				+ "preferredFirstName VARCHAR(255), "
				+ "emailAddress VARCHAR(255), "
				+ "adminRole BOOL DEFAULT FALSE, "
				+ "newStudent BOOL DEFAULT FALSE, "
				+ "newStaff BOOL DEFAULT FALSE)";
		statement.execute(userTable);
		
		// Ensure one-time password column exists (for older DBs)
        try { statement.execute("ALTER TABLE userDB ADD COLUMN IF NOT EXISTS oneTimePassword VARCHAR(255)"); }
        catch (SQLException e) { /* ignore */ }
		
		// Create the invitation codes table
	    String invitationCodesTable = "CREATE TABLE IF NOT EXISTS InvitationCodes ("
	            + "code VARCHAR(10) PRIMARY KEY, "
	    		+ "emailAddress VARCHAR(255), "
	            + "role VARCHAR(10))";
	    statement.execute(invitationCodesTable);
	 // Create the posts table
	    String postsTable = "CREATE TABLE IF NOT EXISTS postsDB ("
	            + "postId VARCHAR(50) PRIMARY KEY, "
	            + "title VARCHAR(100), "
	            + "body VARCHAR(5000), "
	            + "authorUsername VARCHAR(255), "
	            + "thread VARCHAR(100), "
	            + "createdAt TIMESTAMP, "
	            + "lastEditedAt TIMESTAMP, "
	            + "isDeleted BOOLEAN DEFAULT FALSE)";
	    statement.execute(postsTable);
	    
	 // Create the threads table
	    String threadsTable = "CREATE TABLE IF NOT EXISTS threadsDB ("
	            + "threadId VARCHAR(50) PRIMARY KEY, "
	            + "title VARCHAR(100), "
	            + "description VARCHAR(500), "
	            + "status VARCHAR(10) DEFAULT 'OPEN', "
	            + "createdByUsername VARCHAR(255), "
	            + "createdAt TIMESTAMP)";
	    statement.execute(threadsTable);
	    
	    // Create the requests table
	    String requestsTable = "CREATE TABLE IF NOT EXISTS requestsDB ("
	            + "requestId VARCHAR(50) PRIMARY KEY, "
	            + "title VARCHAR(200), "
	            + "description VARCHAR(2000), "
	            + "category VARCHAR(50), "
	            + "status VARCHAR(10) DEFAULT 'OPEN', "
	            + "createdByUsername VARCHAR(255), "
	            + "closedByUsername VARCHAR(255), "
	            + "resolutionNotes VARCHAR(2000), "
	            + "reopenReason VARCHAR(1000), "
	            + "originalRequestId VARCHAR(50), "
	            + "createdAt TIMESTAMP, "
	            + "closedAt TIMESTAMP, "
	            + "reopenedAt TIMESTAMP)";
	    statement.execute(requestsTable);
	    
	    // Create the replies table
	    String repliesTable = "CREATE TABLE IF NOT EXISTS repliesDB ("
	            + "replyId VARCHAR(50) PRIMARY KEY, "
	            + "body VARCHAR(3000), "
	            + "authorUsername VARCHAR(255), "
	            + "parentPostId VARCHAR(50), "
	            + "createdAt TIMESTAMP, "
	            + "lastEditedAt TIMESTAMP, "
	            + "isDeleted BOOLEAN DEFAULT FALSE, "
	            + "isRead BOOLEAN DEFAULT FALSE, "
	            + "isFeedback BOOLEAN DEFAULT FALSE)";
	    statement.execute(repliesTable);
	    
	 // Add isFeedback column to existing table if it doesn't exist (for backward compatibility)
	    try {
	        statement.execute("ALTER TABLE repliesDB ADD COLUMN IF NOT EXISTS isFeedback BOOLEAN DEFAULT FALSE");
	    } catch (SQLException e) {
	        // Column may already exist, ignore
	    }
	    
	    
	 // Create the grading parameters table
	    String parametersTable = "CREATE TABLE IF NOT EXISTS gradingParametersDB ("
	            + "parameterId VARCHAR(50) PRIMARY KEY, "
	            + "name VARCHAR(100), "
	            + "description VARCHAR(500), "
	            + "isActive BOOLEAN DEFAULT TRUE, "
	            + "createdByUsername VARCHAR(255), "
	            + "createdAt TIMESTAMP, "
	            + "requiredPosts INT DEFAULT 0, "
	            + "requiredReplies INT DEFAULT 0, "
	            + "topics VARCHAR(2000), "
	            + "threadId VARCHAR(255) NOT NULL, "
	            + "categories VARCHAR(5000))";
	    statement.execute(parametersTable);
	    
	 // Add new columns to existing table if they don't exist (for backward compatibility)
	    try {
	        statement.execute("ALTER TABLE gradingParametersDB ADD COLUMN IF NOT EXISTS requiredPosts INT DEFAULT 0");
	        statement.execute("ALTER TABLE gradingParametersDB ADD COLUMN IF NOT EXISTS requiredReplies INT DEFAULT 0");
	        statement.execute("ALTER TABLE gradingParametersDB ADD COLUMN IF NOT EXISTS topics VARCHAR(2000)");
	        statement.execute("ALTER TABLE gradingParametersDB ADD COLUMN IF NOT EXISTS threadId VARCHAR(255)");
	        statement.execute("ALTER TABLE gradingParametersDB ADD COLUMN IF NOT EXISTS categories VARCHAR(5000)");
	        // Remove isTemplate column if it exists (no longer needed)
	        try {
	            statement.execute("ALTER TABLE gradingParametersDB DROP COLUMN isTemplate");
	        } catch (SQLException e) { /* Column may not exist, ignore */ }
	    } catch (SQLException e) {
	        // Columns may already exist, ignore
	    }
	    
	    // Create parameter categories table
	    String parameterCategoriesTable = "CREATE TABLE IF NOT EXISTS parameterCategoriesDB ("
	            + "categoryId VARCHAR(50) PRIMARY KEY, "
	            + "parameterId VARCHAR(50) NOT NULL, "
	            + "categoryName VARCHAR(100), "
	            + "weight DOUBLE, "
	            + "categoryOrder INT, "
	            + "FOREIGN KEY (parameterId) REFERENCES gradingParametersDB(parameterId) ON DELETE CASCADE)";
	    statement.execute(parameterCategoriesTable);
	}


/*******
 * <p> Method: isDatabaseEmpty </p>
 * 
 * <p> Description: If the user database has no rows, true is returned, else false.</p>
 * 
 * @return true if the database is empty, else it returns false
 * 
 */
	public boolean isDatabaseEmpty() {
		String query = "SELECT COUNT(*) AS count FROM userDB";
		try {
			ResultSet resultSet = statement.executeQuery(query);
			if (resultSet.next()) {
				return resultSet.getInt("count") == 0;
			}
		}  catch (SQLException e) {
	        return false;
	    }
		return true;
	}
	
	
/*******
 * <p> Method: getNumberOfUsers </p>
 * 
 * <p> Description: Returns an integer .of the number of users currently in the user database. </p>
 * 
 * @return the number of user records in the database.
 * 
 */
	public int getNumberOfUsers() {
		String query = "SELECT COUNT(*) AS count FROM userDB";
		try {
			ResultSet resultSet = statement.executeQuery(query);
			if (resultSet.next()) {
				return resultSet.getInt("count");
			}
		} catch (SQLException e) {
	        return 0;
	    }
		return 0;
	}

/*******
 * <p> Method: register(User user) </p>
 * 
 * <p> Description: Creates a new row in the database using the user parameter. </p>
 * 
 * @throws SQLException when there is an issue creating the SQL command or executing it.
 * 
 * @param user specifies a user object to be added to the database.
 * 
 */
	public void register(User user) throws SQLException {
		String insertUser = "INSERT INTO userDB (userName, password, firstName, middleName, "
				+ "lastName, preferredFirstName, emailAddress, adminRole, newStudent, newStaff) "
				+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		try (PreparedStatement pstmt = connection.prepareStatement(insertUser)) {
			currentUsername = user.getUserName();
			pstmt.setString(1, currentUsername);
			
			currentPassword = user.getPassword();
			pstmt.setString(2, currentPassword);
			
			currentFirstName = user.getFirstName();
			pstmt.setString(3, currentFirstName);
			
			currentMiddleName = user.getMiddleName();			
			pstmt.setString(4, currentMiddleName);
			
			currentLastName = user.getLastName();
			pstmt.setString(5, currentLastName);
			
			currentPreferredFirstName = user.getPreferredFirstName();
			pstmt.setString(6, currentPreferredFirstName);
			
			currentEmailAddress = user.getEmailAddress();
			pstmt.setString(7, currentEmailAddress);
			
			currentAdminRole = user.getAdminRole();
			pstmt.setBoolean(8, currentAdminRole);
			
			currentNewStudent = user.getNewStudent();
			pstmt.setBoolean(9, currentNewStudent);
			
			currentNewStaff = user.getNewStaff();
			pstmt.setBoolean(10, currentNewStaff);
			
			pstmt.executeUpdate();
		}
		
	}
	
/*******
 *  <p> Method: List getUserList() </p>
 *  
 *  <P> Description: Generate an List of Strings, one for each user in the database,
 *  starting with "<Select User>" at the start of the list. </p>
 *  
 *  @return a list of userNames found in the database.
 */
	public List<String> getUserList () {
		List<String> userList = new ArrayList<String>();
		userList.add("<Select a User>");
		String query = "SELECT userName FROM userDB";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				userList.add(rs.getString("userName"));
			}
		} catch (SQLException e) {
			// Log the exception for debugging purposes
	        System.err.println("Error retrieving user list: " + e.getMessage());
	        // Return list with placeholder instead of null to prevent null pointer exceptions
	        // This ensures callers can safely access index 0
	        List<String> errorList = new ArrayList<String>();
	        errorList.add("<Select a User>");
	        return errorList;
	    }
//		System.out.println(userList);
		return userList;
	}

/*******
 * <p> Method: boolean loginAdmin(User user) </p>
 * 
 * <p> Description: Check to see that a user with the specified username, password, and role
 * 		is the same as a row in the table for the username, password, and role. </p>
 * 
 * @param user specifies the specific user that should be logged in playing the Admin role.
 * 
 * @return true if the specified user has been logged in as an Admin else false.
 * 
 */
	public boolean loginAdmin(User user){
		// Validates an admin user's login credentials so the user can login in as an Admin.
		String query = "SELECT * FROM userDB WHERE userName = ? AND password = ? AND "
				+ "adminRole = TRUE";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, user.getUserName());
			pstmt.setString(2, user.getPassword());
			ResultSet rs = pstmt.executeQuery();
			return rs.next();	// If a row is returned, rs.next() will return true		
		} catch  (SQLException e) {
	        e.printStackTrace();
	    }
		return false;
	}
	
	
/*******
 * <p> Method: boolean loginStudent(User user) </p>
 * 
 * <p> Description: Check to see that a user with the specified username, password, and role
 * 		is the same as a row in the table for the username, password, and role. </p>
 * 
 * @param user specifies the specific user that should be logged in playing the Student role.
 * 
 * @return true if the specified user has been logged in as an Student else false.
 * 
 */
	public boolean loginStudent(User user) {
		// Validates a student user's login credentials.
		String query = "SELECT * FROM userDB WHERE userName = ? AND password = ? AND "
				+ "newStudent = TRUE";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, user.getUserName());
			pstmt.setString(2, user.getPassword());
			ResultSet rs = pstmt.executeQuery();
			return rs.next();
		} catch  (SQLException e) {
		       e.printStackTrace();
		}
		return false;
	}

	/*******
	 * <p> Method: boolean loginStaff(User user) </p>
	 * 
	 * <p> Description: Check to see that a user with the specified username, password, and role
	 * 		is the same as a row in the table for the username, password, and role. </p>
	 * 
	 * @param user specifies the specific user that should be logged in playing the Reviewer role.
	 * 
	 * @return true if the specified user has been logged in as an Student else false.
	 * 
	 */
	// Validates a reviewer user's login credentials.
	public boolean loginStaff(User user) {
		String query = "SELECT * FROM userDB WHERE userName = ? AND password = ? AND "
				+ "newStaff = TRUE";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, user.getUserName());
			pstmt.setString(2, user.getPassword());
			ResultSet rs = pstmt.executeQuery();
			return rs.next();
		} catch  (SQLException e) {
		       e.printStackTrace();
		}
		return false;
	}
	
	
	/*******
	 * <p> Method: boolean doesUserExist(User user) </p>
	 * 
	 * <p> Description: Check to see that a user with the specified username is  in the table. </p>
	 * 
	 * @param userName specifies the specific user that we want to determine if it is in the table.
	 * 
	 * @return true if the specified user is in the table else false.
	 * 
	 */
	// Checks if a user already exists in the database based on their userName.
	public boolean doesUserExist(String userName) {
	    String query = "SELECT COUNT(*) FROM userDB WHERE userName = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        
	        pstmt.setString(1, userName);
	        ResultSet rs = pstmt.executeQuery();
	        
	        if (rs.next()) {
	            // If the count is greater than 0, the user exists
	            return rs.getInt(1) > 0;
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return false; // If an error occurs, assume user doesn't exist
	}

	
	/*******
	 * <p> Method: int getNumberOfRoles(User user) </p>
	 * 
	 * <p> Description: Determine the number of roles a specified user plays. </p>
	 * 
	 * @param user specifies the specific user that we want to determine if it is in the table.
	 * 
	 * @return the number of roles this user plays (0 - 5).
	 * 
	 */	
	// Get the number of roles that this user plays
	public int getNumberOfRoles (User user) {
		int numberOfRoles = 0;
		if (user.getAdminRole()) numberOfRoles++;
		if (user.getNewStudent()) numberOfRoles++;
		if (user.getNewStaff()) numberOfRoles++;
		return numberOfRoles;
	}	

	
	/*******
	 * <p> Method: String generateInvitationCode(String emailAddress, String role) </p>
	 * 
	 * <p> Description: Given an email address and a roles, this method establishes and invitation
	 * code and adds a record to the InvitationCodes table.  When the invitation code is used, the
	 * stored email address is used to establish the new user and the record is removed from the
	 * table.</p>
	 * 
	 * @param emailAddress specifies the email address for this new user.
	 * 
	 * @param role specified the role that this new user will play.
	 * 
	 * @return the code of six characters so the new user can use it to securely setup an account.
	 * 
	 */
	// Generates a new invitation code and inserts it into the database.
	public String generateInvitationCode(String emailAddress, String role) {
	    String code = UUID.randomUUID().toString().substring(0, 6); // Generate a random 6-character code
	    String query = "INSERT INTO InvitationCodes (code, emailaddress, role) VALUES (?, ?, ?)";

	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, code);
	        pstmt.setString(2, emailAddress);
	        pstmt.setString(3, role);
	        pstmt.executeUpdate();
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return code;
	}

	
	/*******
	 * <p> Method: int getNumberOfInvitations() </p>
	 * 
	 * <p> Description: Determine the number of outstanding invitations in the table.</p>
	 *  
	 * @return the number of invitations in the table.
	 * 
	 */
	// Number of invitations in the database
	public int getNumberOfInvitations() {
		String query = "SELECT COUNT(*) AS count FROM InvitationCodes";
		try {
			ResultSet resultSet = statement.executeQuery(query);
			if (resultSet.next()) {
				return resultSet.getInt("count");
			}
		} catch  (SQLException e) {
	        e.printStackTrace();
	    }
		return 0;
	}
	
	
	/*******
	 * <p> Method: boolean emailaddressHasBeenUsed(String emailAddress) </p>
	 * 
	 * <p> Description: Determine if an email address has been user to establish a user.</p>
	 * 
	 * @param emailAddress is a string that identifies a user in the table
	 *  
	 * @return true if the email address is in the table, else return false.
	 * 
	 */
	// Check to see if an email address is already in the database
	public boolean emailaddressHasBeenUsed(String emailAddress) {
	    String query = "SELECT COUNT(*) AS count FROM InvitationCodes WHERE emailAddress = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, emailAddress);
	        ResultSet rs = pstmt.executeQuery();
	        System.out.println(rs);
	        if (rs.next()) {
	            // Mark the code as used
	        	return rs.getInt("count")>0;
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
		return false;
	}
	
	
	/*******
	 * <p> Method: String getRoleGivenAnInvitationCode(String code) </p>
	 * 
	 * <p> Description: Get the role associated with an invitation code.</p>
	 * 
	 * @param code is the 6 character String invitation code
	 *  
	 * @return the role for the code or an empty string.
	 * 
	 */
	// Obtain the roles associated with an invitation code.
	public String getRoleGivenAnInvitationCode(String code) {
	    String query = "SELECT * FROM InvitationCodes WHERE code = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, code);
	        ResultSet rs = pstmt.executeQuery();
	        if (rs.next()) {
	            return rs.getString("role");
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return "";
	}

	
	/*******
	 * <p> Method: String getEmailAddressUsingCode (String code ) </p>
	 * 
	 * <p> Description: Get the email addressed associated with an invitation code.</p>
	 * 
	 * @param code is the 6 character String invitation code
	 *  
	 * @return the email address for the code or an empty string.
	 * 
	 */
	// For a given invitation code, return the associated email address of an empty string
	public String getEmailAddressUsingCode (String code ) {
	    String query = "SELECT emailAddress FROM InvitationCodes WHERE code = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, code);
	        ResultSet rs = pstmt.executeQuery();
	        if (rs.next()) {
	            return rs.getString("emailAddress");
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
		return "";
	}
	
	
	/*******
	 * <p> Method: void removeInvitationAfterUse(String code) </p>
	 * 
	 * <p> Description: Remove an invitation record once it is used.</p>
	 * 
	 * @param code is the 6 character String invitation code
	 *  
	 */
	// Remove an invitation using an email address once the user account has been setup
	public void removeInvitationAfterUse(String code) {
	    String query = "SELECT COUNT(*) AS count FROM InvitationCodes WHERE code = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, code);
	        ResultSet rs = pstmt.executeQuery();
	        if (rs.next()) {
	        	int counter = rs.getInt(1);
	            // Only do the remove if the code is still in the invitation table
	        	if (counter > 0) {
        			query = "DELETE FROM InvitationCodes WHERE code = ?";
	        		try (PreparedStatement pstmt2 = connection.prepareStatement(query)) {
	        			pstmt2.setString(1, code);
	        			pstmt2.executeUpdate();
	        		}catch (SQLException e) {
	        	        e.printStackTrace();
	        	    }
	        	}
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
		return;
	}
	
	
	/*******
	 * <p> Method: String getFirstName(String username) </p>
	 * 
	 * <p> Description: Get the first name of a user given that user's username.</p>
	 * 
	 * @param username is the username of the user
	 * 
	 * @return the first name of a user given that user's username 
	 *  
	 */
	// Get the First Name
	public String getFirstName(String username) {
		String query = "SELECT firstName FROM userDB WHERE userName = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, username);
	        ResultSet rs = pstmt.executeQuery();
	        
	        if (rs.next()) {
	            return rs.getString("firstName"); // Return the first name if user exists
	        }
			
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
		return null;
	}
	

	/*******
	 * <p> Method: void updateFirstName(String username, String firstName) </p>
	 * 
	 * <p> Description: Update the first name of a user given that user's username and the new
	 *		first name.</p>
	 * 
	 * @param username is the username of the user
	 * 
	 * @param firstName is the new first name for the user
	 *  
	 */
	// update the first name
	public void updateFirstName(String username, String firstName) {
	    String query = "UPDATE userDB SET firstName = ? WHERE username = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, firstName);
	        pstmt.setString(2, username);
	        pstmt.executeUpdate();
	        currentFirstName = firstName;
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}

	
	/*******
	 * <p> Method: String getMiddleName(String username) </p>
	 * 
	 * <p> Description: Get the middle name of a user given that user's username.</p>
	 * 
	 * @param username is the username of the user
	 * 
	 * @return the middle name of a user given that user's username 
	 *  
	 */
	// get the middle name
	public String getMiddleName(String username) {
		String query = "SELECT MiddleName FROM userDB WHERE userName = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, username);
	        ResultSet rs = pstmt.executeQuery();
	        
	        if (rs.next()) {
	            return rs.getString("middleName"); // Return the middle name if user exists
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
		return null;
	}

	
	/*******
	 * <p> Method: void updateMiddleName(String username, String middleName) </p>
	 * 
	 * <p> Description: Update the middle name of a user given that user's username and the new
	 * 		middle name.</p>
	 * 
	 * @param username is the username of the user
	 *  
	 * @param middleName is the new middle name for the user
	 *  
	 */
	// update the middle name
	public void updateMiddleName(String username, String middleName) {
	    String query = "UPDATE userDB SET middleName = ? WHERE username = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, middleName);
	        pstmt.setString(2, username);
	        pstmt.executeUpdate();
	        currentMiddleName = middleName;
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}
	
	
	/*******
	 * <p> Method: String getLastName(String username) </p>
	 * 
	 * <p> Description: Get the last name of a user given that user's username.</p>
	 * 
	 * @param username is the username of the user
	 * 
	 * @return the last name of a user given that user's username 
	 *  
	 */
	// get he last name
	public String getLastName(String username) {
		String query = "SELECT LastName FROM userDB WHERE userName = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, username);
	        ResultSet rs = pstmt.executeQuery();
	        
	        if (rs.next()) {
	            return rs.getString("lastName"); // Return last name role if user exists
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
		return null;
	}
	
	
	/*******
	 * <p> Method: void updateLastName(String username, String lastName) </p>
	 * 
	 * <p> Description: Update the middle name of a user given that user's username and the new
	 * 		middle name.</p>
	 * 
	 * @param username is the username of the user
	 *  
	 * @param lastName is the new last name for the user
	 *  
	 */
	// update the last name
	public void updateLastName(String username, String lastName) {
	    String query = "UPDATE userDB SET lastName = ? WHERE username = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, lastName);
	        pstmt.setString(2, username);
	        pstmt.executeUpdate();
	        currentLastName = lastName;
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}
	
	
	/*******
	 * <p> Method: String getPreferredFirstName(String username) </p>
	 * 
	 * <p> Description: Get the preferred first name of a user given that user's username.</p>
	 * 
	 * @param username is the username of the user
	 * 
	 * @return the preferred first name of a user given that user's username 
	 *  
	 */
	// get the preferred first name
	public String getPreferredFirstName(String username) {
		String query = "SELECT preferredFirstName FROM userDB WHERE userName = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, username);
	        ResultSet rs = pstmt.executeQuery();
	        
	        if (rs.next()) {
	            return rs.getString("firstName"); // Return the preferred first name if user exists
	        }
			
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
		return null;
	}
	
	
	/*******
	 * <p> Method: void updatePreferredFirstName(String username, String preferredFirstName) </p>
	 * 
	 * <p> Description: Update the preferred first name of a user given that user's username and
	 * 		the new preferred first name.</p>
	 * 
	 * @param username is the username of the user
	 *  
	 * @param preferredFirstName is the new preferred first name for the user
	 *  
	 */
	// update the preferred first name of the user
	public void updatePreferredFirstName(String username, String preferredFirstName) {
	    String query = "UPDATE userDB SET preferredFirstName = ? WHERE username = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, preferredFirstName);
	        pstmt.setString(2, username);
	        pstmt.executeUpdate();
	        currentPreferredFirstName = preferredFirstName;
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}
	
	
	/*******
	 * <p> Method: String getEmailAddress(String username) </p>
	 * 
	 * <p> Description: Get the email address of a user given that user's username.</p>
	 * 
	 * @param username is the username of the user
	 * 
	 * @return the email address of a user given that user's username 
	 *  
	 */
	// get the email address
	public String getEmailAddress(String username) {
		String query = "SELECT emailAddress FROM userDB WHERE userName = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, username);
	        ResultSet rs = pstmt.executeQuery();
	        
	        if (rs.next()) {
	            return rs.getString("emailAddress"); // Return the email address if user exists
	        }
			
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
		return null;
	}
	
	
	/*******
	 * <p> Method: void updateEmailAddress(String username, String emailAddress) </p>
	 * 
	 * <p> Description: Update the email address name of a user given that user's username and
	 * 		the new email address.</p>
	 * 
	 * @param username is the username of the user
	 *  
	 * @param emailAddress is the new preferred first name for the user
	 *  
	 */
	// update the email address
	public void updateEmailAddress(String username, String emailAddress) {
	    String query = "UPDATE userDB SET emailAddress = ? WHERE username = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, emailAddress);
	        pstmt.setString(2, username);
	        pstmt.executeUpdate();
	        currentEmailAddress = emailAddress;
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}
	
	
	/*******
	 * <p> Method: boolean getUserAccountDetails(String username) </p>
	 * 
	 * <p> Description: Get all the attributes of a user given that user's username.</p>
	 * 
	 * @param username is the username of the user
	 * 
	 * @return true of the get is successful, else false
	 *  
	 */
	// get the attributes for a specified user
	public boolean getUserAccountDetails(String username) {
		String query = "SELECT * FROM userDB WHERE username = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, username);
	        ResultSet rs = pstmt.executeQuery();			
			rs.next();
	    	currentUsername = rs.getString(2);
	    	currentPassword = rs.getString(3);
	    	currentFirstName = rs.getString(4);
	    	currentMiddleName = rs.getString(5);
	    	currentLastName = rs.getString(6);
	    	currentPreferredFirstName = rs.getString(7);
	    	currentEmailAddress = rs.getString(8);
	    	currentAdminRole = rs.getBoolean(9);
	    	currentNewStudent = rs.getBoolean(10);
	    	currentNewStaff = rs.getBoolean(11);
			return true;
	    } catch (SQLException e) {
			return false;
	    }
	}
	
	
	/*******
	 * <p> Method: boolean updateUserRole(String username, String role, String value) </p>
	 * 
	 * <p> Description: Update a specified role for a specified user's and set and update all the
	 * 		current user attributes.</p>
	 * 
	 * @param username is the username of the user
	 *  
	 * @param role is string that specifies the role to update
	 * 
	 * @param value is the string that specified TRUE or FALSE for the role
	 * 
	 * @return true if the update was successful, else false
	 *  
	 */
	// Update a users role
	public boolean updateUserRole(String username, String role, String value) {
		if (role.compareTo("Admin") == 0) {
			String query = "UPDATE userDB SET adminRole = ? WHERE username = ?";
			try (PreparedStatement pstmt = connection.prepareStatement(query)) {
				pstmt.setString(1, value);
				pstmt.setString(2, username);
				pstmt.executeUpdate();
				if (value.compareTo("true") == 0)
					currentAdminRole = true;
				else
					currentAdminRole = false;
				return true;
			} catch (SQLException e) {
				return false;
			}
		}
		if (role.compareTo("Student") == 0) {
			String query = "UPDATE userDB SET newStudent = ? WHERE username = ?";
			try (PreparedStatement pstmt = connection.prepareStatement(query)) {
				pstmt.setString(1, value);
				pstmt.setString(2, username);
				pstmt.executeUpdate();
				if (value.compareTo("true") == 0)
					currentNewStudent = true;
				else
					currentNewStudent = false;
				return true;
			} catch (SQLException e) {
				return false;
			}
		}
		if (role.compareTo("Staff") == 0) {
			String query = "UPDATE userDB SET newStaff = ? WHERE username = ?";
			try (PreparedStatement pstmt = connection.prepareStatement(query)) {
				pstmt.setString(1, value);
				pstmt.setString(2, username);
				pstmt.executeUpdate();
				if (value.compareTo("true") == 0)
					currentNewStaff = true;
				else
					currentNewStaff = false;
				return true;
			} catch (SQLException e) {
				return false;
			}
		}
		return false;
	}
	
	
	// Attribute getters for the current user
	/*******
	 * <p> Method: String getCurrentUsername() </p>
	 * 
	 * <p> Description: Get the current user's username.</p>
	 * 
	 * @return the username value is returned
	 *  
	 */
	public String getCurrentUsername() { return currentUsername;};

	
	/*******
	 * <p> Method: String getCurrentPassword() </p>
	 * 
	 * <p> Description: Get the current user's password.</p>
	 * 
	 * @return the password value is returned
	 *  
	 */
	public String getCurrentPassword() { return currentPassword;};

	
	/*******
	 * <p> Method: String getCurrentFirstName() </p>
	 * 
	 * <p> Description: Get the current user's first name.</p>
	 * 
	 * @return the first name value is returned
	 *  
	 */
	public String getCurrentFirstName() { return currentFirstName;};

	
	/*******
	 * <p> Method: String getCurrentMiddleName() </p>
	 * 
	 * <p> Description: Get the current user's middle name.</p>
	 * 
	 * @return the middle name value is returned
	 *  
	 */
	public String getCurrentMiddleName() { return currentMiddleName;};

	
	/*******
	 * <p> Method: String getCurrentLastName() </p>
	 * 
	 * <p> Description: Get the current user's last name.</p>
	 * 
	 * @return the last name value is returned
	 *  
	 */
	public String getCurrentLastName() { return currentLastName;};

	
	/*******
	 * <p> Method: String getCurrentPreferredFirstName( </p>
	 * 
	 * <p> Description: Get the current user's preferred first name.</p>
	 * 
	 * @return the preferred first name value is returned
	 *  
	 */
	public String getCurrentPreferredFirstName() { return currentPreferredFirstName;};

	
	/*******
	 * <p> Method: String getCurrentEmailAddress() </p>
	 * 
	 * <p> Description: Get the current user's email address name.</p>
	 * 
	 * @return the email address value is returned
	 *  
	 */
	public String getCurrentEmailAddress() { return currentEmailAddress;};

	
	/*******
	 * <p> Method: boolean getCurrentAdminRole() </p>
	 * 
	 * <p> Description: Get the current user's Admin role attribute.</p>
	 * 
	 * @return true if this user plays an Admin role, else false
	 *  
	 */
	public boolean getCurrentAdminRole() { return currentAdminRole;};

	
	/*******
	 * <p> Method: boolean getCurrentNewStudent() </p>
	 * 
	 * <p> Description: Get the current user's Student role attribute.</p>
	 * 
	 * @return true if this user plays a Student role, else false
	 *  
	 */
	public boolean getCurrentNewStudent() { return currentNewStudent;};

	
	/*******
	 * <p> Method: boolean getCurrentNewStaff() </p>
	 * 
	 * <p> Description: Get the current user's Reviewer role attribute.</p>
	 * 
	 * @return true if this user plays a Reviewer role, else false
	 *  
	 */
	public boolean getCurrentNewStaff() { return currentNewStaff;};

	
	public boolean deleteUser(String username) {
        String query = "DELETE FROM userDB WHERE userName = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, username);
            int affected = pstmt.executeUpdate();
            return affected > 0;
        } catch (SQLException e) {
            return false;
        }
    }
	
	public List<User> getAllUsers() {
        List<User> users = new ArrayList<User>();
        String query = "SELECT userName, password, firstName, middleName, lastName, " +
                "preferredFirstName, emailAddress, adminRole, newStudent, newStaff FROM userDB ORDER BY userName";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                User u = new User(
                    rs.getString("userName"),
                    rs.getString("password"),
                    rs.getString("firstName"),
                    rs.getString("middleName"),
                    rs.getString("lastName"),
                    rs.getString("preferredFirstName"),
                    rs.getString("emailAddress"),
                    rs.getBoolean("adminRole"),
                    rs.getBoolean("newStudent"),
                    rs.getBoolean("newStaff")
                );
                users.add(u);
            }
        } catch (SQLException e) {
            return users;
        }
        return users;
    }
	
	/***
    
	<p> Method: boolean setOneTimePassword(String username, String otp) </p>
	<p> Description: Sets a one-time password value for a user. </p>*/
	public boolean setOneTimePassword(String username, String otp) {
	    String query = "UPDATE userDB SET oneTimePassword = ? WHERE userName = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, otp);
	        pstmt.setString(2, username);
	        return pstmt.executeUpdate() > 0;} catch (SQLException e) { return false; }}

	    /***
	     
	<p> Method: String getOneTimePassword(String username) </p>
	<p> Description: Returns the one-time password for a user, or null if not set. </p>*/
	public String getOneTimePassword(String username) {
	    String query = "SELECT oneTimePassword FROM userDB WHERE userName = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, username);
	        ResultSet rs = pstmt.executeQuery();
	        if (rs.next()) return rs.getString(1);} catch (SQLException e) { /* ignore */ }
	    return null;}
	/***
	     
	<p> Method: boolean clearOneTimePassword(String username) </p>
	<p> Description: Clears the one-time password value for a user. </p>*/
	public boolean clearOneTimePassword(String username) {
	    String query = "UPDATE userDB SET oneTimePassword = NULL WHERE userName = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, username);
	        return pstmt.executeUpdate() > 0;} catch (SQLException e) { return false; }}

	    /***
	     
	<p> Method: boolean updatePassword(String username, String newPassword) </p>
	<p> Description: Updates the primary password for a user. </p>*/
	public boolean updatePassword(String username, String newPassword) {
	    String query = "UPDATE userDB SET password = ? WHERE userName = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, newPassword);
	        pstmt.setString(2, username);
	        int n = pstmt.executeUpdate();
	        if (n > 0) { currentPassword = newPassword; }
	        return n > 0;} catch (SQLException e) { return false; }}
	
	/*******
	 * <p> Debugging method</p>
	 * 
	 * <p> Description: Debugging method that dumps the database of the console.</p>
	 * 
	 * @throws SQLException if there is an issues accessing the database.
	 * 
	 */
	// Dumps the database.
	public void dump() throws SQLException {
		String query = "SELECT * FROM userDB";
		ResultSet resultSet = statement.executeQuery(query);
		ResultSetMetaData meta = resultSet.getMetaData();
		while (resultSet.next()) {
		for (int i = 0; i < meta.getColumnCount(); i++) {
		System.out.println(
		meta.getColumnLabel(i + 1) + ": " +
				resultSet.getString(i + 1));
		}
		System.out.println();
		}
		resultSet.close();
	}
	
	// ==================== POST DATABASE METHODS ====================
	
		/*******
		 * <p> Method: void savePost(Post post) </p>
		 * 
		 * <p> Description: Saves a post to the database.</p>
		 * 
		 */
		public void savePost(Post post) throws SQLException {
			String query = "MERGE INTO postsDB (postId, title, body, authorUsername, thread, " +
			               "createdAt, lastEditedAt, isDeleted) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
			try (PreparedStatement pstmt = connection.prepareStatement(query)) {
				pstmt.setString(1, post.getPostId());
				pstmt.setString(2, post.getTitle());
				pstmt.setString(3, post.getBody());
				pstmt.setString(4, post.getAuthorUsername());
				pstmt.setString(5, post.getThread());
				pstmt.setObject(6, post.getCreatedAt());
				pstmt.setObject(7, post.getLastEditedAt());
				pstmt.setBoolean(8, post.isDeleted());
				pstmt.executeUpdate();
			}
		}
		
		/*******
		 * <p> Method: List<Post> loadAllPosts() </p>
		 * 
		 * <p> Description: Loads all posts from the database.</p>
		 * 
		 */
		public List<Post> loadAllPosts() throws SQLException {
			List<Post> posts = new ArrayList<>();
			String query = "SELECT * FROM postsDB";
			try (PreparedStatement pstmt = connection.prepareStatement(query)) {
				ResultSet rs = pstmt.executeQuery();
				while (rs.next()) {
					Post post = new Post(
						rs.getString("postId"),
						rs.getString("title"),
						rs.getString("body"),
						rs.getString("authorUsername"),
						rs.getString("thread")
					);
					post.setCreatedAt(rs.getObject("createdAt", LocalDateTime.class));
					post.setLastEditedAt(rs.getObject("lastEditedAt", LocalDateTime.class));
					post.setDeleted(rs.getBoolean("isDeleted"));
					posts.add(post);
				}
			}
			return posts;
		}
		
		/*******
		 * <p> Method: void deletePost(String postId) </p>
		 * 
		 * <p> Description: Deletes a post from the database.</p>
		 * 
		 */
		public void deletePostFromDB(String postId) throws SQLException {
			String query = "DELETE FROM postsDB WHERE postId = ?";
			try (PreparedStatement pstmt = connection.prepareStatement(query)) {
				pstmt.setString(1, postId);
				pstmt.executeUpdate();
			}
		}
		
		// ==================== REPLY DATABASE METHODS ====================
		
		/*******
		 * <p> Method: void saveReply(Reply reply) </p>
		 * 
		 * <p> Description: Saves a reply to the database.</p>
		 * 
		 */
		public void saveReply(Reply reply) throws SQLException {
			String query = "MERGE INTO repliesDB (replyId, body, authorUsername, parentPostId, " +
					"createdAt, lastEditedAt, isDeleted, isRead, isFeedback) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
			try (PreparedStatement pstmt = connection.prepareStatement(query)) {
				pstmt.setString(1, reply.getReplyId());
				pstmt.setString(2, reply.getBody());
				pstmt.setString(3, reply.getAuthorUsername());
				pstmt.setString(4, reply.getParentPostId());
				pstmt.setObject(5, reply.getCreatedAt());
				pstmt.setObject(6, reply.getLastEditedAt());
				pstmt.setBoolean(7, reply.isDeleted());
				pstmt.setBoolean(8, reply.isRead());
				pstmt.setBoolean(9, reply.isFeedback());
				pstmt.executeUpdate();
			}
		}
		
		/*******
		 * <p> Method: List<Reply> loadAllReplies() </p>
		 * 
		 * <p> Description: Loads all replies from the database.</p>
		 * 
		 */
		public List<Reply> loadAllReplies() throws SQLException {
			List<Reply> replies = new ArrayList<>();
			String query = "SELECT * FROM repliesDB";
			try (PreparedStatement pstmt = connection.prepareStatement(query)) {
				ResultSet rs = pstmt.executeQuery();
				while (rs.next()) {
					boolean isFeedback = false;
					try {
						isFeedback = rs.getBoolean("isFeedback");
					} catch (SQLException e) {
						// Column may not exist in older databases, use default
					}
					
					Reply reply = new Reply(
						rs.getString("replyId"),
						rs.getString("body"),
						rs.getString("authorUsername"),
						rs.getString("parentPostId"),
						isFeedback
					);
					reply.setCreatedAt(rs.getObject("createdAt", LocalDateTime.class));
					reply.setLastEditedAt(rs.getObject("lastEditedAt", LocalDateTime.class));
					reply.setDeleted(rs.getBoolean("isDeleted"));
					reply.setRead(rs.getBoolean("isRead"));
					replies.add(reply);
				}
			}
			return replies;
		}
		
		/*******
		 * <p> Method: void deleteReply(String replyId) </p>
		 * 
		 * <p> Description: Deletes a reply from the database.</p>
		 * 
		 */
		public void deleteReplyFromDB(String replyId) throws SQLException {
			String query = "DELETE FROM repliesDB WHERE replyId = ?";
			try (PreparedStatement pstmt = connection.prepareStatement(query)) {
				pstmt.setString(1, replyId);
				pstmt.executeUpdate();
			}
		}


		/*******
		 * <p> Method: void saveParameter(Parameter parameter) </p>
		 * 
		 * <p> Description: Saves a grading parameter to the database.</p>
		 * 
		 */
		public void saveParameter(Parameter parameter) throws SQLException {
			String query = "MERGE INTO gradingParametersDB (parameterId, name, description, " +
		               "isActive, createdByUsername, createdAt, requiredPosts, " +
		               "requiredReplies, topics, threadId) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
			try (PreparedStatement pstmt = connection.prepareStatement(query)) {
				pstmt.setString(1, parameter.getParameterId());
				pstmt.setString(2, parameter.getName());
				pstmt.setString(3, parameter.getDescription());
				pstmt.setBoolean(4, parameter.isActive());
				pstmt.setString(5, parameter.getCreatedByUsername());
				pstmt.setObject(6, parameter.getCreatedAt());
				pstmt.setInt(7, parameter.getRequiredPosts());
				pstmt.setInt(8, parameter.getRequiredReplies());
				// Serialize topics list as comma-separated string
				String topicsStr = String.join(",", parameter.getTopics());
				pstmt.setString(9, topicsStr.isEmpty() ? null : topicsStr);
				pstmt.setString(10, parameter.getThreadId());
				pstmt.executeUpdate();
			}

			// Delete existing categories for this parameter
			String deleteCategories = "DELETE FROM parameterCategoriesDB WHERE parameterId = ?";
			try (PreparedStatement pstmt = connection.prepareStatement(deleteCategories)) {
				pstmt.setString(1, parameter.getParameterId());
				pstmt.executeUpdate();
			}
			
			// Insert new categories
			String insertCategory = "INSERT INTO parameterCategoriesDB (categoryId, parameterId, categoryName, weight, categoryOrder) VALUES (?, ?, ?, ?, ?)";
			List<ParameterCategory> categories = parameter.getCategories();
			for (int i = 0; i < categories.size(); i++) {
				try (PreparedStatement pstmt = connection.prepareStatement(insertCategory)) {
					ParameterCategory category = categories.get(i);
					pstmt.setString(1, parameter.getParameterId() + "_CAT_" + i);
					pstmt.setString(2, parameter.getParameterId());
					pstmt.setString(3, category.getCategoryName());
					pstmt.setDouble(4, category.getWeight());
					pstmt.setInt(5, i);
					pstmt.executeUpdate();
				}
			}
		}
		
		/*******
		 * <p> Method: List<Parameter> loadAllParameters() </p>
		 * 
		 * <p> Description: Loads all grading parameters from the database.</p>
		 * 
		 */
		public List<Parameter> loadAllParameters() throws SQLException {
			List<Parameter> parameters = new ArrayList<>();
			String query = "SELECT * FROM gradingParametersDB";
			try (PreparedStatement pstmt = connection.prepareStatement(query)) {
				ResultSet rs = pstmt.executeQuery();
				while (rs.next()) {
					// Load new fields with defaults for backward compatibility
					int requiredPosts = 0;
					int requiredReplies = 0;
					String threadId = null;
					List<String> topics = new ArrayList<>();
					
					try {
						requiredPosts = rs.getInt("requiredPosts");
					} catch (SQLException e) { /* use default */ }
					
					try {
						requiredReplies = rs.getInt("requiredReplies");
					} catch (SQLException e) { /* use default */ }
					
					try {
						threadId = rs.getString("threadId");
						if (rs.wasNull()) threadId = null;
					} catch (SQLException e) { /* use default */ }
					
					try {
						String topicsStr = rs.getString("topics");
						if (topicsStr != null && !topicsStr.trim().isEmpty()) {
							String[] topicArray = topicsStr.split(",");
							for (String topic : topicArray) {
								if (topic != null && !topic.trim().isEmpty()) {
									topics.add(topic.trim());
								}
							}
						}
					} catch (SQLException e) { /* use default */ }
					
					// Load categories
					List<ParameterCategory> categories = new ArrayList<>();
					String categoriesQuery = "SELECT * FROM parameterCategoriesDB WHERE parameterId = ? ORDER BY categoryOrder";
					try (PreparedStatement catPstmt = connection.prepareStatement(categoriesQuery)) {
						catPstmt.setString(1, rs.getString("parameterId"));
						ResultSet catRs = catPstmt.executeQuery();
						while (catRs.next()) {
							ParameterCategory category = new ParameterCategory(
								catRs.getString("categoryName"),
								catRs.getDouble("weight")
							);
							categories.add(category);
						}
					} catch (SQLException e) {
						// Categories table may not exist yet, use empty list
					}
					
					Parameter parameter = new Parameter(
						rs.getString("parameterId"),
						rs.getString("name"),
						rs.getString("description"),
						rs.getBoolean("isActive"),
						rs.getString("createdByUsername"),
						requiredPosts,
						requiredReplies,
						topics,
						threadId,
						categories
					);
					parameter.setCreatedAt(rs.getObject("createdAt", LocalDateTime.class));
					parameters.add(parameter);
				}
			}
			return parameters;
		}
		
		/*******
		 * <p> Method: List<Parameter> loadParametersByStaff(String staffUsername) </p>
		 * 
		 * <p> Description: Loads all grading parameters created by a specific staff member.</p>
		 * 
		 */
		public List<Parameter> loadParametersByStaff(String staffUsername) throws SQLException {
			List<Parameter> parameters = new ArrayList<>();
			String query = "SELECT * FROM gradingParametersDB WHERE createdByUsername = ? ORDER BY createdAt DESC";
			try (PreparedStatement pstmt = connection.prepareStatement(query)) {
				pstmt.setString(1, staffUsername);
				ResultSet rs = pstmt.executeQuery();
				while (rs.next()) {
					// Load new fields with defaults for backward compatibility
					int requiredPosts = 0;
					int requiredReplies = 0;
					String threadId = null;
					List<String> topics = new ArrayList<>();
					
					try {
						requiredPosts = rs.getInt("requiredPosts");
					} catch (SQLException e) { /* use default */ }
					
					try {
						requiredReplies = rs.getInt("requiredReplies");
					} catch (SQLException e) { /* use default */ }
					
					try {
						threadId = rs.getString("threadId");
						if (rs.wasNull()) threadId = null;
					} catch (SQLException e) { /* use default */ }
					
					try {
						String topicsStr = rs.getString("topics");
						if (topicsStr != null && !topicsStr.trim().isEmpty()) {
							String[] topicArray = topicsStr.split(",");
							for (String topic : topicArray) {
								if (topic != null && !topic.trim().isEmpty()) {
									topics.add(topic.trim());
								}
							}
						}
					} catch (SQLException e) { /* use default */ }
					
					// Load categories
					List<ParameterCategory> categories = new ArrayList<>();
					String categoriesQuery = "SELECT * FROM parameterCategoriesDB WHERE parameterId = ? ORDER BY categoryOrder";
					try (PreparedStatement catPstmt = connection.prepareStatement(categoriesQuery)) {
						catPstmt.setString(1, rs.getString("parameterId"));
						ResultSet catRs = catPstmt.executeQuery();
						while (catRs.next()) {
							ParameterCategory category = new ParameterCategory(
								catRs.getString("categoryName"),
								catRs.getDouble("weight")
							);
							categories.add(category);
						}
					} catch (SQLException e) {
						// Categories table may not exist yet, use empty list
					}
					
					Parameter parameter = new Parameter(
						rs.getString("parameterId"),
						rs.getString("name"),
						rs.getString("description"),
						rs.getBoolean("isActive"),
						rs.getString("createdByUsername"),
						requiredPosts,
						requiredReplies,
						topics,
						threadId,
						categories
					);
					parameter.setCreatedAt(rs.getObject("createdAt", LocalDateTime.class));
					parameters.add(parameter);
				}
			}
			return parameters;
		}
		
		/*******
		 * <p> Method: void updateParameter(Parameter parameter) </p>
		 * 
		 * <p> Description: Updates an existing grading parameter in the database. Uses MERGE INTO
		 * to update the parameter if it exists, or insert if it doesn't.</p>
		 * 
		 * @param parameter the Parameter object with updated values to save
		 * @throws SQLException if a database error occurs
		 */
		public void updateParameter(Parameter parameter) throws SQLException {
			// MERGE INTO works for both insert and update
			saveParameter(parameter);
		}
		
		/*******
		 * <p> Method: boolean deleteParameter(String parameterId) </p>
		 * 
		 * <p> Description: Deletes a grading parameter from the database by its ID.</p>
		 * 
		 * @param parameterId the ID of the parameter to delete
		 * @return true if the parameter was deleted, false if it was not found
		 * @throws SQLException if a database error occurs
		 */
		public boolean deleteParameter(String parameterId) throws SQLException {
			String query = "DELETE FROM gradingParametersDB WHERE parameterId = ?";
			try (PreparedStatement pstmt = connection.prepareStatement(query)) {
				pstmt.setString(1, parameterId);
				int affected = pstmt.executeUpdate();
				return affected > 0;
			}
		}
		
		/*******
		 * <p> Method: void saveThread(Thread thread) </p>
		 * 
		 * <p> Description: Saves a thread to the database using MERGE INTO (upsert).</p>
		 * 
		 * @param thread the Thread object to save
		 * @throws SQLException if a database error occurs
		 */
		public void saveThread(Thread thread) throws SQLException {
			String query = "MERGE INTO threadsDB (threadId, title, description, status, createdByUsername, createdAt) VALUES (?, ?, ?, ?, ?, ?)";
			try (PreparedStatement pstmt = connection.prepareStatement(query)) {
				pstmt.setString(1, thread.getThreadId());
				pstmt.setString(2, thread.getTitle());
				pstmt.setString(3, thread.getDescription());
				pstmt.setString(4, thread.getStatus().toString());
				pstmt.setString(5, thread.getCreatedByUsername());
				pstmt.setObject(6, thread.getCreatedAt());
				pstmt.executeUpdate();
			}
		}
		
		/*******
		 * <p> Method: List<Thread> loadAllThreads() </p>
		 * 
		 * <p> Description: Loads all threads from the database.</p>
		 * 
		 * @return list of all threads
		 * @throws SQLException if a database error occurs
		 */
		public List<Thread> loadAllThreads() throws SQLException {
			List<Thread> threads = new ArrayList<>();
			String query = "SELECT * FROM threadsDB";
			try (PreparedStatement pstmt = connection.prepareStatement(query)) {
				ResultSet rs = pstmt.executeQuery();
				while (rs.next()) {
					Thread thread = new Thread(
						rs.getString("threadId"),
						rs.getString("title"),
						rs.getString("description"),
						rs.getString("createdByUsername")
					);
					thread.setCreatedAt(rs.getObject("createdAt", LocalDateTime.class));
					String statusStr = rs.getString("status");
					if ("OPEN".equals(statusStr)) {
						thread.setStatus(Thread.ThreadStatus.OPEN);
					} else if ("CLOSED".equals(statusStr)) {
						thread.setStatus(Thread.ThreadStatus.CLOSED);
					} else {
						thread.setStatus(Thread.ThreadStatus.OPEN); // Default
					}
					threads.add(thread);
				}
			}
			return threads;
		}
		
		/*******
		 * <p> Method: List<Thread> loadThreadsByStaff(String staffUsername) </p>
		 * 
		 * <p> Description: Loads all threads created by a specific staff member.</p>
		 * 
		 * @param staffUsername the username of the staff member
		 * @return list of threads created by the staff member
		 * @throws SQLException if a database error occurs
		 */
		public List<Thread> loadThreadsByStaff(String staffUsername) throws SQLException {
			List<Thread> threads = new ArrayList<>();
			String query = "SELECT * FROM threadsDB WHERE createdByUsername = ? ORDER BY createdAt DESC";
			try (PreparedStatement pstmt = connection.prepareStatement(query)) {
				pstmt.setString(1, staffUsername);
				ResultSet rs = pstmt.executeQuery();
				while (rs.next()) {
					Thread thread = new Thread(
						rs.getString("threadId"),
						rs.getString("title"),
						rs.getString("description"),
						rs.getString("createdByUsername")
					);
					thread.setCreatedAt(rs.getObject("createdAt", LocalDateTime.class));
					String statusStr = rs.getString("status");
					if ("OPEN".equals(statusStr)) {
						thread.setStatus(Thread.ThreadStatus.OPEN);
					} else if ("CLOSED".equals(statusStr)) {
						thread.setStatus(Thread.ThreadStatus.CLOSED);
					} else {
						thread.setStatus(Thread.ThreadStatus.OPEN); // Default
					}
					threads.add(thread);
				}
			}
			return threads;
		}
		
		/*******
		 * <p> Method: boolean deleteThread(String threadId) </p>
		 * 
		 * <p> Description: Deletes a thread from the database.</p>
		 * 
		 * @param threadId the ID of the thread to delete
		 * @return true if the thread was deleted, false otherwise
		 * @throws SQLException if a database error occurs
		 */
		public boolean deleteThread(String threadId) throws SQLException {
			String query = "DELETE FROM threadsDB WHERE threadId = ?";
			try (PreparedStatement pstmt = connection.prepareStatement(query)) {
				pstmt.setString(1, threadId);
				int rowsAffected = pstmt.executeUpdate();
				return rowsAffected > 0;
			}
		}
		
		/*******
		 * <p> Method: int getPostCountForThread(String threadTitle) </p>
		 * 
		 * <p> Description: Returns the number of posts in a thread (by thread title).</p>
		 * 
		 * @param threadTitle the title of the thread
		 * @return the number of posts in the thread
		 * @throws SQLException if a database error occurs
		 */
		public int getPostCountForThread(String threadTitle) throws SQLException {
			String query = "SELECT COUNT(*) FROM postsDB WHERE thread = ? AND isDeleted = FALSE";
			try (PreparedStatement pstmt = connection.prepareStatement(query)) {
				pstmt.setString(1, threadTitle);
				ResultSet rs = pstmt.executeQuery();
				if (rs.next()) {
					return rs.getInt(1);
				}
			}
			return 0;
		}
		
		/*******
		 * <p> Method: void saveRequest(Request request) </p>
		 * 
		 * <p> Description: Saves a request to the database using MERGE INTO (upsert).</p>
		 * 
		 * @param request the Request object to save
		 * @throws SQLException if a database error occurs
		 */
		public void saveRequest(Request request) throws SQLException {
			String query = "MERGE INTO requestsDB (requestId, title, description, category, status, " +
			               "createdByUsername, closedByUsername, resolutionNotes, reopenReason, " +
			               "originalRequestId, createdAt, closedAt, reopenedAt) " +
			               "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
			try (PreparedStatement pstmt = connection.prepareStatement(query)) {
				pstmt.setString(1, request.getRequestId());
				pstmt.setString(2, request.getTitle());
				pstmt.setString(3, request.getDescription());
				pstmt.setString(4, request.getCategory() != null ? request.getCategory().toString() : null);
				pstmt.setString(5, request.getStatus().toString());
				pstmt.setString(6, request.getCreatedByUsername());
				pstmt.setString(7, request.getClosedByUsername());
				pstmt.setString(8, request.getResolutionNotes());
				pstmt.setString(9, request.getReopenReason());
				pstmt.setString(10, request.getOriginalRequestId());
				pstmt.setObject(11, request.getCreatedAt());
				pstmt.setObject(12, request.getClosedAt());
				pstmt.setObject(13, request.getReopenedAt());
				pstmt.executeUpdate();
			}
		}
		
		/*******
		 * <p> Method: List<Request> loadAllRequests() </p>
		 * 
		 * <p> Description: Loads all requests from the database.</p>
		 * 
		 * @return list of all requests
		 * @throws SQLException if a database error occurs
		 */
		public List<Request> loadAllRequests() throws SQLException {
			List<Request> requests = new ArrayList<>();
			String query = "SELECT * FROM requestsDB";
			try (PreparedStatement pstmt = connection.prepareStatement(query)) {
				ResultSet rs = pstmt.executeQuery();
				while (rs.next()) {
					Request request = parseRequestFromResultSet(rs);
					requests.add(request);
				}
			}
			return requests;
		}
		
		/*******
		 * <p> Method: List<Request> loadRequestsByStaff(String staffUsername) </p>
		 * 
		 * <p> Description: Loads all requests created by a specific staff member.</p>
		 * 
		 * @param staffUsername the username of the staff member
		 * @return list of requests created by the staff member
		 * @throws SQLException if a database error occurs
		 */
		public List<Request> loadRequestsByStaff(String staffUsername) throws SQLException {
			List<Request> requests = new ArrayList<>();
			String query = "SELECT * FROM requestsDB WHERE createdByUsername = ? ORDER BY createdAt DESC";
			try (PreparedStatement pstmt = connection.prepareStatement(query)) {
				pstmt.setString(1, staffUsername);
				ResultSet rs = pstmt.executeQuery();
				while (rs.next()) {
					Request request = parseRequestFromResultSet(rs);
					requests.add(request);
				}
			}
			return requests;
		}
		
		/*******
		 * <p> Method: Request parseRequestFromResultSet(ResultSet rs) </p>
		 * 
		 * <p> Description: Helper method to parse a Request object from a ResultSet.</p>
		 * 
		 * @param rs the ResultSet to parse from
		 * @return a Request object
		 * @throws SQLException if a database error occurs
		 */
		private Request parseRequestFromResultSet(ResultSet rs) throws SQLException {
			// Parse category
			Request.RequestCategory category = null;
			try {
				String categoryStr = rs.getString("category");
				if (categoryStr != null) {
					category = Request.RequestCategory.valueOf(categoryStr);
				}
			} catch (Exception e) {
				// Use default if parsing fails
			}
			
			// Parse status
			Request.RequestStatus status = Request.RequestStatus.OPEN;
			try {
				String statusStr = rs.getString("status");
				if ("CLOSED".equals(statusStr)) {
					status = Request.RequestStatus.CLOSED;
				}
			} catch (Exception e) {
				// Use default if parsing fails
			}
			
			Request request = new Request(
				rs.getString("requestId"),
				rs.getString("title"),
				rs.getString("description"),
				category,
				rs.getString("createdByUsername")
			);
			
			request.setStatus(status);
			request.setClosedByUsername(rs.getString("closedByUsername"));
			request.setResolutionNotes(rs.getString("resolutionNotes"));
			request.setReopenReason(rs.getString("reopenReason"));
			request.setOriginalRequestId(rs.getString("originalRequestId"));
			request.setCreatedAt(rs.getObject("createdAt", LocalDateTime.class));
			request.setClosedAt(rs.getObject("closedAt", LocalDateTime.class));
			request.setReopenedAt(rs.getObject("reopenedAt", LocalDateTime.class));
			
			return request;
		}
		
		/*******
		 * <p> Method: boolean deleteRequest(String requestId) </p>
		 * 
		 * <p> Description: Deletes a request from the database.</p>
		 * 
		 * @param requestId the ID of the request to delete
		 * @return true if the request was deleted, false otherwise
		 * @throws SQLException if a database error occurs
		 */
		public boolean deleteRequest(String requestId) throws SQLException {
			String query = "DELETE FROM requestsDB WHERE requestId = ?";
			try (PreparedStatement pstmt = connection.prepareStatement(query)) {
				pstmt.setString(1, requestId);
				int rowsAffected = pstmt.executeUpdate();
				return rowsAffected > 0;
			}
		}
		
	/*******
	 * <p> Method: void closeConnection()</p>
	 * 
	 * <p> Description: Closes the database statement and connection.</p>
	 * 
	 */
	// Closes the database statement and connection.
	public void closeConnection() {
		try{ 
			if(statement!=null) statement.close(); 
		} catch(SQLException se2) { 
			se2.printStackTrace();
		} 
		try { 
			if(connection!=null) connection.close(); 
		} catch(SQLException se){ 
			se.printStackTrace(); 
		} 
	}
}
