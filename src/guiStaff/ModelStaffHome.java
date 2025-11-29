package guiStaff;

import entityClasses.ParameterCollection;
import entityClasses.Parameter;
import entityClasses.PostCollection;
import entityClasses.ReplyCollection;
import entityClasses.Post;
import entityClasses.Reply;
import entityClasses.Thread;
import entityClasses.ThreadCollection;
import entityClasses.Request;
import entityClasses.RequestCollection;
import database.Database;
import java.sql.SQLException;
import java.util.List;

/*******
 * <p> Title: ModelStaffHome Class. </p>
 * 
 * <p> Description: The Staff Home Page Model. This class manages the grading parameter
 * data for the staff interface. Data is persisted to the database.</p>
 * 
 * <p> Copyright: Joseph © 2025 </p>
 * 
 * @author Joseph
 * 
 * @version 1.00		2025-01-16 Initial version
 *  
 */

public class ModelStaffHome {
	
	/*****
     * <p> Default Constructor </p>
     */
    public ModelStaffHome() {
        // existing initialization or leave empty
    }
	
	// Parameter collection - shared across all staff sessions
	private static ParameterCollection parameterCollection = new ParameterCollection();
	// Post and Reply collections - shared across all sessions
	private static PostCollection postCollection = new PostCollection();
	private static ReplyCollection replyCollection = new ReplyCollection();
	// Thread collection - shared across all sessions
	private static ThreadCollection threadCollection = new ThreadCollection();
	// Request collection - shared across all sessions
	private static RequestCollection requestCollection = new RequestCollection();
	private static Database theDatabase = applicationMain.FoundationsMain.database;
	private static boolean isInitialized = false;
	private static boolean postsInitialized = false;
	private static boolean threadsInitialized = false;
	private static boolean requestsInitialized = false;
	
	/*****
     * <p> Method: void initializeFromDatabase() </p>
     * 
     * <p> Description: Initializes the ParameterCollection by loading all parameters from the
     * database. This method ensures that the in-memory collection is synchronized with the
     * persistent database storage. The initialization is performed only once per application
     * session (singleton pattern) to prevent redundant database queries. If a SQLException
     * occurs during loading, the error is logged but does not prevent application startup.</p>
     * 
     * <p> This method is called automatically by getParameterCollection() if initialization
     * has not yet occurred.</p>
     */
	public static void initializeFromDatabase() {
		if (isInitialized) return;
		
		try {
			// Load all parameters
			List<Parameter> parameters = theDatabase.loadAllParameters();
			for (Parameter parameter : parameters) {
				parameterCollection.addParameter(parameter);
			}
			
			isInitialized = true;
			System.out.println("Grading parameters loaded from database: " + parameters.size() + " parameters");
		} catch (SQLException e) {
			System.err.println("Error loading grading parameters from database: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	/*****
     * <p> Method: void saveParameterToDatabase(Parameter parameter) </p>
     * 
     * <p> Description: Persists a Parameter object to the database. Uses the Database.saveParameter()
     * method to store the parameter data. If a SQLException occurs during the save operation,
     * the error is logged to System.err and printed to the console, but the exception is not
     * propagated to the caller. This method should be called after successfully creating a
     * parameter in the ParameterCollection.</p>
     * 
     * @param parameter the Parameter object to save to the database
     */
	public static void saveParameterToDatabase(Parameter parameter) {
		try {
			theDatabase.saveParameter(parameter);
		} catch (SQLException e) {
			System.err.println("Error saving parameter to database: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	/*****
     * <p> Method: ParameterCollection getParameterCollection() </p>
     * 
     * <p> Description: Returns the shared ParameterCollection instance, ensuring it has been
     * initialized from the database if necessary. This method implements a lazy initialization
     * pattern, calling initializeFromDatabase() on first access. The ParameterCollection is
     * shared across all staff sessions to maintain consistency with the database state.</p>
     * 
     * @return the singleton ParameterCollection instance, initialized from the database
     */
	public static ParameterCollection getParameterCollection() {
		initializeFromDatabase();
		return parameterCollection;
	}
	
	/*****
     * <p> Method: void updateParameterInDatabase(Parameter parameter) </p>
     * 
     * <p> Description: Updates an existing Parameter object in the database. Uses the
     * Database.updateParameter() method to persist the updated parameter data. If a SQLException
     * occurs during the update operation, the error is logged to System.err and printed to the
     * console, but the exception is not propagated to the caller.</p>
     * 
     * @param parameter the Parameter object with updated values to save to the database
     */
	public static void updateParameterInDatabase(Parameter parameter) {
		try {
			theDatabase.updateParameter(parameter);
		} catch (SQLException e) {
			System.err.println("Error updating parameter in database: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	/*****
     * <p> Method: boolean deleteParameterFromDatabase(String parameterId) </p>
     * 
     * <p> Description: Deletes a Parameter from the database by its ID. Uses the
     * Database.deleteParameter() method to remove the parameter. If a SQLException occurs during
     * the delete operation, the error is logged to System.err and printed to the console, and
     * the method returns false.</p>
     * 
     * @param parameterId the ID of the parameter to delete
     * @return true if the parameter was successfully deleted, false otherwise
     */
	public static boolean deleteParameterFromDatabase(String parameterId) {
		try {
			return theDatabase.deleteParameter(parameterId);
		} catch (SQLException e) {
			System.err.println("Error deleting parameter from database: " + e.getMessage());
			e.printStackTrace();
			return false;
		}
	}
	
	/*****
     * <p> Method: void initializePostsFromDatabase() </p>
     * 
     * <p> Description: Initializes the PostCollection and ReplyCollection by loading all posts and replies
     * from the database. This method ensures that the in-memory collections are synchronized with the
     * persistent database storage.</p>
     */
	public static void initializePostsFromDatabase() {
		if (postsInitialized) return;
		refreshPostsFromDatabase();
	}
	
	/*****
     * <p> Method: void refreshPostsFromDatabase() </p>
     * 
     * <p> Description: Refreshes posts and replies from the database, clearing existing data first.
     * This ensures the collections are synchronized with the latest database state.</p>
     */
	public static void refreshPostsFromDatabase() {
		try {
			// Clear existing collections
			postCollection = new PostCollection();
			replyCollection = new ReplyCollection();
			
			// Load all posts
			List<Post> posts = theDatabase.loadAllPosts();
			for (Post post : posts) {
				postCollection.addPost(post);
			}
			
			// Load all replies
			List<Reply> replies = theDatabase.loadAllReplies();
			for (Reply reply : replies) {
				replyCollection.addReply(reply);
			}
			
			postsInitialized = true;
			System.out.println("Posts and replies refreshed from database: " + posts.size() + " posts, " + replies.size() + " replies");
		} catch (SQLException e) {
			System.err.println("Error refreshing posts and replies from database: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	/*****
     * <p> Method: PostCollection getPostCollection() </p>
     * 
     * <p> Description: Returns the shared PostCollection instance, ensuring it has been
     * initialized from the database if necessary.</p>
     * 
     * @return the singleton PostCollection instance
     */
	public static PostCollection getPostCollection() {
		initializePostsFromDatabase();
		return postCollection;
	}
	
	/*****
     * <p> Method: ReplyCollection getReplyCollection() </p>
     * 
     * <p> Description: Returns the shared ReplyCollection instance, ensuring it has been
     * initialized from the database if necessary.</p>
     * 
     * @return the singleton ReplyCollection instance
     */
	public static ReplyCollection getReplyCollection() {
		initializePostsFromDatabase();
		return replyCollection;
	}
	
	/*****
     * <p> Method: void saveReplyToDatabase(Reply reply) </p>
     * 
     * <p> Description: Persists a Reply object to the database.</p>
     * 
     * @param reply the Reply object to save to the database
     */
	public static void saveReplyToDatabase(Reply reply) {
		try {
			theDatabase.saveReply(reply);
		} catch (SQLException e) {
			System.err.println("Error saving reply to database: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	/*****
     * <p> Method: void initializeThreadsFromDatabase() </p>
     * 
     * <p> Description: Initializes the ThreadCollection by loading all threads from the database.</p>
     */
	public static void initializeThreadsFromDatabase() {
		if (threadsInitialized) return;
		refreshThreadsFromDatabase();
	}
	
	/*****
     * <p> Method: void refreshThreadsFromDatabase() </p>
     * 
     * <p> Description: Refreshes threads from the database, clearing existing data first.</p>
     */
	public static void refreshThreadsFromDatabase() {
		try {
			// Clear existing collection
			threadCollection = new ThreadCollection();
			
			// Load all threads
			List<Thread> threads = theDatabase.loadAllThreads();
			for (Thread thread : threads) {
				threadCollection.addThread(thread);
			}
			
			threadsInitialized = true;
			System.out.println("Threads refreshed from database: " + threads.size() + " threads");
		} catch (SQLException e) {
			System.err.println("Error refreshing threads from database: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	/*****
     * <p> Method: ThreadCollection getThreadCollection() </p>
     * 
     * <p> Description: Returns the shared ThreadCollection instance.</p>
     * 
     * @return the singleton ThreadCollection instance
     */
	public static ThreadCollection getThreadCollection() {
		initializeThreadsFromDatabase();
		return threadCollection;
	}
	
	/*****
     * <p> Method: void saveThreadToDatabase(Thread thread) </p>
     * 
     * <p> Description: Persists a Thread object to the database.</p>
     * 
     * @param thread the Thread object to save to the database
     */
	public static void saveThreadToDatabase(Thread thread) {
		try {
			theDatabase.saveThread(thread);
		} catch (SQLException e) {
			System.err.println("Error saving thread to database: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	/*****
     * <p> Method: boolean deleteThreadFromDatabase(String threadId) </p>
     * 
     * <p> Description: Deletes a Thread from the database by its ID.</p>
     * 
     * @param threadId the ID of the thread to delete
     * @return true if the thread was successfully deleted, false otherwise
     */
	public static boolean deleteThreadFromDatabase(String threadId) {
		try {
			return theDatabase.deleteThread(threadId);
		} catch (SQLException e) {
			System.err.println("Error deleting thread from database: " + e.getMessage());
			e.printStackTrace();
			return false;
		}
	}
	
	/*****
     * <p> Method: int getPostCountForThread(String threadTitle) </p>
     * 
     * <p> Description: Returns the number of posts in a thread.</p>
     * 
     * @param threadTitle the title of the thread
     * @return the number of posts in the thread
     */
	public static int getPostCountForThread(String threadTitle) {
		try {
			return theDatabase.getPostCountForThread(threadTitle);
		} catch (SQLException e) {
			System.err.println("Error getting post count for thread: " + e.getMessage());
			e.printStackTrace();
			return 0;
		}
	}
	
	/*****
     * <p> Method: void initializeRequestsFromDatabase() </p>
     * 
     * <p> Description: Initializes the RequestCollection by loading all requests from the database.</p>
     */
	public static void initializeRequestsFromDatabase() {
		if (requestsInitialized) return;
		refreshRequestsFromDatabase();
	}
	
	/*****
     * <p> Method: void refreshRequestsFromDatabase() </p>
     * 
     * <p> Description: Refreshes requests from the database, clearing existing data first.</p>
     */
	public static void refreshRequestsFromDatabase() {
		try {
			// Clear existing collection
			requestCollection = new RequestCollection();
			
			// Load all requests
			List<Request> requests = theDatabase.loadAllRequests();
			for (Request request : requests) {
				requestCollection.addRequest(request);
			}
			
			requestsInitialized = true;
			System.out.println("Requests refreshed from database: " + requests.size() + " requests");
		} catch (SQLException e) {
			System.err.println("Error refreshing requests from database: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	/*****
     * <p> Method: RequestCollection getRequestCollection() </p>
     * 
     * <p> Description: Returns the shared RequestCollection instance.</p>
     * 
     * @return the singleton RequestCollection instance
     */
	public static RequestCollection getRequestCollection() {
		initializeRequestsFromDatabase();
		return requestCollection;
	}
	
	/*****
     * <p> Method: void saveRequestToDatabase(Request request) </p>
     * 
     * <p> Description: Persists a Request object to the database.</p>
     * 
     * @param request the Request object to save to the database
     */
	public static void saveRequestToDatabase(Request request) {
		try {
			theDatabase.saveRequest(request);
		} catch (SQLException e) {
			System.err.println("Error saving request to database: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	/*****
     * <p> Method: boolean deleteRequestFromDatabase(String requestId) </p>
     * 
     * <p> Description: Deletes a Request from the database by its ID.</p>
     * 
     * @param requestId the ID of the request to delete
     * @return true if the request was successfully deleted, false otherwise
     */
	public static boolean deleteRequestFromDatabase(String requestId) {
		try {
			return theDatabase.deleteRequest(requestId);
		} catch (SQLException e) {
			System.err.println("Error deleting request from database: " + e.getMessage());
			e.printStackTrace();
			return false;
		}
	}

}
