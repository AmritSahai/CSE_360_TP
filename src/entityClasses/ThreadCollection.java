package entityClasses;

import java.util.*;

/*******
 * <p> Title: ThreadCollection Class </p>
 * 
 * <p> Description: This ThreadCollection class manages a collection of Thread objects. It provides
 * methods for CRUD operations, filtering by status, and managing threads according to the requirements
 * specified in the document.</p>
 * 
 * <p> Copyright: Joseph © 2025 </p>
 * 
 * @author Joseph
 * 
 * @version 1.00		2025-01-16 Initial version
 */ 

public class ThreadCollection {
    
    /*
     * These are the private attributes for this collection
     */
    private Map<String, Thread> threads;
    private int nextThreadId;
    
    /*****
     * <p> Method: ThreadCollection() </p>
     * 
     * <p> Description: This constructor initializes an empty collection of threads. </p>
     */
    public ThreadCollection() {
        this.threads = new HashMap<>();
        this.nextThreadId = 1;
    }

    /*****
     * <p> Method: String createThread(String title, String description, String createdByUsername) </p>
     * 
     * <p> Description: Creates a new thread with validation. </p>
     * 
     * @param title the title of the thread
     * @param description the description content of the thread
     * @param createdByUsername the username of the staff member creating the thread
     * 
     * @return the thread ID if successful, or an error message if validation fails
     * 
     */
    public String createThread(String title, String description, String createdByUsername) {
        Thread newThread = new Thread(generateThreadId(), title, description, createdByUsername);
        String validationError = newThread.validateThread();
        
        if (!validationError.isEmpty()) {
            return validationError;
        }
        
        threads.put(newThread.getThreadId(), newThread);
        return newThread.getThreadId();
    }
    
    /*****
     * <p> Method: String createThread(String title, String description, String createdByUsername, Thread.ThreadStatus status) </p>
     * 
     * <p> Description: Creates a new thread with validation and specified status. </p>
     * 
     * @param title the title of the thread
     * @param description the description content of the thread
     * @param createdByUsername the username of the staff member creating the thread
     * @param status the status of the thread (OPEN or CLOSED)
     * 
     * @return the thread ID if successful, or an error message if validation fails
     * 
     */
    public String createThread(String title, String description, String createdByUsername, Thread.ThreadStatus status) {
        Thread newThread = new Thread(generateThreadId(), title, description, createdByUsername);
        newThread.setStatus(status);
        String validationError = newThread.validateThread();
        
        if (!validationError.isEmpty()) {
            return validationError;
        }
        
        threads.put(newThread.getThreadId(), newThread);
        return newThread.getThreadId();
    }

    /*****
     * <p> Method: void addThread(Thread thread) </p>
     * 
     * <p> Description: Adds an existing thread to the collection (used when loading from database). </p>
     * 
     * @param thread the thread to add
     * 
     */
    public void addThread(Thread thread) {
        threads.put(thread.getThreadId(), thread);
        // Update nextThreadId if needed
        try {
            int threadNum = Integer.parseInt(thread.getThreadId().replace("THREAD_", ""));
            if (threadNum >= nextThreadId) {
                nextThreadId = threadNum + 1;
            }
        } catch (NumberFormatException e) {
            // Ignore if threadId format is different
        }
    }

    /*****
     * <p> Method: List<Thread> getAllThreads() </p>
     * 
     * <p> Description: Returns all threads in the collection, sorted by status (Open first, then Closed). </p>
     * 
     * @return list of all threads sorted by status
     * 
     */
    public List<Thread> getAllThreads() {
        return threads.values().stream()
            .sorted((t1, t2) -> {
                // Sort by status (Open first), then by creation date (newest first)
                int statusCompare = t1.getStatus().compareTo(t2.getStatus());
                if (statusCompare != 0) {
                    return statusCompare; // Open comes before Closed
                }
                return t2.getCreatedAt().compareTo(t1.getCreatedAt()); // Newest first
            })
            .collect(java.util.stream.Collectors.toList());
    }

    /*****
     * <p> Method: List<Thread> getThreadsByStatus(Thread.ThreadStatus status) </p>
     * 
     * <p> Description: Returns all threads with a specific status. </p>
     * 
     * @param status the status to filter by
     * 
     * @return list of threads with the specified status
     * 
     */
    public List<Thread> getThreadsByStatus(Thread.ThreadStatus status) {
        return threads.values().stream()
            .filter(thread -> thread.getStatus() == status)
            .sorted((t1, t2) -> t2.getCreatedAt().compareTo(t1.getCreatedAt())) // Newest first
            .collect(java.util.stream.Collectors.toList());
    }

    /*****
     * <p> Method: List<Thread> getThreadsByCreator(String createdByUsername) </p>
     * 
     * <p> Description: Returns all threads created by a specific staff member. </p>
     * 
     * @param createdByUsername the username of the staff member
     * 
     * @return list of threads created by the staff member
     * 
     */
    public List<Thread> getThreadsByCreator(String createdByUsername) {
        return threads.values().stream()
            .filter(thread -> createdByUsername.equals(thread.getCreatedByUsername()))
            .sorted((t1, t2) -> {
                // Sort by status (Open first), then by creation date (newest first)
                int statusCompare = t1.getStatus().compareTo(t2.getStatus());
                if (statusCompare != 0) {
                    return statusCompare;
                }
                return t2.getCreatedAt().compareTo(t1.getCreatedAt());
            })
            .collect(java.util.stream.Collectors.toList());
    }

    /*****
     * <p> Method: String updateThread(String threadId, String newTitle, String newDescription, String currentUsername) </p>
     * 
     * <p> Description: Updates an existing thread with validation. </p>
     * 
     * @param threadId the ID of the thread to update
     * @param newTitle the new title content
     * @param newDescription the new description content
     * @param currentUsername the username of the current user
     * 
     * @return empty string if successful, error message if failed
     * 
     */
    public String updateThread(String threadId, String newTitle, String newDescription, String currentUsername) {
        Thread thread = threads.get(threadId);
        
        if (thread == null) {
            return "Thread not found.";
        }
        
        if (!thread.getCreatedByUsername().equals(currentUsername)) {
            return "You can only update threads you created.";
        }
        
        // Create temporary thread to validate new content
        Thread tempThread = new Thread(threadId, newTitle, newDescription, currentUsername);
        String validationError = tempThread.validateThread();
        
        if (!validationError.isEmpty()) {
            return validationError;
        }
        
        thread.setTitle(newTitle);
        thread.setDescription(newDescription);
        return ""; // Success
    }
    
    /*****
     * <p> Method: String updateThread(String threadId, String newTitle, String newDescription, String currentUsername, Thread.ThreadStatus newStatus) </p>
     * 
     * <p> Description: Updates an existing thread with validation and status. </p>
     * 
     * @param threadId the ID of the thread to update
     * @param newTitle the new title content
     * @param newDescription the new description content
     * @param currentUsername the username of the current user
     * @param newStatus the new status of the thread
     * 
     * @return empty string if successful, error message if failed
     * 
     */
    public String updateThread(String threadId, String newTitle, String newDescription, String currentUsername, Thread.ThreadStatus newStatus) {
        Thread thread = threads.get(threadId);
        
        if (thread == null) {
            return "Thread not found.";
        }
        
        if (!thread.getCreatedByUsername().equals(currentUsername)) {
            return "You can only update threads you created.";
        }
        
        // Create temporary thread to validate new content
        Thread tempThread = new Thread(threadId, newTitle, newDescription, currentUsername);
        String validationError = tempThread.validateThread();
        
        if (!validationError.isEmpty()) {
            return validationError;
        }
        
        thread.setTitle(newTitle);
        thread.setDescription(newDescription);
        thread.setStatus(newStatus);
        return ""; // Success
    }

    /*****
     * <p> Method: String deleteThread(String threadId, String currentUsername) </p>
     * 
     * <p> Description: Deletes a thread. </p>
     * 
     * @param threadId the ID of the thread to delete
     * @param currentUsername the username of the current user
     * 
     * @return empty string if successful, error message if failed
     * 
     */
    public String deleteThread(String threadId, String currentUsername) {
        Thread thread = threads.get(threadId);
        
        if (thread == null) {
            return "Thread not found.";
        }
        
        if (!thread.getCreatedByUsername().equals(currentUsername)) {
            return "You can only delete threads you created.";
        }
        
        threads.remove(threadId);
        return ""; // Success
    }

    /*****
     * <p> Method: Thread getThreadById(String threadId) </p>
     * 
     * <p> Description: Retrieves a thread by its ID. </p>
     * 
     * @param threadId the ID of the thread
     * 
     * @return the thread if found, null otherwise
     * 
     */
    public Thread getThreadById(String threadId) {
        return threads.get(threadId);
    }
    
    /*****
     * <p> Method: Thread getThreadByTitle(String title) </p>
     * 
     * <p> Description: Retrieves a thread by its title. </p>
     * 
     * @param title the title of the thread
     * 
     * @return the thread if found, null otherwise
     * 
     */
    public Thread getThreadByTitle(String title) {
        return threads.values().stream()
            .filter(thread -> title.equals(thread.getTitle()))
            .findFirst()
            .orElse(null);
    }

    /*****
     * <p> Method: boolean threadExists(String threadId) </p>
     * 
     * <p> Description: Checks if a thread exists. </p>
     * 
     * @param threadId the ID of the thread
     * 
     * @return true if the thread exists, false otherwise
     * 
     */
    public boolean threadExists(String threadId) {
        return threads.containsKey(threadId);
    }

    /*****
     * <p> Method: int getThreadCount() </p>
     * 
     * <p> Description: Returns the total number of threads in the collection. </p>
     * 
     * @return the number of threads
     * 
     */
    public int getThreadCount() {
        return threads.size();
    }

    /*****
     * <p> Method: String generateThreadId() </p>
     * 
     * <p> Description: Generates a unique thread ID. </p>
     * 
     * @return a unique thread ID
     * 
     */
    private String generateThreadId() {
        String threadId = "THREAD_" + nextThreadId;
        nextThreadId++;
        return threadId;
    }
    
    /*****
     * <p> Method: List<Thread> getOpenThreads() </p>
     * 
     * <p> Description: Returns all open threads. </p>
     * 
     * @return list of open threads
     * 
     */
    public List<Thread> getOpenThreads() {
        return getThreadsByStatus(Thread.ThreadStatus.OPEN);
    }
}

