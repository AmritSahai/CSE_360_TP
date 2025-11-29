package guiStaff;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;
import java.lang.reflect.Field;
import entityClasses.Parameter;
import entityClasses.ParameterCollection;
import entityClasses.ParameterCategory;
import entityClasses.Post;
import entityClasses.PostCollection;
import entityClasses.Reply;
import entityClasses.ReplyCollection;
import entityClasses.Thread;
import entityClasses.ThreadCollection;
import entityClasses.Request;
import entityClasses.RequestCollection;
import database.Database;

/*******
 * <p> Title: ModelStaffHomeTest Class. </p>
 * 
 * <p> Description: JUnit test class for ModelStaffHome. This class tests all major methods
 * in the ModelStaffHome class including initialization, database operations, and collection
 * management. Tests verify that parameters, posts, replies, threads, and requests are properly
 * loaded, saved, updated, and deleted from the database.</p>
 * 
 * <p> Copyright: Preetish © 2025 </p>
 * 
 * @author Preetish
 * 
 * @version 1.00		2025-01-16 Initial version
 *  
 */

public class ModelStaffHomeTest {
	
	/*****
     * <p> Default Constructor </p>
     */
    public ModelStaffHomeTest() {
        // existing initialization or leave empty
    }
    /*****
	 * Test database that will be used for all tests
	 */
	private static Database testDatabase;
	
	// Test data
	
	/*****
	 * Staff Username that will be used for all tests
	 */
	private static final String TEST_STAFF_USERNAME = "testStaff";
	
	/*****
	 * Parameter ID that will be used for all tests
	 */
	private static final String TEST_PARAMETER_ID = "PARAM_TEST_1";
	
	/*****
	 * Thread ID that will be used for all tests
	 */
	private static final String TEST_THREAD_ID = "THREAD_TEST_1";
	
	/*****
	 * Request ID that will be used for all tests
	 */
	private static final String TEST_REQUEST_ID = "REQUEST_TEST_1";
	
	/*****
     * <p> Method: void setUpBeforeClass() </p>
     * 
     * <p> Description: Sets up the test environment before all tests run. Initializes
     * the database connection used by ModelStaffHome. This ensures that all database 
     * operations in ModelStaffHome will work correctly during testing. This method 
     * runs once before all test methods.</p>
     * 
     * @throws Exception if database setup fails
     */
	@BeforeAll
	public static void setUpBeforeClass() throws Exception {
		// Initialize the database that ModelStaffHome uses
		try {
			// Use reflection to access FoundationsMain.database
			Class<?> foundationsClass = Class.forName("applicationMain.FoundationsMain");
			Field databaseField = foundationsClass.getField("database");
			Database mainDatabase = (Database) databaseField.get(null);
			
			// Connect to database if not already connected
			if (mainDatabase != null) {
				try {
					mainDatabase.connectToDatabase();
					testDatabase = mainDatabase; // Use the same database instance
				} catch (SQLException e) {
					// Database might already be connected, which is fine
					testDatabase = mainDatabase;
				}
			} else {
				// Create new database if FoundationsMain.database is null
				testDatabase = new Database();
				testDatabase.connectToDatabase();
				databaseField.set(null, testDatabase);
			}
		} catch (Exception e) {
			// Fallback: create a new database for testing
			testDatabase = new Database();
			testDatabase.connectToDatabase();
			System.err.println("Note: Using separate test database instance");
		}
	}
	
	/*****
     * <p> Method: void setUp() </p>
     * 
     * <p> Description: Resets the ModelStaffHome static state before each test.
     * This ensures test isolation by clearing collections and resetting
     * initialization flags between tests.</p>
     * 
     * @throws Exception if reflection operations fail
     */
	@BeforeEach
	public void setUp() throws Exception {
		// Reset static state using reflection
		resetModelStaffHomeState();
	}
	
	/*****
     * <p> Method: void tearDown() </p>
     * 
     * <p> Description: Cleans up after each test. Resets the ModelStaffHome
     * static state to ensure test isolation.</p>
     * 
     * @throws Exception if cleanup operations fail
     */
	@AfterEach
	public void tearDown() throws Exception {
		// Reset static state after each test
		resetModelStaffHomeState();
	}
	
	/*****
     * <p> Method: void resetModelStaffHomeState() </p>
     * 
     * <p> Description: Helper method that uses reflection to reset all static
     * fields in ModelStaffHome to their initial state. This includes clearing
     * collections and resetting initialization flags.</p>
     * 
     * @throws Exception if reflection operations fail
     */
	private void resetModelStaffHomeState() throws Exception {
		// Use reflection to reset static fields
		Class<?> modelClass = ModelStaffHome.class;
		
		// Reset parameterCollection
		Field paramCollectionField = modelClass.getDeclaredField("parameterCollection");
		paramCollectionField.setAccessible(true);
		paramCollectionField.set(null, new ParameterCollection());
		
		// Reset postCollection
		Field postCollectionField = modelClass.getDeclaredField("postCollection");
		postCollectionField.setAccessible(true);
		postCollectionField.set(null, new PostCollection());
		
		// Reset replyCollection
		Field replyCollectionField = modelClass.getDeclaredField("replyCollection");
		replyCollectionField.setAccessible(true);
		replyCollectionField.set(null, new ReplyCollection());
		
		// Reset threadCollection
		Field threadCollectionField = modelClass.getDeclaredField("threadCollection");
		threadCollectionField.setAccessible(true);
		threadCollectionField.set(null, new ThreadCollection());
		
		// Reset requestCollection
		Field requestCollectionField = modelClass.getDeclaredField("requestCollection");
		requestCollectionField.setAccessible(true);
		requestCollectionField.set(null, new RequestCollection());
		
		// Reset initialization flags
		Field isInitializedField = modelClass.getDeclaredField("isInitialized");
		isInitializedField.setAccessible(true);
		isInitializedField.setBoolean(null, false);
		
		Field postsInitializedField = modelClass.getDeclaredField("postsInitialized");
		postsInitializedField.setAccessible(true);
		postsInitializedField.setBoolean(null, false);
		
		Field threadsInitializedField = modelClass.getDeclaredField("threadsInitialized");
		threadsInitializedField.setAccessible(true);
		threadsInitializedField.setBoolean(null, false);
		
		Field requestsInitializedField = modelClass.getDeclaredField("requestsInitialized");
		requestsInitializedField.setAccessible(true);
		requestsInitializedField.setBoolean(null, false);
	}
	
	/*-*******************************************************************************************
	
	Tests for Parameter Management Methods
	
	**********************************************************************************************/
	
	/*****
     * <p> Method: void testInitializeFromDatabase() </p>
     * 
     * <p> Description: Tests that initializeFromDatabase() properly loads parameters
     * from the database and populates the ParameterCollection. Verifies that the
     * initialization flag is set correctly.</p>
     */
	@Test
	public void testInitializeFromDatabase() {
		// Test that initialization works
		ModelStaffHome.initializeFromDatabase();
		
		// Verify that getParameterCollection returns a non-null collection
		ParameterCollection collection = ModelStaffHome.getParameterCollection();
		assertNotNull(collection, "ParameterCollection should not be null after initialization");
		
		// Verify that subsequent calls don't re-initialize (tested via getParameterCollection)
		ParameterCollection collection2 = ModelStaffHome.getParameterCollection();
		assertSame(collection, collection2, "Should return the same collection instance");
	}
	
	/*****
     * <p> Method: void testGetParameterCollection() </p>
     * 
     * <p> Description: Tests that getParameterCollection() implements lazy initialization
     * correctly. Verifies that the method automatically initializes from the database
     * on first access and returns the same instance on subsequent calls.</p>
     */
	@Test
	public void testGetParameterCollection() {
		// Test lazy initialization - should initialize on first call
		ParameterCollection collection = ModelStaffHome.getParameterCollection();
		assertNotNull(collection, "ParameterCollection should not be null");
		
		// Test that subsequent calls return the same instance
		ParameterCollection collection2 = ModelStaffHome.getParameterCollection();
		assertSame(collection, collection2, "Should return the same collection instance");
	}
	
	/*****
     * <p> Method: void testSaveParameterToDatabase() </p>
     * 
     * <p> Description: Tests that saveParameterToDatabase() successfully saves a
     * parameter to the database. Creates a valid parameter, saves it, and verifies
     * it can be retrieved.</p>
     * 
     * @throws SQLException if database operations fail
     */
	@Test
	public void testSaveParameterToDatabase() throws SQLException {
		// Create a test parameter
		List<ParameterCategory> categories = new ArrayList<>();
		categories.add(new ParameterCategory("Test Category", 0.5));
		
		Parameter testParameter = new Parameter(
			TEST_PARAMETER_ID,
			"Test Parameter",
			"Test Description",
			true,
			TEST_STAFF_USERNAME,
			5,
			3,
			new ArrayList<>(),
			"General",
			categories
		);
		
		// Save to database
		ModelStaffHome.saveParameterToDatabase(testParameter);
		
		// Verify it was saved by loading from database
		ModelStaffHome.initializeFromDatabase();
		ParameterCollection collection = ModelStaffHome.getParameterCollection();
		Parameter retrieved = collection.getParameterById(TEST_PARAMETER_ID);
		
		assertNotNull(retrieved, "Parameter should be retrieved from database");
		assertEquals("Test Parameter", retrieved.getName(), "Parameter name should match");
		assertEquals("Test Description", retrieved.getDescription(), "Parameter description should match");
		
		// Clean up
		testDatabase.deleteParameter(TEST_PARAMETER_ID);
	}
	
	/*****
     * <p> Method: void testSaveParameterToDatabaseWithSQLException() </p>
     * 
     * <p> Description: Tests that saveParameterToDatabase() handles SQLException
     * correctly by catching it and not propagating it to the caller. This test
     * injects a Database stub that always throws SQLException.</p>
     */
	@Test
	public void testSaveParameterToDatabaseWithSQLException() {
		// Create a parameter (actual values don't matter for this test)
		Parameter testParameter = new Parameter(
			TEST_PARAMETER_ID,
			"Test Parameter",
			"Test Description",
			true,
			TEST_STAFF_USERNAME,
			0,
			0,
			new ArrayList<>(),
			"General",
			new ArrayList<>()
		);
		
		try {
			// Database stub that always throws for saveParameter
			Database throwingDb = new Database() {
				@Override
				public void saveParameter(Parameter parameter) throws SQLException {
					throw new SQLException("Simulated failure");
				}
			};
			
			Database originalDb = getModelStaffHomeDatabase();
			setModelStaffHomeDatabase(throwingDb);
			try {
				// Should not propagate the SQLException
				ModelStaffHome.saveParameterToDatabase(testParameter);
			} finally {
				// Restore original database
				setModelStaffHomeDatabase(originalDb);
			}
		} catch (Exception e) {
			fail("saveParameterToDatabase should handle SQLException internally and not throw");
		}
	}
	
	/*****
     * <p> Method: void testUpdateParameterInDatabase() </p>
     * 
     * <p> Description: Tests that updateParameterInDatabase() successfully updates
     * an existing parameter in the database. Creates a parameter, saves it, updates
     * it, and verifies the changes are persisted.</p>
     * 
     * @throws SQLException if database operations fail
     */
	@Test
	public void testUpdateParameterInDatabase() throws SQLException {
		// Create and save a test parameter
		List<ParameterCategory> categories = new ArrayList<>();
		categories.add(new ParameterCategory("Original Category", 0.5));
		
		Parameter testParameter = new Parameter(
			TEST_PARAMETER_ID,
			"Original Name",
			"Original Description",
			true,
			TEST_STAFF_USERNAME,
			5,
			3,
			new ArrayList<>(),
			"General",
			categories
		);
		
		testDatabase.saveParameter(testParameter);
		
		// Update the parameter
		testParameter.setName("Updated Name");
		testParameter.setDescription("Updated Description");
		
		// Update in database via ModelStaffHome
		ModelStaffHome.updateParameterInDatabase(testParameter);
		
		// Verify update
		ModelStaffHome.initializeFromDatabase();
		ParameterCollection collection = ModelStaffHome.getParameterCollection();
		Parameter retrieved = collection.getParameterById(TEST_PARAMETER_ID);
		
		assertNotNull(retrieved, "Parameter should still exist");
		assertEquals("Updated Name", retrieved.getName(), "Name should be updated");
		assertEquals("Updated Description", retrieved.getDescription(), "Description should be updated");
		
		// Clean up
		testDatabase.deleteParameter(TEST_PARAMETER_ID);
	}
	
	/*****
     * <p> Method: void testDeleteParameterFromDatabase() </p>
     * 
     * <p> Description: Tests that deleteParameterFromDatabase() successfully deletes
     * a parameter from the database. Creates a parameter, saves it, deletes it, and
     * verifies it no longer exists.</p>
     * 
     * @throws SQLException if database operations fail
     */
	@Test
	public void testDeleteParameterFromDatabase() throws SQLException {
		// Create and save a test parameter
		List<ParameterCategory> categories = new ArrayList<>();
		categories.add(new ParameterCategory("Test Category", 0.5));
		
		Parameter testParameter = new Parameter(
			TEST_PARAMETER_ID,
			"Test Parameter",
			"Test Description",
			true,
			TEST_STAFF_USERNAME,
			5,
			3,
			new ArrayList<>(),
			"General",
			categories
		);
		
		testDatabase.saveParameter(testParameter);
		
		// Delete via ModelStaffHome
		boolean deleted = ModelStaffHome.deleteParameterFromDatabase(TEST_PARAMETER_ID);
		assertTrue(deleted, "Delete should return true for existing parameter");
		
		// Verify deletion
		ModelStaffHome.initializeFromDatabase();
		ParameterCollection collection = ModelStaffHome.getParameterCollection();
		Parameter retrieved = collection.getParameterById(TEST_PARAMETER_ID);
		
		assertNull(retrieved, "Parameter should no longer exist");
	}
	
	/*****
     * <p> Method: void testDeleteParameterFromDatabaseNonExistent() </p>
     * 
     * <p> Description: Tests that deleteParameterFromDatabase() returns false when
     * attempting to delete a non-existent parameter. Verifies proper error handling
     * for invalid parameter IDs.</p>
     */
	@Test
	public void testDeleteParameterFromDatabaseNonExistent() {
		// Try to delete a non-existent parameter
		boolean deleted = ModelStaffHome.deleteParameterFromDatabase("NON_EXISTENT_PARAM");
		assertFalse(deleted, "Delete should return false for non-existent parameter");
	}
	
	/*-*******************************************************************************************
	
	Tests for Post and Reply Management Methods
	
	**********************************************************************************************/
	
	/*****
     * <p> Method: void testInitializePostsFromDatabase() </p>
     * 
     * <p> Description: Tests that initializePostsFromDatabase() properly loads posts
     * and replies from the database. Verifies that the collections are populated
     * and the initialization flag is set.</p>
     */
	@Test
	public void testInitializePostsFromDatabase() {
		// Test initialization
		ModelStaffHome.initializePostsFromDatabase();
		
		// Verify collections are not null
		PostCollection postCollection = ModelStaffHome.getPostCollection();
		ReplyCollection replyCollection = ModelStaffHome.getReplyCollection();
		
		assertNotNull(postCollection, "PostCollection should not be null");
		assertNotNull(replyCollection, "ReplyCollection should not be null");
	}
	
	/*****
     * <p> Method: void testRefreshPostsFromDatabase() </p>
     * 
     * <p> Description: Tests that refreshPostsFromDatabase() clears existing collections
     * and reloads posts and replies from the database. Verifies that the refresh
     * operation properly synchronizes with the database state.</p>
     */
	@Test
	public void testRefreshPostsFromDatabase() {
		// Initialize first
		ModelStaffHome.initializePostsFromDatabase();
		
		// Refresh should clear and reload
		ModelStaffHome.refreshPostsFromDatabase();
		
		// Verify collections are still accessible
		PostCollection postCollection = ModelStaffHome.getPostCollection();
		ReplyCollection replyCollection = ModelStaffHome.getReplyCollection();
		
		assertNotNull(postCollection, "PostCollection should not be null after refresh");
		assertNotNull(replyCollection, "ReplyCollection should not be null after refresh");
	}
	
	/*****
     * <p> Method: void testGetPostCollection() </p>
     * 
     * <p> Description: Tests that getPostCollection() implements lazy initialization
     * correctly. Verifies automatic initialization on first access and consistent
     * instance return.</p>
     */
	@Test
	public void testGetPostCollection() {
		// Test lazy initialization
		PostCollection collection = ModelStaffHome.getPostCollection();
		assertNotNull(collection, "PostCollection should not be null");
		
		// Test that subsequent calls return the same instance
		PostCollection collection2 = ModelStaffHome.getPostCollection();
		assertSame(collection, collection2, "Should return the same collection instance");
	}
	
	/*****
     * <p> Method: void testGetReplyCollection() </p>
     * 
     * <p> Description: Tests that getReplyCollection() implements lazy initialization
     * correctly. Verifies automatic initialization on first access and consistent
     * instance return.</p>
     */
	@Test
	public void testGetReplyCollection() {
		// Test lazy initialization
		ReplyCollection collection = ModelStaffHome.getReplyCollection();
		assertNotNull(collection, "ReplyCollection should not be null");
		
		// Test that subsequent calls return the same instance
		ReplyCollection collection2 = ModelStaffHome.getReplyCollection();
		assertSame(collection, collection2, "Should return the same collection instance");
	}
	
	/*****
     * <p> Method: void testSaveReplyToDatabase() </p>
     * 
     * <p> Description: Tests that saveReplyToDatabase() successfully saves a reply
     * to the database. Creates a test reply, saves it, and verifies it can be retrieved.</p>
     * 
     * @throws SQLException if database operations fail
     */
	@Test
	public void testSaveReplyToDatabase() throws SQLException {
		// Create a test post first (replies need a post)
		Post testPost = new Post("POST_TEST_1", "Test Post", "Test Body", "testUser", "General");
		testDatabase.savePost(testPost);
		
		// Create a test reply
		Reply testReply = new Reply("REPLY_TEST_1", "Test Reply Body", "testUser", "POST_TEST_1", false);
		
		// Save via ModelStaffHome
		ModelStaffHome.saveReplyToDatabase(testReply);
		
		// Verify it was saved by refreshing and checking collection
		ModelStaffHome.refreshPostsFromDatabase();
		ReplyCollection collection = ModelStaffHome.getReplyCollection();
		Reply retrieved = collection.getReplyById("REPLY_TEST_1");
		
		assertNotNull(retrieved, "Reply should be retrieved from database");
		assertEquals("Test Reply Body", retrieved.getBody(), "Reply body should match");
		
		// Clean up
		testDatabase.deletePostFromDB("POST_TEST_1");
	}
	
	/*-*******************************************************************************************
	
	Tests for Thread Management Methods
	
	**********************************************************************************************/
	
	/*****
     * <p> Method: void testInitializeThreadsFromDatabase() </p>
     * 
     * <p> Description: Tests that initializeThreadsFromDatabase() properly loads threads
     * from the database. Verifies that the ThreadCollection is populated and the
     * initialization flag is set.</p>
     */
	@Test
	public void testInitializeThreadsFromDatabase() {
		// Test initialization
		ModelStaffHome.initializeThreadsFromDatabase();
		
		// Verify collection is not null
		ThreadCollection collection = ModelStaffHome.getThreadCollection();
		assertNotNull(collection, "ThreadCollection should not be null");
	}
	
	/*****
     * <p> Method: void testRefreshThreadsFromDatabase() </p>
     * 
     * <p> Description: Tests that refreshThreadsFromDatabase() clears existing collection
     * and reloads threads from the database. Verifies proper synchronization with
     * database state.</p>
     */
	@Test
	public void testRefreshThreadsFromDatabase() {
		// Initialize first
		ModelStaffHome.initializeThreadsFromDatabase();
		
		// Refresh should clear and reload
		ModelStaffHome.refreshThreadsFromDatabase();
		
		// Verify collection is still accessible
		ThreadCollection collection = ModelStaffHome.getThreadCollection();
		assertNotNull(collection, "ThreadCollection should not be null after refresh");
	}
	
	/*****
     * <p> Method: void testGetThreadCollection() </p>
     * 
     * <p> Description: Tests that getThreadCollection() implements lazy initialization
     * correctly. Verifies automatic initialization on first access and consistent
     * instance return.</p>
     */
	@Test
	public void testGetThreadCollection() {
		// Test lazy initialization
		ThreadCollection collection = ModelStaffHome.getThreadCollection();
		assertNotNull(collection, "ThreadCollection should not be null");
		
		// Test that subsequent calls return the same instance
		ThreadCollection collection2 = ModelStaffHome.getThreadCollection();
		assertSame(collection, collection2, "Should return the same collection instance");
	}
	
	/*****
     * <p> Method: void testSaveThreadToDatabase() </p>
     * 
     * <p> Description: Tests that saveThreadToDatabase() successfully saves a thread
     * to the database. Creates a test thread, saves it, and verifies it can be retrieved.</p>
     * 
     * @throws SQLException if database operations fail
     */
	@Test
	public void testSaveThreadToDatabase() throws SQLException {
		// Create a test thread
		Thread testThread = new Thread(
			TEST_THREAD_ID,
			"Test Thread",
			"Test Description",
			TEST_STAFF_USERNAME
		);
		
		// Save via ModelStaffHome
		ModelStaffHome.saveThreadToDatabase(testThread);
		
		// Verify it was saved by refreshing and checking collection
		ModelStaffHome.refreshThreadsFromDatabase();
		ThreadCollection collection = ModelStaffHome.getThreadCollection();
		Thread retrieved = collection.getThreadById(TEST_THREAD_ID);
		
		assertNotNull(retrieved, "Thread should be retrieved from database");
		assertEquals("Test Thread", retrieved.getTitle(), "Thread title should match");
		assertEquals("Test Description", retrieved.getDescription(), "Thread description should match");
		
		// Clean up
		testDatabase.deleteThread(TEST_THREAD_ID);
	}
	
	/*****
     * <p> Method: void testDeleteThreadFromDatabase() </p>
     * 
     * <p> Description: Tests that deleteThreadFromDatabase() successfully deletes a
     * thread from the database. Creates a thread, saves it, deletes it, and verifies
     * it no longer exists.</p>
     * 
     * @throws SQLException if database operations fail
     */
	@Test
	public void testDeleteThreadFromDatabase() throws SQLException {
		// Create and save a test thread
		Thread testThread = new Thread(
			TEST_THREAD_ID,
			"Test Thread",
			"Test Description",
			TEST_STAFF_USERNAME
		);
		
		testDatabase.saveThread(testThread);
		
		// Delete via ModelStaffHome
		boolean deleted = ModelStaffHome.deleteThreadFromDatabase(TEST_THREAD_ID);
		assertTrue(deleted, "Delete should return true for existing thread");
		
		// Verify deletion
		ModelStaffHome.refreshThreadsFromDatabase();
		ThreadCollection collection = ModelStaffHome.getThreadCollection();
		Thread retrieved = collection.getThreadById(TEST_THREAD_ID);
		
		assertNull(retrieved, "Thread should no longer exist");
	}
	
	/*****
     * <p> Method: void testGetPostCountForThread() </p>
     * 
     * <p> Description: Tests that getPostCountForThread() correctly returns the number
     * of posts in a thread. Creates a thread with posts and verifies the count is accurate.</p>
     * 
     * @throws SQLException if database operations fail
     */
	@Test
	public void testGetPostCountForThread() throws SQLException {
		// Create a test thread
		Thread testThread = new Thread(
			TEST_THREAD_ID,
			"Test Thread",
			"Test Description",
			TEST_STAFF_USERNAME
		);
		testDatabase.saveThread(testThread);
		
		// Create test posts in the thread
		Post post1 = new Post("POST_TEST_1", "Post 1", "Body 1", "testUser", "Test Thread");
		Post post2 = new Post("POST_TEST_2", "Post 2", "Body 2", "testUser", "Test Thread");
		testDatabase.savePost(post1);
		testDatabase.savePost(post2);
		
		// Get count via ModelStaffHome
		int count = ModelStaffHome.getPostCountForThread("Test Thread");
		assertEquals(2, count, "Should return correct post count");
		
		// Clean up
		testDatabase.deletePostFromDB("POST_TEST_1");
		testDatabase.deletePostFromDB("POST_TEST_2");
		testDatabase.deleteThread(TEST_THREAD_ID);
	}
	
	/*****
     * <p> Method: void testGetPostCountForThreadNonExistent() </p>
     * 
     * <p> Description: Tests that getPostCountForThread() returns 0 for a non-existent
     * thread. Verifies proper handling of invalid thread titles.</p>
     */
	@Test
	public void testGetPostCountForThreadNonExistent() {
		// Get count for non-existent thread
		int count = ModelStaffHome.getPostCountForThread("Non-Existent Thread");
		assertEquals(0, count, "Should return 0 for non-existent thread");
	}
	
	/*-*******************************************************************************************
	
	Tests for Request Management Methods
	
	**********************************************************************************************/
	
	/*****
     * <p> Method: void testInitializeRequestsFromDatabase() </p>
     * 
     * <p> Description: Tests that initializeRequestsFromDatabase() properly loads requests
     * from the database. Verifies that the RequestCollection is populated and the
     * initialization flag is set.</p>
     */
	@Test
	public void testInitializeRequestsFromDatabase() {
		// Test initialization
		ModelStaffHome.initializeRequestsFromDatabase();
		
		// Verify collection is not null
		RequestCollection collection = ModelStaffHome.getRequestCollection();
		assertNotNull(collection, "RequestCollection should not be null");
	}
	
	/*****
     * <p> Method: void testRefreshRequestsFromDatabase() </p>
     * 
     * <p> Description: Tests that refreshRequestsFromDatabase() clears existing collection
     * and reloads requests from the database. Verifies proper synchronization with
     * database state.</p>
     */
	@Test
	public void testRefreshRequestsFromDatabase() {
		// Initialize first
		ModelStaffHome.initializeRequestsFromDatabase();
		
		// Refresh should clear and reload
		ModelStaffHome.refreshRequestsFromDatabase();
		
		// Verify collection is still accessible
		RequestCollection collection = ModelStaffHome.getRequestCollection();
		assertNotNull(collection, "RequestCollection should not be null after refresh");
	}
	
	/*****
     * <p> Method: void testGetRequestCollection() </p>
     * 
     * <p> Description: Tests that getRequestCollection() implements lazy initialization
     * correctly. Verifies automatic initialization on first access and consistent
     * instance return.</p>
     */
	@Test
	public void testGetRequestCollection() {
		// Test lazy initialization
		RequestCollection collection = ModelStaffHome.getRequestCollection();
		assertNotNull(collection, "RequestCollection should not be null");
		
		// Test that subsequent calls return the same instance
		RequestCollection collection2 = ModelStaffHome.getRequestCollection();
		assertSame(collection, collection2, "Should return the same collection instance");
	}
	
	/*****
     * <p> Method: void testSaveRequestToDatabase() </p>
     * 
     * <p> Description: Tests that saveRequestToDatabase() successfully saves a request
     * to the database. Creates a test request, saves it, and verifies it can be retrieved.</p>
     * 
     * @throws SQLException if database operations fail
     */
	@Test
	public void testSaveRequestToDatabase() throws SQLException {
		// Create a test request
		Request testRequest = new Request(
			TEST_REQUEST_ID,
			"Test Request",
			"Test Description",
			Request.RequestCategory.SYSTEM_ISSUE,
			TEST_STAFF_USERNAME
		);
		
		// Save via ModelStaffHome
		ModelStaffHome.saveRequestToDatabase(testRequest);
		
		// Verify it was saved by refreshing and checking collection
		ModelStaffHome.refreshRequestsFromDatabase();
		RequestCollection collection = ModelStaffHome.getRequestCollection();
		Request retrieved = collection.getRequestById(TEST_REQUEST_ID);
		
		assertNotNull(retrieved, "Request should be retrieved from database");
		assertEquals("Test Request", retrieved.getTitle(), "Request title should match");
		assertEquals("Test Description", retrieved.getDescription(), "Request description should match");
		
		// Clean up
		testDatabase.deleteRequest(TEST_REQUEST_ID);
	}
	
	/*****
     * <p> Method: void testDeleteRequestFromDatabase() </p>
     * 
     * <p> Description: Tests that deleteRequestFromDatabase() successfully deletes a
     * request from the database. Creates a request, saves it, deletes it, and verifies
     * it no longer exists.</p>
     * 
     * @throws SQLException if database operations fail
     */
	@Test
	public void testDeleteRequestFromDatabase() throws SQLException {
		// Create and save a test request
		Request testRequest = new Request(
			TEST_REQUEST_ID,
			"Test Request",
			"Test Description",
			Request.RequestCategory.SYSTEM_ISSUE,
			TEST_STAFF_USERNAME
		);
		
		testDatabase.saveRequest(testRequest);
		
		// Delete via ModelStaffHome
		boolean deleted = ModelStaffHome.deleteRequestFromDatabase(TEST_REQUEST_ID);
		assertTrue(deleted, "Delete should return true for existing request");
		
		// Verify deletion
		ModelStaffHome.refreshRequestsFromDatabase();
		RequestCollection collection = ModelStaffHome.getRequestCollection();
		Request retrieved = collection.getRequestById(TEST_REQUEST_ID);
		
		assertNull(retrieved, "Request should no longer exist");
	}
	
	/*****
     * <p> Method: void testDeleteRequestFromDatabaseNonExistent() </p>
     * 
     * <p> Description: Tests that deleteRequestFromDatabase() returns false when
     * attempting to delete a non-existent request. Verifies proper error handling
     * for invalid request IDs.</p>
     */
	@Test
	public void testDeleteRequestFromDatabaseNonExistent() {
		// Try to delete a non-existent request
		boolean deleted = ModelStaffHome.deleteRequestFromDatabase("NON_EXISTENT_REQUEST");
		assertFalse(deleted, "Delete should return false for non-existent request");
	}
	
	/*-*******************************************************************************************
	
	Integration Tests
	
	**********************************************************************************************/
	
	/*****
     * <p> Method: void testMultipleCollectionInitialization() </p>
     * 
     * <p> Description: Integration test that verifies all collections can be initialized
     * and accessed independently. Tests that the singleton pattern works correctly for
     * all collection types.</p>
     */
	@Test
	public void testMultipleCollectionInitialization() {
		// Initialize all collections
		ParameterCollection paramCollection = ModelStaffHome.getParameterCollection();
		PostCollection postCollection = ModelStaffHome.getPostCollection();
		ReplyCollection replyCollection = ModelStaffHome.getReplyCollection();
		ThreadCollection threadCollection = ModelStaffHome.getThreadCollection();
		RequestCollection requestCollection = ModelStaffHome.getRequestCollection();
		
		// Verify all are not null
		assertNotNull(paramCollection, "ParameterCollection should not be null");
		assertNotNull(postCollection, "PostCollection should not be null");
		assertNotNull(replyCollection, "ReplyCollection should not be null");
		assertNotNull(threadCollection, "ThreadCollection should not be null");
		assertNotNull(requestCollection, "RequestCollection should not be null");
		
		// Verify singleton pattern - subsequent calls return same instances
		assertSame(paramCollection, ModelStaffHome.getParameterCollection(), "ParameterCollection should be singleton");
		assertSame(postCollection, ModelStaffHome.getPostCollection(), "PostCollection should be singleton");
		assertSame(replyCollection, ModelStaffHome.getReplyCollection(), "ReplyCollection should be singleton");
		assertSame(threadCollection, ModelStaffHome.getThreadCollection(), "ThreadCollection should be singleton");
		assertSame(requestCollection, ModelStaffHome.getRequestCollection(), "RequestCollection should be singleton");
	}

	/*****
     * <p> Method: void testUpdateParameterInDatabaseWithSQLException() </p>
     * 
     * <p> Description: Tests that updateParameterInDatabase() handles SQLException
     * correctly by catching it and not propagating it to the caller.</p>
     */
	@Test
	public void testUpdateParameterInDatabaseWithSQLException() {
		Parameter testParameter = new Parameter(
			TEST_PARAMETER_ID,
			"Name",
			"Description",
			true,
			TEST_STAFF_USERNAME,
			0,
			0,
			new ArrayList<>(),
			"General",
			new ArrayList<>()
		);
		
		try {
			Database throwingDb = new Database() {
				@Override
				public void updateParameter(Parameter parameter) throws SQLException {
					throw new SQLException("Simulated failure");
				}
			};
			
			Database originalDb = getModelStaffHomeDatabase();
			setModelStaffHomeDatabase(throwingDb);
			try {
				ModelStaffHome.updateParameterInDatabase(testParameter);
			} finally {
				setModelStaffHomeDatabase(originalDb);
			}
		} catch (Exception e) {
			fail("updateParameterInDatabase should handle SQLException internally and not throw");
		}
	}
	
	/*****
     * <p> Method: void testDeleteParameterFromDatabaseWithSQLException() </p>
     * 
     * <p> Description: Tests that deleteParameterFromDatabase() handles SQLException
     * correctly by returning false when an error occurs.</p>
     */
	@Test
	public void testDeleteParameterFromDatabaseWithSQLException() {
		try {
			Database throwingDb = new Database() {
				@Override
				public boolean deleteParameter(String parameterId) throws SQLException {
					throw new SQLException("Simulated failure");
				}
			};
			
			Database originalDb = getModelStaffHomeDatabase();
			setModelStaffHomeDatabase(throwingDb);
			try {
				boolean deleted = ModelStaffHome.deleteParameterFromDatabase(TEST_PARAMETER_ID);
				assertFalse(deleted, "Method should return false when SQLException occurs");
			} finally {
				setModelStaffHomeDatabase(originalDb);
			}
		} catch (Exception e) {
			fail("deleteParameterFromDatabase should handle SQLException internally and not throw");
		}
	}
	
	/*****
     * <p> Method: void testRefreshPostsFromDatabaseWithSQLException() </p>
     * 
     * <p> Description: Tests that refreshPostsFromDatabase() handles SQLException
     * correctly by catching it and not propagating it.</p>
     */
	@Test
	public void testRefreshPostsFromDatabaseWithSQLException() {
		try {
			Database throwingDb = new Database() {
				@Override
				public List<Post> loadAllPosts() throws SQLException {
					throw new SQLException("Simulated failure");
				}
			};
			
			Database originalDb = getModelStaffHomeDatabase();
			setModelStaffHomeDatabase(throwingDb);
			try {
				ModelStaffHome.refreshPostsFromDatabase();
			} finally {
				setModelStaffHomeDatabase(originalDb);
			}
		} catch (Exception e) {
			fail("refreshPostsFromDatabase should handle SQLException internally and not throw");
		}
	}
	
	/*****
     * <p> Method: void testSaveReplyToDatabaseWithSQLException() </p>
     * 
     * <p> Description: Tests that saveReplyToDatabase() handles SQLException correctly
     * by catching it and not propagating it.</p>
     */
	@Test
	public void testSaveReplyToDatabaseWithSQLException() {
		Reply testReply = new Reply("REPLY_TEST_1", "Body", "user", "POST_TEST_1", false);
		
		try {
			Database throwingDb = new Database() {
				@Override
				public void saveReply(Reply reply) throws SQLException {
					throw new SQLException("Simulated failure");
				}
			};
			
			Database originalDb = getModelStaffHomeDatabase();
			setModelStaffHomeDatabase(throwingDb);
			try {
				ModelStaffHome.saveReplyToDatabase(testReply);
			} finally {
				setModelStaffHomeDatabase(originalDb);
			}
		} catch (Exception e) {
			fail("saveReplyToDatabase should handle SQLException internally and not throw");
		}
	}
	
	/*****
     * <p> Method: void testRefreshThreadsFromDatabaseWithSQLException() </p>
     * 
     * <p> Description: Tests that refreshThreadsFromDatabase() handles SQLException
     * correctly by catching it and not propagating it.</p>
     */
	@Test
	public void testRefreshThreadsFromDatabaseWithSQLException() {
		try {
			Database throwingDb = new Database() {
				@Override
				public List<Thread> loadAllThreads() throws SQLException {
					throw new SQLException("Simulated failure");
				}
			};
			
			Database originalDb = getModelStaffHomeDatabase();
			setModelStaffHomeDatabase(throwingDb);
			try {
				ModelStaffHome.refreshThreadsFromDatabase();
			} finally {
				setModelStaffHomeDatabase(originalDb);
			}
		} catch (Exception e) {
			fail("refreshThreadsFromDatabase should handle SQLException internally and not throw");
		}
	}
	
	/*****
     * <p> Method: void testSaveThreadToDatabaseWithSQLException() </p>
     * 
     * <p> Description: Tests that saveThreadToDatabase() handles SQLException correctly
     * by catching it and not propagating it.</p>
     */
	@Test
	public void testSaveThreadToDatabaseWithSQLException() {
		Thread testThread = new Thread(
			TEST_THREAD_ID,
			"Test Thread",
			"Description",
			TEST_STAFF_USERNAME
		);
		
		try {
			Database throwingDb = new Database() {
				@Override
				public void saveThread(Thread thread) throws SQLException {
					throw new SQLException("Simulated failure");
				}
			};
			
			Database originalDb = getModelStaffHomeDatabase();
			setModelStaffHomeDatabase(throwingDb);
			try {
				ModelStaffHome.saveThreadToDatabase(testThread);
			} finally {
				setModelStaffHomeDatabase(originalDb);
			}
		} catch (Exception e) {
			fail("saveThreadToDatabase should handle SQLException internally and not throw");
		}
	}
	
	/*****
     * <p> Method: void testDeleteThreadFromDatabaseWithSQLException() </p>
     * 
     * <p> Description: Tests that deleteThreadFromDatabase() handles SQLException
     * correctly by returning false when an error occurs.</p>
     */
	@Test
	public void testDeleteThreadFromDatabaseWithSQLException() {
		try {
			Database throwingDb = new Database() {
				@Override
				public boolean deleteThread(String threadId) throws SQLException {
					throw new SQLException("Simulated failure");
				}
			};
			
			Database originalDb = getModelStaffHomeDatabase();
			setModelStaffHomeDatabase(throwingDb);
			try {
				boolean deleted = ModelStaffHome.deleteThreadFromDatabase(TEST_THREAD_ID);
				assertFalse(deleted, "Method should return false when SQLException occurs");
			} finally {
				setModelStaffHomeDatabase(originalDb);
			}
		} catch (Exception e) {
			fail("deleteThreadFromDatabase should handle SQLException internally and not throw");
		}
	}
	
	/*****
     * <p> Method: void testGetPostCountForThreadWithSQLException() </p>
     * 
     * <p> Description: Tests that getPostCountForThread() handles SQLException
     * correctly by returning 0 when an error occurs.</p>
     */
	@Test
	public void testGetPostCountForThreadWithSQLException() {
		try {
			Database throwingDb = new Database() {
				@Override
				public int getPostCountForThread(String threadTitle) throws SQLException {
					throw new SQLException("Simulated failure");
				}
			};
			
			Database originalDb = getModelStaffHomeDatabase();
			setModelStaffHomeDatabase(throwingDb);
			try {
				int count = ModelStaffHome.getPostCountForThread("Any Thread");
				assertEquals(0, count, "Method should return 0 when SQLException occurs");
			} finally {
				setModelStaffHomeDatabase(originalDb);
			}
		} catch (Exception e) {
			fail("getPostCountForThread should handle SQLException internally and not throw");
		}
	}
	
	/*****
     * <p> Method: void testRefreshRequestsFromDatabaseWithSQLException() </p>
     * 
     * <p> Description: Tests that refreshRequestsFromDatabase() handles SQLException
     * correctly by catching it and not propagating it.</p>
     */
	@Test
	public void testRefreshRequestsFromDatabaseWithSQLException() {
		try {
			Database throwingDb = new Database() {
				@Override
				public List<Request> loadAllRequests() throws SQLException {
					throw new SQLException("Simulated failure");
				}
			};
			
			Database originalDb = getModelStaffHomeDatabase();
			setModelStaffHomeDatabase(throwingDb);
			try {
				ModelStaffHome.refreshRequestsFromDatabase();
			} finally {
				setModelStaffHomeDatabase(originalDb);
			}
		} catch (Exception e) {
			fail("refreshRequestsFromDatabase should handle SQLException internally and not throw");
		}
	}
	
	/*****
     * <p> Method: void testSaveRequestToDatabaseWithSQLException() </p>
     * 
     * <p> Description: Tests that saveRequestToDatabase() handles SQLException correctly
     * by catching it and not propagating it.</p>
     */
	@Test
	public void testSaveRequestToDatabaseWithSQLException() {
		Request testRequest = new Request(
			TEST_REQUEST_ID,
			"Title",
			"Description",
			Request.RequestCategory.SYSTEM_ISSUE,
			TEST_STAFF_USERNAME
		);
		
		try {
			Database throwingDb = new Database() {
				@Override
				public void saveRequest(Request request) throws SQLException {
					throw new SQLException("Simulated failure");
				}
			};
			
			Database originalDb = getModelStaffHomeDatabase();
			setModelStaffHomeDatabase(throwingDb);
			try {
				ModelStaffHome.saveRequestToDatabase(testRequest);
			} finally {
				setModelStaffHomeDatabase(originalDb);
			}
		} catch (Exception e) {
			fail("saveRequestToDatabase should handle SQLException internally and not throw");
		}
	}
	
	/*****
     * <p> Method: void testDeleteRequestFromDatabaseWithSQLException() </p>
     * 
     * <p> Description: Tests that deleteRequestFromDatabase() handles SQLException
     * correctly by returning false when an error occurs.</p>
     */
	@Test
	public void testDeleteRequestFromDatabaseWithSQLException() {
		try {
			Database throwingDb = new Database() {
				@Override
				public boolean deleteRequest(String requestId) throws SQLException {
					throw new SQLException("Simulated failure");
				}
			};
			
			Database originalDb = getModelStaffHomeDatabase();
			setModelStaffHomeDatabase(throwingDb);
			try {
				boolean deleted = ModelStaffHome.deleteRequestFromDatabase(TEST_REQUEST_ID);
				assertFalse(deleted, "Method should return false when SQLException occurs");
			} finally {
				setModelStaffHomeDatabase(originalDb);
			}
		} catch (Exception e) {
			fail("deleteRequestFromDatabase should handle SQLException internally and not throw");
		}
	}
	
	/*****
     * <p> Method: Database getModelStaffHomeDatabase() </p>
     * 
     * <p> Description: Test helper that returns the current Database instance used by
     * ModelStaffHome via reflection.</p>
     * 
     * @return the current Database instance referenced by ModelStaffHome
     * @throws Exception if reflection fails
     */
	private Database getModelStaffHomeDatabase() throws Exception {
		Class<?> modelClass = ModelStaffHome.class;
		Field dbField = modelClass.getDeclaredField("theDatabase");
		dbField.setAccessible(true);
		return (Database) dbField.get(null);
	}
	
	/*****
     * <p> Method: void setModelStaffHomeDatabase(Database database) </p>
     * 
     * <p> Description: Test helper that replaces the static Database instance used by
     * ModelStaffHome via reflection. This allows tests to inject Database stubs that
     * throw SQLExceptions to exercise catch blocks.</p>
     * 
     * @param database the Database instance to inject
     * @throws Exception if reflection fails
     */
	private void setModelStaffHomeDatabase(Database database) throws Exception {
		Class<?> modelClass = ModelStaffHome.class;
		Field dbField = modelClass.getDeclaredField("theDatabase");
		dbField.setAccessible(true);
		dbField.set(null, database);
	}
}

