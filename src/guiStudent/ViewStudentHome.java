package guiStudent;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import database.Database;
import entityClasses.User;
import guiUserUpdate.ViewUserUpdate;


/*******
 * <p> Title: ViewStudentHome Class. </p>
 * 
 * <p> Description: The Student Home Page View. This class defines the JavaFX graphical
 * interface elements displayed to the student user. It communicates with the
 * ControllerStudentHome class to handle button clicks, menu actions, and other
 * user interactions.</p>
 * 
 * <p>Supports features such as displaying posts and replies, creating and searching posts,
 * updating account information, and logging out. This class provides the graphical layer
 * through which students interact with all Student user functionalities.</p>
 * 
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 * 
 * @author Joseph and Vrishik
 * 
 * @version 2.00		2025-10-27 TP2 interface and documentation updates
 *  
 */

public class ViewStudentHome {
	
	/*-*******************************************************************************************

	Attributes
	
	 */
	
	// These are the application values required by the user interface
	
	/** The standard width for the Student Home window, retrieved from the main application. */
	private static double width = applicationMain.FoundationsMain.WINDOW_WIDTH;
	
	/** The standard height for the Student Home window, retrieved from the main application. */
	private static double height = applicationMain.FoundationsMain.WINDOW_HEIGHT;


	// These are the widget attributes for the GUI. There are 3 areas for this GUI.
	
	// GUI Area 1: It informs the user about the purpose of this page, whose account is being used,
	// and a button to allow this user to update the account settings
	
	/** The title label displayed at the top of the Student Home page. */
	protected static Label label_PageTitle = new Label();
	
	
	/** The label displaying the current user's name and role. */
	protected static Label label_UserDetails = new Label();
	
	/** Button allowing the user to open their account update page. */
	protected static Button button_UpdateThisUser = new Button("Account Update");
	
	
	/** Horizontal line separating the header section from the forum section. */
	protected static Line line_Separator1 = new Line(20, 95, width-20, 95);

	// GUI Area 2: Forum functionality widgets
	
	/** Label heading for the forum section. */
	protected static Label label_ForumSection = new Label("Forum");
		
	
	/** Button for creating a new discussion post. */
	protected static Button button_CreatePost = new Button("Create Post");
	
	
	/** Button for searching posts by keyword or topic. */
	protected static Button button_SearchPosts = new Button("Search Posts");
	
	
	/** Button for viewing posts created by the logged-in user. */
	protected static Button button_ViewMyPosts = new Button("My Posts");
	
	
	/** Button for viewing all posts in the discussion forum. */
	protected static Button button_ViewAllPosts = new Button("View All Posts");
	
	
	/** Horizontal line separating the forum section from the logout area. */
	protected static Line line_Separator4 = new Line(20, 525, width-20,525);
	
	// GUI Area 3: This is last of the GUI areas.  It is used for quitting the application and for
	// logging out.
	
	
	/** Button for logging out of the Student Home interface. */
	protected static Button button_Logout = new Button("Logout");
	
	
	/** Button for quitting the entire application. */
	protected static Button button_Quit = new Button("Quit");

	// This is the end of the GUI objects for the page.
	
	// These attributes are used to configure the page and populate it with this user's information
	
	
	/** Reference to the singleton instance of this View class. */
	private static ViewStudentHome theView;		
												

	/** Reference to the shared database instance from the main application. */
	private static Database theDatabase = applicationMain.FoundationsMain.database;

	
	/** Reference to the main Stage object used by JavaFX to render this GUI window. */
	protected static Stage theStage;		
	
	
	/** The root Pane that contains all GUI components for this page. */
	protected static Pane theRootPane;
	
	
	/** The User object representing the currently logged-in student. */
	protected static User theUser;
	
	/** Shared Scene for displaying the Student Home GUI. */
	private static Scene theViewStudentHomeScene;	
	
	/** The integer role identifier for Student users. */
	protected static final int theRole = 2;	

	/*-*******************************************************************************************

	Constructors
	
	 */


	/**********
	 * <p> Method: displayStudentHome(Stage ps, User user) </p>
	 * 
	 * <p> Description: This method is the single entry point from outside this package to cause
	 * the Student Home page to be displayed.
	 * 
	 * It first sets up every shared attributes so we don't have to pass parameters.
	 * 
	 * It then checks to see if the page has been setup.  If not, it instantiates the class, 
	 * initializes all the static aspects of the GIUI widgets (e.g., location on the page, font,
	 * size, and any methods to be performed).
	 * 
	 * After the instantiation, the code then populates the elements that change based on the user
	 * and the system's current state.  It then sets the Scene onto the stage, and makes it visible
	 * to the user.
	 * 
	 * @param ps specifies the JavaFX Stage to be used for this GUI and it's methods
	 * 
	 * @param user specifies the User for this GUI and it's methods
	 * 
	 */
	public static void displayStudentHome(Stage ps, User user) {
		
		// Establish the references to the GUI and the current user
		theStage = ps;
		theUser = user;
		
		// If not yet established, populate the static aspects of the GUI
		if (theView == null) theView = new ViewStudentHome();		// Instantiate singleton if needed
		
		// Populate the dynamic aspects of the GUI with the data from the user and the current
		// state of the system.
		theDatabase.getUserAccountDetails(user.getUserName());
		applicationMain.FoundationsMain.activeHomePage = theRole;
		
		label_UserDetails.setText("User: " + theUser.getUserName());
				
		// Set the title for the window, display the page, and wait for the Admin to do something
		theStage.setTitle("CSE 360 Foundations: Student Home Page");
		theStage.setScene(theViewStudentHomeScene);
		theStage.show();
	}
	
	/**********
	 * <p> Constructor: ViewStudentHome() </p>
	 * 
	 * <p> Description: This method initializes all the elements of the graphical user interface.
	 * It determines the location, size, font, color, and event handlers for each GUI object.</p>
	 * 
	 * <p>This is a singleton and is only performed once. Subsequent uses fill in the changeable
	 * fields using the displayStaffHome method.</p>
	 */
	private ViewStudentHome() {

		// Create the Pane for the list of widgets and the Scene for the window
		theRootPane = new Pane();
		theViewStudentHomeScene = new Scene(theRootPane, width, height);	// Create the scene
		
		// Set the title for the window
		
		// Populate the window with the title and other common widgets and set their static state
		
		// GUI Area 1
		label_PageTitle.setText("Student Home Page");
		setupLabelUI(label_PageTitle, "Arial", 28, width, Pos.CENTER, 0, 5);

		label_UserDetails.setText("User: " + theUser.getUserName());
		setupLabelUI(label_UserDetails, "Arial", 20, width, Pos.BASELINE_LEFT, 20, 55);
		
		setupButtonUI(button_UpdateThisUser, "Dialog", 18, 170, Pos.CENTER, 610, 45);
		button_UpdateThisUser.setOnAction((event) ->
			{ViewUserUpdate.displayUserUpdate(theStage, theUser); });
		
		// GUI Area 2 - Forum functionality
		setupLabelUI(label_ForumSection, "Arial", 22, 200, Pos.BASELINE_LEFT, 20, 110);
				
		setupButtonUI(button_CreatePost, "Dialog", 16, 200, Pos.CENTER, 20, 150);
		button_CreatePost.setOnAction((event) -> {ControllerStudentHome.createPost(); });
				
		setupButtonUI(button_SearchPosts, "Dialog", 16, 200, Pos.CENTER, 20, 200);
		button_SearchPosts.setOnAction((event) -> {ControllerStudentHome.searchPosts(); });
				
		setupButtonUI(button_ViewMyPosts, "Dialog", 16, 200, Pos.CENTER, 20, 250);
		button_ViewMyPosts.setOnAction((event) -> {ControllerStudentHome.viewMyPosts(); });
				
		setupButtonUI(button_ViewAllPosts, "Dialog", 16, 200, Pos.CENTER, 20, 300);
		button_ViewAllPosts.setOnAction((event) -> {ControllerStudentHome.viewAllPosts(); });
			
		// GUI Area 3
        setupButtonUI(button_Logout, "Dialog", 18, 250, Pos.CENTER, 20, 540);
        button_Logout.setOnAction((event) -> {ControllerStudentHome.performLogout(); });
        
        setupButtonUI(button_Quit, "Dialog", 18, 250, Pos.CENTER, 300, 540);
        button_Quit.setOnAction((event) -> {ControllerStudentHome.performQuit(); });

		// This is the end of the GUI initialization code
		
		// Place all of the widget items into the Root Pane's list of children
         theRootPane.getChildren().addAll(
			label_PageTitle, label_UserDetails, button_UpdateThisUser, line_Separator1,
			label_ForumSection, button_CreatePost, button_SearchPosts, 
			button_ViewMyPosts, button_ViewAllPosts,
	        line_Separator4, button_Logout, button_Quit);
}
	
	
	/*-********************************************************************************************

	Helper methods to reduce code length

	 */
	
	/*******
     * <p> Method: setupLabelUI(Label l, String ff, double f, double w, Pos p, double x, double y) </p>
     *
     * <p> Description: Helper method that initializes the standard formatting properties
     * for a JavaFX Label. It sets the font family, size, alignment, width, and layout
     * position to ensure consistent visual appearance across the Student interface. </p>
     *
     * @param l The Label object to be initialized.
     * @param ff The font family name (e.g., "Arial" or "Dialog").
     * @param f The font size to be applied.
     * @param w The minimum width of the label.
     * @param p The alignment for text positioning within the label.
     * @param x The x-coordinate of the label’s placement.
     * @param y The y-coordinate of the label’s placement.
     *
     */
	private static void setupLabelUI(Label l, String ff, double f, double w, Pos p, double x, 
			double y){
		l.setFont(Font.font(ff, f));
		l.setMinWidth(w);
		l.setAlignment(p);
		l.setLayoutX(x);
		l.setLayoutY(y);		
	}
	
	
	/*******
     * <p> Method: setupButtonUI(Button b, String ff, double f, double w, Pos p, double x, double y) </p>
     *
     * <p> Description: Helper method that standardizes the appearance and layout of all
     * JavaFX Button elements in the Student Home GUI. It applies consistent fonts,
     * widths, alignment, and spatial positioning across the application. </p>
     *
     * @param b The Button object to be initialized.
     * @param ff The font family name to use for button text.
     * @param f The font size for button text.
     * @param w The minimum width of the button.
     * @param p The text alignment setting within the button.
     * @param x The horizontal (x-axis) coordinate of the button.
     * @param y The vertical (y-axis) coordinate of the button.
     *
     */
	private static void setupButtonUI(Button b, String ff, double f, double w, Pos p, double x, 
			double y){
		b.setFont(Font.font(ff, f));
		b.setMinWidth(w);
		b.setAlignment(p);
		b.setLayoutX(x);
		b.setLayoutY(y);		
	}
}
