package guiStaff;

import java.util.List;
import java.util.ArrayList;
import java.util.Optional;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.Insets;
import javafx.scene.layout.Priority;
import entityClasses.Parameter;
import entityClasses.ParameterCollection;
import entityClasses.ParameterCategory;
import entityClasses.Post;
import entityClasses.Reply;
import entityClasses.PostCollection;
import entityClasses.ReplyCollection;
import entityClasses.Thread;
import entityClasses.ThreadCollection;
import entityClasses.Request;
import entityClasses.RequestCollection;

/*******
 * <p> Title: ControllerStaffHome Class </p>
 * 
 * <p> Description: The Staff Home Page Controller. This class handles all user interactions
 * and business logic for the staff home page interface. It manages actions such as user
 * updates, logout, quit, and grading parameter management (creation and viewing). The controller
 * coordinates between the View (UI components) and Model (data management) layers according to
 * the MVC pattern.</p>
 * 
 * <p> Copyright: Joseph © 2025 </p>
 * 
 * @author Joseph
 * 
 * @version 1.00		2025-01-16 Initial version
 */
public class ControllerStaffHome {
	
	/*****
     * <p> Default Constructor </p>
     */
    public ControllerStaffHome() {
        // existing initialization or leave empty
    }
	
	/*-*******************************************************************************************

	User Interface Actions for this page
	
	**********************************************************************************************/
	
	/*****
     * <p> Method: void performUpdate() </p>
     * 
     * <p> Description: Handles the user update action. Displays the user update interface
     * allowing the current staff user to modify their account information.</p>
     */
	protected static void performUpdate () {
		guiUserUpdate.ViewUserUpdate.displayUserUpdate(ViewStaffHome.theStage, ViewStaffHome.theUser);
	}	

	/*****
     * <p> Method: void performLogout() </p>
     * 
     * <p> Description: Handles the logout action. Returns the user to the login screen,
     * effectively ending the current staff session.</p>
     */
	protected static void performLogout() {
		guiUserLogin.ViewUserLogin.displayUserLogin(ViewStaffHome.theStage);
	}
	
	/*****
     * <p> Method: void performQuit() </p>
     * 
     * <p> Description: Handles the quit action. Terminates the application entirely.</p>
     */
	protected static void performQuit() {
		System.exit(0);
	}

	
	/**********
	 * <p> Method: void createParameter() </p>
	 * 
	 * <p> Description: Handles the creation of a new grading parameter. Displays a dialog
	 * interface allowing staff to input parameter details including name, description, minimum
	 * value (0-100), maximum value (0-100), weight (0.0-1.0), and active status. Validates all
	 * user inputs, displays appropriate error messages for invalid data, and saves successful
	 * parameter creations to the database. Upon successful creation, shows a confirmation message
	 * and refreshes the parameter list view.</p>
	 * 
	 * <p> The method handles NumberFormatException for invalid numeric inputs and displays
	 * validation errors returned from the Parameter class validation logic.</p>
	 */
	protected static void createParameter() {
		ParameterCollection parameters = ModelStaffHome.getParameterCollection();
		String currentUsername = ViewStaffHome.theUser.getUserName();
		
		// Create dialog for parameter creation
		Dialog<ButtonType> dialog = new Dialog<>();
		dialog.setTitle("Create Grading Parameter");
		dialog.setHeaderText("Create a new grading parameter");
		
		// Set up dialog content
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(20, 150, 10, 10));
		
		TextField nameField = new TextField();
		nameField.setPromptText("Parameter name (max 100 characters)");
		nameField.setPrefWidth(400);
		
		TextArea descriptionArea = new TextArea();
		descriptionArea.setPromptText("Description (max 500 characters)");
		descriptionArea.setPrefRowCount(5);
		descriptionArea.setPrefWidth(400);
		descriptionArea.setWrapText(true);
		
		CheckBox activeCheckBox = new CheckBox("Active");
		activeCheckBox.setSelected(true);
		
		// New fields for User Story #1
		TextField requiredPostsField = new TextField("0");
		requiredPostsField.setPromptText("Required posts (0 or more)");
		requiredPostsField.setPrefWidth(200);
		
		TextField requiredRepliesField = new TextField("0");
		requiredRepliesField.setPromptText("Required replies (0 or more)");
		requiredRepliesField.setPrefWidth(200);
		
		TextArea topicsArea = new TextArea();
		topicsArea.setPromptText("Enter topics (one per line, max 20 topics)");
		topicsArea.setPrefRowCount(4);
		topicsArea.setPrefWidth(400);
		topicsArea.setWrapText(true);
		
		ComboBox<String> threadComboBox = new ComboBox<>();
		threadComboBox.setPromptText("Select thread *");
		threadComboBox.setPrefWidth(400);
		// TODO: Populate with available threads from PostCollection
		threadComboBox.getItems().add("General"); // Placeholder
		
		// Categories section
		VBox categoriesBox = new VBox(10);
		categoriesBox.setPrefWidth(450);
		ScrollPane categoriesScroll = new ScrollPane(categoriesBox);
		categoriesScroll.setPrefHeight(200);
		categoriesScroll.setFitToWidth(true);
		categoriesScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
		categoriesScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
		
		List<HBox> categoryRows = new ArrayList<>();
		
		Button addCategoryBtn = new Button("Add Category");
		addCategoryBtn.setOnAction(e -> {
			HBox categoryRow = new HBox(10);
			categoryRow.setPrefWidth(Region.USE_COMPUTED_SIZE);
			TextField catNameField = new TextField();
			catNameField.setPromptText("Category name");
			catNameField.setPrefWidth(200);
			catNameField.setMinWidth(Region.USE_PREF_SIZE);
			TextField catWeightField = new TextField();
			catWeightField.setPromptText("Weight (0.0-1.0)");
			catWeightField.setPrefWidth(120);
			catWeightField.setMinWidth(Region.USE_PREF_SIZE);
			Button removeBtn = new Button("Remove");
			removeBtn.setMinWidth(Region.USE_PREF_SIZE);
			removeBtn.setOnAction(ev -> categoriesBox.getChildren().remove(categoryRow));
			categoryRow.getChildren().addAll(catNameField, catWeightField, removeBtn);
			categoriesBox.getChildren().add(categoryRow);
			categoryRows.add(categoryRow);
		});
		
		// Create labels with proper width to prevent truncation
		Label nameLabel = new Label("Name:*");
		nameLabel.setMinWidth(Region.USE_PREF_SIZE);
		Label descLabel = new Label("Description:*");
		descLabel.setMinWidth(Region.USE_PREF_SIZE);
		Label weightLabel = new Label("Weight (0.0-1.0):*");
		weightLabel.setMinWidth(Region.USE_PREF_SIZE);
		Label statusLabel = new Label("Status:");
		statusLabel.setMinWidth(Region.USE_PREF_SIZE);
		Label reqPostsLabel = new Label("Required Posts:");
		reqPostsLabel.setMinWidth(Region.USE_PREF_SIZE);
		Label reqRepliesLabel = new Label("Required Replies:");
		reqRepliesLabel.setMinWidth(Region.USE_PREF_SIZE);
		Label topicsLabel = new Label("Topics (one per line):");
		topicsLabel.setMinWidth(Region.USE_PREF_SIZE);
		Label threadLabel = new Label("Thread:*");
		threadLabel.setMinWidth(Region.USE_PREF_SIZE);
		Label categoriesLabel = new Label("Categories:*");
		categoriesLabel.setMinWidth(Region.USE_PREF_SIZE);
		
		grid.add(nameLabel, 0, 0);
		grid.add(nameField, 1, 0);
		grid.add(descLabel, 0, 1);
		grid.add(descriptionArea, 1, 1);
		grid.add(statusLabel, 0, 2);
		grid.add(activeCheckBox, 1, 3);
		grid.add(reqPostsLabel, 0, 4);
		grid.add(requiredPostsField, 1, 4);
		grid.add(reqRepliesLabel, 0, 5);
		grid.add(requiredRepliesField, 1, 5);
		grid.add(topicsLabel, 0, 6);
		grid.add(topicsArea, 1, 6);
		grid.add(threadLabel, 0, 7);
		grid.add(threadComboBox, 1, 7);
		grid.add(categoriesLabel, 0, 8);
		grid.add(addCategoryBtn, 1, 8);
		grid.add(new Label(""), 0, 9);
		grid.add(categoriesScroll, 1, 9);
		
		// Wrap grid in ScrollPane to fix scrolling issue
		ScrollPane scrollPane = new ScrollPane(grid);
		scrollPane.setFitToWidth(true);
		scrollPane.setPrefViewportHeight(600);
		scrollPane.setPrefViewportWidth(700);
		
		dialog.getDialogPane().setContent(scrollPane);
		dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
		
		Optional<ButtonType> result = dialog.showAndWait();
		if (result.isPresent() && result.get() == ButtonType.OK) {
			String name = nameField.getText();
			String description = descriptionArea.getText();
			boolean isActive = activeCheckBox.isSelected();
			
			// Parse new fields
			String requiredPostsStr = requiredPostsField.getText();
			String requiredRepliesStr = requiredRepliesField.getText();
			String topicsText = topicsArea.getText();
			String selectedThread = threadComboBox.getValue();
			
			// Validate thread selection
			if (selectedThread == null || selectedThread.trim().isEmpty()) {
				Alert alert = new Alert(Alert.AlertType.ERROR);
				alert.setTitle("Create Parameter");
				alert.setHeaderText("Validation Error");
				alert.setContentText("Thread selection is required.");
				alert.showAndWait();
				return;
			}
			
			// Parse and validate numeric values
			try {
				int requiredPosts = requiredPostsStr.isEmpty() ? 0 : Integer.parseInt(requiredPostsStr);
				int requiredReplies = requiredRepliesStr.isEmpty() ? 0 : Integer.parseInt(requiredRepliesStr);
				
				// Parse topics (one per line)
				List<String> topics = new ArrayList<>();
				if (topicsText != null && !topicsText.trim().isEmpty()) {
					String[] lines = topicsText.split("\n");
					for (String line : lines) {
						String topic = line.trim();
						if (!topic.isEmpty()) {
							topics.add(topic);
						}
					}
				}
				
				// Collect categories
				List<ParameterCategory> categories = new ArrayList<>();
				for (HBox row : categoryRows) {
					TextField catNameField = (TextField) row.getChildren().get(0);
					TextField catWeightField = (TextField) row.getChildren().get(1);
					String catName = catNameField.getText().trim();
					String catWeightStr = catWeightField.getText().trim();
					
					if (!catName.isEmpty() && !catWeightStr.isEmpty()) {
						try {
							double catWeight = Double.parseDouble(catWeightStr);
							categories.add(new ParameterCategory(catName, catWeight));
						} catch (NumberFormatException e) {
							Alert alert = new Alert(Alert.AlertType.ERROR);
							alert.setTitle("Create Parameter");
							alert.setHeaderText("Invalid Input");
							alert.setContentText("Category weights must be valid numbers between 0.0 and 1.0.");
							alert.showAndWait();
							return;
						}
					}
				}
				
				// Create parameter - validation happens in Parameter class
				String parameterIdOrError = parameters.createParameter(
					name, description, isActive, currentUsername,
					requiredPosts, requiredReplies, topics, selectedThread.trim(), categories
				);
				
				Alert alert = new Alert(Alert.AlertType.INFORMATION);
				alert.setTitle("Create Parameter");
				if (parameterIdOrError.startsWith("PARAM_")) {
					// Save to database
					Parameter newParameter = parameters.getParameterById(parameterIdOrError);
					ModelStaffHome.saveParameterToDatabase(newParameter);
					
					alert.setHeaderText("Parameter Created");
					alert.setContentText("The grading parameter has been created successfully.");
					alert.showAndWait();
					
					// Refresh the parameter list view
					viewMyParameters();
				} else {
					alert.setAlertType(Alert.AlertType.ERROR);
					alert.setHeaderText("Validation Error");
					alert.setContentText(parameterIdOrError);
					alert.showAndWait();
				}
			} catch (NumberFormatException e) {
				Alert alert = new Alert(Alert.AlertType.ERROR);
				alert.setTitle("Create Parameter");
				alert.setHeaderText("Invalid Input");
				alert.setContentText("Required posts and required replies must be valid numbers.");
				alert.showAndWait();
			}
		}
	}
	
	
	/**********
	 * <p> Method: void viewMyParameters() </p>
	 * 
	 * <p> Description: Retrieves and displays all grading parameters created by the currently
	 * logged-in staff member. Calls the displayParameterList() helper method to render the
	 * parameters in a scrollable dialog interface. If no parameters exist for the staff member,
	 * displays an informational message instead.</p>
	 */
	protected static void viewMyParameters() {
		ParameterCollection parameters = ModelStaffHome.getParameterCollection();
		String currentUsername = ViewStaffHome.theUser.getUserName();
		
		List<Parameter> myParameters = parameters.getParametersByStaff(currentUsername);
		displayParameterList(myParameters, "My Grading Parameters");
	}
	
	
	/**********
	 * <p> Method: void displayParameterList(List&lt;Parameter&gt; parameterList, String title) </p>
	 * 
	 * <p> Description: Helper method that displays a list of parameters in a formatted,
	 * scrollable dialog interface. Each parameter is displayed as a card showing its name,
	 * active status, description, value range, weight, and creation timestamp. Each parameter
	 * card includes Update and Delete buttons for management. If the parameter list is empty,
	 * displays an informational message instead. The dialog includes a header showing the total
	 * number of parameters found.</p>
	 * 
	 * @param parameterList the list of Parameter objects to display
	 * @param title the title for the dialog window
	 */
	private static void displayParameterList(List<Parameter> parameterList, String title) {
		if (parameterList.isEmpty()) {
			Alert alert = new Alert(Alert.AlertType.INFORMATION);
			alert.setTitle(title);
			alert.setHeaderText("No Parameters Found");
			alert.setContentText("You have not created any grading parameters yet.");
			alert.showAndWait();
			return;
		}
		
		// Create a scrollable pane with individual parameter cards
		ScrollPane scrollPane = new ScrollPane();
		VBox parameterContainer = new VBox(10);
		parameterContainer.setPadding(new Insets(10));
		
		for (Parameter parameter : parameterList) {
			// Create a card for each parameter
			VBox parameterCard = new VBox(5);
			parameterCard.setStyle("-fx-border-color: #cccccc; -fx-border-width: 1; -fx-padding: 10; -fx-background-color: #f9f9f9;");
			
			// Parameter header with name and status
			HBox headerBox = new HBox(10);
			Label nameLabel = new Label(parameter.getName());
			nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");
			
			Label statusLabel = new Label(parameter.isActive() ? "[ACTIVE]" : "[INACTIVE]");
			statusLabel.setStyle(parameter.isActive() ? 
				"-fx-font-size: 12; -fx-text-fill: #4CAF50; -fx-font-weight: bold;" : 
				"-fx-font-size: 12; -fx-text-fill: #999999;");
			
			headerBox.getChildren().addAll(nameLabel, statusLabel);
			
			// Parameter description
			Label descriptionLabel = new Label(parameter.getDescription());
			descriptionLabel.setWrapText(true);
			descriptionLabel.setStyle("-fx-font-size: 12;");
			
			// Parameter details
			HBox detailsBox = new HBox(20);
			
			Label dateLabel = new Label("Created: " + parameter.getFormattedCreatedAt());
			dateLabel.setStyle("-fx-font-size: 11; -fx-text-fill: #999999;");
			
			detailsBox.getChildren().addAll(dateLabel);
			
			// Action buttons for update and delete
			HBox actionBox = new HBox(10);
			Button updateButton = new Button("Update");
			updateButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
			updateButton.setOnAction(e -> {
				updateParameter(parameter);
				// Refresh the list after update
				viewMyParameters();
			});
			
			Button deleteButton = new Button("Delete");
			deleteButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
			deleteButton.setOnAction(e -> {
				deleteParameter(parameter.getParameterId());
				// Refresh the list after delete
				viewMyParameters();
			});
			
			actionBox.getChildren().addAll(updateButton, deleteButton);
			
			// Add all elements to the parameter card
			parameterCard.getChildren().addAll(headerBox, descriptionLabel, detailsBox, actionBox);
			parameterContainer.getChildren().add(parameterCard);
		}
		
		scrollPane.setContent(parameterContainer);
		scrollPane.setPrefSize(700, 500);
		scrollPane.setFitToWidth(true);
		
		Dialog<Void> dialog = new Dialog<>();
		dialog.setTitle(title);
		dialog.setHeaderText(parameterList.size() + " parameter(s) found");
		dialog.getDialogPane().setContent(scrollPane);
		
		ButtonType closeBtn = new ButtonType("Close", ButtonBar.ButtonData.CANCEL_CLOSE);
		dialog.getDialogPane().getButtonTypes().add(closeBtn);
		dialog.showAndWait();
	}
	
	/**********
	 * <p> Method: void updateParameter(Parameter parameter) </p>
	 * 
	 * <p> Description: Handles the update of an existing grading parameter. Displays a dialog
	 * interface pre-filled with the current parameter values, allowing staff to modify all
	 * parameter details. Uses the same validation as creation. Validates all user inputs,
	 * displays appropriate error messages for invalid data, and saves successful parameter
	 * updates to the database. Upon successful update, shows a confirmation message.</p>
	 * 
	 * @param parameter the Parameter object to update
	 */
	protected static void updateParameter(Parameter parameter) {
		ParameterCollection parameters = ModelStaffHome.getParameterCollection();
		
		// Create dialog for parameter update (same structure as createParameter)
		Dialog<ButtonType> dialog = new Dialog<>();
		dialog.setTitle("Update Grading Parameter");
		dialog.setHeaderText("Update grading parameter: " + parameter.getName());
		
		// Set up dialog content
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(20, 20, 10, 20));
		
		// Set column constraints to ensure labels have enough space
		ColumnConstraints labelColumn = new ColumnConstraints();
		labelColumn.setMinWidth(180);
		labelColumn.setPrefWidth(180);
		labelColumn.setHgrow(Priority.NEVER);
		
		ColumnConstraints inputColumn = new ColumnConstraints();
		inputColumn.setHgrow(Priority.ALWAYS);
		
		grid.getColumnConstraints().addAll(labelColumn, inputColumn);
		
		TextField nameField = new TextField(parameter.getName());
		nameField.setPromptText("Parameter name (max 100 characters)");
		nameField.setPrefWidth(400);
		
		TextArea descriptionArea = new TextArea(parameter.getDescription());
		descriptionArea.setPromptText("Description (max 500 characters)");
		descriptionArea.setPrefRowCount(5);
		descriptionArea.setPrefWidth(400);
		descriptionArea.setWrapText(true);
		
		CheckBox activeCheckBox = new CheckBox("Active");
		activeCheckBox.setSelected(parameter.isActive());
		
		TextField requiredPostsField = new TextField(String.valueOf(parameter.getRequiredPosts()));
		requiredPostsField.setPromptText("Required posts (0 or more)");
		requiredPostsField.setPrefWidth(200);
		
		TextField requiredRepliesField = new TextField(String.valueOf(parameter.getRequiredReplies()));
		requiredRepliesField.setPromptText("Required replies (0 or more)");
		requiredRepliesField.setPrefWidth(200);
		
		TextArea topicsArea = new TextArea(String.join("\n", parameter.getTopics()));
		topicsArea.setPromptText("Enter topics (one per line, max 20 topics)");
		topicsArea.setPrefRowCount(4);
		topicsArea.setPrefWidth(400);
		topicsArea.setWrapText(true);
		
		ComboBox<String> threadComboBox = new ComboBox<>();
		threadComboBox.setPromptText("Select thread *");
		threadComboBox.setPrefWidth(400);
		// TODO: Populate with available threads
		threadComboBox.getItems().add("General"); // Placeholder
		if (parameter.getThreadId() != null) {
			threadComboBox.setValue(parameter.getThreadId());
		}
		
		// Categories section
		VBox categoriesBox = new VBox(10);
		categoriesBox.setPrefWidth(450);
		ScrollPane categoriesScroll = new ScrollPane(categoriesBox);
		categoriesScroll.setPrefHeight(200);
		categoriesScroll.setFitToWidth(true);
		categoriesScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
		categoriesScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
		
		List<HBox> categoryRows = new ArrayList<>();
		
		// Pre-fill with existing categories
		for (ParameterCategory cat : parameter.getCategories()) {
			HBox categoryRow = new HBox(10);
			categoryRow.setPrefWidth(Region.USE_COMPUTED_SIZE);
			TextField catNameField = new TextField(cat.getCategoryName());
			catNameField.setPrefWidth(200);
			catNameField.setMinWidth(Region.USE_PREF_SIZE);
			TextField catWeightField = new TextField(String.valueOf(cat.getWeight()));
			catWeightField.setPrefWidth(120);
			catWeightField.setMinWidth(Region.USE_PREF_SIZE);
			Button removeBtn = new Button("Remove");
			removeBtn.setMinWidth(Region.USE_PREF_SIZE);
			removeBtn.setOnAction(ev -> categoriesBox.getChildren().remove(categoryRow));
			categoryRow.getChildren().addAll(catNameField, catWeightField, removeBtn);
			categoriesBox.getChildren().add(categoryRow);
			categoryRows.add(categoryRow);
		}
		
		Button addCategoryBtn = new Button("Add Category");
		addCategoryBtn.setOnAction(e -> {
			HBox categoryRow = new HBox(10);
			categoryRow.setPrefWidth(Region.USE_COMPUTED_SIZE);
			TextField catNameField = new TextField();
			catNameField.setPromptText("Category name");
			catNameField.setPrefWidth(200);
			catNameField.setMinWidth(Region.USE_PREF_SIZE);
			TextField catWeightField = new TextField();
			catWeightField.setPromptText("Weight (0.0-1.0)");
			catWeightField.setPrefWidth(120);
			catWeightField.setMinWidth(Region.USE_PREF_SIZE);
			Button removeBtn = new Button("Remove");
			removeBtn.setMinWidth(Region.USE_PREF_SIZE);
			removeBtn.setOnAction(ev -> categoriesBox.getChildren().remove(categoryRow));
			categoryRow.getChildren().addAll(catNameField, catWeightField, removeBtn);
			categoriesBox.getChildren().add(categoryRow);
			categoryRows.add(categoryRow);
		});
		
		// Create labels with proper width to prevent truncation
		Label nameLabel = new Label("Name:*");
		nameLabel.setMinWidth(Region.USE_PREF_SIZE);
		Label descLabel = new Label("Description:*");
		descLabel.setMinWidth(Region.USE_PREF_SIZE);
		Label weightLabel = new Label("Weight (0.0-1.0):*");
		weightLabel.setMinWidth(Region.USE_PREF_SIZE);
		Label statusLabel = new Label("Status:");
		statusLabel.setMinWidth(Region.USE_PREF_SIZE);
		Label reqPostsLabel = new Label("Required Posts:");
		reqPostsLabel.setMinWidth(Region.USE_PREF_SIZE);
		Label reqRepliesLabel = new Label("Required Replies:");
		reqRepliesLabel.setMinWidth(Region.USE_PREF_SIZE);
		Label topicsLabel = new Label("Topics (one per line):");
		topicsLabel.setMinWidth(Region.USE_PREF_SIZE);
		Label threadLabel = new Label("Thread:*");
		threadLabel.setMinWidth(Region.USE_PREF_SIZE);
		Label categoriesLabel = new Label("Categories:*");
		categoriesLabel.setMinWidth(Region.USE_PREF_SIZE);
		
		grid.add(nameLabel, 0, 0);
		grid.add(nameField, 1, 0);
		grid.add(descLabel, 0, 1);
		grid.add(descriptionArea, 1, 1);
		grid.add(statusLabel, 0, 2);
		grid.add(activeCheckBox, 1, 3);
		grid.add(reqPostsLabel, 0, 4);
		grid.add(requiredPostsField, 1, 4);
		grid.add(reqRepliesLabel, 0, 5);
		grid.add(requiredRepliesField, 1, 5);
		grid.add(topicsLabel, 0, 6);
		grid.add(topicsArea, 1, 6);
		grid.add(threadLabel, 0, 7);
		grid.add(threadComboBox, 1, 7);
		grid.add(categoriesLabel, 0, 8);
		grid.add(addCategoryBtn, 1, 8);
		grid.add(new Label(""), 0, 9);
		grid.add(categoriesScroll, 1, 9);
		
		// Wrap grid in ScrollPane to fix scrolling issue
		ScrollPane scrollPane = new ScrollPane(grid);
		scrollPane.setFitToWidth(true);
		scrollPane.setPrefViewportHeight(600);
		scrollPane.setPrefViewportWidth(700);
		
		dialog.getDialogPane().setContent(scrollPane);
		dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
		
		Optional<ButtonType> result = dialog.showAndWait();
		if (result.isPresent() && result.get() == ButtonType.OK) {
			String name = nameField.getText();
			String description = descriptionArea.getText();
			boolean isActive = activeCheckBox.isSelected();
			
			// Parse new fields
			String requiredPostsStr = requiredPostsField.getText();
			String requiredRepliesStr = requiredRepliesField.getText();
			String topicsText = topicsArea.getText();
			String selectedThread = threadComboBox.getValue();
			
			// Validate thread selection
			if (selectedThread == null || selectedThread.trim().isEmpty()) {
				Alert alert = new Alert(Alert.AlertType.ERROR);
				alert.setTitle("Update Parameter");
				alert.setHeaderText("Validation Error");
				alert.setContentText("Thread selection is required.");
				alert.showAndWait();
				return;
			}
			
			// Parse and validate numeric values
			try {
				int requiredPosts = requiredPostsStr.isEmpty() ? 0 : Integer.parseInt(requiredPostsStr);
				int requiredReplies = requiredRepliesStr.isEmpty() ? 0 : Integer.parseInt(requiredRepliesStr);
				
				// Parse topics (one per line)
				List<String> topics = new ArrayList<>();
				if (topicsText != null && !topicsText.trim().isEmpty()) {
					String[] lines = topicsText.split("\n");
					for (String line : lines) {
						String topic = line.trim();
						if (!topic.isEmpty()) {
							topics.add(topic);
						}
					}
				}
				
				// Collect categories
				List<ParameterCategory> categories = new ArrayList<>();
				for (HBox row : categoryRows) {
					TextField catNameField = (TextField) row.getChildren().get(0);
					TextField catWeightField = (TextField) row.getChildren().get(1);
					String catName = catNameField.getText().trim();
					String catWeightStr = catWeightField.getText().trim();
					
					if (!catName.isEmpty() && !catWeightStr.isEmpty()) {
						try {
							double catWeight = Double.parseDouble(catWeightStr);
							categories.add(new ParameterCategory(catName, catWeight));
						} catch (NumberFormatException e) {
							Alert alert = new Alert(Alert.AlertType.ERROR);
							alert.setTitle("Update Parameter");
							alert.setHeaderText("Invalid Input");
							alert.setContentText("Category weights must be valid numbers between 0.0 and 1.0.");
							alert.showAndWait();
							return;
						}
					}
				}
				
				// Update parameter - uses same validation as creation
				String errorMessage = parameters.updateParameter(
					parameter.getParameterId(), name, description, isActive,
					requiredPosts, requiredReplies, topics, selectedThread.trim(), categories
				);
				
				Alert alert = new Alert(Alert.AlertType.INFORMATION);
				alert.setTitle("Update Parameter");
				if (errorMessage.isEmpty()) {
					// Update in database
					Parameter updatedParameter = parameters.getParameterById(parameter.getParameterId());
					ModelStaffHome.updateParameterInDatabase(updatedParameter);
					
					alert.setHeaderText("Parameter Updated");
					alert.setContentText("The grading parameter has been updated successfully.");
					alert.showAndWait();
				} else {
					alert.setAlertType(Alert.AlertType.ERROR);
					alert.setHeaderText("Validation Error");
					alert.setContentText(errorMessage);
					alert.showAndWait();
				}
			} catch (NumberFormatException e) {
				Alert alert = new Alert(Alert.AlertType.ERROR);
				alert.setTitle("Update Parameter");
				alert.setHeaderText("Invalid Input");
				alert.setContentText("Required posts and required replies must be valid numbers.");
				alert.showAndWait();
			}
		}
	}
	
	/**********
	 * <p> Method: void deleteParameter(String parameterId) </p>
	 * 
	 * <p> Description: Handles the deletion of a grading parameter. Displays a confirmation
	 * dialog to ensure the user wants to delete the parameter. If confirmed, removes the parameter
	 * from both the collection and the database. Upon successful deletion, shows a confirmation
	 * message.</p>
	 * 
	 * @param parameterId the ID of the parameter to delete
	 */
	protected static void deleteParameter(String parameterId) {
		ParameterCollection parameters = ModelStaffHome.getParameterCollection();
		Parameter parameter = parameters.getParameterById(parameterId);
		
		if (parameter == null) {
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setTitle("Delete Parameter");
			alert.setHeaderText("Parameter Not Found");
			alert.setContentText("The parameter could not be found.");
			alert.showAndWait();
			return;
		}
		
		// Confirm deletion
		Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
		confirmAlert.setTitle("Delete Parameter");
		confirmAlert.setHeaderText("Confirm Deletion");
		confirmAlert.setContentText("Are you sure you want to delete the parameter \"" + 
		                           parameter.getName() + "\"? This action cannot be undone.");
		
		Optional<ButtonType> result = confirmAlert.showAndWait();
		if (result.isPresent() && result.get() == ButtonType.OK) {
			// Delete from collection
			boolean deleted = parameters.deleteParameter(parameterId);
			
			if (deleted) {
				// Delete from database
				boolean dbDeleted = ModelStaffHome.deleteParameterFromDatabase(parameterId);
				
				Alert alert = new Alert(Alert.AlertType.INFORMATION);
				alert.setTitle("Delete Parameter");
				if (dbDeleted) {
					alert.setHeaderText("Parameter Deleted");
					alert.setContentText("The grading parameter has been deleted successfully.");
				} else {
					alert.setAlertType(Alert.AlertType.WARNING);
					alert.setHeaderText("Partial Deletion");
					alert.setContentText("The parameter was removed from memory but may not have been deleted from the database.");
				}
				alert.showAndWait();
			} else {
				Alert alert = new Alert(Alert.AlertType.ERROR);
				alert.setTitle("Delete Parameter");
				alert.setHeaderText("Deletion Failed");
				alert.setContentText("The parameter could not be deleted.");
				alert.showAndWait();
			}
		}
	}
	
	/**********
	 * <p> Method: void deleteAllParameters() </p>
	 * 
	 * <p> Description: Handles deletion of parameters with selection interface. Staff can select
	 * which parameters to delete and must confirm before deletion occurs.</p>
	 */
	protected static void deleteAllParameters() {
		ParameterCollection parameters = ModelStaffHome.getParameterCollection();
		String currentUsername = ViewStaffHome.theUser.getUserName();
		
		List<Parameter> myParameters = parameters.getParametersByStaff(currentUsername);
		
		if (myParameters.isEmpty()) {
			Alert alert = new Alert(Alert.AlertType.INFORMATION);
			alert.setTitle("Delete Parameters");
			alert.setHeaderText("No Parameters Found");
			alert.setContentText("You have not created any parameters to delete.");
			alert.showAndWait();
			return;
		}
		
		// Create selection dialog
		Dialog<ButtonType> dialog = new Dialog<>();
		dialog.setTitle("Delete Parameters");
		dialog.setHeaderText("Select parameters to delete");
		
		VBox checkBoxContainer = new VBox(10);
		checkBoxContainer.setPadding(new Insets(10));
		List<CheckBox> checkBoxes = new ArrayList<>();
		
		for (Parameter param : myParameters) {
			CheckBox checkBox = new CheckBox(param.getName() + " - " + param.getDescription());
			checkBox.setSelected(false);
			checkBoxes.add(checkBox);
			checkBoxContainer.getChildren().add(checkBox);
		}
		
		ScrollPane scrollPane = new ScrollPane(checkBoxContainer);
		scrollPane.setPrefSize(600, 400);
		scrollPane.setFitToWidth(true);
		
		dialog.getDialogPane().setContent(scrollPane);
		dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
		
		Optional<ButtonType> result = dialog.showAndWait();
		if (result.isPresent() && result.get() == ButtonType.OK) {
			// Collect selected parameter IDs
			List<String> selectedIds = new ArrayList<>();
			for (int i = 0; i < checkBoxes.size(); i++) {
				if (checkBoxes.get(i).isSelected()) {
					selectedIds.add(myParameters.get(i).getParameterId());
				}
			}
			
			if (selectedIds.isEmpty()) {
				Alert alert = new Alert(Alert.AlertType.INFORMATION);
				alert.setTitle("Delete Parameters");
				alert.setHeaderText("No Selection");
				alert.setContentText("No parameters were selected for deletion.");
				alert.showAndWait();
				return;
			}
			
			// Confirmation dialog
			Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
			confirmAlert.setTitle("Confirm Deletion");
			confirmAlert.setHeaderText("Delete " + selectedIds.size() + " parameter(s)?");
			confirmAlert.setContentText("Are you sure you want to delete the selected parameters? This action cannot be undone.");
			
			Optional<ButtonType> confirmResult = confirmAlert.showAndWait();
			if (confirmResult.isPresent() && confirmResult.get() == ButtonType.OK) {
				// Delete selected parameters
				boolean deleted = parameters.deleteSelectedParameters(selectedIds);
				
				if (deleted) {
					// Delete from database
					for (String paramId : selectedIds) {
						ModelStaffHome.deleteParameterFromDatabase(paramId);
					}
					
					Alert alert = new Alert(Alert.AlertType.INFORMATION);
					alert.setTitle("Delete Parameters");
					alert.setHeaderText("Parameters Deleted");
					alert.setContentText(selectedIds.size() + " parameter(s) have been deleted successfully.");
					alert.showAndWait();
				} else {
					Alert alert = new Alert(Alert.AlertType.ERROR);
					alert.setTitle("Delete Parameters");
					alert.setHeaderText("Deletion Failed");
					alert.setContentText("Failed to delete the selected parameters.");
					alert.showAndWait();
				}
			}
		}
	}
	
	/**********
	 * <p> Method: void viewAllPosts() </p>
	 * 
	 * <p> Description: Shows all posts in the forum for staff to review and provide feedback.</p>
	 * 
	 */
	protected static void viewAllPosts() {
		// Refresh collections from database to ensure we have the latest data
		ModelStaffHome.refreshPostsFromDatabase();
		PostCollection posts = ModelStaffHome.getPostCollection();
		ReplyCollection replies = ModelStaffHome.getReplyCollection();
		
		List<Post> allPosts = posts.getAllPosts();
		displayPostListForStaff(allPosts, replies, "All Posts");
	}
	
	/**********
	 * <p> Method: void displayPostListForStaff() </p>
	 * 
	 * <p> Description: Helper method to display a list of posts for staff with a Feedback button.</p>
	 * 
	 * @param postList List of Posts that will be displayed
	 * @param replies the replies that will be displayed with the posts
	 * @param title the title of the alert or dialog
	 */
	private static void displayPostListForStaff(List<Post> postList, ReplyCollection replies, String title) {
		
		if (postList.isEmpty()) {
			Alert alert = new Alert(Alert.AlertType.INFORMATION);
			alert.setTitle(title);
			alert.setHeaderText("No Posts Found");
			alert.setContentText("There are no posts to display.");
			alert.showAndWait();
			return;
		}
		
		// Create a scrollable pane with individual post cards
		ScrollPane scrollPane = new ScrollPane();
		VBox postContainer = new VBox(10);
		postContainer.setPadding(new Insets(10));
		
		for (Post post : postList) {
			int replyCount = replies.getReplyCountForPost(post.getPostId());
			
			// Create a card for each post
			VBox postCard = new VBox(5);
			postCard.setStyle("-fx-border-color: #cccccc; -fx-border-width: 1; -fx-padding: 10; -fx-background-color: #f9f9f9;");
			
			// Post header with title and metadata
			HBox headerBox = new HBox(10);
			Label titleLabel = new Label(post.getTitle());
			titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");
			
			Label authorLabel = new Label("by " + post.getAuthorUsername());
			authorLabel.setStyle("-fx-font-size: 12; -fx-text-fill: #666666;");
			
			Label dateLabel = new Label(post.getFormattedCreatedAt());
			dateLabel.setStyle("-fx-font-size: 12; -fx-text-fill: #666666;");
			
			headerBox.getChildren().addAll(titleLabel, authorLabel, dateLabel);
			
			// Post content
			Label threadLabel = new Label("Thread: " + post.getThread());
			threadLabel.setStyle("-fx-font-size: 12; -fx-text-fill: #666666;");
			
			Label replyLabel = new Label("Replies: " + replyCount);
			replyLabel.setStyle("-fx-font-size: 12; -fx-text-fill: #666666;");
			
			// Post body preview
			Label bodyLabel;
			if (post.isDeleted()) {
				bodyLabel = new Label("[DELETED POST]");
				bodyLabel.setStyle("-fx-font-style: italic; -fx-text-fill: #999999;");
			} else {
				String preview = post.getBody().length() > 150 ? 
					post.getBody().substring(0, 150) + "..." : post.getBody();
				bodyLabel = new Label(preview);
				bodyLabel.setWrapText(true);
			}
			
			// Action buttons
			HBox buttonBox = new HBox(10);
			Button viewBtn = new Button("View Details");
			viewBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
			viewBtn.setOnAction(e -> viewPostDetailsForStaff(post.getPostId()));
			
			Button feedbackBtn = new Button("Feedback");
			feedbackBtn.setStyle("-fx-background-color: #FF9800; -fx-text-fill: white;");
			feedbackBtn.setOnAction(e -> createFeedback(post.getPostId()));
			
			buttonBox.getChildren().addAll(viewBtn, feedbackBtn);
			
			// Add all elements to the post card
			postCard.getChildren().addAll(headerBox, threadLabel, replyLabel, bodyLabel, buttonBox);
			postContainer.getChildren().add(postCard);
		}
		
		scrollPane.setContent(postContainer);
		scrollPane.setPrefSize(800, 600);
		scrollPane.setFitToWidth(true);
		
		Dialog<Void> dialog = new Dialog<>();
		dialog.setTitle(title);
		dialog.setHeaderText(postList.size() + " post(s) found - Use the buttons to view details or provide feedback");
		dialog.getDialogPane().setContent(scrollPane);
		
		ButtonType closeBtn = new ButtonType("Close", ButtonBar.ButtonData.CANCEL_CLOSE);
		dialog.getDialogPane().getButtonTypes().add(closeBtn);
		dialog.showAndWait();
	}
	
	/**********
	 * <p> Method: void viewPostDetailsForStaff() </p>
	 * 
	 * <p> Description: Displays full post details with replies and feedback for staff.</p>
	 * 
	 * @param postId the postId for the post that will be viewed
	 */
	private static void viewPostDetailsForStaff(String postId) {
		// Refresh collections from database to ensure we have the latest data
		ModelStaffHome.refreshPostsFromDatabase();
		PostCollection posts = ModelStaffHome.getPostCollection();
		ReplyCollection replies = ModelStaffHome.getReplyCollection();
		String currentUsername = ViewStaffHome.theUser.getUserName();
		
		Post post = posts.getPostById(postId);
		if (post == null) {
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setTitle("View Post");
			alert.setHeaderText("Post Not Found");
			alert.setContentText("No post found with ID: " + postId);
			alert.showAndWait();
			return;
		}
		
		// Create main container
		VBox mainContainer = new VBox(15);
		mainContainer.setPadding(new Insets(15));
		
		// Post details section
		VBox postSection = new VBox(10);
		postSection.setStyle("-fx-border-color: #cccccc; -fx-border-width: 1; -fx-padding: 15; -fx-background-color: #f9f9f9;");
		
		// Post header
		HBox postHeader = new HBox(10);
		Label postTitle = new Label(post.getTitle());
		postTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 16;");
		
		Label postAuthor = new Label("by " + post.getAuthorUsername());
		postAuthor.setStyle("-fx-font-size: 12; -fx-text-fill: #666666;");
		
		Label postDate = new Label(post.getFormattedCreatedAt());
		postDate.setStyle("-fx-font-size: 12; -fx-text-fill: #666666;");
		
		postHeader.getChildren().addAll(postTitle, postAuthor, postDate);
		
		// Post metadata
		Label threadLabel = new Label("Thread: " + post.getThread());
		threadLabel.setStyle("-fx-font-size: 12; -fx-text-fill: #666666;");
		
		if (post.getLastEditedAt() != null) {
			Label editedLabel = new Label("Edited: " + post.getFormattedLastEditedAt());
			editedLabel.setStyle("-fx-font-size: 12; -fx-text-fill: #666666;");
			postSection.getChildren().addAll(postHeader, threadLabel, editedLabel);
		} else {
			postSection.getChildren().addAll(postHeader, threadLabel);
		}
		
		// Post body
		Label postBody;
		if (post.isDeleted()) {
			postBody = new Label("[Original post deleted]");
			postBody.setStyle("-fx-font-style: italic; -fx-text-fill: #999999;");
		} else {
			postBody = new Label(post.getBody());
			postBody.setWrapText(true);
		}
		postSection.getChildren().add(postBody);
		
		// Post action buttons
		HBox postButtonBox = new HBox(10);
		Button feedbackBtn = new Button("Provide Feedback");
		feedbackBtn.setStyle("-fx-background-color: #FF9800; -fx-text-fill: white;");
		feedbackBtn.setOnAction(e -> createFeedback(postId));
		
		postButtonBox.getChildren().addAll(feedbackBtn);
		postSection.getChildren().add(postButtonBox);
		
		mainContainer.getChildren().add(postSection);
		
		// Replies section (non-feedback)
		List<Reply> replyList = replies.getRepliesForPost(postId);
		if (!replyList.isEmpty()) {
			Label repliesHeader = new Label("REPLIES (" + replyList.size() + ")");
			repliesHeader.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");
			mainContainer.getChildren().add(repliesHeader);
			
			// Create scrollable container for replies
			ScrollPane repliesScrollPane = new ScrollPane();
			VBox repliesContainer = new VBox(10);
			repliesContainer.setPadding(new Insets(5));
			
			for (Reply reply : replyList) {
				// Create reply card
				VBox replyCard = new VBox(5);
				replyCard.setStyle("-fx-border-color: #e0e0e0; -fx-border-width: 1; -fx-padding: 10; -fx-background-color: #ffffff;");
				
				// Reply header
				HBox replyHeader = new HBox(10);
				Label replyAuthor = new Label(reply.getAuthorUsername());
				replyAuthor.setStyle("-fx-font-weight: bold; -fx-font-size: 12;");
				
				Label replyDate = new Label(reply.getFormattedCreatedAt());
				replyDate.setStyle("-fx-font-size: 11; -fx-text-fill: #999999;");
				
				replyHeader.getChildren().addAll(replyAuthor, replyDate);
				
				// Reply body
				Label replyBody = new Label(reply.getDisplayBody());
				replyBody.setWrapText(true);
				
				replyCard.getChildren().addAll(replyHeader, replyBody);
				repliesContainer.getChildren().add(replyCard);
			}
			
			repliesScrollPane.setContent(repliesContainer);
			repliesScrollPane.setPrefHeight(200);
			repliesScrollPane.setFitToWidth(true);
			mainContainer.getChildren().add(repliesScrollPane);
		}
		
		// Feedback section (only visible to post author and feedback author)
		List<Reply> feedbackList = replies.getFeedbackForPost(postId, currentUsername, post.getAuthorUsername());
		if (!feedbackList.isEmpty()) {
			Label feedbackHeader = new Label("FEEDBACK (" + feedbackList.size() + ")");
			feedbackHeader.setStyle("-fx-font-weight: bold; -fx-font-size: 14; -fx-text-fill: #FF9800;");
			mainContainer.getChildren().add(feedbackHeader);
			
			// Create scrollable container for feedback
			ScrollPane feedbackScrollPane = new ScrollPane();
			VBox feedbackContainer = new VBox(10);
			feedbackContainer.setPadding(new Insets(5));
			
			for (Reply feedback : feedbackList) {
				// Create feedback card
				VBox feedbackCard = new VBox(5);
				feedbackCard.setStyle("-fx-border-color: #FF9800; -fx-border-width: 2; -fx-padding: 10; -fx-background-color: #FFF3E0;");
				
				// Feedback header
				HBox feedbackHeaderBox = new HBox(10);
				Label feedbackAuthor = new Label("Feedback from: " + feedback.getAuthorUsername());
				feedbackAuthor.setStyle("-fx-font-weight: bold; -fx-font-size: 12; -fx-text-fill: #E65100;");
				
				Label feedbackDate = new Label(feedback.getFormattedCreatedAt());
				feedbackDate.setStyle("-fx-font-size: 11; -fx-text-fill: #999999;");
				
				feedbackHeaderBox.getChildren().addAll(feedbackAuthor, feedbackDate);
				
				// Feedback body
				Label feedbackBody = new Label(feedback.getDisplayBody());
				feedbackBody.setWrapText(true);
				
				feedbackCard.getChildren().addAll(feedbackHeaderBox, feedbackBody);
				feedbackContainer.getChildren().add(feedbackCard);
			}
			
			feedbackScrollPane.setContent(feedbackContainer);
			feedbackScrollPane.setPrefHeight(200);
			feedbackScrollPane.setFitToWidth(true);
			mainContainer.getChildren().add(feedbackScrollPane);
		}
		
		// Create scrollable dialog
		ScrollPane mainScrollPane = new ScrollPane(mainContainer);
		mainScrollPane.setFitToWidth(true);
		mainScrollPane.setPrefSize(800, 600);
		
		Dialog<Void> dialog = new Dialog<>();
		dialog.setTitle("Post Details");
		dialog.setHeaderText("Post: " + post.getTitle());
		dialog.getDialogPane().setContent(mainScrollPane);
		
		ButtonType closeBtn = new ButtonType("Close", ButtonBar.ButtonData.CANCEL_CLOSE);
		dialog.getDialogPane().getButtonTypes().add(closeBtn);
		dialog.showAndWait();
	}
	
	/**********
	 * <p> Method: void createFeedback() </p>
	 * 
	 * <p> Description: Handles the creation of private feedback for a post. Displays a dialog
	 * interface allowing staff to input feedback content. The feedback is marked as private
	 * and only visible to the post author and the staff member who provided it.</p>
	 * 
	 * @param postId the ID of the post to provide feedback on
	 */
	protected static void createFeedback(String postId) {
		PostCollection posts = ModelStaffHome.getPostCollection();
		ReplyCollection replies = ModelStaffHome.getReplyCollection();
		String currentUsername = ViewStaffHome.theUser.getUserName();
		
		Post post = posts.getPostById(postId);
		if (post == null) {
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setTitle("Provide Feedback");
			alert.setHeaderText("Post Not Found");
			alert.setContentText("No post found with ID: " + postId);
			alert.showAndWait();
			return;
		}
		
		// Create dialog for feedback
		Dialog<ButtonType> dialog = new Dialog<>();
		dialog.setTitle("Provide Feedback");
		dialog.setHeaderText("Provide private feedback for: " + post.getTitle() + "\n(by " + post.getAuthorUsername() + ")");
		
		// Set up dialog content
		VBox content = new VBox(10);
		content.setPadding(new Insets(20));
		
		Label infoLabel = new Label("This feedback will be private and only visible to you and the post author.");
		infoLabel.setStyle("-fx-font-size: 12; -fx-text-fill: #666666;");
		infoLabel.setWrapText(true);
		
		TextArea feedbackArea = new TextArea();
		feedbackArea.setPromptText("Enter your feedback here (max 3000 characters)");
		feedbackArea.setPrefRowCount(10);
		feedbackArea.setPrefWidth(500);
		feedbackArea.setWrapText(true);
		
		content.getChildren().addAll(infoLabel, feedbackArea);
		dialog.getDialogPane().setContent(content);
		dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
		
		Optional<ButtonType> result = dialog.showAndWait();
		if (result.isPresent() && result.get() == ButtonType.OK) {
			String feedbackBody = feedbackArea.getText().trim();
			
			if (feedbackBody.isEmpty()) {
				Alert alert = new Alert(Alert.AlertType.ERROR);
				alert.setTitle("Provide Feedback");
				alert.setHeaderText("Validation Error");
				alert.setContentText("Feedback cannot be empty.");
				alert.showAndWait();
				return;
			}
			
			// Create feedback reply
			String replyIdOrError = replies.createFeedback(feedbackBody, currentUsername, postId);
			
			Alert alert = new Alert(Alert.AlertType.INFORMATION);
			alert.setTitle("Provide Feedback");
			if (replyIdOrError.startsWith("REPLY_")) {
				// Save to database
				Reply newFeedback = replies.getReplyById(replyIdOrError);
				ModelStaffHome.saveReplyToDatabase(newFeedback);
				
				alert.setHeaderText("Feedback Submitted");
				alert.setContentText("Your feedback has been submitted successfully. The post author will be able to view it.");
				alert.showAndWait();
			} else {
				alert.setAlertType(Alert.AlertType.ERROR);
				alert.setHeaderText("Validation Error");
				alert.setContentText(replyIdOrError);
				alert.showAndWait();
			}
		}
	}
	
	/**********
	 * <p> Method: void createThread() </p>
	 * 
	 * <p> Description: Handles the creation of a new discussion thread. Displays a dialog
	 * interface allowing staff to input thread details including title and description.
	 * Validates all user inputs and saves successful thread creations to the database.</p>
	 * 
	 */
	protected static void createThread() {
		ThreadCollection threads = ModelStaffHome.getThreadCollection();
		String currentUsername = ViewStaffHome.theUser.getUserName();
		
		// Create dialog for thread creation
		Dialog<ButtonType> dialog = new Dialog<>();
		dialog.setTitle("Create Thread");
		dialog.setHeaderText("Create a new discussion thread");
		
		// Set up dialog content
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(20, 150, 10, 10));
		
		TextField titleField = new TextField();
		titleField.setPromptText("Thread title (max 100 characters) *");
		titleField.setPrefWidth(400);
		
		TextArea descriptionArea = new TextArea();
		descriptionArea.setPromptText("Thread description (max 500 characters) *");
		descriptionArea.setPrefRowCount(8);
		descriptionArea.setPrefWidth(400);
		descriptionArea.setWrapText(true);
		
		ComboBox<String> statusComboBox = new ComboBox<>();
		statusComboBox.getItems().addAll("Open", "Closed");
		statusComboBox.setValue("Open"); // Default to Open
		statusComboBox.setPrefWidth(400);
		
		grid.add(new Label("Title:*"), 0, 0);
		grid.add(titleField, 1, 0);
		grid.add(new Label("Description:*"), 0, 1);
		grid.add(descriptionArea, 1, 1);
		grid.add(new Label("Status:*"), 0, 2);
		grid.add(statusComboBox, 1, 2);
		
		dialog.getDialogPane().setContent(grid);
		dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
		
		Optional<ButtonType> result = dialog.showAndWait();
		if (result.isPresent() && result.get() == ButtonType.OK) {
			String title = titleField.getText().trim();
			String description = descriptionArea.getText().trim();
			String statusStr = statusComboBox.getValue();
			
			if (statusStr == null || statusStr.trim().isEmpty()) {
				Alert alert = new Alert(Alert.AlertType.ERROR);
				alert.setTitle("Create Thread");
				alert.setHeaderText("Validation Error");
				alert.setContentText("Please select a status.");
				alert.showAndWait();
				return;
			}
			
			Thread.ThreadStatus status = "Open".equals(statusStr) ? Thread.ThreadStatus.OPEN : Thread.ThreadStatus.CLOSED;
			
			String threadIdOrError = threads.createThread(title, description, currentUsername, status);
			
			Alert alert = new Alert(Alert.AlertType.INFORMATION);
			alert.setTitle("Create Thread");
			if (threadIdOrError.startsWith("THREAD_")) {
				// Save to database
				Thread newThread = threads.getThreadById(threadIdOrError);
				ModelStaffHome.saveThreadToDatabase(newThread);
				
				alert.setHeaderText("Thread Created");
				alert.setContentText("Thread '" + title + "' has been created successfully.");
				alert.showAndWait();
			} else {
				alert.setAlertType(Alert.AlertType.ERROR);
				alert.setHeaderText("Validation Error");
				alert.setContentText(threadIdOrError);
				alert.showAndWait();
			}
		}
	}
	
	/**********
	 * <p> Method: void viewMyThreads() </p>
	 * 
	 * <p> Description: Displays all threads created by the current staff member, sorted by status
	 * (Open first, then Closed). Shows thread title, status, and post count for each thread.</p>
	 * 
	 */
	protected static void viewMyThreads() {
		ModelStaffHome.refreshThreadsFromDatabase();
		ThreadCollection threads = ModelStaffHome.getThreadCollection();
		String currentUsername = ViewStaffHome.theUser.getUserName();
		
		List<Thread> myThreads = threads.getThreadsByCreator(currentUsername);
		
		if (myThreads.isEmpty()) {
			Alert alert = new Alert(Alert.AlertType.INFORMATION);
			alert.setTitle("My Threads");
			alert.setHeaderText("No Threads Found");
			alert.setContentText("You have not created any threads yet.");
			alert.showAndWait();
			return;
		}
		
		// Create a scrollable pane with individual thread cards
		ScrollPane scrollPane = new ScrollPane();
		VBox threadContainer = new VBox(10);
		threadContainer.setPadding(new Insets(10));
		
		// Group threads by status
		List<Thread> openThreads = new ArrayList<>();
		List<Thread> closedThreads = new ArrayList<>();
		for (Thread thread : myThreads) {
			if (thread.isOpen()) {
				openThreads.add(thread);
			} else {
				closedThreads.add(thread);
			}
		}
		
		// Display Open threads
		if (!openThreads.isEmpty()) {
			Label openHeader = new Label("OPEN THREADS");
			openHeader.setStyle("-fx-font-weight: bold; -fx-font-size: 16; -fx-text-fill: #4CAF50;");
			threadContainer.getChildren().add(openHeader);
			
			for (Thread thread : openThreads) {
				int postCount = ModelStaffHome.getPostCountForThread(thread.getTitle());
				VBox threadCard = createThreadCard(thread, postCount, currentUsername);
				threadContainer.getChildren().add(threadCard);
			}
		}
		
		// Display Closed threads
		if (!closedThreads.isEmpty()) {
			Label closedHeader = new Label("CLOSED THREADS");
			closedHeader.setStyle("-fx-font-weight: bold; -fx-font-size: 16; -fx-text-fill: #999999;");
			if (!openThreads.isEmpty()) {
				closedHeader.setPadding(new Insets(20, 0, 0, 0)); // Add spacing
			}
			threadContainer.getChildren().add(closedHeader);
			
			for (Thread thread : closedThreads) {
				int postCount = ModelStaffHome.getPostCountForThread(thread.getTitle());
				VBox threadCard = createThreadCard(thread, postCount, currentUsername);
				threadContainer.getChildren().add(threadCard);
			}
		}
		
		scrollPane.setContent(threadContainer);
		scrollPane.setPrefSize(800, 600);
		scrollPane.setFitToWidth(true);
		
		Dialog<Void> dialog = new Dialog<>();
		dialog.setTitle("My Threads");
		dialog.setHeaderText("Total: " + myThreads.size() + " thread(s) - " + openThreads.size() + " Open, " + closedThreads.size() + " Closed");
		dialog.getDialogPane().setContent(scrollPane);
		
		ButtonType closeBtn = new ButtonType("Close", ButtonBar.ButtonData.CANCEL_CLOSE);
		dialog.getDialogPane().getButtonTypes().add(closeBtn);
		dialog.showAndWait();
	}
	
	/**********
	 * <p> Method: VBox createThreadCard() </p>
	 * 
	 * <p> Description: Helper method to create a visual card for a thread with action buttons.</p>
	 * 
	 * @param thread the thread to display
	 * @param postCount the number of posts in the thread
	 * @param currentUsername the current user's username
	 * @return a VBox containing the thread card UI
	 */
	private static VBox createThreadCard(Thread thread, int postCount, String currentUsername) {
		VBox threadCard = new VBox(5);
		threadCard.setStyle("-fx-border-color: #cccccc; -fx-border-width: 1; -fx-padding: 10; -fx-background-color: #f9f9f9;");
		
		// Thread header
		HBox headerBox = new HBox(10);
		Label titleLabel = new Label(thread.getTitle());
		titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");
		
		Label statusLabel = new Label("Status: " + thread.getStatusString());
		if (thread.isOpen()) {
			statusLabel.setStyle("-fx-font-size: 12; -fx-text-fill: #4CAF50; -fx-font-weight: bold;");
		} else {
			statusLabel.setStyle("-fx-font-size: 12; -fx-text-fill: #999999;");
		}
		
		headerBox.getChildren().addAll(titleLabel, statusLabel);
		
		// Thread description
		Label descLabel = new Label(thread.getDescription());
		descLabel.setWrapText(true);
		descLabel.setStyle("-fx-font-size: 12;");
		
		// Thread metadata
		Label postCountLabel = new Label("Posts: " + postCount);
		postCountLabel.setStyle("-fx-font-size: 12; -fx-text-fill: #666666;");
		
		Label dateLabel = new Label("Created: " + thread.getFormattedCreatedAt());
		dateLabel.setStyle("-fx-font-size: 11; -fx-text-fill: #999999;");
		
		// Action buttons
		HBox buttonBox = new HBox(10);
		Button editBtn = new Button("Edit Thread");
		editBtn.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
		editBtn.setOnAction(e -> editThread(thread));
		
		Button deleteBtn = new Button("Delete Thread");
		deleteBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
		deleteBtn.setOnAction(e -> deleteThread(thread));
		
		buttonBox.getChildren().addAll(editBtn, deleteBtn);
		
		threadCard.getChildren().addAll(headerBox, descLabel, postCountLabel, dateLabel, buttonBox);
		return threadCard;
	}
	
	/**********
	 * <p> Method: void editThread() </p>
	 * 
	 * <p> Description: Handles editing an existing thread. Displays a dialog with pre-filled
	 * values and allows staff to update the title and description.</p>
	 * 
	 * @param thread the thread to edit
	 */
	protected static void editThread(Thread thread) {
		ThreadCollection threads = ModelStaffHome.getThreadCollection();
		String currentUsername = ViewStaffHome.theUser.getUserName();
		
		// Create dialog for thread editing
		Dialog<ButtonType> dialog = new Dialog<>();
		dialog.setTitle("Edit Thread");
		dialog.setHeaderText("Edit thread: " + thread.getTitle());
		
		// Set up dialog content
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(20, 150, 10, 10));
		
		TextField titleField = new TextField(thread.getTitle());
		titleField.setPromptText("Thread title (max 100 characters) *");
		titleField.setPrefWidth(400);
		
		TextArea descriptionArea = new TextArea(thread.getDescription());
		descriptionArea.setPromptText("Thread description (max 500 characters) *");
		descriptionArea.setPrefRowCount(8);
		descriptionArea.setPrefWidth(400);
		descriptionArea.setWrapText(true);
		
		ComboBox<String> statusComboBox = new ComboBox<>();
		statusComboBox.getItems().addAll("Open", "Closed");
		statusComboBox.setValue(thread.getStatusString()); // Set current status
		statusComboBox.setPrefWidth(400);
		
		grid.add(new Label("Title:*"), 0, 0);
		grid.add(titleField, 1, 0);
		grid.add(new Label("Description:*"), 0, 1);
		grid.add(descriptionArea, 1, 1);
		grid.add(new Label("Status:*"), 0, 2);
		grid.add(statusComboBox, 1, 2);
		
		dialog.getDialogPane().setContent(grid);
		dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
		
		Optional<ButtonType> result = dialog.showAndWait();
		if (result.isPresent() && result.get() == ButtonType.OK) {
			String newTitle = titleField.getText().trim();
			String newDescription = descriptionArea.getText().trim();
			String statusStr = statusComboBox.getValue();
			
			if (statusStr == null || statusStr.trim().isEmpty()) {
				Alert alert = new Alert(Alert.AlertType.ERROR);
				alert.setTitle("Edit Thread");
				alert.setHeaderText("Validation Error");
				alert.setContentText("Please select a status.");
				alert.showAndWait();
				return;
			}
			
			Thread.ThreadStatus newStatus = "Open".equals(statusStr) ? Thread.ThreadStatus.OPEN : Thread.ThreadStatus.CLOSED;
			
			String error = threads.updateThread(thread.getThreadId(), newTitle, newDescription, currentUsername, newStatus);
			
			Alert alert = new Alert(Alert.AlertType.INFORMATION);
			alert.setTitle("Edit Thread");
			if (error.isEmpty()) {
				// Save to database
				Thread updatedThread = threads.getThreadById(thread.getThreadId());
				ModelStaffHome.saveThreadToDatabase(updatedThread);
				
				alert.setHeaderText("Thread Updated");
				alert.setContentText("Thread has been updated successfully.");
				alert.showAndWait();
			} else {
				alert.setAlertType(Alert.AlertType.ERROR);
				alert.setHeaderText("Validation Error");
				alert.setContentText(error);
				alert.showAndWait();
			}
		}
	}
	
	/**********
	 * <p> Method: void deleteThread() </p>
	 * 
	 * <p> Description: Handles deletion of a thread with confirmation. Requires staff to answer
	 * a confirmation question before deleting.</p>
	 * 
	 * @param thread the thread to delete
	 */
	protected static void deleteThread(Thread thread) {
		// Confirmation dialog
		Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
		confirmAlert.setTitle("Delete Thread");
		confirmAlert.setHeaderText("Are you sure you want to delete this thread?");
		confirmAlert.setContentText("Thread: " + thread.getTitle() + "\n\nThis action cannot be undone.");
		
		Optional<ButtonType> result = confirmAlert.showAndWait();
		if (result.isPresent() && result.get() == ButtonType.OK) {
			ThreadCollection threads = ModelStaffHome.getThreadCollection();
			String currentUsername = ViewStaffHome.theUser.getUserName();
			
			String error = threads.deleteThread(thread.getThreadId(), currentUsername);
			
			Alert alert = new Alert(Alert.AlertType.INFORMATION);
			alert.setTitle("Delete Thread");
			if (error.isEmpty()) {
				// Delete from database
				ModelStaffHome.deleteThreadFromDatabase(thread.getThreadId());
				
				alert.setHeaderText("Thread Deleted");
				alert.setContentText("Thread '" + thread.getTitle() + "' has been deleted successfully.");
				alert.showAndWait();
			} else {
				alert.setAlertType(Alert.AlertType.ERROR);
				alert.setHeaderText("Deletion Failed");
				alert.setContentText(error);
				alert.showAndWait();
			}
		}
	}
	
	/**********
	 * <p> Method: void createRequest() </p>
	 * 
	 * <p> Description: Handles the creation of a new admin request. Displays a dialog
	 * interface allowing staff to input request details including title, description,
	 * and category. Validates all user inputs and saves successful request creations
	 * to the database.</p>
	 * 
	 */
	protected static void createRequest() {
		RequestCollection requests = ModelStaffHome.getRequestCollection();
		String currentUsername = ViewStaffHome.theUser.getUserName();
		
		// Create dialog for request creation
		Dialog<ButtonType> dialog = new Dialog<>();
		dialog.setTitle("Create Request");
		dialog.setHeaderText("Create a new admin request");
		
		// Set up dialog content
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(20, 150, 10, 10));
		
		TextField titleField = new TextField();
		titleField.setPromptText("Request title (max 200 characters) *");
		titleField.setPrefWidth(400);
		
		ComboBox<String> categoryComboBox = new ComboBox<>();
		categoryComboBox.getItems().addAll(
			"System Issue",
			"Account Issue",
			"Parameter Issue",
			"Grading Question",
			"Other"
		);
		categoryComboBox.setPromptText("Select category *");
		categoryComboBox.setPrefWidth(400);
		
		TextArea descriptionArea = new TextArea();
		descriptionArea.setPromptText("Describe what you need help with and what you have done so far to resolve the issue (max 2000 characters) *");
		descriptionArea.setPrefRowCount(10);
		descriptionArea.setPrefWidth(400);
		descriptionArea.setWrapText(true);
		
		grid.add(new Label("Title:*"), 0, 0);
		grid.add(titleField, 1, 0);
		grid.add(new Label("Category:*"), 0, 1);
		grid.add(categoryComboBox, 1, 1);
		grid.add(new Label("Description:*"), 0, 2);
		grid.add(descriptionArea, 1, 2);
		
		dialog.getDialogPane().setContent(grid);
		dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
		
		Optional<ButtonType> result = dialog.showAndWait();
		if (result.isPresent() && result.get() == ButtonType.OK) {
			String title = titleField.getText().trim();
			String categoryStr = categoryComboBox.getValue();
			String description = descriptionArea.getText().trim();
			
			if (categoryStr == null || categoryStr.trim().isEmpty()) {
				Alert alert = new Alert(Alert.AlertType.ERROR);
				alert.setTitle("Create Request");
				alert.setHeaderText("Validation Error");
				alert.setContentText("Please select a category.");
				alert.showAndWait();
				return;
			}
			
			// Convert category string to enum
			Request.RequestCategory category;
			switch (categoryStr) {
				case "System Issue":
					category = Request.RequestCategory.SYSTEM_ISSUE;
					break;
				case "Account Issue":
					category = Request.RequestCategory.ACCOUNT_ISSUE;
					break;
				case "Parameter Issue":
					category = Request.RequestCategory.PARAMETER_ISSUE;
					break;
				case "Grading Question":
					category = Request.RequestCategory.GRADING_QUESTION;
					break;
				case "Other":
					category = Request.RequestCategory.OTHER;
					break;
				default:
					category = Request.RequestCategory.OTHER;
			}
			
			String requestIdOrError = requests.createRequest(title, description, category, currentUsername);
			
			Alert alert = new Alert(Alert.AlertType.INFORMATION);
			alert.setTitle("Create Request");
			if (requestIdOrError.startsWith("REQUEST_")) {
				// Save to database
				Request newRequest = requests.getRequestById(requestIdOrError);
				ModelStaffHome.saveRequestToDatabase(newRequest);
				
				alert.setHeaderText("Request Created");
				alert.setContentText("Request #" + requestIdOrError + " has been created successfully. An admin will review it.");
				alert.showAndWait();
			} else {
				alert.setAlertType(Alert.AlertType.ERROR);
				alert.setHeaderText("Validation Error");
				alert.setContentText(requestIdOrError);
				alert.showAndWait();
			}
		}
	}
	
	/**********
	 * <p> Method: void viewMyRequests() </p>
	 * 
	 * <p> Description: Displays all requests created by the current staff member, sorted by status
	 * (Open first, then Closed). Shows request ID, title, category, status, and allows reopening
	 * closed requests.</p>
	 * 
	 */
	protected static void viewMyRequests() {
		ModelStaffHome.refreshRequestsFromDatabase();
		RequestCollection requests = ModelStaffHome.getRequestCollection();
		String currentUsername = ViewStaffHome.theUser.getUserName();
		
		List<Request> myRequests = requests.getRequestsByCreator(currentUsername);
		
		if (myRequests.isEmpty()) {
			Alert alert = new Alert(Alert.AlertType.INFORMATION);
			alert.setTitle("My Requests");
			alert.setHeaderText("No Requests Found");
			alert.setContentText("You have not created any requests yet.");
			alert.showAndWait();
			return;
		}
		
		// Create a scrollable pane with individual request cards
		ScrollPane scrollPane = new ScrollPane();
		VBox requestContainer = new VBox(10);
		requestContainer.setPadding(new Insets(10));
		
		// Group requests by status
		List<Request> openRequests = new ArrayList<>();
		List<Request> closedRequests = new ArrayList<>();
		for (Request request : myRequests) {
			if (request.isOpen()) {
				openRequests.add(request);
			} else {
				closedRequests.add(request);
			}
		}
		
		// Display Open requests
		if (!openRequests.isEmpty()) {
			Label openHeader = new Label("OPEN REQUESTS");
			openHeader.setStyle("-fx-font-weight: bold; -fx-font-size: 16; -fx-text-fill: #4CAF50;");
			requestContainer.getChildren().add(openHeader);
			
			for (Request request : openRequests) {
				VBox requestCard = createRequestCard(request, currentUsername);
				requestContainer.getChildren().add(requestCard);
			}
		}
		
		// Display Closed requests
		if (!closedRequests.isEmpty()) {
			Label closedHeader = new Label("CLOSED REQUESTS");
			closedHeader.setStyle("-fx-font-weight: bold; -fx-font-size: 16; -fx-text-fill: #999999;");
			if (!openRequests.isEmpty()) {
				closedHeader.setPadding(new Insets(20, 0, 0, 0)); // Add spacing
			}
			requestContainer.getChildren().add(closedHeader);
			
			for (Request request : closedRequests) {
				VBox requestCard = createRequestCard(request, currentUsername);
				requestContainer.getChildren().add(requestCard);
			}
		}
		
		scrollPane.setContent(requestContainer);
		scrollPane.setPrefSize(900, 600);
		scrollPane.setFitToWidth(true);
		
		Dialog<Void> dialog = new Dialog<>();
		dialog.setTitle("My Requests");
		dialog.setHeaderText("Total: " + myRequests.size() + " request(s) - " + openRequests.size() + " Open, " + closedRequests.size() + " Closed");
		dialog.getDialogPane().setContent(scrollPane);
		
		ButtonType closeBtn = new ButtonType("Close", ButtonBar.ButtonData.CANCEL_CLOSE);
		dialog.getDialogPane().getButtonTypes().add(closeBtn);
		dialog.showAndWait();
	}
	
	/**********
	 * <p> Method: VBox createRequestCard() </p>
	 * 
	 * <p> Description: Helper method to create a visual card for a request with action buttons.</p>
	 * 
	 * @param request the request to display
	 * @param currentUsername the current user's username
	 * @return a VBox containing the request card UI
	 */
	private static VBox createRequestCard(Request request, String currentUsername) {
		VBox requestCard = new VBox(5);
		requestCard.setStyle("-fx-border-color: #cccccc; -fx-border-width: 1; -fx-padding: 10; -fx-background-color: #f9f9f9;");
		
		// Request header
		HBox headerBox = new HBox(10);
		Label requestIdLabel = new Label("Request #" + request.getRequestId());
		requestIdLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");
		
		Label titleLabel = new Label(request.getTitle());
		titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");
		
		Label statusLabel = new Label("Status: " + request.getStatusString());
		if (request.isOpen()) {
			statusLabel.setStyle("-fx-font-size: 12; -fx-text-fill: #4CAF50; -fx-font-weight: bold;");
		} else {
			statusLabel.setStyle("-fx-font-size: 12; -fx-text-fill: #999999;");
		}
		
		headerBox.getChildren().addAll(requestIdLabel, titleLabel, statusLabel);
		
		// Request metadata
		Label categoryLabel = new Label("Category: " + request.getCategoryString());
		categoryLabel.setStyle("-fx-font-size: 12; -fx-text-fill: #666666;");
		
		Label dateLabel = new Label("Created: " + request.getFormattedCreatedAt());
		dateLabel.setStyle("-fx-font-size: 11; -fx-text-fill: #999999;");
		
		// Request description
		Label descLabel = new Label(request.getDescription());
		descLabel.setWrapText(true);
		descLabel.setStyle("-fx-font-size: 12;");
		
		// Resolution notes (if closed)
		VBox contentBox = new VBox(5);
		contentBox.getChildren().addAll(headerBox, categoryLabel, dateLabel, descLabel);
		
		if (request.isClosed() && request.getResolutionNotes() != null && !request.getResolutionNotes().isEmpty()) {
			Label resolutionHeader = new Label("Resolution Notes:");
			resolutionHeader.setStyle("-fx-font-weight: bold; -fx-font-size: 12; -fx-text-fill: #2196F3;");
			Label resolutionLabel = new Label(request.getResolutionNotes());
			resolutionLabel.setWrapText(true);
			resolutionLabel.setStyle("-fx-font-size: 12; -fx-text-fill: #333333; -fx-background-color: #E3F2FD; -fx-padding: 5;");
			Label closedByLabel = new Label("Closed by: " + request.getClosedByUsername() + " on " + request.getFormattedClosedAt());
			closedByLabel.setStyle("-fx-font-size: 11; -fx-text-fill: #999999;");
			contentBox.getChildren().addAll(resolutionHeader, resolutionLabel, closedByLabel);
		}
		
		// Reopen reason (if reopened)
		if (request.isReopened() && request.getReopenReason() != null && !request.getReopenReason().isEmpty()) {
			Label reopenHeader = new Label("Reopen Reason:");
			reopenHeader.setStyle("-fx-font-weight: bold; -fx-font-size: 12; -fx-text-fill: #FF9800;");
			Label reopenLabel = new Label(request.getReopenReason());
			reopenLabel.setWrapText(true);
			reopenLabel.setStyle("-fx-font-size: 12; -fx-text-fill: #333333; -fx-background-color: #FFF3E0; -fx-padding: 5;");
			Label reopenedLabel = new Label("Reopened on: " + request.getFormattedReopenedAt());
			reopenedLabel.setStyle("-fx-font-size: 11; -fx-text-fill: #999999;");
			contentBox.getChildren().addAll(reopenHeader, reopenLabel, reopenedLabel);
			
			// Link to original request
			if (request.getOriginalRequestId() != null) {
				Label originalLabel = new Label("Original Request: #" + request.getOriginalRequestId());
				originalLabel.setStyle("-fx-font-size: 11; -fx-text-fill: #666666; -fx-font-style: italic;");
				contentBox.getChildren().add(originalLabel);
			}
		}
		
		// Action buttons
		HBox buttonBox = new HBox(10);
		if (request.isClosed() && request.getCreatedByUsername().equals(currentUsername)) {
			Button reopenBtn = new Button("Reopen Request");
			reopenBtn.setStyle("-fx-background-color: #FF9800; -fx-text-fill: white;");
			reopenBtn.setOnAction(e -> reopenRequest(request));
			buttonBox.getChildren().add(reopenBtn);
		}
		
		requestCard.getChildren().addAll(contentBox);
		if (!buttonBox.getChildren().isEmpty()) {
			requestCard.getChildren().add(buttonBox);
		}
		
		return requestCard;
	}
	
	/**********
	 * <p> Method: void reopenRequest() </p>
	 * 
	 * <p> Description: Handles reopening a closed request. Displays a dialog requiring
	 * staff to explain why they are reopening the request.</p>
	 * 
	 * @param originalRequest the original request to reopen
	 */
	protected static void reopenRequest(Request originalRequest) {
		RequestCollection requests = ModelStaffHome.getRequestCollection();
		String currentUsername = ViewStaffHome.theUser.getUserName();
		
		// Create dialog for reopening request
		Dialog<ButtonType> dialog = new Dialog<>();
		dialog.setTitle("Reopen Request");
		dialog.setHeaderText("Reopen Request #" + originalRequest.getRequestId() + "\n" + originalRequest.getTitle());
		
		// Set up dialog content
		VBox content = new VBox(10);
		content.setPadding(new Insets(20));
		
		Label infoLabel = new Label("Please explain why you are reopening this request:");
		infoLabel.setStyle("-fx-font-size: 12; -fx-text-fill: #666666;");
		infoLabel.setWrapText(true);
		
		TextArea reasonArea = new TextArea();
		reasonArea.setPromptText("Enter reason for reopening (required, max 1000 characters)");
		reasonArea.setPrefRowCount(8);
		reasonArea.setPrefWidth(500);
		reasonArea.setWrapText(true);
		
		content.getChildren().addAll(infoLabel, reasonArea);
		dialog.getDialogPane().setContent(content);
		dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
		
		Optional<ButtonType> result = dialog.showAndWait();
		if (result.isPresent() && result.get() == ButtonType.OK) {
			String reopenReason = reasonArea.getText().trim();
			
			String newRequestIdOrError = requests.reopenRequest(originalRequest.getRequestId(), currentUsername, reopenReason);
			
			Alert alert = new Alert(Alert.AlertType.INFORMATION);
			alert.setTitle("Reopen Request");
			if (newRequestIdOrError.startsWith("REQUEST_")) {
				// Save to database
				Request newRequest = requests.getRequestById(newRequestIdOrError);
				ModelStaffHome.saveRequestToDatabase(newRequest);
				
				alert.setHeaderText("Request Reopened");
				alert.setContentText("Request #" + newRequestIdOrError + " has been reopened successfully. An admin will review it.");
				alert.showAndWait();
			} else {
				alert.setAlertType(Alert.AlertType.ERROR);
				alert.setHeaderText("Reopen Failed");
				alert.setContentText(newRequestIdOrError);
				alert.showAndWait();
			}
		}
	}
}
