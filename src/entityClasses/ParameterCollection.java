package entityClasses;

import java.util.*;
import java.util.stream.Collectors;

/*******
 * <p> Title: ParameterCollection Class </p>
 * 
 * <p> Description: This ParameterCollection class manages a collection of Parameter objects. It provides
 * methods for CRUD operations and managing grading parameters according to the requirements.</p>
 * 
 * <p> Copyright: Joseph © 2025 </p>
 * 
 * @author Joseph
 * 
 * @version 1.00		2025-01-16 Initial version
 */ 

public class ParameterCollection {
    
    /*
     * These are the private attributes for this collection
     */
    private Map<String, Parameter> parameters;
    private int nextParameterId;
    
    /*****
     * <p> Method: ParameterCollection() </p>
     * 
     * <p> Description: This constructor initializes an empty collection of parameters. </p>
     */
    public ParameterCollection() {
        this.parameters = new HashMap<>();
        this.nextParameterId = 1;
    }

    /*****
     * <p> Method: String createParameter(String name, String description, int minValue, int maxValue, 
     * double weight, boolean isActive, String createdByUsername) </p>
     * 
     * <p> Description: Creates a new parameter with validation (backward compatible version). </p>
     * 
     * @param name the name of the parameter
     * @param description the description of the parameter
     * @param minValue the minimum value (0-100)
     * @param maxValue the maximum value (0-100)
     * @param weight the weight (0.0 - 1.0)
     * @param isActive whether the parameter is active
     * @param createdByUsername the username of the staff who created it
     * 
     * @return the parameter ID if successful, or an error message if validation fails
     * 
     */
    /*****
     * <p> Method: String createParameter(String name, String description, 
     * boolean isActive, String createdByUsername, int requiredPosts, int requiredReplies,
     * List&lt;String&gt; topics, String threadId, List&lt;ParameterCategory&gt; categories) </p>
     * 
     * <p> Description: Creates a new parameter with validation including thread association, participation requirements, and categories. </p>
     * 
     * @param name the name of the parameter
     * @param description the description of the parameter
     * @param isActive whether the parameter is active
     * @param createdByUsername the username of the staff who created it
     * @param requiredPosts the required number of posts for students
     * @param requiredReplies the required number of replies for students
     * @param topics the list of topics students should focus on
     * @param threadId the thread this parameter is associated with (required)
     * @param categories the list of categories with weights (0.0 - 1.0)
     * 
     * @return the parameter ID if successful, or an error message if validation fails
     * 
     */
    public String createParameter(String name, String description, 
                                 boolean isActive, String createdByUsername,
                                 int requiredPosts, int requiredReplies, List<String> topics, 
                                 String threadId, List<ParameterCategory> categories) {
        Parameter newParameter = new Parameter(generateParameterId(), name, description, 
                                              isActive, createdByUsername,
                                              requiredPosts, requiredReplies, topics, threadId, categories);
        String validationError = newParameter.validateParameter();
        
        if (!validationError.isEmpty()) {
            return validationError;
        }
        
        parameters.put(newParameter.getParameterId(), newParameter);
        return newParameter.getParameterId();
    }

    /*****
     * <p> Method: void addParameter(Parameter parameter) </p>
     * 
     * <p> Description: Adds an existing parameter to the collection (used when loading from database). </p>
     * 
     * @param parameter the parameter to add
     * 
     */
    public void addParameter(Parameter parameter) {
        parameters.put(parameter.getParameterId(), parameter);
        // Update nextParameterId if needed
        try {
            int paramNum = Integer.parseInt(parameter.getParameterId().replace("PARAM_", ""));
            if (paramNum >= nextParameterId) {
                nextParameterId = paramNum + 1;
            }
        } catch (NumberFormatException e) {
            // Ignore if parameterId format is different
        }
    }

    /*****
     * <p> Method: List<Parameter> getAllParameters() </p>
     * 
     * <p> Description: Returns all parameters in the collection. </p>
     * 
     * @return list of all parameters
     * 
     */
    public List<Parameter> getAllParameters() {
        return new ArrayList<>(parameters.values());
    }

    /*****
     * <p> Method: List<Parameter> getParametersByStaff(String staffUsername) </p>
     * 
     * <p> Description: Returns all parameters created by a specific staff member. </p>
     * 
     * @param staffUsername the username of the staff member
     * 
     * @return list of parameters created by the staff member
     * 
     */
    public List<Parameter> getParametersByStaff(String staffUsername) {
        return parameters.values().stream()
            .filter(param -> staffUsername.equals(param.getCreatedByUsername()))
            .sorted((p1, p2) -> p2.getCreatedAt().compareTo(p1.getCreatedAt())) // Newest first
            .collect(Collectors.toList());
    }

    /*****
     * <p> Method: List<Parameter> getActiveParameters() </p>
     * 
     * <p> Description: Returns all active parameters. </p>
     * 
     * @return list of active parameters
     * 
     */
    public List<Parameter> getActiveParameters() {
        return parameters.values().stream()
            .filter(Parameter::isActive)
            .sorted((p1, p2) -> p2.getCreatedAt().compareTo(p1.getCreatedAt())) // Newest first
            .collect(Collectors.toList());
    }

    /*****
     * <p> Method: List<Parameter> getActiveParametersByStaff(String staffUsername) </p>
     * 
     * <p> Description: Returns all active parameters created by a specific staff member. </p>
     * 
     * @param staffUsername the username of the staff member
     * 
     * @return list of active parameters created by the staff member
     * 
     */
    public List<Parameter> getActiveParametersByStaff(String staffUsername) {
        return parameters.values().stream()
            .filter(param -> staffUsername.equals(param.getCreatedByUsername()))
            .filter(Parameter::isActive)
            .sorted((p1, p2) -> p2.getCreatedAt().compareTo(p1.getCreatedAt())) // Newest first
            .collect(Collectors.toList());
    }
    
    /*****
     * <p> Method: List<Parameter> getParametersByThread(String threadId) </p>
     * 
     * <p> Description: Returns all parameters associated with a specific thread. </p>
     * 
     * @param threadId the thread ID
     * 
     * @return list of parameters for the thread
     * 
     */
    public List<Parameter> getParametersByThread(String threadId) {
        return parameters.values().stream()
            .filter(param -> threadId != null && threadId.equals(param.getThreadId()))
            .sorted((p1, p2) -> p2.getCreatedAt().compareTo(p1.getCreatedAt())) // Newest first
            .collect(Collectors.toList());
    }
    
    
    /*****
     * <p> Method: boolean deleteAllParametersByStaff(String staffUsername) </p>
     * 
     * <p> Description: Deletes all parameters created by a specific staff member. </p>
     * 
     * @param staffUsername the username of the staff member
     * 
     * @return true if any parameters were deleted, false otherwise
     * 
     */
    public boolean deleteAllParametersByStaff(String staffUsername) {
        List<String> toDelete = parameters.values().stream()
            .filter(param -> staffUsername.equals(param.getCreatedByUsername()))
            .map(Parameter::getParameterId)
            .collect(Collectors.toList());
        
        boolean deleted = false;
        for (String paramId : toDelete) {
            if (parameters.remove(paramId) != null) {
                deleted = true;
            }
        }
        return deleted;
    }
    
    /*****
     * <p> Method: boolean deleteSelectedParameters(List&lt;String&gt; parameterIds) </p>
     * 
     * <p> Description: Deletes selected parameters by their IDs. </p>
     * 
     * @param parameterIds the list of parameter IDs to delete
     * 
     * @return true if any parameters were deleted, false otherwise
     * 
     */
    public boolean deleteSelectedParameters(List<String> parameterIds) {
        boolean deleted = false;
        for (String paramId : parameterIds) {
            if (parameters.remove(paramId) != null) {
                deleted = true;
            }
        }
        return deleted;
    }

    /*****
     * <p> Method: Parameter getParameterById(String parameterId) </p>
     * 
     * <p> Description: Retrieves a parameter by its ID. </p>
     * 
     * @param parameterId the ID of the parameter
     * 
     * @return the parameter if found, null otherwise
     * 
     */
    public Parameter getParameterById(String parameterId) {
        return parameters.get(parameterId);
    }

    /*****
     * <p> Method: boolean parameterExists(String parameterId) </p>
     * 
     * <p> Description: Checks if a parameter exists. </p>
     * 
     * @param parameterId the ID of the parameter
     * 
     * @return true if the parameter exists, false otherwise
     * 
     */
    public boolean parameterExists(String parameterId) {
        return parameters.containsKey(parameterId);
    }

    /*****
     * <p> Method: int getParameterCount() </p>
     * 
     * <p> Description: Returns the total number of parameters in the collection. </p>
     * 
     * @return the number of parameters
     * 
     */
    public int getParameterCount() {
        return parameters.size();
    }

    /*****
     * <p> Method: int getActiveParameterCount() </p>
     * 
     * <p> Description: Returns the number of active parameters in the collection. </p>
     * 
     * @return the number of active parameters
     * 
     */
    public int getActiveParameterCount() {
        return (int) parameters.values().stream()
            .filter(Parameter::isActive)
            .count();
    }

    /*****
     * <p> Method: String updateParameter(String parameterId, String name, String description, 
     * int minValue, int maxValue, double weight, boolean isActive) </p>
     * 
     * <p> Description: Updates an existing parameter with new values (backward compatible version). </p>
     * 
     * @param parameterId the ID of the parameter to update
     * @param name the new name of the parameter
     * @param description the new description of the parameter
     * @param minValue the new minimum value (0-100)
     * @param maxValue the new maximum value (0-100)
     * @param weight the new weight (0.0 - 1.0)
     * @param isActive the new active status
     * 
     * @return empty string if successful, error message if validation fails or parameter not found
     * 
     */
    /*****
     * <p> Method: String updateParameter(String parameterId, String name, String description, 
     * boolean isActive, int requiredPosts, int requiredReplies,
     * List&lt;String&gt; topics, String threadId, List&lt;ParameterCategory&gt; categories) </p>
     * 
     * <p> Description: Updates an existing parameter with new values including thread association, participation requirements, and categories.
     * Uses the same validation as creation. </p>
     * 
     * @param parameterId the ID of the parameter to update
     * @param name the new name of the parameter
     * @param description the new description of the parameter
     * @param isActive the new active status
     * @param requiredPosts the new required number of posts
     * @param requiredReplies the new required number of replies
     * @param topics the new list of topics
     * @param threadId the new thread ID (required)
     * @param categories the new list of categories with weights
     * 
     * @return empty string if successful, error message if validation fails or parameter not found
     * 
     */
    public String updateParameter(String parameterId, String name, String description, 
                                 boolean isActive,
                                 int requiredPosts, int requiredReplies, List<String> topics,
                                 String threadId, List<ParameterCategory> categories) {
        Parameter existingParameter = parameters.get(parameterId);
        if (existingParameter == null) {
            return "Parameter not found.";
        }
        
        // Create a temporary parameter to validate the new values (same validation as creation)
        Parameter tempParameter = new Parameter(parameterId, name, description, 
                                                isActive, 
                                                existingParameter.getCreatedByUsername(),
                                                requiredPosts, requiredReplies, topics, threadId, categories);
        tempParameter.setCreatedAt(existingParameter.getCreatedAt()); // Preserve creation date
        
        String validationError = tempParameter.validateParameter();
        if (!validationError.isEmpty()) {
            return validationError;
        }
        
        // Update the parameter in the collection
        existingParameter.setName(name);
        existingParameter.setDescription(description);
        existingParameter.setActive(isActive);
        existingParameter.setRequiredPosts(requiredPosts);
        existingParameter.setRequiredReplies(requiredReplies);
        existingParameter.setTopics(topics);
        existingParameter.setThreadId(threadId);
        existingParameter.setCategories(categories);
        
        return ""; // Success
    }

    /*****
     * <p> Method: boolean deleteParameter(String parameterId) </p>
     * 
     * <p> Description: Deletes a parameter from the collection by its ID.</p>
     * 
     * @param parameterId the ID of the parameter to delete
     * 
     * @return true if the parameter was deleted, false if it was not found
     * 
     */
    public boolean deleteParameter(String parameterId) {
        Parameter removed = parameters.remove(parameterId);
        return removed != null;
    }

    /*****
     * <p> Method: String generateParameterId() </p>
     * 
     * <p> Description: Generates a unique parameter ID. </p>
     * 
     * @return a unique parameter ID
     * 
     */
    private String generateParameterId() {
        String parameterId = "PARAM_" + nextParameterId;
        nextParameterId++;
        return parameterId;
    }
}

