package guiStudent;

import java.util.List;
import java.util.Optional;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.Insets;
import entityClasses.Post;
import entityClasses.Reply;
import entityClasses.PostCollection;
import entityClasses.ReplyCollection;

public class ControllerStudentHome {

	/*-*******************************************************************************************

	User Interface Actions for this page
	
	This controller is not a class that gets instantiated.  Rather, it is a collection of protected
	static methods that can be called by the View (which is a singleton instantiated object) and 
	the Model is often just a stub, or will be a singleton instantiated object.
	
	 */

	
 	/**********
	 * <p> Method: performLogout() </p>
	 * 
	 * <p> Description: This method logs out the current user and proceeds to the normal login
	 * page where existing users can log in or potential new users with a invitation code can
	 * start the process of setting up an account. </p>
	 * 
	 */
	protected static void performLogout() {
		guiUserLogin.ViewUserLogin.displayUserLogin(ViewStudentHome.theStage);
	}
	
	
	/**********
	 * <p> Method: performQuit() </p>
	 * 
	 * <p> Description: This method terminates the execution of the program.  It leaves the
	 * database in a state where the normal login page will be displayed when the application is
	 * restarted.</p>
	 * 
	 */	
	protected static void performQuit() {
		System.exit(0);
	}
	
	/**********
	 * <p> Method: createPost() </p>
	 * 
	 * <p> Description: Creates a new post with title, body, and thread assignment.</p>
	 * 
	 */
	protected static void createPost() {
		PostCollection posts = ModelStudentHome.getPostCollection();
		String currentUsername = ViewStudentHome.theUser.getUserName();
		
		// Create dialog for post creation
		Dialog<ButtonType> dialog = new Dialog<>();
		dialog.setTitle("Create Post");
		dialog.setHeaderText("Create a new post");
		
		// Set up dialog content
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(20, 150, 10, 10));
		
		TextField titleField = new TextField();
		titleField.setPromptText("Title (max 100 characters)");
		titleField.setPrefWidth(400);
		
		TextArea bodyArea = new TextArea();
		bodyArea.setPromptText("Body (max 5000 characters)");
		bodyArea.setPrefRowCount(10);
		bodyArea.setPrefWidth(400);
		bodyArea.setWrapText(true);
		
		TextField threadField = new TextField();
		threadField.setPromptText("Thread (default: General)");
		threadField.setText("General");
		
		grid.add(new Label("Title:"), 0, 0);
		grid.add(titleField, 1, 0);
		grid.add(new Label("Body:"), 0, 1);
		grid.add(bodyArea, 1, 1);
		grid.add(new Label("Thread:"), 0, 2);
		grid.add(threadField, 1, 2);
		
		dialog.getDialogPane().setContent(grid);
		dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
		
		Optional<ButtonType> result = dialog.showAndWait();
		if (result.isPresent() && result.get() == ButtonType.OK) {
			String title = titleField.getText();
			String body = bodyArea.getText();
			String thread = threadField.getText();
			
			String postIdOrError = posts.createPost(title, body, currentUsername, thread);
			
			Alert alert = new Alert(Alert.AlertType.INFORMATION);
			alert.setTitle("Create Post");
			if (postIdOrError.startsWith("POST_")) {
				// Save to database
				Post newPost = posts.getPostById(postIdOrError);
				ModelStudentHome.savePostToDatabase(newPost);
				
				alert.setHeaderText("Post Created");
				alert.setContentText("Your post has been created successfully.");
			} else {
				alert.setHeaderText("Error");
				alert.setContentText(postIdOrError);
			}
			alert.showAndWait();
		}
	}
	
	
	/**********
	 * <p> Method: searchPosts() </p>
	 * 
	 * <p> Description: Searches for posts with keywords.</p>
	 * 
	 */
	protected static void searchPosts() {
		PostCollection posts = ModelStudentHome.getPostCollection();
		ReplyCollection replies = ModelStudentHome.getReplyCollection();
		
		// Create search dialog
		TextInputDialog dialog = new TextInputDialog();
		dialog.setTitle("Search Posts");
		dialog.setHeaderText("Search posts by keyword");
		dialog.setContentText("Enter keyword:");
		
		Optional<String> result = dialog.showAndWait();
		if (result.isPresent()) {
			String keyword = result.get();
			
			if (keyword.trim().isEmpty()) {
				Alert alert = new Alert(Alert.AlertType.WARNING);
				alert.setTitle("Search Posts");
				alert.setHeaderText("No Keyword Entered");
				alert.setContentText("Please enter a keyword to search.");
				alert.showAndWait();
				return;
			}
			
			List<Post> foundPosts = posts.searchPosts(keyword, "All");
			displayPostList(foundPosts, replies, "Search Results");
		}
	}
	
	
	/**********
	 * <p> Method: viewMyPosts() </p>
	 * 
	 * <p> Description: Shows the current user's posts with reply counts.</p>
	 * 
	 */
	protected static void viewMyPosts() {
		PostCollection posts = ModelStudentHome.getPostCollection();
		ReplyCollection replies = ModelStudentHome.getReplyCollection();
		String currentUsername = ViewStudentHome.theUser.getUserName();
		
		List<Post> myPosts = posts.getPostsByAuthor(currentUsername);
		displayPostList(myPosts, replies, "My Posts");
	}
	
	
	/**********
	 * <p> Method: viewAllPosts() </p>
	 * 
	 * <p> Description: Shows all posts in the forum.</p>
	 * 
	 */
	protected static void viewAllPosts() {
		PostCollection posts = ModelStudentHome.getPostCollection();
		ReplyCollection replies = ModelStudentHome.getReplyCollection();
		
		List<Post> allPosts = posts.getAllPosts();
		displayPostList(allPosts, replies, "All Posts");
	}
	
	
	/**********
	 * <p> Method: displayPostList() </p>
	 * 
	 * <p> Description: Helper method to display a list of posts.</p>
	 * 
	 */
	private static void displayPostList(List<Post> postList, ReplyCollection replies, String title) {
		String currentUsername = ViewStudentHome.theUser.getUserName();
		
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
			int unreadCount = replies.getUnreadReplyCountForPost(post.getPostId(), currentUsername);
			
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
						
			Label replyLabel = new Label("Replies: " + replyCount + 
				(unreadCount > 0 ? " (" + unreadCount + " unread)" : ""));
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
			viewBtn.setOnAction(e -> viewPostDetails(post.getPostId()));
						
			Button editBtn = new Button("Edit");
			editBtn.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
			editBtn.setOnAction(e -> editPost(post.getPostId()));
			editBtn.setDisable(!post.canEdit(currentUsername));
						
			Button deleteBtn = new Button("Delete");
			deleteBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
			deleteBtn.setOnAction(e -> deletePost(post.getPostId()));
			deleteBtn.setDisable(!post.canDelete(currentUsername));
						
			buttonBox.getChildren().addAll(viewBtn, editBtn, deleteBtn);
						
			// Add all elements to the post card
			postCard.getChildren().addAll(headerBox, threadLabel, replyLabel, bodyLabel, buttonBox);
			postContainer.getChildren().add(postCard);
		}
		
		scrollPane.setContent(postContainer);
		scrollPane.setPrefSize(800, 600);
		scrollPane.setFitToWidth(true);
		
		Dialog<Void> dialog = new Dialog<>();
		dialog.setTitle(title);
		dialog.setHeaderText(postList.size() + " post(s) found - Use the buttons to interact with posts");
		dialog.getDialogPane().setContent(scrollPane);
		
		ButtonType closeBtn = new ButtonType("Close", ButtonBar.ButtonData.CANCEL_CLOSE);
		dialog.getDialogPane().getButtonTypes().add(closeBtn);
		dialog.showAndWait();
	}
	
	
	/**********
	 * <p> Method: viewPostDetails() </p>
	 * 
	 * <p> Description: Displays full post details with replies.</p>
	 * 
	 */
	private static void viewPostDetails(String postId) {
		PostCollection posts = ModelStudentHome.getPostCollection();
		ReplyCollection replies = ModelStudentHome.getReplyCollection();
		String currentUsername = ViewStudentHome.theUser.getUserName();
		
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
		Button editPostBtn = new Button("Edit Post");
		editPostBtn.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
		editPostBtn.setOnAction(e -> editPost(postId));
		editPostBtn.setDisable(!post.canEdit(currentUsername));
				
		Button deletePostBtn = new Button("Delete Post");
		deletePostBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
		deletePostBtn.setOnAction(e -> deletePost(postId));
		deletePostBtn.setDisable(!post.canDelete(currentUsername));
				
		Button addReplyBtn = new Button("Add Reply");
		addReplyBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
		addReplyBtn.setOnAction(e -> createReply(postId));
				
		postButtonBox.getChildren().addAll(editPostBtn, deletePostBtn, addReplyBtn);
		postSection.getChildren().add(postButtonBox);
				
		mainContainer.getChildren().add(postSection);
				
		// Replies section
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
				replyDate.setStyle("-fx-font-size: 11; -fx-text-fill: #666666;");
				if (reply.isUnread()) {
					Label unreadLabel = new Label("[UNREAD]");
					unreadLabel.setStyle("-fx-font-size: 11; -fx-text-fill: #ff6b6b; -fx-font-weight: bold;");
					replyHeader.getChildren().addAll(replyAuthor, replyDate, unreadLabel);
				} else {
					replyHeader.getChildren().addAll(replyAuthor, replyDate);
				}
				
				// Reply body
				Label replyBody = new Label(reply.getDisplayBody());
				replyBody.setWrapText(true);
				
				// Reply action buttons
				HBox replyButtonBox = new HBox(10);
				Button editReplyBtn = new Button("Edit");
				editReplyBtn.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 11;");
				editReplyBtn.setOnAction(e -> editReply(reply.getReplyId()));
				editReplyBtn.setDisable(!reply.canEdit(currentUsername));
				
				Button deleteReplyBtn = new Button("Delete");
				deleteReplyBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-size: 11;");
				deleteReplyBtn.setOnAction(e -> deleteReply(reply.getReplyId()));
				deleteReplyBtn.setDisable(!reply.canDelete(currentUsername));
				
				replyButtonBox.getChildren().addAll(editReplyBtn, deleteReplyBtn);
				
				replyCard.getChildren().addAll(replyHeader, replyBody, replyButtonBox);
				repliesContainer.getChildren().add(replyCard);
				
				// Mark as read when viewing
				if (reply.isUnread()) {
					reply.markAsRead();
					ModelStudentHome.saveReplyToDatabase(reply);
				}
			}
			
			repliesScrollPane.setContent(repliesContainer);
			repliesScrollPane.setPrefSize(800, 300);
			repliesScrollPane.setFitToWidth(true);
			mainContainer.getChildren().add(repliesScrollPane);
		} else {
			Label noRepliesLabel = new Label("No replies yet.");
			noRepliesLabel.setStyle("-fx-font-style: italic; -fx-text-fill: #666666;");
			mainContainer.getChildren().add(noRepliesLabel);
		}
		
		// Create scrollable container for the entire content
		ScrollPane mainScrollPane = new ScrollPane();
		mainScrollPane.setContent(mainContainer);
		mainScrollPane.setPrefSize(850, 600);
		mainScrollPane.setFitToWidth(true);
		
		Dialog<Void> dialog = new Dialog<>();
		dialog.setTitle("Post Details");
		dialog.setHeaderText("Post ID: " + postId);
		dialog.getDialogPane().setContent(mainScrollPane);
		
		ButtonType closeBtn = new ButtonType("Close", ButtonBar.ButtonData.CANCEL_CLOSE);
		dialog.getDialogPane().getButtonTypes().add(closeBtn);
		dialog.showAndWait();
	}
	
	
	/**********
	 * <p> Method: editPost() </p>
	 * 
	 * <p> Description: Edits an existing post.</p>
	 * 
	 */
	private static void editPost(String postId) {
		PostCollection posts = ModelStudentHome.getPostCollection();
		String currentUsername = ViewStudentHome.theUser.getUserName();
		
		Post post = posts.getPostById(postId);
		if (post == null || !post.canEdit(currentUsername)) {
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setTitle("Edit Post");
			alert.setHeaderText("Cannot Edit Post");
			alert.setContentText("You can only edit your own posts.");
			alert.showAndWait();
			return;
		}
		
		// Create edit dialog
		Dialog<ButtonType> dialog = new Dialog<>();
		dialog.setTitle("Edit Post");
		dialog.setHeaderText("Edit your post");
		
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(20, 150, 10, 10));
		
		TextField titleField = new TextField(post.getTitle());
		titleField.setPrefWidth(400);
		
		TextArea bodyArea = new TextArea(post.getBody());
		bodyArea.setPrefRowCount(10);
		bodyArea.setPrefWidth(400);
		bodyArea.setWrapText(true);
		
		grid.add(new Label("Title:"), 0, 0);
		grid.add(titleField, 1, 0);
		grid.add(new Label("Body:"), 0, 1);
		grid.add(bodyArea, 1, 1);
		
		dialog.getDialogPane().setContent(grid);
		dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
		
		Optional<ButtonType> result = dialog.showAndWait();
		if (result.isPresent() && result.get() == ButtonType.OK) {
			String newTitle = titleField.getText();
			String newBody = bodyArea.getText();
			
			String error = posts.updatePost(postId, newTitle, newBody, currentUsername);
			
			Alert alert = new Alert(Alert.AlertType.INFORMATION);
			alert.setTitle("Edit Post");
			if (error.isEmpty()) {
				// Save to database
				Post updatedPost = posts.getPostById(postId);
				ModelStudentHome.savePostToDatabase(updatedPost);
				
				alert.setHeaderText("Post Updated");
				alert.setContentText("Your post has been updated successfully.");
			} else {
				alert.setHeaderText("Error");
				alert.setContentText(error);
			}
			alert.showAndWait();
		}
	}
	
	
	/**********
	 * <p> Method: deletePost() </p>
	 * 
	 * <p> Description: Deletes a post after confirmation.</p>
	 * 
	 */
	private static void deletePost(String postId) {
		PostCollection posts = ModelStudentHome.getPostCollection();
		String currentUsername = ViewStudentHome.theUser.getUserName();
		
		Post post = posts.getPostById(postId);
		if (post == null || !post.canDelete(currentUsername)) {
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setTitle("Delete Post");
			alert.setHeaderText("Cannot Delete Post");
			alert.setContentText("You can only delete your own posts.");
			alert.showAndWait();
			return;
		}
		
		// Confirmation dialog
		Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
		confirm.setTitle("Delete Post");
		confirm.setHeaderText("Are you sure?");
		confirm.setContentText("Do you want to delete this post? Replies will remain but show that the original post was deleted.");
		
		Optional<ButtonType> result = confirm.showAndWait();
		if (result.isPresent() && result.get() == ButtonType.OK) {
			String error = posts.deletePost(postId, currentUsername);
			
			Alert alert = new Alert(Alert.AlertType.INFORMATION);
			alert.setTitle("Delete Post");
			if (error.isEmpty()) {
				// Save to database
				Post deletedPost = posts.getPostById(postId);
				ModelStudentHome.savePostToDatabase(deletedPost);
				
				alert.setHeaderText("Post Deleted");
				alert.setContentText("Your post has been deleted.");
			} else {
				alert.setHeaderText("Error");
				alert.setContentText(error);
			}
			alert.showAndWait();
		}
	}
	
	
	/**********
	 * <p> Method: createReply() </p>
	 * 
	 * <p> Description: Creates a reply to a post.</p>
	 * 
	 */
	private static void createReply(String postId) {
		ReplyCollection replies = ModelStudentHome.getReplyCollection();
		String currentUsername = ViewStudentHome.theUser.getUserName();
		
		TextInputDialog dialog = new TextInputDialog();
		dialog.setTitle("Add Reply");
		dialog.setHeaderText("Reply to post " + postId);
		dialog.setContentText("Your reply (max 3000 characters):");
		dialog.getEditor().setPrefColumnCount(50);
		
		Optional<String> result = dialog.showAndWait();
		if (result.isPresent()) {
			String body = result.get();
			
			String replyIdOrError = replies.createReply(body, currentUsername, postId);
			
			Alert alert = new Alert(Alert.AlertType.INFORMATION);
			alert.setTitle("Add Reply");
			if (replyIdOrError.startsWith("REPLY_")) {
				// Save to database
				Reply newReply = replies.getReplyById(replyIdOrError);
				ModelStudentHome.saveReplyToDatabase(newReply);
				
				alert.setHeaderText("Reply Added");
				alert.setContentText("Your reply has been added successfully.");
			} else {
				alert.setHeaderText("Error");
				alert.setContentText(replyIdOrError);
			}
			alert.showAndWait();
		}
	}
	
	/**********
	 * <p> Method: editReply() </p>
	 * 
	 * <p> Description: Edits an existing reply.</p>
	 * 
	 */
	private static void editReply(String replyId) {
		ReplyCollection replies = ModelStudentHome.getReplyCollection();
		String currentUsername = ViewStudentHome.theUser.getUserName();
		
		Reply reply = replies.getReplyById(replyId);
		
		if (reply == null) {
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setTitle("Edit Reply");
			alert.setHeaderText("Reply Not Found");
			alert.setContentText("No reply found with ID: " + replyId);
			alert.showAndWait();
			return;
		}
		
		if (!reply.canEdit(currentUsername)) {
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setTitle("Edit Reply");
			alert.setHeaderText("Cannot Edit Reply");
			alert.setContentText("You can only edit your own replies.");
			alert.showAndWait();
			return;
		}
		
		// Create edit dialog
		Dialog<ButtonType> dialog = new Dialog<>();
		dialog.setTitle("Edit Reply");
		dialog.setHeaderText("Edit your reply");
		
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(20, 150, 10, 10));
		
		TextArea bodyArea = new TextArea(reply.getBody());
		bodyArea.setPrefRowCount(10);
		bodyArea.setPrefWidth(400);
		bodyArea.setWrapText(true);
		
		grid.add(new Label("Body:"), 0, 0);
		grid.add(bodyArea, 1, 0);
		
		dialog.getDialogPane().setContent(grid);
		dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
		
		Optional<ButtonType> result = dialog.showAndWait();
		if (result.isPresent() && result.get() == ButtonType.OK) {
			String newBody = bodyArea.getText();
			
			String error = replies.updateReply(replyId, newBody, currentUsername);
			
			Alert alert = new Alert(Alert.AlertType.INFORMATION);
			alert.setTitle("Edit Reply");
			if (error.isEmpty()) {
				// Save to database
				Reply updatedReply = replies.getReplyById(replyId);
				ModelStudentHome.saveReplyToDatabase(updatedReply);
				
				alert.setHeaderText("Reply Updated");
				alert.setContentText("Your reply has been updated successfully.");
			} else {
				alert.setHeaderText("Error");
				alert.setContentText(error);
			}
			alert.showAndWait();
		}
	}
	
	
	/**********
	 * <p> Method: deleteReply() </p>
	 * 
	 * <p> Description: Deletes a reply after confirmation.</p>
	 * 
	 */
	private static void deleteReply(String replyId) {
		ReplyCollection replies = ModelStudentHome.getReplyCollection();
		String currentUsername = ViewStudentHome.theUser.getUserName();
		
		Reply reply = replies.getReplyById(replyId);
		
		if (reply == null) {
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setTitle("Delete Reply");
			alert.setHeaderText("Reply Not Found");
			alert.setContentText("No reply found with ID: " + replyId);
			alert.showAndWait();
			return;
		}
		
		if (!reply.canDelete(currentUsername)) {
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setTitle("Delete Reply");
			alert.setHeaderText("Cannot Delete Reply");
			alert.setContentText("You can only delete your own replies.");
			alert.showAndWait();
			return;
		}
		
		// Confirmation dialog
		Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
		confirm.setTitle("Delete Reply");
		confirm.setHeaderText("Are you sure?");
		confirm.setContentText("Do you want to delete this reply?");
		
		Optional<ButtonType> result = confirm.showAndWait();
		if (result.isPresent() && result.get() == ButtonType.OK) {
			String error = replies.deleteReply(replyId, currentUsername);
			
			Alert alert = new Alert(Alert.AlertType.INFORMATION);
			alert.setTitle("Delete Reply");
			if (error.isEmpty()) {
				// Save to database
				Reply deletedReply = replies.getReplyById(replyId);
				ModelStudentHome.saveReplyToDatabase(deletedReply);
				
				alert.setHeaderText("Reply Deleted");
				alert.setContentText("Your reply has been deleted.");
			} else {
				alert.setHeaderText("Error");
				alert.setContentText(error);
			}
			alert.showAndWait();
		}
	}
}
