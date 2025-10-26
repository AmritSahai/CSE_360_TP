package entityClasses;

import java.util.*;
import java.util.stream.Collectors;

/*******
 * <p> Title: PostCollection Class </p>
 * 
 * <p> Description: The PostCollection class manages all Post objects created within the system. 
 * It acts as both container and controller for post entities, enabling Create, Read, Update, and 
 * Delete (CRUD) operations as well as search and filtering capabilities. </p>
 * 
 * <p> The PostCollection class supports the student user stories that involve viewing discussion threads, 
 * creating new posts, searching for posts by keyword or thread, editing one’s own posts, and 
 * deleting one’s own posts. It provides the main collection logic used by the student GUI 
 * and maintains data integrity between multiple Post objects. </p>
 * 
 * <p> Copyright: Joseph © 2025 </p>
 * 
 * @author Joseph and Vrishik
 * 
 * @version 2.00		2025-10-19 TP2 Updated Javadoc Version
 */ 

public class PostCollection {
    
	/*
     * These are the private attributes for this collection
     */

    /** 
     * A mapping of post IDs to Post objects. 
     * Provides efficient retrieval and ensures each post has a unique key.
     */
    private Map<String, Post> posts;
    
    /** 
     * Counter used to generate sequential unique post IDs.
     */
    private int nextPostId;
    
    /*****
     * <p> Method: PostCollection() </p>
     * 
     * <p> Description: Constructs an empty collection of posts. 
     * Initializes the internal map and sets the starting post ID counter. </p>
     */
    public PostCollection() {
        this.posts = new HashMap<>();
        this.nextPostId = 1;
    }

    /*****
     * <p> Method: createPost(String title, String body, String authorUsername, String thread) </p>
     * 
     * <p> Description: Creates and validates a new post. 
     * Supports the student user story that allows users to write and submit new discussion posts. 
     * Ensures that the input meets validation rules before adding the post to the collection. </p>
     * 
     * @param title the title of the post
     * @param body the body content of the post
     * @param authorUsername the username of the post author
     * @param thread the thread this post belongs to (can be null for default)
     * @return the post ID if successful, or an error message if validation fails
     */
    public String createPost(String title, String body, String authorUsername, String thread) {
        // Validate search input length
        if (title != null && title.length() > 100) {
            return "Search/filter input cannot exceed 100 characters.";
        }
        
        Post newPost = new Post(generatePostId(), title, body, authorUsername, thread);
        String validationError = newPost.validatePost();
        
        if (!validationError.isEmpty()) {
            return validationError;
        }
        
        posts.put(newPost.getPostId(), newPost);
        return newPost.getPostId();
    }

    /*****
     * <p> Method: addPost(Post post) </p>
     * 
     * <p> Description: Adds an existing post object to the collection, typically used when loading 
     * saved data from a database or file. Updates the ID counter if necessary. </p>
     * 
     * @param post the Post object to add
     */
    public void addPost(Post post) {
        posts.put(post.getPostId(), post);
        // Update nextPostId if needed
        try {
            int postNum = Integer.parseInt(post.getPostId().replace("POST_", ""));
            if (postNum >= nextPostId) {
                nextPostId = postNum + 1;
            }
        } catch (NumberFormatException e) {
            // Ignore if postId format is different
        }
    }

    /*****
     * <p> Method: getAllPosts() </p>
     * 
     * <p> Description: Retrieves a list of all posts in the system, regardless of thread or author. 
     * Supports features like displaying all discussions for staff or admins. </p>
     * 
     * @return a list of all Post objects
     */
    public List<Post> getAllPosts() {
        return new ArrayList<>(posts.values());
    }

    /*****
     * <p> Method: searchPosts(String keyword, String threadFilter) </p>
     * 
     * <p> Description: Searches for posts containing the keyword and/or matching the selected thread. 
     * Supports the student user story that allows keyword searching within the discussion forum. </p>
     * 
     * @param keyword the word or phrase to search for
     * @param threadFilter the thread to filter by (can be "All" for all threads)
     * @return list of posts that match the search and filter criteria
     */
    public List<Post> searchPosts(String keyword, String threadFilter) {
        // Validate search input length
        if (keyword != null && keyword.length() > 100) {
            return new ArrayList<>(); // Return empty list for invalid input
        }
        
        // If no keyword entered, return empty list (prompt for input instead of running empty search)
        if (keyword == null || keyword.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        return posts.values().stream()
            .filter(post -> post.matchesSearch(keyword))
            .filter(post -> threadFilter == null || "All".equals(threadFilter) || 
                           threadFilter.equals(post.getThread()))
            .sorted((p1, p2) -> p2.getCreatedAt().compareTo(p1.getCreatedAt())) // Newest first
            .collect(Collectors.toList());
    }

    /*****
     * <p> Method: getPostsByAuthor(String authorUsername) </p>
     * 
     * <p> Description: Retrieves all posts written by a particular author, 
     * supporting the student and staff features that allow viewing one’s own or another user’s posts. </p>
     * 
     * @param authorUsername the username of the author
     * @return list of posts created by the specified author
     */
    public List<Post> getPostsByAuthor(String authorUsername) {
        return posts.values().stream()
            .filter(post -> authorUsername.equals(post.getAuthorUsername()))
            .sorted((p1, p2) -> p2.getCreatedAt().compareTo(p1.getCreatedAt())) // Newest first
            .collect(Collectors.toList());
    }

    /*****
     * <p> Method: getPostsByThread(String thread) </p>
     * 
     * <p> Description: Retrieves all posts that belong to a specific discussion thread. 
     * Supports student and staff browsing features for organized thread-based discussions. </p>
     * 
     * @param thread the name of the discussion thread
     * @return a list of posts within the specified thread
     */
    public List<Post> getPostsByThread(String thread) {
        return posts.values().stream()
            .filter(post -> thread.equals(post.getThread()))
            .sorted((p1, p2) -> p2.getCreatedAt().compareTo(p1.getCreatedAt())) // Newest first
            .collect(Collectors.toList());
    }

    /*****
     * <p> Method: updatePost(String postId, String newTitle, String newBody, String currentUsername) </p>
     * 
     * <p> Description: Updates the title and body of an existing post after validating new input. 
     * Supports the student feature that allows editing one’s own posts while enforcing permission checks. </p>
     * 
     * @param postId the ID of the post to update
     * @param newTitle the new post title
     * @param newBody the new post body
     * @param currentUsername the username of the user requesting the edit
     * @return an empty string if successful, or a descriptive error message if validation fails
     */
    public String updatePost(String postId, String newTitle, String newBody, String currentUsername) {
        Post post = posts.get(postId);
        
        if (post == null) {
            return "Post not found.";
        }
        
        if (!post.canEdit(currentUsername)) {
            return "You can only edit your own posts.";
        }
        
        // Create temporary post to validate new content
        Post tempPost = new Post(postId, newTitle, newBody, currentUsername, post.getThread());
        String validationError = tempPost.validatePost();
        
        if (!validationError.isEmpty()) {
            return validationError;
        }
        
        post.updateContent(newTitle, newBody);
        return ""; // Success
    }

    /*****
     * <p> Method: deletePost(String postId, String currentUsername) </p>
     * 
     * <p> Description: Marks a post as deleted without permanently removing it from the collection. 
     * Supports the student feature that allows removing one’s own posts while preserving data 
     * for audit and staff review. </p>
     * 
     * @param postId the ID of the post to delete
     * @param currentUsername the username of the user requesting deletion
     * @return an empty string if successful, or an error message if permission is denied
     */
    public String deletePost(String postId, String currentUsername) {
        Post post = posts.get(postId);
        
        if (post == null) {
            return "Post not found.";
        }
        
        if (!post.canDelete(currentUsername)) {
            return "You can only delete your own posts.";
        }
        
        post.markAsDeleted();
        return ""; // Success
    }

    /*****
     * <p> Method: getPostById(String postId) </p>
     * 
     * <p> Description: Retrieves a single Post object by its unique ID. </p>
     * 
     * @param postId the ID of the post
     * @return the Post object if found, or null otherwise
     */
    public Post getPostById(String postId) {
        return posts.get(postId);
    }

    /*****
     * <p> Method: postExists(String postId) </p>
     * 
     * <p> Description: Determines whether a post with the specified ID exists in the collection. </p>
     * 
     * @param postId the ID to check
     * @return true if the post exists, false otherwise
     */
    public boolean postExists(String postId) {
        return posts.containsKey(postId);
    }

    /*****
     * <p> Method: getAllThreads() </p>
     * 
     * <p> Description: Returns a set of all unique thread names found among posts in the collection. 
     * Supports GUI components that display thread selection lists. </p>
     * 
     * @return a set of unique thread names
     */
    public Set<String> getAllThreads() {
        return posts.values().stream()
            .map(Post::getThread)
            .collect(Collectors.toSet());
    }

    /*****
     * <p> Method: getPostCount() </p>
     * 
     * <p> Description: Returns the total number of posts in the collection, including deleted ones. </p>
     * 
     * @return the total count of posts
     */
    public int getPostCount() {
        return posts.size();
    }

    /*****
     * <p> Method: getActivePostCount() </p>
     * 
     * <p> Description: Returns the number of posts that have not been marked as deleted. 
     * Useful for dashboards and staff analytics. </p>
     * 
     * @return the number of active (non-deleted) posts
     */
    public int getActivePostCount() {
        return (int) posts.values().stream()
            .filter(post -> !post.isDeleted())
            .count();
    }

    /*****
     * <p> Method: generatePostId() </p>
     * 
     * <p> Description: Generates a unique post ID using the format "POST_n". 
     * Increments the counter after each use to prevent ID duplication. </p>
     * 
     * @return a unique post ID string
     */
    private String generatePostId() {
        String postId = "POST_" + nextPostId;
        nextPostId++;
        return postId;
    }

    /*****
     * <p> Method: getRecentPosts(int count) </p>
     * 
     * <p> Description: Retrieves a list of the most recently created posts, sorted in descending order. 
     * Supports home-page and dashboard views showing the latest activity. </p>
     * 
     * @param count the number of posts to retrieve
     * @return a list of recent posts up to the specified count
     */
    public List<Post> getRecentPosts(int count) {
        return posts.values().stream()
            .filter(post -> !post.isDeleted())
            .sorted((p1, p2) -> p2.getCreatedAt().compareTo(p1.getCreatedAt()))
            .limit(count)
            .collect(Collectors.toList());
    }
}