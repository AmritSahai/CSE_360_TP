package guiStudent;

import entityClasses.PostCollection;
import entityClasses.ReplyCollection;
import entityClasses.Post;
import entityClasses.Reply;
import database.Database;
import java.sql.SQLException;
import java.util.List;

/*******
 * <p> Title: ModelStudentHome Class. </p>
 * 
 * <p> Description: The Student Home Page Model. This class manages the forum data
 * including posts and replies for the student interface. All data is persisted to
 * the database and shared across student sessions.</p>
 * 
 * <p>Supports features such as creating, editing, deleting, and retrieving posts and replies.
 * This model handles database persistence and provides data access to both the Controller
 * and View components that implement the Student user features.</p>
 * 
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 * 
 * @author Joseph and Vrishik
 * 
 * @version 2.00		2025-10-27 TP2 updates and documentation improvements
 *  
 */

public class ModelStudentHome {
	
	// Forum collections shared across all student sessions
	
	/** The shared collection of all posts currently loaded in the system. */
	private static PostCollection postCollection = new PostCollection();
	
	/** The shared collection of all replies currently loaded in the system. */
	private static ReplyCollection replyCollection = new ReplyCollection();
	
	/** Reference to the central application database for saving and loading forum data. */
	private static Database theDatabase = applicationMain.FoundationsMain.database;
	
	/** Flag to ensure that posts and replies are loaded from the database only once. */
	private static boolean isInitialized = false;
	
	/*****
     * <p> Method: ModelStudentHome() </p>
     * 
     * <p> Description: This private constructor prevents instantiation of this class.
     * All attributes and methods are static, so there is no need to create an
     * object of this class. </p>
     */
	private ModelStudentHome() {
		// Prevent instantiation
	}
	
	
	/*****
     * <p> Method: initializeFromDatabase() </p>
     * 
     * <p> Loads all posts and replies from the database into memory. </p>
     * 
     */
	public static void initializeFromDatabase() {
		if (isInitialized) return;
		
		try {
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
			
			isInitialized = true;
			System.out.println("Forum data loaded from database: " + posts.size() + " posts, " + replies.size() + " replies");
		} catch (SQLException e) {
			System.err.println("Error loading forum data from database: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	/*****
     * <p> Method: savePostToDatabase(Post post) </p>
     * 
     * <p> Description: Saves a post to the database. </p>
     * @param post the Post object to save in the database 
     * 
     */
	public static void savePostToDatabase(Post post) {
		try {
			theDatabase.savePost(post);
		} catch (SQLException e) {
			System.err.println("Error saving post to database: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	/*****
     * <p> Method: saveReplyToDatabase(Reply reply) </p>
     * 
     * <p> Description: Saves a reply to the database. </p>
     * @param reply the Reply object to save in the database
     * 
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
     * <p> Method: getPostCollection() </p>
     * 
     * <p> Description: Returns the shared PostCollection instance. </p>
     * 
     * @return the PostCollection instance
     */
	public static PostCollection getPostCollection() {
		initializeFromDatabase();
		return postCollection;
	}
	
	/*****
     * <p> Method: getReplyCollection() </p>
     * 
     * <p> Description: Returns the shared ReplyCollection instance. </p>
     * 
     * @return the ReplyCollection instance
     */
	public static ReplyCollection getReplyCollection() {
		initializeFromDatabase();
		return replyCollection;
	}
}
