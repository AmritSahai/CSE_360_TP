package entityClasses;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/*******
 * <p> Title: Request Class </p>
 * 
 * <p> Description: This Request class represents an admin request entity in the system. It contains
 * the request's details such as ID, title, description, category, status (Open/Closed), creator,
 * resolver, resolution notes, and timestamps. Staff members create requests for admins to resolve.</p>
 * 
 * <p> Copyright: Joseph © 2025 </p>
 * 
 * @author Joseph
 * 
 * @version 1.00		2025-01-16 Initial version
 */ 

public class Request {
	
	/*
	 * These are the private attributes for this entity object
	 */
    private String requestId;
    private String title;
    private String description;
    private RequestCategory category;
    private RequestStatus status;
    private String createdByUsername;
    private String closedByUsername;
    private String resolutionNotes;
    private String reopenReason;
    private String originalRequestId; // Link to original request if this is a reopened request
    private LocalDateTime createdAt;
    private LocalDateTime closedAt;
    private LocalDateTime reopenedAt;
    
    // Constants for validation
    public static final int MAX_TITLE_LENGTH = 200;
    public static final int MAX_DESCRIPTION_LENGTH = 2000;
    public static final int MAX_RESOLUTION_NOTES_LENGTH = 2000;
    public static final int MAX_REOPEN_REASON_LENGTH = 1000;
    
    // Request category enum
    public enum RequestCategory {
        SYSTEM_ISSUE("System Issue"),
        ACCOUNT_ISSUE("Account Issue"),
        PARAMETER_ISSUE("Parameter Issue"),
        GRADING_QUESTION("Grading Question"),
        OTHER("Other");
        
        private final String displayName;
        
        RequestCategory(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    // Request status enum
    public enum RequestStatus {
        OPEN, CLOSED
    }
    
    /*****
     * <p> Method: Request() </p>
     * 
     * <p> Description: This default constructor is not used in this system. </p>
     */
    public Request() {
    	
    }

    /*****
     * <p> Method: Request(String requestId, String title, String description, RequestCategory category, String createdByUsername) </p>
     * 
     * <p> Description: This constructor is used to establish request entity objects. </p>
     * 
     * @param requestId specifies the unique identifier for this request
     * @param title specifies the title of the request
     * @param description specifies the description/content of the request
     * @param category specifies the category of the request
     * @param createdByUsername specifies the username of the staff member who created the request
     * 
     */
    public Request(String requestId, String title, String description, RequestCategory category, String createdByUsername) {
        this.requestId = requestId;
        this.title = title;
        this.description = description;
        this.category = category;
        this.createdByUsername = createdByUsername;
        this.status = RequestStatus.OPEN;
        this.closedByUsername = null;
        this.resolutionNotes = null;
        this.reopenReason = null;
        this.originalRequestId = null;
        this.createdAt = LocalDateTime.now();
        this.closedAt = null;
        this.reopenedAt = null;
    }

    /*****
     * <p> Method: String validateRequest() </p>
     * 
     * <p> Description: Validates the request data according to the requirements. </p>
     * 
     * @return empty string if valid, error message if invalid
     * 
     */
    public String validateRequest() {
        if (title == null || title.trim().isEmpty()) {
            return "Request title cannot be empty.";
        }
        
        if (description == null || description.trim().isEmpty()) {
            return "Request description cannot be empty. Please explain what you need help with and what you have done so far.";
        }
        
        if (category == null) {
            return "Request category must be selected.";
        }
        
        if (title.length() > MAX_TITLE_LENGTH) {
            return "Request title cannot exceed " + MAX_TITLE_LENGTH + " characters.";
        }
        
        if (description.length() > MAX_DESCRIPTION_LENGTH) {
            return "Request description cannot exceed " + MAX_DESCRIPTION_LENGTH + " characters.";
        }
        
        return ""; // Valid
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
     * <p> Method: String getFormattedClosedAt() </p>
     * 
     * <p> Description: Returns a formatted string of the close timestamp. </p>
     * 
     * @return formatted close timestamp or "Not closed" if not closed
     * 
     */
    public String getFormattedClosedAt() {
        if (closedAt == null) {
            return "Not closed";
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return closedAt.format(formatter);
    }

    /*****
     * <p> Method: String getFormattedReopenedAt() </p>
     * 
     * <p> Description: Returns a formatted string of the reopen timestamp. </p>
     * 
     * @return formatted reopen timestamp or "Not reopened" if not reopened
     * 
     */
    public String getFormattedReopenedAt() {
        if (reopenedAt == null) {
            return "Not reopened";
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return reopenedAt.format(formatter);
    }

    // Getters and Setters
    public String getRequestId() { 
        return requestId; 
    }

    public void setRequestId(String requestId) { 
        this.requestId = requestId; 
    }

    public String getTitle() { 
        return title; 
    }

    public void setTitle(String title) { 
        this.title = title; 
    }

    public String getDescription() { 
        return description; 
    }

    public void setDescription(String description) { 
        this.description = description; 
    }

    public RequestCategory getCategory() { 
        return category; 
    }

    public void setCategory(RequestCategory category) { 
        this.category = category; 
    }
    
    public String getCategoryString() {
        return category != null ? category.getDisplayName() : "";
    }

    public RequestStatus getStatus() { 
        return status; 
    }

    public void setStatus(RequestStatus status) { 
        this.status = status; 
    }
    
    public String getStatusString() {
        return status == RequestStatus.OPEN ? "Open" : "Closed";
    }
    
    public boolean isOpen() {
        return status == RequestStatus.OPEN;
    }
    
    public boolean isClosed() {
        return status == RequestStatus.CLOSED;
    }

    public String getCreatedByUsername() { 
        return createdByUsername; 
    }

    public void setCreatedByUsername(String createdByUsername) { 
        this.createdByUsername = createdByUsername; 
    }

    public String getClosedByUsername() { 
        return closedByUsername; 
    }

    public void setClosedByUsername(String closedByUsername) { 
        this.closedByUsername = closedByUsername; 
    }

    public String getResolutionNotes() { 
        return resolutionNotes; 
    }

    public void setResolutionNotes(String resolutionNotes) { 
        this.resolutionNotes = resolutionNotes; 
    }

    public String getReopenReason() { 
        return reopenReason; 
    }

    public void setReopenReason(String reopenReason) { 
        this.reopenReason = reopenReason; 
    }

    public String getOriginalRequestId() { 
        return originalRequestId; 
    }

    public void setOriginalRequestId(String originalRequestId) { 
        this.originalRequestId = originalRequestId; 
    }
    
    public boolean isReopened() {
        return originalRequestId != null;
    }

    public LocalDateTime getCreatedAt() { 
        return createdAt; 
    }

    public void setCreatedAt(LocalDateTime createdAt) { 
        this.createdAt = createdAt; 
    }

    public LocalDateTime getClosedAt() { 
        return closedAt; 
    }

    public void setClosedAt(LocalDateTime closedAt) { 
        this.closedAt = closedAt; 
    }

    public LocalDateTime getReopenedAt() { 
        return reopenedAt; 
    }

    public void setReopenedAt(LocalDateTime reopenedAt) { 
        this.reopenedAt = reopenedAt; 
    }
}

