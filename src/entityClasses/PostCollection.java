package entityClasses;

import java.util.*;
import java.util.stream.Collectors;

/*******
 * <p> Title: PostCollection Class </p>
 * 
 * <p> Description: This PostCollection class manages a collection of Post objects. It provides
 * methods for CRUD operations, searching, filtering, and managing posts according to the
 * requirements specified in the document.</p>
 * 
 * <p> Copyright: Joseph © 2025 </p>
 * 
 * @author Joseph
 * 
 * @version 1.00		2025-01-16 Initial version
 */ 

public class PostCollection {
    
    /*
     * These are the private attributes for this collection
     */
    private Map<String, Post> posts;
    private int nextPostId;
    
    /*****
     * <p> Method: PostCollection() </p>
     * 
     * <p> Description: This constructor initializes an empty collection of posts. </p>
     */
    public PostCollection() {
        this.posts = new HashMap<>();
        this.nextPostId = 1;
    }

    /*****
     * <p> Method: String createPost(String title, String body, String authorUsername, String thread) </p>
     * 
     * <p> Description: Creates a new post with validation. </p>
     * 
     * @param title the title of the post
     * @param body the body content of the post
     * @param authorUsername the username of the post author
     * @param thread the thread this post belongs to (can be null for default)
     * 
     * @return the post ID if successful, or an error message if validation fails
     * 
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
     * <p> Method: void addPost(Post post) </p>
     * 
     * <p> Description: Adds an existing post to the collection (used when loading from database). </p>
     * 
     * @param post the post to add
     * 
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
     * <p> Method: List<Post> getAllPosts() </p>
     * 
     * <p> Description: Returns all posts in the collection. </p>
     * 
     * @return list of all posts
     * 
     */
    public List<Post> getAllPosts() {
        return new ArrayList<>(posts.values());
    }

    /*****
     * <p> Method: List<Post> searchPosts(String keyword, String threadFilter) </p>
     * 
     * <p> Description: Searches for posts matching the keyword and thread filter. </p>
     * 
     * @param keyword the search keyword (can be null or empty for all posts)
     * @param threadFilter the thread to filter by (can be "All" for all threads)
     * 
     * @return list of matching posts
     * 
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
     * <p> Method: List<Post> getPostsByAuthor(String authorUsername) </p>
     * 
     * <p> Description: Returns all posts by a specific author. </p>
     * 
     * @param authorUsername the username of the author
     * 
     * @return list of posts by the author
     * 
     */
    public List<Post> getPostsByAuthor(String authorUsername) {
        return posts.values().stream()
            .filter(post -> authorUsername.equals(post.getAuthorUsername()))
            .sorted((p1, p2) -> p2.getCreatedAt().compareTo(p1.getCreatedAt())) // Newest first
            .collect(Collectors.toList());
    }

    /*****
     * <p> Method: List<Post> getPostsByThread(String thread) </p>
     * 
     * <p> Description: Returns all posts in a specific thread. </p>
     * 
     * @param thread the thread name
     * 
     * @return list of posts in the thread
     * 
     */
    public List<Post> getPostsByThread(String thread) {
        return posts.values().stream()
            .filter(post -> thread.equals(post.getThread()))
            .sorted((p1, p2) -> p2.getCreatedAt().compareTo(p1.getCreatedAt())) // Newest first
            .collect(Collectors.toList());
    }

    /*****
     * <p> Method: String updatePost(String postId, String newTitle, String newBody, String currentUsername) </p>
     * 
     * <p> Description: Updates an existing post with validation. </p>
     * 
     * @param postId the ID of the post to update
     * @param newTitle the new title
     * @param newBody the new body content
     * @param currentUsername the username of the current user
     * 
     * @return empty string if successful, error message if failed
     * 
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
     * <p> Method: String deletePost(String postId, String currentUsername) </p>
     * 
     * <p> Description: Marks a post as deleted with confirmation. </p>
     * 
     * @param postId the ID of the post to delete
     * @param currentUsername the username of the current user
     * 
     * @return empty string if successful, error message if failed
     * 
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
     * <p> Method: Post getPostById(String postId) </p>
     * 
     * <p> Description: Retrieves a post by its ID. </p>
     * 
     * @param postId the ID of the post
     * 
     * @return the post if found, null otherwise
     * 
     */
    public Post getPostById(String postId) {
        return posts.get(postId);
    }

    /*****
     * <p> Method: boolean postExists(String postId) </p>
     * 
     * <p> Description: Checks if a post exists. </p>
     * 
     * @param postId the ID of the post
     * 
     * @return true if the post exists, false otherwise
     * 
     */
    public boolean postExists(String postId) {
        return posts.containsKey(postId);
    }

    /*****
     * <p> Method: Set<String> getAllThreads() </p>
     * 
     * <p> Description: Returns all unique thread names in the collection. </p>
     * 
     * @return set of all thread names
     * 
     */
    public Set<String> getAllThreads() {
        return posts.values().stream()
            .map(Post::getThread)
            .collect(Collectors.toSet());
    }

    /*****
     * <p> Method: int getPostCount() </p>
     * 
     * <p> Description: Returns the total number of posts in the collection. </p>
     * 
     * @return the number of posts
     * 
     */
    public int getPostCount() {
        return posts.size();
    }

    /*****
     * <p> Method: int getActivePostCount() </p>
     * 
     * <p> Description: Returns the number of non-deleted posts in the collection. </p>
     * 
     * @return the number of active posts
     * 
     */
    public int getActivePostCount() {
        return (int) posts.values().stream()
            .filter(post -> !post.isDeleted())
            .count();
    }

    /*****
     * <p> Method: String generatePostId() </p>
     * 
     * <p> Description: Generates a unique post ID. </p>
     * 
     * @return a unique post ID
     * 
     */
    private String generatePostId() {
        String postId = "POST_" + nextPostId;
        nextPostId++;
        return postId;
    }

    /*****
     * <p> Method: List<Post> getRecentPosts(int count) </p>
     * 
     * <p> Description: Returns the most recent posts. </p>
     * 
     * @param count the number of recent posts to return
     * 
     * @return list of recent posts
     * 
     */
    public List<Post> getRecentPosts(int count) {
        return posts.values().stream()
            .filter(post -> !post.isDeleted())
            .sorted((p1, p2) -> p2.getCreatedAt().compareTo(p1.getCreatedAt()))
            .limit(count)
            .collect(Collectors.toList());
    }
}