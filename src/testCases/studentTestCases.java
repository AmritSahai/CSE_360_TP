package testCases;
import entityClasses.Post;
import entityClasses.PostCollection;
import entityClasses.Reply;
import entityClasses.ReplyCollection;
import entityClasses.User;

/*******
 * <p> Title: studentTestCases Class. </p>
 * 
 * <p> Description: The studdentTestCases Class tests all of the CRUD functions and other functions 
 * that were stated in the student user stories. This includes testing creating posts, searching for 
 * posts, deleting posts, creating replies, and deleting replies.</p>
 * 
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 * 
 * @author Preetish
 * 
 * @version 2.00		2025-10-27 TP2 updates and Javadoc completion
 *  
 */

public class studentTestCases {
	
	private PostCollection pc;
	private ReplyCollection rc;
	private User student;
	private Post postToDelete1;
	private Post postToDelete2;
	private Reply replyToDelete;
	
	/*******
     * <p> Constructor: studentTestCases() </p>
     *
     * <p> Description: Has no functionality.</p>
     */
	public studentTestCases() {
		
	}
	/**********
	 * <p> Method: setup </p>
	 * 
	 * <p> Description: This method is used to instantiate the different PostCollection, ReplyCollection
	 * User, Post, and Reply objects that will be used by this class to test our code. Two post objects
	 * and one reply object get added to the post and reply collections so they can be properly deleted.</p>
	 * 
	 */
	public void setup() {
		pc = new PostCollection();
		rc = new ReplyCollection();
		student = new User("Student1", "Abc123*", "", "", "", "", "", false, true, false);
		postToDelete1 = new Post("POST_1", "Delete me", "delete me", "Student1", "General");
		postToDelete2 = new Post("POST_2", "Delete me", "delete me", "Student1", "General");
		replyToDelete = new Reply("REPLY_1", "Delete me", "Student1", "POST_2");
		
		// Add test posts to the collection for testing
		pc.addPost(postToDelete1);
		pc.addPost(postToDelete2);
		rc.addReply(replyToDelete);
	}
	
	/**********
	 * <p> Method: testCreatePosts </p>
	 * 
	 * <p> Description: This method tests the createPosts method in the PostCollection class. 3 unique
	 * posts are created to test 3 scenarios. The first scenario tests whether a student can post a normal 
	 * post. The second scenario tests if a post is created without a thread specified it will default to the 
	 * general thread. Finally the third scenario tests that if no title, content, or thread is specified
	 * then an error will be thrown.</p>
	 * 
	 */
	public void testCreatePosts() {
		System.out.println("=== Testing Post Creation ===");
		
		//Scenario 1: Student posts a normal post with a title, content, and thread.
		System.out.println("Scenario 1: Creating valid post...");
		String result1 = pc.createPost("Hello", "I am a student", "Student1", "General");
		System.out.println("Result: " + result1);
		if (result1.startsWith("POST_")) {
			System.out.println("✓ PASS: Valid post created successfully");
		} else {
			System.out.println("✗ FAIL: Expected POST_ID, got: " + result1);
		}
		
		//Scenario 2: Student posts a post with a title, content, and no thread.
		System.out.println("\nScenario 2: Creating post with null thread...");
		String result2 = pc.createPost("Hello", "I am a student", "Student1", null);
		System.out.println("Result: " + result2);
		if (result2.startsWith("POST_")) {
			System.out.println("✓ PASS: Post with null thread created successfully");
		} else {
			System.out.println("✗ FAIL: Expected POST_ID, got: " + result2);
		}
		
		//Scenario 3: Student posts a post with no title, no content, or a thread
		System.out.println("\nScenario 3: Creating invalid post (null title/content)...");
		String result3 = pc.createPost(null, null, "Student1", null);
		System.out.println("Result: " + result3);
		if (!result3.startsWith("POST_")) {
			System.out.println("✓ PASS: Invalid post correctly rejected");
		} else {
			System.out.println("✗ FAIL: Expected error message, got: " + result3);
		}
		
		System.out.println("=== Post Creation Tests Complete ===\n");
	}
	
	/**********
	 * <p> Method: testSearchPosts </p>
	 * 
	 * <p> Description: This method tests the searchPosts method in the PostCollection class. There are 2
	 * scenarios that are tested here. The first is searching for a post using a keyword normally and the
	 * second is searching for a post using no keyword. </p>
	 * 
	 */
	public void testSearchPosts() {
		System.out.println("=== Testing Post Search ===");
		
		//Scenario 1: Student searches for a post using a keyword.
		System.out.println("Scenario 1: Searching with keyword 'Hello'...");
		
		var results1 = pc.searchPosts("Hello", "All");
		
		System.out.println("Found " + results1.size() + " posts");
		
		if (results1.size() > 0) {
			System.out.println("✓ PASS: Search with keyword returned results");
		} else {
			System.out.println("✗ FAIL: Expected to find posts with 'Hello'");
		}
		
		//Scenario 2: Student searches for a post using no keyword.
		System.out.println("\nScenario 2: Searching with null keyword...");
		
		var results2 = pc.searchPosts(null, "All");
		
		System.out.println("Found " + results2.size() + " posts");
		
		System.out.println("✓ PASS: Search with null keyword handled");
		
		System.out.println("=== Post Search Tests Complete ===\n");
	}
	
	/**********
	 * <p> Method: testDeletePosts </p>
	 * 
	 * <p> Description: This method tests the deletePosts method in the PostCollection class. There are 2
	 * scenarios that are tested here. The first is deleting a post with no replies and the
	 * second is deleting a post with replies. </p>
	 * 
	 */
	
	public void testDeletePosts() {
		System.out.println("=== Testing Post Deletion ===");
		
		//Scenario 1: Student deletes a post with no replies.
		System.out.println("Scenario 1: Deleting post POST_1 with no replies");
		
		// Before deleting POST_1
		System.out.println("Checking POST_1 reply count before deletion...");
		int count1 = rc.getReplyCountForPost("POST_1");
		System.out.println("POST_1 has " + count1 + " replies");
		
		String result1 = pc.deletePost("POST_1", "Student1");
		System.out.println("Result: " + result1);
		if (result1.isEmpty()) {
			System.out.println("✓ PASS: Post deleted successfully");
		} else {
			System.out.println("✗ FAIL: Expected empty result, got: " + result1);
		}
		
		//Scenario 2: Student deletes a post with replies to them and a confirmation alert appears.
		System.out.println("\nScenario 2: Deleting post POST_2 with replies");
		
		// Before deleting POST_2  
		System.out.println("Checking POST_2 reply count before deletion...");
		int count2 = rc.getReplyCountForPost("POST_2");
		System.out.println("POST_2 has " + count2 + " replies");
		
		String result2 = pc.deletePost("POST_2", "Student1");
		
		System.out.println("Result: " + result2);
		
		if (result2.isEmpty()) {
			System.out.println("✓ PASS: Post with replies deleted successfully");
		} else {
			System.out.println("✗ FAIL: Expected empty result, got: " + result2);
		}
		
		System.out.println("=== Post Deletion Tests Complete ===\n");
	}
	
	/**********
	 * <p> Method: testCreateReplies </p>
	 * 
	 * <p> Description: This method tests the createReplies method in the ReplyCollection class. There are 2
	 * scenarios that are tested here. The first is replying to a post normally and the second is replying 
	 * to a post with no content in the reply. </p>
	 * 
	 */
	public void testCreateReplies() {
		System.out.println("=== Testing Reply Creation ===");
		
		//Scenario 1: Student replies to another student's post normally
		System.out.println("Scenario 1: Creating valid reply...");
		
		String result1 = rc.createReply("Hey", "Student1", "POST_2");
		
		System.out.println("Result: " + result1);
		
		if (result1.startsWith("REPLY_")) {
			System.out.println("✓ PASS: Valid reply created successfully");
		} else {
			System.out.println("✗ FAIL: Expected REPLY_ID, got: " + result1);
		}
		
		//Scenario 2: Student replies to another student's post by putting no content
		System.out.println("\nScenario 2: Creating reply with null content...");
		
		String result2 = rc.createReply(null, "Student1", "POST_2");
		
		System.out.println("Result: " + result2);
		
		if (!result2.startsWith("REPLY_")) {
			System.out.println("✓ PASS: Invalid reply correctly rejected");
		} else {
			System.out.println("✗ FAIL: Expected error message, got: " + result2);
		}
		
		System.out.println("=== Reply Creation Tests Complete ===\n");
	}
	
	/**********
	 * <p> Method: testDeleteReplies </p>
	 * 
	 * <p> Description: This method tests the deleteReplies method in the ReplyCollection class. There is 
	 * one scenario being tested here. It is where a student deletes their reply to a post. </p>
	 * 
	 */
	public void testDeleteReplies() {
		System.out.println("=== Testing Reply Deletion ===");
		
		//Scenario 1: Student deletes their reply to a post
		System.out.println("Scenario 1: Deleting reply REPLY_1...");
		String result = rc.deleteReply("REPLY_1", "Student1");
		System.out.println("Result: " + result);
		if (result.isEmpty()) {
			System.out.println("✓ PASS: Reply deleted successfully");
		} else {
			System.out.println("✗ FAIL: Expected empty result, got: " + result);
		}
		
		System.out.println("=== Reply Deletion Tests Complete ===\n");
	}

}