package entityClasses;

import java.util.*;

/*******
 * <p> Title: RequestCollection Class </p>
 * 
 * <p> Description: This RequestCollection class manages a collection of Request objects. It provides
 * methods for CRUD operations, filtering by status, and managing requests according to the requirements
 * specified in the document.</p>
 * 
 * <p> Copyright: Joseph © 2025 </p>
 * 
 * @author Joseph
 * 
 * @version 1.00		2025-01-16 Initial version
 */ 

public class RequestCollection {
    
    /*
     * These are the private attributes for this collection
     */
    private Map<String, Request> requests;
    private int nextRequestId;
    
    /*****
     * <p> Method: RequestCollection() </p>
     * 
     * <p> Description: This constructor initializes an empty collection of requests. </p>
     */
    public RequestCollection() {
        this.requests = new HashMap<>();
        this.nextRequestId = 1;
    }

    /*****
     * <p> Method: String createRequest(String title, String description, Request.RequestCategory category, String createdByUsername) </p>
     * 
     * <p> Description: Creates a new request with validation. </p>
     * 
     * @param title the title of the request
     * @param description the description content of the request
     * @param category the category of the request
     * @param createdByUsername the username of the staff member creating the request
     * 
     * @return the request ID if successful, or an error message if validation fails
     * 
     */
    public String createRequest(String title, String description, Request.RequestCategory category, String createdByUsername) {
        Request newRequest = new Request(generateRequestId(), title, description, category, createdByUsername);
        String validationError = newRequest.validateRequest();
        
        if (!validationError.isEmpty()) {
            return validationError;
        }
        
        requests.put(newRequest.getRequestId(), newRequest);
        return newRequest.getRequestId();
    }

    /*****
     * <p> Method: void addRequest(Request request) </p>
     * 
     * <p> Description: Adds an existing request to the collection (used when loading from database). </p>
     * 
     * @param request the request to add
     * 
     */
    public void addRequest(Request request) {
        requests.put(request.getRequestId(), request);
        // Update nextRequestId if needed
        try {
            int requestNum = Integer.parseInt(request.getRequestId().replace("REQUEST_", ""));
            if (requestNum >= nextRequestId) {
                nextRequestId = requestNum + 1;
            }
        } catch (NumberFormatException e) {
            // Ignore if requestId format is different
        }
    }

    /*****
     * <p> Method: List<Request> getAllRequests() </p>
     * 
     * <p> Description: Returns all requests in the collection, sorted by status (Open first, then Closed). </p>
     * 
     * @return list of all requests sorted by status
     * 
     */
    public List<Request> getAllRequests() {
        return requests.values().stream()
            .sorted((r1, r2) -> {
                // Sort by status (Open first), then by creation date (newest first)
                int statusCompare = r1.getStatus().compareTo(r2.getStatus());
                if (statusCompare != 0) {
                    return statusCompare; // Open comes before Closed
                }
                return r2.getCreatedAt().compareTo(r1.getCreatedAt()); // Newest first
            })
            .collect(java.util.stream.Collectors.toList());
    }

    /*****
     * <p> Method: List<Request> getRequestsByStatus(Request.RequestStatus status) </p>
     * 
     * <p> Description: Returns all requests with a specific status. </p>
     * 
     * @param status the status to filter by
     * 
     * @return list of requests with the specified status
     * 
     */
    public List<Request> getRequestsByStatus(Request.RequestStatus status) {
        return requests.values().stream()
            .filter(request -> request.getStatus() == status)
            .sorted((r1, r2) -> r2.getCreatedAt().compareTo(r1.getCreatedAt())) // Newest first
            .collect(java.util.stream.Collectors.toList());
    }

    /*****
     * <p> Method: List<Request> getRequestsByCreator(String createdByUsername) </p>
     * 
     * <p> Description: Returns all requests created by a specific staff member. </p>
     * 
     * @param createdByUsername the username of the staff member
     * 
     * @return list of requests created by the staff member
     * 
     */
    public List<Request> getRequestsByCreator(String createdByUsername) {
        return requests.values().stream()
            .filter(request -> createdByUsername.equals(request.getCreatedByUsername()))
            .sorted((r1, r2) -> {
                // Sort by status (Open first), then by creation date (newest first)
                int statusCompare = r1.getStatus().compareTo(r2.getStatus());
                if (statusCompare != 0) {
                    return statusCompare;
                }
                return r2.getCreatedAt().compareTo(r1.getCreatedAt());
            })
            .collect(java.util.stream.Collectors.toList());
    }

    /*****
     * <p> Method: String closeRequest(String requestId, String closedByUsername, String resolutionNotes) </p>
     * 
     * <p> Description: Closes a request with resolution notes. </p>
     * 
     * @param requestId the ID of the request to close
     * @param closedByUsername the username of the admin closing the request
     * @param resolutionNotes the documentation of steps taken to resolve the request
     * 
     * @return empty string if successful, error message if failed
     * 
     */
    public String closeRequest(String requestId, String closedByUsername, String resolutionNotes) {
        Request request = requests.get(requestId);
        
        if (request == null) {
            return "Request not found.";
        }
        
        if (request.isClosed()) {
            return "Request is already closed.";
        }
        
        if (resolutionNotes == null || resolutionNotes.trim().isEmpty()) {
            return "Resolution notes are required when closing a request.";
        }
        
        if (resolutionNotes.length() > Request.MAX_RESOLUTION_NOTES_LENGTH) {
            return "Resolution notes cannot exceed " + Request.MAX_RESOLUTION_NOTES_LENGTH + " characters.";
        }
        
        request.setStatus(Request.RequestStatus.CLOSED);
        request.setClosedByUsername(closedByUsername);
        request.setResolutionNotes(resolutionNotes.trim());
        request.setClosedAt(java.time.LocalDateTime.now());
        return ""; // Success
    }

    /*****
     * <p> Method: String reopenRequest(String requestId, String reopenedByUsername, String reopenReason) </p>
     * 
     * <p> Description: Reopens a closed request with a reason. Only the original creator can reopen. </p>
     * 
     * @param requestId the ID of the request to reopen
     * @param reopenedByUsername the username of the staff member reopening the request
     * @param reopenReason the reason for reopening the request
     * 
     * @return the new request ID if successful, or an error message if failed
     * 
     */
    public String reopenRequest(String requestId, String reopenedByUsername, String reopenReason) {
        Request originalRequest = requests.get(requestId);
        
        if (originalRequest == null) {
            return "Request not found.";
        }
        
        if (!originalRequest.isClosed()) {
            return "Request is not closed.";
        }
        
        if (!originalRequest.getCreatedByUsername().equals(reopenedByUsername)) {
            return "You can only reopen requests you created.";
        }
        
        if (reopenReason == null || reopenReason.trim().isEmpty()) {
            return "Reopen reason is required when reopening a request.";
        }
        
        if (reopenReason.length() > Request.MAX_REOPEN_REASON_LENGTH) {
            return "Reopen reason cannot exceed " + Request.MAX_REOPEN_REASON_LENGTH + " characters.";
        }
        
        // Create a new request linked to the original
        String newRequestId = generateRequestId();
        Request newRequest = new Request(newRequestId, originalRequest.getTitle(), originalRequest.getDescription(), 
                                        originalRequest.getCategory(), reopenedByUsername);
        newRequest.setOriginalRequestId(originalRequest.getRequestId());
        newRequest.setReopenReason(reopenReason.trim());
        newRequest.setReopenedAt(java.time.LocalDateTime.now());
        
        requests.put(newRequestId, newRequest);
        return newRequestId; // Success
    }

    /*****
     * <p> Method: Request getRequestById(String requestId) </p>
     * 
     * <p> Description: Retrieves a request by its ID. </p>
     * 
     * @param requestId the ID of the request
     * 
     * @return the request if found, null otherwise
     * 
     */
    public Request getRequestById(String requestId) {
        return requests.get(requestId);
    }

    /*****
     * <p> Method: boolean requestExists(String requestId) </p>
     * 
     * <p> Description: Checks if a request exists. </p>
     * 
     * @param requestId the ID of the request
     * 
     * @return true if the request exists, false otherwise
     * 
     */
    public boolean requestExists(String requestId) {
        return requests.containsKey(requestId);
    }

    /*****
     * <p> Method: int getRequestCount() </p>
     * 
     * <p> Description: Returns the total number of requests in the collection. </p>
     * 
     * @return the number of requests
     * 
     */
    public int getRequestCount() {
        return requests.size();
    }

    /*****
     * <p> Method: String generateRequestId() </p>
     * 
     * <p> Description: Generates a unique request ID. </p>
     * 
     * @return a unique request ID
     * 
     */
    private String generateRequestId() {
        String requestId = "REQUEST_" + nextRequestId;
        nextRequestId++;
        return requestId;
    }
    
    /*****
     * <p> Method: List<Request> getOpenRequests() </p>
     * 
     * <p> Description: Returns all open requests. </p>
     * 
     * @return list of open requests
     * 
     */
    public List<Request> getOpenRequests() {
        return getRequestsByStatus(Request.RequestStatus.OPEN);
    }
    
    /*****
     * <p> Method: List<Request> getClosedRequests() </p>
     * 
     * <p> Description: Returns all closed requests. </p>
     * 
     * @return list of closed requests
     * 
     */
    public List<Request> getClosedRequests() {
        return getRequestsByStatus(Request.RequestStatus.CLOSED);
    }
}

