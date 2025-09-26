package guiUserLogin;

import database.Database;
import entityClasses.User;
import javafx.stage.Stage;

public class ControllerUserLogin {
	
	/*-********************************************************************************************

	The User Interface Actions for this page
	
	This controller is not a class that gets instantiated.  Rather, it is a collection of protected
	static methods that can be called by the View (which is a singleton instantiated object) and 
	the Model is often just a stub, or will be a singleton instantiated object.
	
	*/


	// Reference for the in-memory database so this package has access
	private static Database theDatabase = applicationMain.FoundationsMain.database;

	private static Stage theStage;	
	
	/**********
	 * <p> Method: public doLogin() </p>
	 * 
	 * <p> Description: This method is called when the user has clicked on the Login button. This
	 * method checks the username and password to see if they are valid.  If so, it then logs that
	 * user in my determining which role to use.
	 * 
	 * The method reaches batch to the view page and to fetch the information needed rather than
	 * passing that information as parameters.
	 * 
	 */	
	protected static void doLogin(Stage ts) {
		theStage = ts;
		String username = ViewUserLogin.text_Username.getText();
		String password = ViewUserLogin.text_Password.getText();
    	boolean loginResult = false;
    	
		// Fetch the user and verify the username
     	if (theDatabase.getUserAccountDetails(username) == false) {
     		// Don't provide too much information.  Don't say the username is invalid or the
     		// password is invalid.  Just say the pair is invalid.
    		ViewUserLogin.alertUsernamePasswordError.setContentText(
    				"Incorrect username/password. Try again!");
    		ViewUserLogin.alertUsernamePasswordError.showAndWait();
    		return;
    	}
		System.out.println("*** Username is valid");
		
		// Check to see that the login password matches the account password OR the one-time password
        String actualPassword = theDatabase.getCurrentPassword();
        String otp = theDatabase.getOneTimePassword(username);
        boolean usingOtp = (otp != null && otp.length() > 0 && password.compareTo(otp) == 0);
        boolean usingNormal = (password.compareTo(actualPassword) == 0);
        if (!usingOtp && !usingNormal) {
            ViewUserLogin.alertUsernamePasswordError.setContentText(
                    "Incorrect username/password. Try again!");
            ViewUserLogin.alertUsernamePasswordError.showAndWait();
            return;
        }
        System.out.println(usingOtp ? " One-Time Password accepted" : " Password is valid for this user");
		
		// Establish this user's details
    	User user = new User(username, password, theDatabase.getCurrentFirstName(), 
    			theDatabase.getCurrentMiddleName(), theDatabase.getCurrentLastName(), 
    			theDatabase.getCurrentPreferredFirstName(), theDatabase.getCurrentEmailAddress(), 
    			theDatabase.getCurrentAdminRole(), 
    			theDatabase.getCurrentNewStudent(), theDatabase.getCurrentNewStaff());
    	
    	// If using OTP, force the user to set a new password, then clear OTP and ret urn to login
        if (usingOtp) {
            while (true) {
            	javafx.scene.control.Dialog<String> newPwdDialog = new javafx.scene.control.Dialog<String>();
                newPwdDialog.setTitle("Set New Password");
                newPwdDialog.setHeaderText("Enter a new password for your account");
                javafx.scene.control.ButtonType okBtn = new javafx.scene.control.ButtonType("OK", javafx.scene.control.ButtonBar.ButtonData.OK_DONE);
                javafx.scene.control.ButtonType cancelBtn = new javafx.scene.control.ButtonType("Cancel", javafx.scene.control.ButtonBar.ButtonData.CANCEL_CLOSE);
                newPwdDialog.getDialogPane().getButtonTypes().addAll(okBtn, cancelBtn);
                javafx.scene.control.PasswordField pf = new javafx.scene.control.PasswordField();
                pf.setPromptText("New Password");
                newPwdDialog.getDialogPane().setContent(pf);
                newPwdDialog.setResultConverter(dialogButton -> {
                    if (dialogButton == okBtn) return pf.getText();
                    return null;
                });

                java.util.Optional<String> newPwdResult = newPwdDialog.showAndWait();
                if (!newPwdResult.isPresent()) {
                    return; // user cancelled
                }
                String newPwd = newPwdResult.get();
                String validation = PasswordEvaluator.PasswordEvaluator.evaluatePassword(newPwd);
                if (validation != null && validation.length() > 0) {
                    javafx.scene.control.Alert err = new javafx.scene.control.Alert(
                        javafx.scene.control.Alert.AlertType.INFORMATION);
                    err.setTitle("Invalid Password");
                    err.setHeaderText("Password does not meet requirements");
                    err.setContentText(validation);
                    err.showAndWait();
                    continue;
                }
                // Valid password
                theDatabase.updatePassword(username, newPwd);
                theDatabase.clearOneTimePassword(username);
                javafx.scene.control.Alert info = new javafx.scene.control.Alert(
                    javafx.scene.control.Alert.AlertType.INFORMATION);
                info.setTitle("Password Updated");
                info.setHeaderText("Password updated successfully");
                info.setContentText("Please log in again with your new password.");
                info.showAndWait();
                // Return to login screen
                guiUserLogin.ViewUserLogin.displayUserLogin(theStage);
                return;
            }
        }

        // See which home page dispatch to use (normal login)
    	
    	// See which home page dispatch to use
		int numberOfRoles = theDatabase.getNumberOfRoles(user);		
		System.out.println("*** The number of roles: "+ numberOfRoles);
		if (numberOfRoles == 1) {
			// Single Account Home Page - The user has no choice here
			
			// Admin role
			if (user.getAdminRole()) {
				loginResult = theDatabase.loginAdmin(user);
				if (loginResult) {
					guiAdminHome.ViewAdminHome.displayAdminHome(theStage, user);
				}
			} else if (user.getNewStudent()) {
				loginResult = theDatabase.loginStudent(user);
				if (loginResult) {
					guiStudent.ViewStudentHome.displayStudentHome(theStage, user);
				}
			} else if (user.getNewStaff()) {
				loginResult = theDatabase.loginStaff(user);
				if (loginResult) {
					guiStaff.ViewStaffHome.displayStaffHome(theStage, user);
				}
				// Other roles
			} else {
				System.out.println("***** UserLogin goToUserHome request has an invalid role");
			}
		} else if (numberOfRoles > 1) {
			// Multiple Account Home Page - The user chooses which role to play
			System.out.println("*** Going to displayMultipleRoleDispatch");
			guiMultipleRoleDispatch.ViewMultipleRoleDispatch.
				displayMultipleRoleDispatch(theStage, user);
		}
	}
	
		
	/**********
	 * <p> Method: setup() </p>
	 * 
	 * <p> Description: This method is called to reset the page and then populate it with new
	 * content.</p>
	 * 
	 */
	protected static void doSetupAccount(Stage theStage, String invitationCode) {
		guiNewAccount.ViewNewAccount.displayNewAccount(theStage, invitationCode);
	}

	
	/**********
	 * <p> Method: public performQuit() </p>
	 * 
	 * <p> Description: This method is called when the user has clicked on the Quit button.  Doing
	 * this terminates the execution of the application.  All important data must be stored in the
	 * database, so there is no cleanup required.  (This is important so we can minimize the impact
	 * of crashed.)
	 * 
	 */	
	protected static void performQuit() {
		System.out.println("Perform Quit");
		System.exit(0);
	}	

}
