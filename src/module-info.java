module FoundationsF25 {
	requires javafx.controls;
	requires javafx.graphics;
	requires javafx.fxml;
	requires java.sql;
	
	opens applicationMain to javafx.graphics, javafx.fxml;
	opens guiFirstAdmin to javafx.graphics, javafx.fxml;
	opens guiUserLogin to javafx.graphics, javafx.fxml;
	opens guiAdminHome to javafx.graphics, javafx.fxml;
	opens guiNewAccount to javafx.graphics, javafx.fxml;
	opens guiUserUpdate to javafx.graphics, javafx.fxml;
	opens guiAddRemoveRoles to javafx.graphics, javafx.fxml;
	opens guiMultipleRoleDispatch to javafx.graphics, javafx.fxml;
	opens guiRole1 to javafx.graphics, javafx.fxml;
	opens guiRole2 to javafx.graphics, javafx.fxml;
	opens guiTools to javafx.graphics, javafx.fxml;
}
