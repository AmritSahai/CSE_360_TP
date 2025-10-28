package testCases;

/**
 * <p> Title: Test Runner for Student Test Cases </p>
 * 
 * <p> Description: This class provides a main method to run all student test cases
 * and display the results. It serves as an entry point for executing the test suite
 * without requiring JavaFX or GUI components.</p>
 * 
 * @author [Your Name]
 * @version 1.0
 */
public class TestRunner {
	
	/*******
     * <p> Constructor: TestRunner() </p>
     *
     * <p> Description: Has no functionality.</p>
     */
	public TestRunner() {
		
	}
	
	/*******
	 * <p> Title: TestRunner</p>
	 * 
	 * <p> Description: This main method tests the code for the CRUD functions and other functions
	 * specified in the student user stories. It calls all of the methods in the studentTestCases class
	 * and prints all the information to the terminal.</p>
	 * 
	 * @param args String[]   The array of command lines parameters.
	 */
    public static void main(String[] args) {
        System.out.println("=== STUDENT POST TEST CASES ===");
        System.out.println("Starting test execution...\n");
        
        // Create test instance
        studentTestCases tests = new studentTestCases();
        
        try {
            // Run setup first
            System.out.println("1. Running setup()...");
            tests.setup();
            System.out.println("   Setup completed successfully.\n");
            
            // Run each test method
            System.out.println("2. Running testCreatePosts()...");
            tests.testCreatePosts();
            
            System.out.println("3. Running testSearchPosts()...");
            tests.testSearchPosts();
            
            System.out.println("4. Running testReplies()...");
            tests.testCreateReplies();
            
            System.out.println("5. Running testDeleteReplies()...");
            tests.testDeleteReplies();
            
            System.out.println("6. Running testDeletePosts()...");
            tests.testDeletePosts();
            
            System.out.println("=== ALL TESTS COMPLETED ===");
            System.out.println("Test execution finished. Check output above for results.");
            
        } catch (Exception e) {
            System.err.println("ERROR: Test execution failed!");
            System.err.println("Exception: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

