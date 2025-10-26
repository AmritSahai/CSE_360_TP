package entityClasses;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/*******
 * <p> Title: Reply Class </p>
 * 
 * <p> Description: The Reply class represents a response posted to an existing discussion post. 
 * It stores reply content, author information, parent post linkage, timestamps, and read/deletion status. </p>
 * 
 * <p> The Reply class supports all CRUD operations (Create, Read, Update, Delete) for replies, including 
 * input validation, editing, marking as deleted, and marking as read or unread. It enables the 
 * student features related to replying to posts, editing one’s replies, deleting replies, and tracking 
 * unread messages for staff and peer review. </p>
 * 
 * <p> Copyright: Joseph © 2025 </p>
 * 
 * @author Joseph and Vrishik
 * 
 * @version 2.00		2025-10-19 TP2 Updated Javadoc Version
 */ 

public class Reply {
	
	/*
	 * These are the private attributes for this entity object
	 */
	
    /** Unique identifier for this reply, used for database and UI tracking. */
    private String replyId;
    
    /** The body text of the reply. Represents the student's response content. */
    private String body;
    
    /** Username of the student who authored the reply. Used for permission checking. */
    private String authorUsername;
    
    /** ID of the parent post that this reply belongs to. Links replies to specific discussions. */
    private String parentPostId;
    
    /** Timestamp recording when this reply was first created. */
    private LocalDateTime createdAt;
    
    /** Timestamp for the most recent edit to this reply. Null if never edited. */
    private LocalDateTime lastEditedAt;
    
    /** Boolean flag indicating whether the reply has been marked as deleted. */
    private boolean isDeleted;
    
    /** Boolean flag indicating whether this reply has been marked as read by staff or peers. */
    private boolean isRead;
    
    // Constants for validation
    
    /** Maximum allowed number of characters in a reply body. */
    public static final int MAX_BODY_LENGTH = 3000;
    
    /*****
     * <p> Method: Reply() </p>
     * 
     * <p> Description: Default constructor (not used in this implementation). </p>
     */
    public Reply() {
    	
    }

    /*****
     * <p> Method: Reply(String replyId, String body, String authorUsername, String parentPostId) </p>
     * 
     * <p> Description: Constructs a new Reply object with the given data and initializes timestamps. </p>
     * 
     * @param replyId the unique identifier for this reply
     * @param body the content/body of the reply
     * @param authorUsername the username of the reply’s author
     * @param parentPostId the ID of the post that this reply is linked to
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
    }

    /*****
     * <p> Method: validateReply() </p>
     * 
     * <p> Description: Validates the reply’s input fields against system constraints. 
     * Ensures replies are not empty, exceed character limits, or reference nonexistent posts. 
     * Supports the student feature that allows posting valid replies with appropriate error messages. </p>
     * 
     * @return an empty string if valid; otherwise, a descriptive error message
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
     * <p> Method: canEdit(String currentUsername) </p>
     * 
     * <p> Description: Determines whether the specified user has permission to edit this reply. 
     * Supports the student feature that allows editing only one’s own replies. </p>
     * 
     * @param currentUsername the username of the user attempting to edit
     * @return true if the user can edit; false otherwise
     */
    public boolean canEdit(String currentUsername) {
        return !isDeleted && authorUsername.equals(currentUsername);
    }

    /*****
     * <p> Method: canDelete(String currentUsername) </p>
     * 
     * <p> Description: Determines whether the specified user has permission to delete this reply. 
     * Supports the student feature that allows deleting only one’s own replies. </p>
     * 
     * @param currentUsername the username of the user attempting deletion
     * @return true if the user can delete; false otherwise
     */
    public boolean canDelete(String currentUsername) {
        return !isDeleted && authorUsername.equals(currentUsername);
    }

    /*****
     * <p> Method: markAsDeleted() </p>
     * 
     * <p> Description: Marks this reply as deleted. The reply remains in storage for audit purposes 
     * and displays a placeholder message instead of the original content. </p>
     */
    public void markAsDeleted() {
        this.isDeleted = true;
    }

    /*****
     * <p> Method: updateContent(String newBody) </p>
     * 
     * <p> Description: Updates the reply body with the given content and records the time of edit. 
     * Supports the student feature that allows editing existing replies. </p>
     * 
     * @param newBody the updated reply content
     */
    public void updateContent(String newBody) {
        this.body = newBody;
        this.lastEditedAt = LocalDateTime.now();
    }

    /*****
     * <p> Method: markAsRead() </p>
     * 
     * <p> Description: Marks this reply as read. Used for tracking which replies have been reviewed 
     * by staff or other users. </p>
     */
    public void markAsRead() {
        this.isRead = true;
    }

    /*****
     * <p> Method: markAsUnread() </p>
     * 
     * <p> Description: Marks this reply as unread. Used when resetting read status for monitoring. </p>
     */
    public void markAsUnread() {
        this.isRead = false;
    }

    /*****
     * <p> Method: getFormattedCreatedAt() </p>
     * 
     * <p> Description: Returns a formatted date-time string of when the reply was created. </p>
     * 
     * @return formatted creation timestamp
     */
    public String getFormattedCreatedAt() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return createdAt.format(formatter);
    }

    /*****
     * <p> Method: getFormattedLastEditedAt() </p>
     * 
     * <p> Description: Returns a formatted string representing the time of the last edit. 
     * Returns “Never” if the reply has not been edited. </p>
     * 
     * @return formatted edit timestamp or "Never" if not edited
     */
    public String getFormattedLastEditedAt() {
        if (lastEditedAt == null) {
            return "Never";
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return lastEditedAt.format(formatter);
    }

    /*****
     * <p> Method: getDisplayBody() </p>
     * 
     * <p> Description: Returns the reply body for display. 
     * If the reply is deleted, returns a placeholder message instead of the original content. </p>
     * 
     * @return the visible reply text or a deletion message
     */
    public String getDisplayBody() {
        if (isDeleted) {
            return "This reply has been deleted";
        }
        return body;
    }

    /*****
     * <p> Method: isUnread() </p>
     * 
     * <p> Description: Determines whether the reply is marked as unread. </p>
     * 
     * @return true if unread; false otherwise
     */
    public boolean isUnread() {
        return !isRead;
    }

 // Getters and Setters

    /** 
     * Returns the unique ID assigned to this reply.
     * 
     * @return the reply ID
     */
    public String getReplyId() { 
        return replyId; 
    }

    /** 
     * Sets the unique ID for this reply.
     * 
     * @param replyId the ID to assign
     */
    public void setReplyId(String replyId) { 
        this.replyId = replyId; 
    }

    /** 
     * Returns the body of this reply.
     * 
     * @return the reply body text
     */
    public String getBody() { 
        return body; 
    }

    /** 
     * Sets the main body of this reply.
     * 
     * @param body the content text to assign to this reply
     */
    public void setBody(String body) { 
        this.body = body; 
    }

    /** 
     * Returns the username of the author who created this reply.
     * 
     * @return the author’s username
     */
    public String getAuthorUsername() { 
        return authorUsername; 
    }

    /** 
     * Sets the username of the author who created this reply.
     * 
     * @param authorUsername the author’s username to assign
     */
    public void setAuthorUsername(String authorUsername) { 
        this.authorUsername = authorUsername; 
    }

    /** 
     * Returns the ID of the parent post that this reply is linked to.
     * 
     * @return the parent post ID
     */
    public String getParentPostId() { 
        return parentPostId; 
    }

    /** 
     * Sets the ID of the parent post that this reply is linked to.
     * 
     * @param parentPostId the parent post ID to assign
     */
    public void setParentPostId(String parentPostId) { 
        this.parentPostId = parentPostId; 
    }

    /** 
     * Returns the date and time when this reply was created.
     * 
     * @return the creation timestamp
     */
    public LocalDateTime getCreatedAt() { 
        return createdAt; 
    }

    /** 
     * Sets the creation timestamp for this reply.
     * 
     * @param createdAt the creation time to assign
     */
    public void setCreatedAt(LocalDateTime createdAt) { 
        this.createdAt = createdAt; 
    }

    /** 
     * Returns the date and time when this reply was last edited.
     * 
     * @return the last edit time, or null if never edited
     */
    public LocalDateTime getLastEditedAt() { 
        return lastEditedAt; 
    }

    /** 
     * Sets the timestamp for when this reply was last edited.
     * 
     * @param lastEditedAt the edit time to assign
     */
    public void setLastEditedAt(LocalDateTime lastEditedAt) { 
        this.lastEditedAt = lastEditedAt; 
    }

    /** 
     * Checks whether this reply has been marked as deleted.
     * 
     * @return true if the reply is deleted, false otherwise
     */
    public boolean isDeleted() { 
        return isDeleted; 
    }

    /** 
     * Sets the deletion status of this reply.
     * 
     * @param deleted true to mark the reply as deleted, false otherwise
     */
    public void setDeleted(boolean deleted) { 
        this.isDeleted = deleted; 
    }

    /** 
     * Checks whether this reply has been marked as read.
     * 
     * @return true if the reply is read, false otherwise
     */
    public boolean isRead() { 
        return isRead; 
    }

    /** 
     * Sets the read status of this reply.
     * 
     * @param read true to mark the reply as read, false otherwise
     */
    public void setRead(boolean read) { 
        this.isRead = read; 
    }
}