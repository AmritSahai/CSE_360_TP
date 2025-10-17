package guiStudent;

import java.util.List;
import java.util.Optional;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
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
		
		StringBuilder sb = new StringBuilder();
		for (Post post : postList) {
			int replyCount = replies.getReplyCountForPost(post.getPostId());
			int unreadCount = replies.getUnreadReplyCountForPost(post.getPostId(), currentUsername);
			
			sb.append("━".repeat(60)).append("\n");
			sb.append("ID: ").append(post.getPostId()).append("\n");
			sb.append("Title: ").append(post.getTitle()).append("\n");
			sb.append("Author: ").append(post.getAuthorUsername()).append("\n");
			sb.append("Thread: ").append(post.getThread()).append("\n");
			sb.append("Created: ").append(post.getFormattedCreatedAt()).append("\n");
			if (post.getLastEditedAt() != null) {
				sb.append("Edited: ").append(post.getFormattedLastEditedAt()).append("\n");
			}
			sb.append("Replies: ").append(replyCount);
			if (unreadCount > 0) {
				sb.append(" (").append(unreadCount).append(" unread)");
			}
			sb.append("\n");
			
			if (post.isDeleted()) {
				sb.append("[DELETED POST]\n");
			} else {
				String preview = post.getBody().length() > 100 ? 
					post.getBody().substring(0, 100) + "..." : post.getBody();
				sb.append("Body: ").append(preview).append("\n");
			}
			sb.append("\n");
		}
		
		TextArea textArea = new TextArea(sb.toString());
		textArea.setEditable(false);
		textArea.setWrapText(true);
		textArea.setPrefRowCount(20);
		textArea.setPrefColumnCount(60);
		
		Dialog<Void> dialog = new Dialog<>();
		dialog.setTitle(title);
		dialog.setHeaderText(postList.size() + " post(s) found - Click on a post ID to view details");
		dialog.getDialogPane().setContent(textArea);
		
		// Use "Continue" button instead of "Close"
		ButtonType continueBtn = new ButtonType("Continue", ButtonBar.ButtonData.OK_DONE);
		dialog.getDialogPane().getButtonTypes().add(continueBtn);
		dialog.showAndWait();
		
		// Prompt to view a specific post
		promptViewPost();
	}
	
	
	/**********
	 * <p> Method: promptViewPost() </p>
	 * 
	 * <p> Description: Prompts user to view a specific post.</p>
	 * 
	 */
	private static void promptViewPost() {
		TextInputDialog dialog = new TextInputDialog();
		dialog.setTitle("View Post");
		dialog.setHeaderText("View post details");
		dialog.setContentText("Enter Post ID:");
		
		Optional<String> result = dialog.showAndWait();
		if (result.isPresent() && !result.get().trim().isEmpty()) {
			viewPostDetails(result.get().trim());
		}
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
		
		Post post = posts.getPostById(postId);
		if (post == null) {
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setTitle("View Post");
			alert.setHeaderText("Post Not Found");
			alert.setContentText("No post found with ID: " + postId);
			alert.showAndWait();
			return;
		}
		
		// Build post details
		StringBuilder sb = new StringBuilder();
		sb.append("Title: ").append(post.getTitle()).append("\n");
		sb.append("Author: ").append(post.getAuthorUsername()).append("\n");
		sb.append("Thread: ").append(post.getThread()).append("\n");
		sb.append("Created: ").append(post.getFormattedCreatedAt()).append("\n");
		if (post.getLastEditedAt() != null) {
			sb.append("Edited: ").append(post.getFormattedLastEditedAt()).append("\n");
		}
		sb.append("\n");
		
		if (post.isDeleted()) {
			sb.append("[Original post deleted]\n\n");
		} else {
			sb.append(post.getBody()).append("\n\n");
		}
		
		// Get replies
		List<Reply> replyList = replies.getRepliesForPost(postId);
		sb.append("═".repeat(60)).append("\n");
		sb.append("REPLIES (").append(replyList.size()).append(")\n");
		sb.append("═".repeat(60)).append("\n\n");
		
		if (replyList.isEmpty()) {
			sb.append("No replies yet.\n");
		} else {
			for (Reply reply : replyList) {
				sb.append("━".repeat(40)).append("\n");
				sb.append("Reply ID: ").append(reply.getReplyId()).append("\n");
				sb.append("Author: ").append(reply.getAuthorUsername());
				if (reply.isUnread()) {
					sb.append(" [UNREAD]");
				}
				sb.append("\n");
				sb.append("Posted: ").append(reply.getFormattedCreatedAt()).append("\n");
				if (reply.getLastEditedAt() != null) {
					sb.append("Edited: ").append(reply.getFormattedLastEditedAt()).append("\n");
				}
				sb.append("\n");
			sb.append(reply.getDisplayBody()).append("\n\n");
			
			// Mark as read when viewing (both your own replies and others' replies)
			if (reply.isUnread()) {
				reply.markAsRead();
				// Save read status to database
				ModelStudentHome.saveReplyToDatabase(reply);
			}
		}
	}
		
		TextArea textArea = new TextArea(sb.toString());
		textArea.setEditable(false);
		textArea.setWrapText(true);
		textArea.setPrefRowCount(25);
		textArea.setPrefColumnCount(70);
		
		Dialog<ButtonType> dialog = new Dialog<>();
		dialog.setTitle("Post Details");
		dialog.setHeaderText("Post ID: " + postId);
		dialog.getDialogPane().setContent(textArea);
		
		// Add action buttons
		ButtonType editPostBtn = new ButtonType("Edit Post", ButtonBar.ButtonData.OTHER);
		ButtonType deletePostBtn = new ButtonType("Delete Post", ButtonBar.ButtonData.OTHER);
		ButtonType replyBtn = new ButtonType("Add Reply", ButtonBar.ButtonData.OTHER);
		ButtonType editReplyBtn = new ButtonType("Edit Reply", ButtonBar.ButtonData.OTHER);
		ButtonType deleteReplyBtn = new ButtonType("Delete Reply", ButtonBar.ButtonData.OTHER);
		ButtonType closeBtn = new ButtonType("Close", ButtonBar.ButtonData.CANCEL_CLOSE);
		
		dialog.getDialogPane().getButtonTypes().addAll(editPostBtn, deletePostBtn, replyBtn, 
				editReplyBtn, deleteReplyBtn, closeBtn);
		
		Optional<ButtonType> result = dialog.showAndWait();
		if (result.isPresent()) {
			if (result.get() == editPostBtn) {
				editPost(postId);
			} else if (result.get() == deletePostBtn) {
				deletePost(postId);
			} else if (result.get() == replyBtn) {
				createReply(postId);
			} else if (result.get() == editReplyBtn) {
				editReply();
			} else if (result.get() == deleteReplyBtn) {
				deleteReply();
			}
		}
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
	private static void editReply() {
		ReplyCollection replies = ModelStudentHome.getReplyCollection();
		String currentUsername = ViewStudentHome.theUser.getUserName();
		
		// Prompt for reply ID
		TextInputDialog idDialog = new TextInputDialog();
		idDialog.setTitle("Edit Reply");
		idDialog.setHeaderText("Enter the Reply ID to edit");
		idDialog.setContentText("Reply ID:");
		
		Optional<String> idResult = idDialog.showAndWait();
		if (!idResult.isPresent() || idResult.get().trim().isEmpty()) {
			return;
		}
		
		String replyId = idResult.get().trim();
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
	private static void deleteReply() {
		ReplyCollection replies = ModelStudentHome.getReplyCollection();
		String currentUsername = ViewStudentHome.theUser.getUserName();
		
		// Prompt for reply ID
		TextInputDialog idDialog = new TextInputDialog();
		idDialog.setTitle("Delete Reply");
		idDialog.setHeaderText("Enter the Reply ID to delete");
		idDialog.setContentText("Reply ID:");
		
		Optional<String> idResult = idDialog.showAndWait();
		if (!idResult.isPresent() || idResult.get().trim().isEmpty()) {
			return;
		}
		
		String replyId = idResult.get().trim();
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
