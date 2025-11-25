package entityClasses;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/*******
 * <p> Title: Thread Class </p>
 * 
 * <p> Description: This Thread class represents a discussion thread entity in the system. It contains
 * the thread's details such as title, description, status (Open/Closed), creator, timestamp, and
 * creation date. Threads are created by staff members to organize discussion topics for students.</p>
 * 
 * <p> Copyright: Joseph © 2025 </p>
 * 
 * @author Joseph
 * 
 * @version 1.00		2025-01-16 Initial version
 */ 

public class Thread {
	
	/*
	 * These are the private attributes for this entity object
	 */
    private String threadId;
    private String title;
    private String description;
    private ThreadStatus status;
    private String createdByUsername;
    private LocalDateTime createdAt;
    
    // Constants for validation
    public static final int MAX_TITLE_LENGTH = 100;
    public static final int MAX_DESCRIPTION_LENGTH = 500;
    
    // Thread status enum
    public enum ThreadStatus {
        OPEN, CLOSED
    }
    
    /*****
     * <p> Method: Thread() </p>
     * 
     * <p> Description: This default constructor is not used in this system. </p>
     */
    public Thread() {
    	
    }

    /*****
     * <p> Method: Thread(String threadId, String title, String description, String createdByUsername) </p>
     * 
     * <p> Description: This constructor is used to establish thread entity objects. </p>
     * 
     * @param threadId specifies the unique identifier for this thread
     * @param title specifies the title of the thread
     * @param description specifies the description/content of the thread
     * @param createdByUsername specifies the username of the staff member who created the thread
     * 
     */
    public Thread(String threadId, String title, String description, String createdByUsername) {
        this.threadId = threadId;
        this.title = title;
        this.description = description;
        this.createdByUsername = createdByUsername;
        this.status = ThreadStatus.OPEN;
        this.createdAt = LocalDateTime.now();
    }

    /*****
     * <p> Method: String validateThread() </p>
     * 
     * <p> Description: Validates the thread data according to the requirements. </p>
     * 
     * @return empty string if valid, error message if invalid
     * 
     */
    public String validateThread() {
        if (title == null || title.trim().isEmpty()) {
            return "Thread title cannot be empty.";
        }
        
        if (description == null || description.trim().isEmpty()) {
            return "Thread description cannot be empty.";
        }
        
        if (title.length() > MAX_TITLE_LENGTH) {
            return "Thread title cannot exceed " + MAX_TITLE_LENGTH + " characters.";
        }
        
        if (description.length() > MAX_DESCRIPTION_LENGTH) {
            return "Thread description cannot exceed " + MAX_DESCRIPTION_LENGTH + " characters.";
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

    // Getters and Setters
    public String getThreadId() { 
        return threadId; 
    }

    public void setThreadId(String threadId) { 
        this.threadId = threadId; 
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

    public ThreadStatus getStatus() { 
        return status; 
    }

    public void setStatus(ThreadStatus status) { 
        this.status = status; 
    }
    
    public String getStatusString() {
        return status == ThreadStatus.OPEN ? "Open" : "Closed";
    }
    
    public boolean isOpen() {
        return status == ThreadStatus.OPEN;
    }
    
    public boolean isClosed() {
        return status == ThreadStatus.CLOSED;
    }

    public String getCreatedByUsername() { 
        return createdByUsername; 
    }

    public void setCreatedByUsername(String createdByUsername) { 
        this.createdByUsername = createdByUsername; 
    }

    public LocalDateTime getCreatedAt() { 
        return createdAt; 
    }

    public void setCreatedAt(LocalDateTime createdAt) { 
        this.createdAt = createdAt; 
    }
}

