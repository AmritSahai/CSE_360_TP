/**
 * Module: FoundationsF25
 * Description: This module defines the main entry point and module dependencies 
 * for this application.
 */
module FoundationsF25 {
	requires javafx.controls;
	requires java.sql;
	
	opens applicationMain to javafx.graphics, javafx.fxml;
}