package entityClasses;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/*******
 * <p> Title: Parameter Class </p>
 * 
 * <p> Description: This Parameter class represents a grading parameter entity that staff can use
 * to grade student discussions. It contains the parameter's name, description, min/max values,
 * weight, and active status with validation as specified in the requirements.</p>
 * 
 * <p> Copyright: Joseph © 2025 </p>
 * 
 * @author Joseph
 * 
 * @version 1.00		2025-01-16 Initial version
 */ 

public class Parameter {
	
	/*
	 * These are the private attributes for this entity object
	 */
    private String parameterId;
    private String name;
    private String description;
    private boolean isActive;
    private String createdByUsername; // Staff member who created it
    private LocalDateTime createdAt;
    
    // New fields for User Story #1
    private int requiredPosts; // Required amount of posts for students
    private int requiredReplies; // Required amount of replies for students
    private List<String> topics; // Topics students should focus on
    private String threadId; // Thread this parameter is associated with (required)
    private List<ParameterCategory> categories; // Categories with weights (0.0 - 1.0)
    
    // Constants for validation
    public static final int MAX_NAME_LENGTH = 100;
    public static final int MAX_DESCRIPTION_LENGTH = 500;
    public static final int MAX_TOPICS = 20; // Maximum number of topics
    public static final int MAX_TOPIC_LENGTH = 100; // Maximum length per topic
    public static final int MAX_CATEGORIES = 20; // Maximum number of categories
    
    /*****
     * <p> Method: Parameter() </p>
     * 
     * <p> Description: This default constructor is not used in this system. </p>
     */
    public Parameter() {
    	this.categories = new ArrayList<>();
    	this.topics = new ArrayList<>();
    }

    /*****
     * <p> Method: Parameter(String parameterId, String name, String description, int minValue, 
     * int maxValue, double weight, boolean isActive, String createdByUsername) </p>
     * 
     * <p> Description: This constructor is used to establish parameter entity objects. </p>
     * 
     * @param parameterId specifies the unique identifier for this parameter
     * @param name specifies the name of the parameter
     * @param description specifies the description of the parameter
     * @param minValue specifies the minimum value (0-100)
     * @param maxValue specifies the maximum value (0-100)
     * @param weight specifies the weight (0.0 - 1.0)
     * @param isActive specifies if the parameter is active
     * @param createdByUsername specifies the username of the staff who created it
     * 
     */
    public Parameter(String parameterId, String name, String description, 
                    boolean isActive, String createdByUsername) {
        this.parameterId = parameterId;
        this.name = name;
        this.description = description;
        this.isActive = isActive;
        this.createdByUsername = createdByUsername;
        this.createdAt = LocalDateTime.now();
        this.requiredPosts = 0;
        this.requiredReplies = 0;
        this.topics = new ArrayList<>();
        this.threadId = null;
        this.categories = new ArrayList<>();
    }
    
    /*****
     * <p> Method: Parameter(String parameterId, String name, String description, 
     * boolean isActive, String createdByUsername, int requiredPosts,
     * int requiredReplies, List&lt;String&gt; topics, String threadId, List&lt;ParameterCategory&gt; categories) </p>
     * 
     * <p> Description: Extended constructor with all fields including thread association, participation requirements, and categories. </p>
     * 
     * @param parameterId specifies the unique identifier for this parameter
     * @param name specifies the name of the parameter
     * @param description specifies the description of the parameter
     * @param isActive specifies if the parameter is active
     * @param createdByUsername specifies the username of the staff who created it
     * @param requiredPosts specifies the required number of posts for students
     * @param requiredReplies specifies the required number of replies for students
     * @param topics specifies the list of topics students should focus on
     * @param threadId specifies the thread this parameter is associated with
     * @param categories specifies the list of categories with weights
     * 
     */
    public Parameter(String parameterId, String name, String description, 
                    boolean isActive, String createdByUsername,
                    int requiredPosts, int requiredReplies, List<String> topics, String threadId, List<ParameterCategory> categories) {
        this.parameterId = parameterId;
        this.name = name;
        this.description = description;
        this.isActive = isActive;
        this.createdByUsername = createdByUsername;
        this.createdAt = LocalDateTime.now();
        this.requiredPosts = requiredPosts;
        this.requiredReplies = requiredReplies;
        this.topics = (topics != null) ? new ArrayList<>(topics) : new ArrayList<>();
        this.threadId = threadId;
        this.categories = (categories != null) ? new ArrayList<>(categories) : new ArrayList<>();
    }

    /*****
     * <p> Method: String validateParameter() </p>
     * 
     * <p> Description: Validates the parameter data according to the requirements. </p>
     * 
     * @return empty string if valid, error message if invalid
     * 
     */
    public String validateParameter() {
        // Check if name is empty or null
        if (name == null || name.trim().isEmpty()) {
            return "Parameter name cannot be empty.";
        }
        
        // Check if description is empty or null
        if (description == null || description.trim().isEmpty()) {
            return "Parameter description cannot be empty.";
        }
        
        // Check name length
        if (name.length() > MAX_NAME_LENGTH) {
            return "Parameter name cannot exceed " + MAX_NAME_LENGTH + " characters.";
        }
        
        // Check description length
        if (description.length() > MAX_DESCRIPTION_LENGTH) {
            return "Parameter description cannot exceed " + MAX_DESCRIPTION_LENGTH + " characters.";
        }
        
        // Check required posts (must be >= 0)
        if (requiredPosts < 0) {
            return "Required posts must be 0 or greater.";
        }
        
        // Check required replies (must be >= 0)
        if (requiredReplies < 0) {
            return "Required replies must be 0 or greater.";
        }
        
        // Check topics
        if (topics != null) {
            if (topics.size() > MAX_TOPICS) {
                return "Cannot have more than " + MAX_TOPICS + " topics.";
            }
            for (String topic : topics) {
                if (topic != null && topic.length() > MAX_TOPIC_LENGTH) {
                    return "Each topic cannot exceed " + MAX_TOPIC_LENGTH + " characters.";
                }
            }
        }
        
        // Check threadId is required
        if (threadId == null || threadId.trim().isEmpty()) {
            return "Thread selection is required.";
        }
        
        // Check categories
        if (categories == null || categories.isEmpty()) {
            return "At least one category is required.";
        }
        
        if (categories.size() > MAX_CATEGORIES) {
            return "Cannot have more than " + MAX_CATEGORIES + " categories.";
        }
        
        // Validate each category
        for (ParameterCategory category : categories) {
            String error = category.validateCategory();
            if (!error.isEmpty()) {
                return error;
            }
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
    
    /*****
     * <p> Method: String getParameterId() </p>
     * 
     * <p> Description: Returns the unique identifier for this parameter. </p>
     * 
     * @return the parameter ID
     */
    public String getParameterId() { 
        return parameterId; 
    }

    /*****
     * <p> Method: void setParameterId(String parameterId) </p>
     * 
     * <p> Description: Sets the unique identifier for this parameter. </p>
     * 
     * @param parameterId the parameter ID to set
     */
    public void setParameterId(String parameterId) { 
        this.parameterId = parameterId; 
    }

    /*****
     * <p> Method: String getName() </p>
     * 
     * <p> Description: Returns the name of this parameter. </p>
     * 
     * @return the parameter name
     */
    public String getName() { 
        return name; 
    }

    /*****
     * <p> Method: void setName(String name) </p>
     * 
     * <p> Description: Sets the name of this parameter. </p>
     * 
     * @param name the parameter name to set (must not exceed MAX_NAME_LENGTH characters)
     */
    public void setName(String name) { 
        this.name = name; 
    }

    /*****
     * <p> Method: String getDescription() </p>
     * 
     * <p> Description: Returns the description of this parameter. </p>
     * 
     * @return the parameter description
     */
    public String getDescription() { 
        return description; 
    }

    /*****
     * <p> Method: void setDescription(String description) </p>
     * 
     * <p> Description: Sets the description of this parameter. </p>
     * 
     * @param description the parameter description to set (must not exceed MAX_DESCRIPTION_LENGTH characters)
     */
    public void setDescription(String description) { 
        this.description = description; 
    }



    /*****
     * <p> Method: boolean isActive() </p>
     * 
     * <p> Description: Returns whether this parameter is currently active. </p>
     * 
     * @return true if the parameter is active, false otherwise
     */
    public boolean isActive() { 
        return isActive; 
    }

    /*****
     * <p> Method: void setActive(boolean isActive) </p>
     * 
     * <p> Description: Sets the active status of this parameter. </p>
     * 
     * @param isActive true to mark the parameter as active, false to mark it as inactive
     */
    public void setActive(boolean isActive) { 
        this.isActive = isActive; 
    }

    /*****
     * <p> Method: String getCreatedByUsername() </p>
     * 
     * <p> Description: Returns the username of the staff member who created this parameter. </p>
     * 
     * @return the username of the creator
     */
    public String getCreatedByUsername() { 
        return createdByUsername; 
    }

    /*****
     * <p> Method: void setCreatedByUsername(String createdByUsername) </p>
     * 
     * <p> Description: Sets the username of the staff member who created this parameter. </p>
     * 
     * @param createdByUsername the username of the creator
     */
    public void setCreatedByUsername(String createdByUsername) { 
        this.createdByUsername = createdByUsername; 
    }

    /*****
     * <p> Method: LocalDateTime getCreatedAt() </p>
     * 
     * <p> Description: Returns the timestamp when this parameter was created. </p>
     * 
     * @return the creation timestamp
     */
    public LocalDateTime getCreatedAt() { 
        return createdAt; 
    }

    /*****
     * <p> Method: void setCreatedAt(LocalDateTime createdAt) </p>
     * 
     * <p> Description: Sets the timestamp when this parameter was created. </p>
     * 
     * @param createdAt the creation timestamp to set
     */
    public void setCreatedAt(LocalDateTime createdAt) { 
        this.createdAt = createdAt; 
    }
    
    /*****
     * <p> Method: int getRequiredPosts() </p>
     * 
     * <p> Description: Returns the required number of posts for students. </p>
     * 
     * @return the required number of posts
     */
    public int getRequiredPosts() {
        return requiredPosts;
    }
    
    /*****
     * <p> Method: void setRequiredPosts(int requiredPosts) </p>
     * 
     * <p> Description: Sets the required number of posts for students. </p>
     * 
     * @param requiredPosts the required number of posts (must be >= 0)
     */
    public void setRequiredPosts(int requiredPosts) {
        this.requiredPosts = requiredPosts;
    }
    
    /*****
     * <p> Method: int getRequiredReplies() </p>
     * 
     * <p> Description: Returns the required number of replies for students. </p>
     * 
     * @return the required number of replies
     */
    public int getRequiredReplies() {
        return requiredReplies;
    }
    
    /*****
     * <p> Method: void setRequiredReplies(int requiredReplies) </p>
     * 
     * <p> Description: Sets the required number of replies for students. </p>
     * 
     * @param requiredReplies the required number of replies (must be >= 0)
     */
    public void setRequiredReplies(int requiredReplies) {
        this.requiredReplies = requiredReplies;
    }
    
    /*****
     * <p> Method: List&lt;String&gt; getTopics() </p>
     * 
     * <p> Description: Returns the list of topics students should focus on. </p>
     * 
     * @return the list of topics
     */
    public List<String> getTopics() {
        return topics != null ? new ArrayList<>(topics) : new ArrayList<>();
    }
    
    /*****
     * <p> Method: void setTopics(List&lt;String&gt; topics) </p>
     * 
     * <p> Description: Sets the list of topics students should focus on. </p>
     * 
     * @param topics the list of topics to set
     */
    public void setTopics(List<String> topics) {
        this.topics = (topics != null) ? new ArrayList<>(topics) : new ArrayList<>();
    }
    
    /*****
     * <p> Method: String getThreadId() </p>
     * 
     * <p> Description: Returns the thread ID this parameter is associated with. </p>
     * 
     * @return the thread ID, or null if this is a template
     */
    public String getThreadId() {
        return threadId;
    }
    
    /*****
     * <p> Method: void setThreadId(String threadId) </p>
     * 
     * <p> Description: Sets the thread ID this parameter is associated with. </p>
     * 
     * @param threadId the thread ID to set (required)
     */
    public void setThreadId(String threadId) {
        this.threadId = threadId;
    }
    
    /*****
     * <p> Method: List&lt;ParameterCategory&gt; getCategories() </p>
     * 
     * <p> Description: Returns the list of categories for this parameter. </p>
     * 
     * @return the list of categories
     */
    public List<ParameterCategory> getCategories() {
        return categories != null ? new ArrayList<>(categories) : new ArrayList<>();
    }
    
    /*****
     * <p> Method: void setCategories(List&lt;ParameterCategory&gt; categories) </p>
     * 
     * <p> Description: Sets the list of categories for this parameter. </p>
     * 
     * @param categories the list of categories to set
     */
    public void setCategories(List<ParameterCategory> categories) {
        this.categories = (categories != null) ? new ArrayList<>(categories) : new ArrayList<>();
    }
}

