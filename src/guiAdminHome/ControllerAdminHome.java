package guiAdminHome;

import java.util.List;
import java.util.ArrayList;
import java.util.Optional;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.Insets;
import database.Database;
import entityClasses.Request;
import entityClasses.RequestCollection;

/*******
 * <p> Title: GUIAdminHomePage Class. </p>
 * 
 * <p> Description: The Java/FX-based Admin Home Page.  This class provides the controller actions
 * basic on the user's use of the JavaFX GUI widgets defined by the View class.
 * 
 * This page contains a number of buttons that have not yet been implemented.  WHen those buttons
 * are pressed, an alert pops up to tell the user that the function associated with the button has
 * not been implemented. Also, be aware that What has been implemented may not work the way the
 * final product requires and there maybe defects in this code.
 * 
 * The class has been written assuming that the View or the Model are the only class methods that
 * can invoke these methods.  This is why each has been declared at "protected".  Do not change any
 * of these methods to public.</p>
 * 
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 * 
 * @author Lynn Robert Carter
 * 
 * @version 1.00		2025-08-17 Initial version
 *  
 */

public class ControllerAdminHome {
	
	/*-*******************************************************************************************

	User Interface Actions for this page
	
	This controller is not a class that gets instantiated.  Rather, it is a collection of protected
	static methods that can be called by the View (which is a singleton instantiated object) and 
	the Model is often just a stub, or will be a singleton instantiated object.
	
	*/

	// Reference for the in-memory database so this package has access
	private static Database theDatabase = applicationMain.FoundationsMain.database;

	/**********
	 * <p> 
	 * 
	 * Title: performInvitation () Method. </p>
	 * 
	 * <p> Description: Protected method to send an email inviting a potential user to establish
	 * an account and a specific role. </p>
	 */
	protected static void performInvitation () {
		// Verify that the email address is valid - If not alert the user and return
		String emailAddress = ViewAdminHome.text_InvitationEmailAddress.getText();
		if (invalidEmailAddress(emailAddress)) {
			return;
		}
		
		// Check to ensure that we are not sending a second message with a new invitation code to
		// the same email address.  
		if (theDatabase.emailaddressHasBeenUsed(emailAddress)) {
			ViewAdminHome.alertEmailError.setContentText(
					"An invitation has already been sent to this email address.");
			ViewAdminHome.alertEmailError.showAndWait();
			return;
		}
		
		// Inform the user that the invitation has been sent and display the invitation code
		String theSelectedRole = (String) ViewAdminHome.combobox_SelectRole.getValue();
		String invitationCode = theDatabase.generateInvitationCode(emailAddress,
				theSelectedRole);
		String msg = "Code: " + invitationCode + " for role " + theSelectedRole + 
				" was sent to: " + emailAddress;
		System.out.println(msg);
		ViewAdminHome.alertEmailSent.setContentText(msg);
		ViewAdminHome.alertEmailSent.showAndWait();
		
		// Update the Admin Home pages status
		ViewAdminHome.text_InvitationEmailAddress.setText("");
		ViewAdminHome.label_NumberOfInvitations.setText("Number of outstanding invitations: " + 
				theDatabase.getNumberOfInvitations());
	}
	
	/**********
	 * <p> 
	 * 
	 * Title: manageInvitations () Method. </p>
	 * 
	 * <p> Description: Protected method that is currently a stub informing the user that
	 * this function has not yet been implemented. </p>
	 */
	protected static void manageInvitations () {
		System.out.println("\n*** WARNING ***: Manage Invitations Not Yet Implemented");
		ViewAdminHome.alertNotImplemented.setTitle("*** WARNING ***");
		ViewAdminHome.alertNotImplemented.setHeaderText("Manage Invitations Issue");
		ViewAdminHome.alertNotImplemented.setContentText("Manage Invitations Not Yet Implemented");
		ViewAdminHome.alertNotImplemented.showAndWait();
	}
	
	/**********
	 * <p> 
	 * 
	 * Title: setOnetimePassword () Method. </p>
	 * 
	 * <p> Description: Protected method that is currently a stub informing the user that
	 * this function has not yet been implemented. </p>
	 */
	protected static void setOnetimePassword () {
		java.util.List<String> users = theDatabase.getUserList();
        if (users == null || users.size() <= 1) {
            ViewAdminHome.alertNotImplemented.setTitle("One-Time Password");
            ViewAdminHome.alertNotImplemented.setHeaderText("No Users Found");
            ViewAdminHome.alertNotImplemented.setContentText("There are no users to set a one-time password for.");
            ViewAdminHome.alertNotImplemented.showAndWait();
            return;
        }

        // Remove placeholder and the current admin
        users = new java.util.ArrayList<String>(users);
        if (users.get(0).compareTo("<Select a User>") == 0) users.remove(0);
        String loggedInUser = guiAdminHome.ViewAdminHome.theUser.getUserName();
        users.remove(loggedInUser);
        if (users.isEmpty()) {
            ViewAdminHome.alertNotImplemented.setTitle("One-Time Password");
            ViewAdminHome.alertNotImplemented.setHeaderText("No Eligible Users");
            ViewAdminHome.alertNotImplemented.setContentText("You cannot set a one-time password for yourself.");
            ViewAdminHome.alertNotImplemented.showAndWait();
            return;
        }

        javafx.scene.control.ChoiceDialog<String> userDialog =
            new javafx.scene.control.ChoiceDialog<String>(users.get(0), users);
        userDialog.setTitle("One-Time Password");
        userDialog.setHeaderText("Select a user to set a one-time password");
        userDialog.setContentText("User:");
        java.util.Optional<String> userResult = userDialog.showAndWait();
        if (!userResult.isPresent()) return;
        String selectedUser = userResult.get();
        javafx.scene.control.TextInputDialog otpDialog = new javafx.scene.control.TextInputDialog("");
        otpDialog.setTitle("One-Time Password");
        otpDialog.setHeaderText("Enter a one-time password for '" + selectedUser + "'");
        otpDialog.setContentText("One-Time Password:");
        java.util.Optional<String> otpResult = otpDialog.showAndWait();
        if (!otpResult.isPresent()) return;
        String otp = otpResult.get();
        if (otp == null || otp.trim().length() == 0) {
            javafx.scene.control.Alert info = new javafx.scene.control.Alert(
            javafx.scene.control.Alert.AlertType.INFORMATION);
            info.setTitle("One-Time Password");
            info.setHeaderText("No Password Provided");
            info.setContentText("One-time password was not set.");
            info.showAndWait();
            return;
        }

        boolean ok = theDatabase.setOneTimePassword(selectedUser, otp);
        javafx.scene.control.Alert info = new javafx.scene.control.Alert(
            javafx.scene.control.Alert.AlertType.INFORMATION);
        info.setTitle("One-Time Password");
        if (ok) {
            info.setHeaderText("One-Time Password Set");
            info.setContentText("A one-time password was set for '" + selectedUser + "'.");
        } else {
            info.setHeaderText("Operation Failed");
            info.setContentText("Unable to set a one-time password for '" + selectedUser + "'.");
        }
        info.showAndWait();

	}
	
	/**********
	 * <p> 
	 * 
	 * Title: deleteUser () Method. </p>
	 * 
	 * <p> Description: Protected method that is currently a stub informing the user that
	 * this function has not yet been implemented. </p>
	 */
	protected static void deleteUser() {
        java.util.List<String> users = theDatabase.getUserList();
        if (users == null || users.size() <= 1) {
            ViewAdminHome.alertNotImplemented.setTitle("Delete User");
            ViewAdminHome.alertNotImplemented.setHeaderText("No Deletable Users Found");
            ViewAdminHome.alertNotImplemented.setContentText("There are no users to delete.");
            ViewAdminHome.alertNotImplemented.showAndWait();
            return;
        }

        // Remove the placeholder entry and the currently logged-in admin (no self-delete)
        users = new java.util.ArrayList<String>(users);
        if (users.get(0).compareTo("<Select a User>") == 0) users.remove(0);
        String loggedInUser = guiAdminHome.ViewAdminHome.theUser.getUserName();
        users.remove(loggedInUser);
        if (users.isEmpty()) {
            ViewAdminHome.alertNotImplemented.setTitle("Delete User");
            ViewAdminHome.alertNotImplemented.setHeaderText("No Deletable Users Found");
            ViewAdminHome.alertNotImplemented.setContentText("You cannot delete your own account, and no other users are available.");
            ViewAdminHome.alertNotImplemented.showAndWait();
            return;
        }
        
        javafx.scene.control.ChoiceDialog<String> dialog =
        		new javafx.scene.control.ChoiceDialog<String>(users.get(0), users);
        dialog.setTitle("Delete User");
        dialog.setHeaderText("Select a user to delete");
        dialog.setContentText("User:");

        // Change OK/Cancel → Yes/No
        dialog.getDialogPane().getButtonTypes().setAll(
        	   new javafx.scene.control.ButtonType("Yes", javafx.scene.control.ButtonBar.ButtonData.OK_DONE),
        	   new javafx.scene.control.ButtonType("No", javafx.scene.control.ButtonBar.ButtonData.CANCEL_CLOSE)
        );

        java.util.Optional<String> result = dialog.showAndWait();
        if (!result.isPresent()) return;

        String selectedUser = result.get();
        if (selectedUser.compareTo(loggedInUser) == 0) {
            javafx.scene.control.Alert selfBlock = new javafx.scene.control.Alert(
                javafx.scene.control.Alert.AlertType.INFORMATION);
            selfBlock.setTitle("Delete User");
            selfBlock.setHeaderText("Action Not Allowed");
            selfBlock.setContentText("You cannot delete your own account.");
            selfBlock.showAndWait();
            return;
        }
        
        javafx.scene.control.Alert confirm = new javafx.scene.control.Alert(
            javafx.scene.control.Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Deletion");
        confirm.setHeaderText("Delete user '" + selectedUser + "'?");
        confirm.setContentText("This action cannot be undone.");
        
     // Clear default buttons (OK/Cancel) and replace with Yes/No
        confirm.getButtonTypes().setAll(
            new javafx.scene.control.ButtonType("Yes", javafx.scene.control.ButtonBar.ButtonData.OK_DONE),
            new javafx.scene.control.ButtonType("No", javafx.scene.control.ButtonBar.ButtonData.CANCEL_CLOSE)
        );

        java.util.Optional<javafx.scene.control.ButtonType> confirmed = confirm.showAndWait();
        if (!confirmed.isPresent() || confirmed.get().getButtonData() != javafx.scene.control.ButtonBar.ButtonData.OK_DONE) {
            return; // Treat "No" or close as cancel
        }

        boolean ok = theDatabase.deleteUser(selectedUser);
        javafx.scene.control.Alert info = new javafx.scene.control.Alert(
            javafx.scene.control.Alert.AlertType.INFORMATION);
        info.setTitle("Delete User");
        if (ok) {
            info.setHeaderText("User Deleted");
            info.setContentText("User '" + selectedUser + "' was deleted.");
            info.showAndWait();
            // Refresh the count label
            ViewAdminHome.label_NumberOfUsers.setText("Number of users: " +
                    theDatabase.getNumberOfUsers());
        } else {
            info.setHeaderText("Delete Failed");
            info.setContentText("Could not delete user '" + selectedUser + "'.");
            info.showAndWait();
        }
    }
	
	/**********
	 * <p> 
	 * 
	 * Title: listUsers () Method. </p>
	 * 
	 * <p> Description: Protected method that is currently a stub informing the user that
	 * this function has not yet been implemented. </p>
	 */
	protected static void listUsers() {
		java.util.List<entityClasses.User> users = theDatabase.getAllUsers();
        StringBuilder sb = new StringBuilder();
        for (entityClasses.User u : users) {
            String username = u.getUserName();
            String fullName = safe(u.getFirstName()) +
                    ((u.getMiddleName() != null && u.getMiddleName().length() > 0) ? " " + u.getMiddleName() : "") +
                    ((u.getLastName() != null && u.getLastName().length() > 0) ? " " + u.getLastName() : "");
            String email = safe(u.getEmailAddress());
            java.util.List<String> roles = new java.util.ArrayList<String>();
            if (u.getAdminRole()) roles.add("Admin");
            if (u.getNewStudent()) roles.add("Student");
            if (u.getNewStaff()) roles.add("Staff");
            String rolesStr = roles.isEmpty() ? "(none)" : String.join(", ", roles);
            sb.append("Username: ").append(username)
                .append("\nName: ").append(fullName.trim())
                .append("\nEmail: ").append(email)
                .append("\nRoles: ").append(rolesStr)
                .append("\n\n");
        }

        javafx.scene.control.TextArea ta = new javafx.scene.control.TextArea(sb.toString());
        ta.setEditable(false);
        ta.setWrapText(true);
        ta.setPrefRowCount(20);
        javafx.scene.control.Dialog<Void> dialog = new javafx.scene.control.Dialog<Void>();
        dialog.setTitle("All Users");
        dialog.setHeaderText("User Accounts");
        javafx.scene.control.ButtonType closeBtn = new javafx.scene.control.ButtonType("Close", javafx.scene.control.ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().add(closeBtn);
        dialog.getDialogPane().setContent(ta);
        dialog.showAndWait();
    }

    private static String safe(String s) { return (s == null) ? "" : s; }

	
	/**********
	 * <p> 
	 * 
	 * Title: addRemoveRoles () Method. </p>
	 * 
	 * <p> Description: Protected method that allows an admin to add and remove roles for any of
	 * the users currently in the system.  This is done by invoking the AddRemoveRoles Page. There
	 * is no need to specify the home page for the return as this can only be initiated by and
	 * Admin.</p>
	 */
	protected static void addRemoveRoles() {
		guiAddRemoveRoles.ViewAddRemoveRoles.displayAddRemoveRoles(ViewAdminHome.theStage, 
				ViewAdminHome.theUser);
	}
	
	/**********
	 * <p> 
	 * 
	 * Title: invalidEmailAddress () Method. </p>
	 * 
	 * <p> Description: Protected method that is intended to check an email address before it is
	 * used to reduce errors.  The code currently only checks to see that the email address is not
	 * empty.  In the future, a syntactic check must be performed and maybe there is a way to check
	 * if a properly email address is active.</p>
	 * 
	 * @param emailAddress	This String holds what is expected to be an email address
	 */
	protected static boolean invalidEmailAddress(String emailAddress) {
		if (emailAddress.length() == 0) {
			ViewAdminHome.alertEmailError.setContentText(
					"Correct the email address and try again.");
			ViewAdminHome.alertEmailError.showAndWait();
			return true;
		}
		return false;
	}
	
	/**********
	 * <p> 
	 * 
	 * Title: performLogout () Method. </p>
	 * 
	 * <p> Description: Protected method that logs this user out of the system and returns to the
	 * login page for future use.</p>
	 */
	protected static void performLogout() {
		guiUserLogin.ViewUserLogin.displayUserLogin(ViewAdminHome.theStage);
	}
	
	/**********
	 * <p> 
	 * 
	 * Title: performQuit () Method. </p>
	 * 
	 * <p> Description: Protected method that gracefully terminates the execution of the program.
	 * </p>
	 */
	protected static void performQuit() {
		System.exit(0);
	}
	
	/**********
	 * <p> Method: void viewAllRequests() </p>
	 * 
	 * <p> Description: Displays all requests created by staff, sorted by status (Open first,
	 * then Closed). Shows request ID, title, category, status, creator, and allows closing
	 * open requests.</p>
	 * 
	 */
	protected static void viewAllRequests() {
		// Use ModelStaffHome to access request collection
		guiStaff.ModelStaffHome.refreshRequestsFromDatabase();
		RequestCollection requests = guiStaff.ModelStaffHome.getRequestCollection();
		
		List<Request> allRequests = requests.getAllRequests();
		
		if (allRequests.isEmpty()) {
			Alert alert = new Alert(Alert.AlertType.INFORMATION);
			alert.setTitle("All Requests");
			alert.setHeaderText("No Requests Found");
			alert.setContentText("There are no requests from staff.");
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
		for (Request request : allRequests) {
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
				VBox requestCard = createRequestCardForAdmin(request);
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
				VBox requestCard = createRequestCardForAdmin(request);
				requestContainer.getChildren().add(requestCard);
			}
		}
		
		scrollPane.setContent(requestContainer);
		scrollPane.setPrefSize(900, 600);
		scrollPane.setFitToWidth(true);
		
		Dialog<Void> dialog = new Dialog<>();
		dialog.setTitle("All Requests");
		dialog.setHeaderText("Total: " + allRequests.size() + " request(s) - " + openRequests.size() + " Open, " + closedRequests.size() + " Closed");
		dialog.getDialogPane().setContent(scrollPane);
		
		ButtonType closeBtn = new ButtonType("Close", ButtonBar.ButtonData.CANCEL_CLOSE);
		dialog.getDialogPane().getButtonTypes().add(closeBtn);
		dialog.showAndWait();
	}
	
	/**********
	 * <p> Method: VBox createRequestCardForAdmin() </p>
	 * 
	 * <p> Description: Helper method to create a visual card for a request with close button for admin.</p>
	 * 
	 * @param request the request to display
	 * @return a VBox containing the request card UI
	 */
	private static VBox createRequestCardForAdmin(Request request) {
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
		
		Label creatorLabel = new Label("Created by: " + request.getCreatedByUsername() + " on " + request.getFormattedCreatedAt());
		creatorLabel.setStyle("-fx-font-size: 11; -fx-text-fill: #999999;");
		
		// Request description
		Label descLabel = new Label(request.getDescription());
		descLabel.setWrapText(true);
		descLabel.setStyle("-fx-font-size: 12;");
		
		// Resolution notes (if closed)
		VBox contentBox = new VBox(5);
		contentBox.getChildren().addAll(headerBox, categoryLabel, creatorLabel, descLabel);
		
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
		if (request.isOpen()) {
			Button closeBtn = new Button("Close Request");
			closeBtn.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
			closeBtn.setOnAction(e -> closeRequest(request));
			buttonBox.getChildren().add(closeBtn);
		}
		
		requestCard.getChildren().addAll(contentBox);
		if (!buttonBox.getChildren().isEmpty()) {
			requestCard.getChildren().add(buttonBox);
		}
		
		return requestCard;
	}
	
	/**********
	 * <p> Method: void closeRequest() </p>
	 * 
	 * <p> Description: Handles closing a request. If no request is provided, displays a dialog
	 * to select a request. Requires admin to document steps taken to resolve the request.</p>
	 * 
	 * @param request the request to close (null if called from button)
	 */
	protected static void closeRequest(Request request) {
		// If request is null, show all open requests and let admin select
		if (request == null) {
			guiStaff.ModelStaffHome.refreshRequestsFromDatabase();
			RequestCollection requests = guiStaff.ModelStaffHome.getRequestCollection();
			List<Request> openRequests = requests.getOpenRequests();
			
			if (openRequests.isEmpty()) {
				Alert alert = new Alert(Alert.AlertType.INFORMATION);
				alert.setTitle("Close Request");
				alert.setHeaderText("No Open Requests");
				alert.setContentText("There are no open requests to close.");
				alert.showAndWait();
				return;
			}
			
			// Create selection dialog
			Dialog<ButtonType> selectDialog = new Dialog<>();
			selectDialog.setTitle("Select Request to Close");
			selectDialog.setHeaderText("Select a request to close:");
			
			ComboBox<String> requestComboBox = new ComboBox<>();
			for (Request req : openRequests) {
				requestComboBox.getItems().add("Request #" + req.getRequestId() + ": " + req.getTitle());
			}
			requestComboBox.setPrefWidth(500);
			
			VBox content = new VBox(10);
			content.setPadding(new Insets(20));
			content.getChildren().add(requestComboBox);
			selectDialog.getDialogPane().setContent(content);
			selectDialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
			
			Optional<ButtonType> selectResult = selectDialog.showAndWait();
			if (selectResult.isPresent() && selectResult.get() == ButtonType.OK) {
				String selected = requestComboBox.getValue();
				if (selected == null || selected.isEmpty()) {
					Alert alert = new Alert(Alert.AlertType.ERROR);
					alert.setTitle("Close Request");
					alert.setHeaderText("Validation Error");
					alert.setContentText("Please select a request.");
					alert.showAndWait();
					return;
				}
				
				// Extract request ID from selection
				String requestId = selected.substring(selected.indexOf("#") + 1, selected.indexOf(":"));
				request = requests.getRequestById(requestId);
			} else {
				return; // User cancelled
			}
		}
		
		if (request == null) {
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setTitle("Close Request");
			alert.setHeaderText("Error");
			alert.setContentText("Request not found.");
			alert.showAndWait();
			return;
		}
		
		// Create dialog for closing request with resolution notes
		Dialog<ButtonType> dialog = new Dialog<>();
		dialog.setTitle("Close Request");
		dialog.setHeaderText("Close Request #" + request.getRequestId() + "\n" + request.getTitle());
		
		// Set up dialog content
		VBox content = new VBox(10);
		content.setPadding(new Insets(20));
		
		Label infoLabel = new Label("Please document the steps you have taken to resolve this request:");
		infoLabel.setStyle("-fx-font-size: 12; -fx-text-fill: #666666;");
		infoLabel.setWrapText(true);
		
		TextArea resolutionArea = new TextArea();
		resolutionArea.setPromptText("Enter resolution notes (required, max 2000 characters)");
		resolutionArea.setPrefRowCount(10);
		resolutionArea.setPrefWidth(500);
		resolutionArea.setWrapText(true);
		
		content.getChildren().addAll(infoLabel, resolutionArea);
		dialog.getDialogPane().setContent(content);
		dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
		
		Optional<ButtonType> result = dialog.showAndWait();
		if (result.isPresent() && result.get() == ButtonType.OK) {
			String resolutionNotes = resolutionArea.getText().trim();
			String currentUsername = ViewAdminHome.theUser.getUserName();
			
			guiStaff.ModelStaffHome.refreshRequestsFromDatabase();
			RequestCollection requests = guiStaff.ModelStaffHome.getRequestCollection();
			String error = requests.closeRequest(request.getRequestId(), currentUsername, resolutionNotes);
			
			Alert alert = new Alert(Alert.AlertType.INFORMATION);
			alert.setTitle("Close Request");
			if (error.isEmpty()) {
				// Save to database
				Request updatedRequest = requests.getRequestById(request.getRequestId());
				guiStaff.ModelStaffHome.saveRequestToDatabase(updatedRequest);
				
				alert.setHeaderText("Request Closed");
				alert.setContentText("Request #" + request.getRequestId() + " has been closed successfully.");
				alert.showAndWait();
			} else {
				alert.setAlertType(Alert.AlertType.ERROR);
				alert.setHeaderText("Close Failed");
				alert.setContentText(error);
				alert.showAndWait();
			}
		}
	}
}
