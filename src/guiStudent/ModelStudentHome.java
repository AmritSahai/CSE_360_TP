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
 * <p> Description: The Student (Student) Home Page Model. This class manages the forum
 * data including posts and replies for the student interface. Data is persisted to
 * the database.</p>
 * 
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 * 
 * @author Lynn Robert Carter
 * 
 * @version 1.00		2025-08-15 Initial version
 *  
 */

public class ModelStudentHome {
	
	// Forum collections - shared across all student sessions
	private static PostCollection postCollection = new PostCollection();
	private static ReplyCollection replyCollection = new ReplyCollection();
	private static Database theDatabase = applicationMain.FoundationsMain.database;
	private static boolean isInitialized = false;
	
	/*****
     * <p> Method: initializeFromDatabase() </p>
     * 
     * <p> Description: Loads all posts and replies from the database. </p>
     */
	public static void initializeFromDatabase() {
		if (isInitialized) return;
		refreshFromDatabase();
	}
	
	/*****
     * <p> Method: refreshFromDatabase() </p>
     * 
     * <p> Description: Refreshes posts and replies from the database, clearing existing data first.
     * This ensures the collections are synchronized with the latest database state.</p>
     */
	public static void refreshFromDatabase() {
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
			
			isInitialized = true;
			System.out.println("Forum data refreshed from database: " + posts.size() + " posts, " + replies.size() + " replies");
		} catch (SQLException e) {
			System.err.println("Error refreshing forum data from database: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	/*****
     * <p> Method: savePostToDatabase(Post post) </p>
     * 
     * <p> Description: Saves a post to the database. </p>
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
