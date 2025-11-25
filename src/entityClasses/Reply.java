package entityClasses;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/*******
 * <p> Title: Reply Class </p>
 * 
 * <p> Description: This Reply class represents a forum reply entity in the system. It contains the reply's
 * details such as body, author, parent post, timestamp, and status. It supports all CRUD operations
 * and validation as specified in the requirements.</p>
 * 
 * <p> Copyright: Joseph © 2025 </p>
 * 
 * @author Joseph
 * 
 * @version 1.00		2025-01-16 Initial version
 */ 

public class Reply {
	
	/*
	 * These are the private attributes for this entity object
	 */
    private String replyId;
    private String body;
    private String authorUsername;
    private String parentPostId;
    private LocalDateTime createdAt;
    private LocalDateTime lastEditedAt;
    private boolean isDeleted;
    private boolean isRead;
    private boolean isFeedback; // True if this is private feedback from staff
    
    // Constants for validation
    public static final int MAX_BODY_LENGTH = 3000;
    
    /*****
     * <p> Method: Reply() </p>
     * 
     * <p> Description: This default constructor is not used in this system. </p>
     */
    public Reply() {
    	
    }

    /*****
     * <p> Method: Reply(String replyId, String body, String authorUsername, String parentPostId) </p>
     * 
     * <p> Description: This constructor is used to establish reply entity objects. </p>
     * 
     * @param replyId specifies the unique identifier for this reply
     * 
     * @param body specifies the content/body of the reply
     * 
     * @param authorUsername specifies the username of the reply author
     * 
     * @param parentPostId specifies the ID of the post this reply belongs to
     * 
     */
    public Reply(String replyId, String body, String authorUsername, String parentPostId) {
        this.replyId = replyId;
        this.body = body;
        this.authorUsername = authorUsername;
        this.parentPostId = parentPostId;
        this.createdAt = LocalDateTime.now();
        this.lastEditedAt = null;
        this.isDeleted = false;
        this.isRead = false;
        this.isFeedback = false;
    }
    
    /*****
     * <p> Method: Reply(String replyId, String body, String authorUsername, String parentPostId, boolean isFeedback) </p>
     * 
     * <p> Description: Constructor for creating a reply with feedback flag. </p>
     * 
     * @param replyId specifies the unique identifier for this reply
     * @param body specifies the content/body of the reply
     * @param authorUsername specifies the username of the reply author
     * @param parentPostId specifies the ID of the post this reply belongs to
     * @param isFeedback specifies if this is private feedback from staff
     * 
     */
    public Reply(String replyId, String body, String authorUsername, String parentPostId, boolean isFeedback) {
        this.replyId = replyId;
        this.body = body;
        this.authorUsername = authorUsername;
        this.parentPostId = parentPostId;
        this.createdAt = LocalDateTime.now();
        this.lastEditedAt = null;
        this.isDeleted = false;
        this.isRead = false;
        this.isFeedback = isFeedback;
    }

    /*****
     * <p> Method: String validateReply() </p>
     * 
     * <p> Description: Validates the reply data according to the requirements. </p>
     * 
     * @return empty string if valid, error message if invalid
     * 
     */
    public String validateReply() {
        if (body == null || body.trim().isEmpty()) {
            return "Reply body cannot be empty.";
        }
        
        if (body.length() > MAX_BODY_LENGTH) {
            return "Reply body cannot exceed " + MAX_BODY_LENGTH + " characters.";
        }
        
        if (parentPostId == null || parentPostId.trim().isEmpty()) {
            return "Reply must reference an existing post.";
        }
        
        return ""; // Valid
    }

    /*****
     * <p> Method: boolean canEdit(String currentUsername) </p>
     * 
     * <p> Description: Checks if the current user can edit this reply. </p>
     * 
     * @param currentUsername the username of the current user
     * 
     * @return true if the user can edit this reply, false otherwise
     * 
     */
    public boolean canEdit(String currentUsername) {
        return !isDeleted && authorUsername.equals(currentUsername);
    }

    /*****
     * <p> Method: boolean canDelete(String currentUsername) </p>
     * 
     * <p> Description: Checks if the current user can delete this reply. </p>
     * 
     * @param currentUsername the username of the current user
     * 
     * @return true if the user can delete this reply, false otherwise
     * 
     */
    public boolean canDelete(String currentUsername) {
        return !isDeleted && authorUsername.equals(currentUsername);
    }

    /*****
     * <p> Method: void markAsDeleted() </p>
     * 
     * <p> Description: Marks the reply as deleted. </p>
     * 
     */
    public void markAsDeleted() {
        this.isDeleted = true;
    }

    /*****
     * <p> Method: void updateContent(String newBody) </p>
     * 
     * <p> Description: Updates the reply content and sets the edit timestamp. </p>
     * 
     * @param newBody the new body content for the reply
     * 
     */
    public void updateContent(String newBody) {
        this.body = newBody;
        this.lastEditedAt = LocalDateTime.now();
    }

    /*****
     * <p> Method: void markAsRead() </p>
     * 
     * <p> Description: Marks the reply as read. </p>
     * 
     */
    public void markAsRead() {
        this.isRead = true;
    }

    /*****
     * <p> Method: void markAsUnread() </p>
     * 
     * <p> Description: Marks the reply as unread. </p>
     * 
     */
    public void markAsUnread() {
        this.isRead = false;
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

    /*****
     * <p> Method: String getDisplayBody() </p>
     * 
     * <p> Description: Returns the body text to display, accounting for deleted status. </p>
     * 
     * @return the body text or deletion message
     * 
     */
    public String getDisplayBody() {
        if (isDeleted) {
            return "This reply has been deleted";
        }
        return body;
    }

    /*****
     * <p> Method: boolean isUnread() </p>
     * 
     * <p> Description: Checks if the reply is unread. </p>
     * 
     * @return true if the reply is unread, false otherwise
     * 
     */
    public boolean isUnread() {
        return !isRead;
    }

    // Getters and Setters
    public String getReplyId() { 
        return replyId; 
    }

    public void setReplyId(String replyId) { 
        this.replyId = replyId; 
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

    public String getParentPostId() { 
        return parentPostId; 
    }

    public void setParentPostId(String parentPostId) { 
        this.parentPostId = parentPostId; 
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

    public boolean isRead() { 
        return isRead; 
    }

    public void setRead(boolean read) { 
        this.isRead = read; 
    }
    
    public boolean isFeedback() {
        return isFeedback;
    }
    
    public void setFeedback(boolean feedback) {
        this.isFeedback = feedback;
    }
}
