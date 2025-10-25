package entityClasses;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/*******
 * <p> Title: Post Class </p>
 * 
 * <p> Description: This Post class represents a forum post entity in the system. It contains the post's
 * details such as title, body, author, thread, timestamp, and status. It supports all CRUD operations
 * and validation as specified in the requirements.</p>
 * 
 * <p> Copyright: Joseph © 2025 </p>
 * 
 * @author Joseph
 * 
 * @version 1.00		2025-01-16 Initial version
 */ 

public class Post {
	
	/*
	 * These are the private attributes for this entity object
	 */
    private String postId;
    private String title;
    private String body;
    private String authorUsername;
    private String thread;
    private LocalDateTime createdAt;
    private LocalDateTime lastEditedAt;
    private boolean isDeleted;
    
    // Constants for validation
    public static final int MAX_TITLE_LENGTH = 100;
    public static final int MAX_BODY_LENGTH = 5000;
    public static final String DEFAULT_THREAD = "General";
    
    /*****
     * <p> Method: Post() </p>
     * 
     * <p> Description: This default constructor is not used in this system. </p>
     */
    public Post() {
    	
    }

    /*****
     * <p> Method: Post(String postId, String title, String body, String authorUsername, String thread) </p>
     * 
     * <p> Description: This constructor is used to establish post entity objects. </p>
     * 
     * @param postId specifies the unique identifier for this post
     * 
     * @param title specifies the title of the post
     * 
     * @param body specifies the content/body of the post
     * 
     * @param authorUsername specifies the username of the post author
     * 
     * @param thread specifies the thread this post belongs to
     * 
     */
    public Post(String postId, String title, String body, String authorUsername, String thread) {
        this.postId = postId;
        this.title = title;
        this.body = body;
        this.authorUsername = authorUsername;
        this.thread = (thread != null && !thread.trim().isEmpty()) ? thread : DEFAULT_THREAD;
        this.createdAt = LocalDateTime.now();
        this.lastEditedAt = null;
        this.isDeleted = false;
    }

    /*****
     * <p> Method: String validatePost() </p>
     * 
     * <p> Description: Validates the post data according to the requirements. </p>
     * 
     * @return empty string if valid, error message if invalid
     * 
     */
    public String validatePost() {
        if (title == null || title.trim().isEmpty()) {
            return "Post title cannot be empty.";
        }
        
        if (body == null || body.trim().isEmpty()) {
            return "Post body cannot be empty.";
        }
        
        if (title.length() > MAX_TITLE_LENGTH) {
            return "Post title cannot exceed " + MAX_TITLE_LENGTH + " characters.";
        }
        
        if (body.length() > MAX_BODY_LENGTH) {
            return "Post body cannot exceed " + MAX_BODY_LENGTH + " characters.";
        }
        
        return ""; // Valid
    }

    /*****
     * <p> Method: boolean canEdit(String currentUsername) </p>
     * 
     * <p> Description: Checks if the current user can edit this post. </p>
     * 
     * @param currentUsername the username of the current user
     * 
     * @return true if the user can edit this post, false otherwise
     * 
     */
    public boolean canEdit(String currentUsername) {
        return !isDeleted && authorUsername.equals(currentUsername);
    }

    /*****
     * <p> Method: boolean canDelete(String currentUsername) </p>
     * 
     * <p> Description: Checks if the current user can delete this post. </p>
     * 
     * @param currentUsername the username of the current user
     * 
     * @return true if the user can delete this post, false otherwise
     * 
     */
    public boolean canDelete(String currentUsername) {
        return !isDeleted && authorUsername.equals(currentUsername);
    }

    /*****
     * <p> Method: void markAsDeleted() </p>
     * 
     * <p> Description: Marks the post as deleted. </p>
     * 
     */
    public void markAsDeleted() {
        this.isDeleted = true;
    }

    /*****
     * <p> Method: void updateContent(String newTitle, String newBody) </p>
     * 
     * <p> Description: Updates the post content and sets the edit timestamp. </p>
     * 
     * @param newTitle the new title for the post
     * @param newBody the new body content for the post
     * 
     */
    public void updateContent(String newTitle, String newBody) {
        this.title = newTitle;
        this.body = newBody;
        this.lastEditedAt = LocalDateTime.now();
    }

    /*****
     * <p> Method: boolean matchesSearch(String keyword) </p>
     * 
     * <p> Description: Checks if the post matches a search keyword. </p>
     * 
     * @param keyword the search keyword
     * 
     * @return true if the post matches the keyword, false otherwise
     * 
     */
    public boolean matchesSearch(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return true; // Empty keyword matches all
        }
        
        String lowerKeyword = keyword.toLowerCase();
        return (title != null && title.toLowerCase().contains(lowerKeyword)) ||
               (body != null && body.toLowerCase().contains(lowerKeyword));
    }

    /*****
     * <p> Method: String getFormattedCreatedAt() </p>
     * 
     * <p> Description: Returns a formatted string of the creation timestamp. </p>
     * 
     * @return formatted creation timestamp
     * 
     */
    public String getFormattedCreatedAt() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return createdAt.format(formatter);
    }

    /*****
     * <p> Method: String getFormattedLastEditedAt() </p>
     * 
     * <p> Description: Returns a formatted string of the last edit timestamp. </p>
     * 
     * @return formatted edit timestamp or "Never" if not edited
     * 
     */
    public String getFormattedLastEditedAt() {
        if (lastEditedAt == null) {
            return "Never";
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return lastEditedAt.format(formatter);
    }

    // Getters and Setters
    public String getPostId() { 
        return postId; 
    }

    public void setPostId(String postId) { 
        this.postId = postId; 
    }

    public String getTitle() { 
        return title; 
    }

    public void setTitle(String title) { 
        this.title = title; 
    }

    public String getBody() { 
        return body; 
    }

    public void setBody(String body) { 
        this.body = body; 
    }

    public String getAuthorUsername() { 
        return authorUsername; 
    }

    public void setAuthorUsername(String authorUsername) { 
        this.authorUsername = authorUsername; 
    }

    public String getThread() { 
        return thread; 
    }

    public void setThread(String thread) { 
        this.thread = thread; 
    }

    public LocalDateTime getCreatedAt() { 
        return createdAt; 
    }

    public void setCreatedAt(LocalDateTime createdAt) { 
        this.createdAt = createdAt; 
    }

    public LocalDateTime getLastEditedAt() { 
        return lastEditedAt; 
    }

    public void setLastEditedAt(LocalDateTime lastEditedAt) { 
        this.lastEditedAt = lastEditedAt; 
    }

    public boolean isDeleted() { 
        return isDeleted; 
    }

    public void setDeleted(boolean deleted) { 
        this.isDeleted = deleted; 
    }
}