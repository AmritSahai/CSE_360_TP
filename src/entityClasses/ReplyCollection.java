package entityClasses;

import java.util.*;
import java.util.stream.Collectors;

/*******
 * <p> Title: ReplyCollection Class </p>
 * 
 * <p> Description: The ReplyCollection class manages all Reply objects created in the discussion system. 
 * It serves as a container and controller for reply entities, enabling Create, Read, Update, and Delete 
 * (CRUD) operations as well as filtering and tracking unread messages. </p>
 * 
 * <p> The ReplyCollection class supports the student and staff user stories that involve replying to posts, 
 * reading and managing replies, viewing unread messages, and maintaining discussion integrity through 
 * validation and access control. </p>
 * 
 * <p> Copyright: Joseph © 2025 </p>
 * 
 * @author Joseph and Vrishik
 * 
 * @version 2.00		2025-10-19 TP2 Updated Javadoc Version
 */ 

public class ReplyCollection {
    
    /*
     * These are the private attributes for this collection
     */
	
	/** 
     * A mapping of reply IDs to Reply objects.
     * Provides quick access and ensures each reply has a unique identifier.
     */
    private Map<String, Reply> replies;
    
    /** 
     * Counter used to generate sequential unique reply IDs.
     */
    private int nextReplyId;
    
    /*****
     * <p> Method: ReplyCollection() </p>
     * 
     * <p> Description: Constructs an empty collection of replies. 
     * Initializes the internal map and sets the starting ID counter. </p>
     */
    public ReplyCollection() {
        this.replies = new HashMap<>();
        this.nextReplyId = 1;
    }

    /*****
     * <p> Method: createReply(String body, String authorUsername, String parentPostId) </p>
     * 
     * <p> Description: Creates and validates a new reply before adding it to the collection. 
     * Supports the student feature that allows posting replies to existing discussion posts. </p>
     * 
     * @param body the reply content text
     * @param authorUsername the username of the reply author
     * @param parentPostId the ID of the parent post being replied to
     * @return the reply ID if successful, or a descriptive error message if validation fails
     */
    public String createReply(String body, String authorUsername, String parentPostId) {
        Reply newReply = new Reply(generateReplyId(), body, authorUsername, parentPostId);
        String validationError = newReply.validateReply();
        
        if (!validationError.isEmpty()) {
            return validationError;
        }
        
        replies.put(newReply.getReplyId(), newReply);
        return newReply.getReplyId();
    }

    /*****
     * <p> Method: addReply(Reply reply) </p>
     * 
     * <p> Description: Adds an existing reply to the collection, typically used when loading 
     * replies from a database or saved file. Automatically adjusts the ID counter if needed. </p>
     * 
     * @param reply the Reply object to add
     */
    public void addReply(Reply reply) {
        replies.put(reply.getReplyId(), reply);
        // Update nextReplyId if needed
        try {
            int replyNum = Integer.parseInt(reply.getReplyId().replace("REPLY_", ""));
            if (replyNum >= nextReplyId) {
                nextReplyId = replyNum + 1;
            }
        } catch (NumberFormatException e) {
            // Ignore if replyId format is different
        }
    }

    /*****
     * <p> Method: getAllReplies() </p>
     * 
     * <p> Description: Returns all replies in the collection, including deleted ones. 
     * Supports staff review and debugging features. </p>
     * 
     * @return a list of all Reply objects
     */
    public List<Reply> getAllReplies() {
        return new ArrayList<>(replies.values());
    }

    /*****
     * <p> Method: getRepliesForPost(String postId) </p>
     * 
     * <p> Description: Retrieves all replies linked to a specific post. 
     * Supports student and staff viewing of discussion threads. </p>
     * 
     * @param postId the ID of the parent post
     * @return a list of replies for the given post, ordered from oldest to newest
     */
    public List<Reply> getRepliesForPost(String postId) {
        return replies.values().stream()
            .filter(reply -> postId.equals(reply.getParentPostId()))
            .sorted((r1, r2) -> r1.getCreatedAt().compareTo(r2.getCreatedAt())) // Oldest first
            .collect(Collectors.toList());
    }

    /*****
     * <p> Method: getRepliesForPost(String postId, boolean unreadOnly) </p>
     * 
     * <p> Description: Retrieves all replies for a specific post, optionally filtering 
     * by unread status. Supports the feature that allows users to focus only on unread replies. </p>
     * 
     * @param postId the ID of the parent post
     * @param unreadOnly true to return only unread replies; false to return all
     * @return a list of replies matching the given filters
     */
    public List<Reply> getRepliesForPost(String postId, boolean unreadOnly) {
        return replies.values().stream()
            .filter(reply -> postId.equals(reply.getParentPostId()))
            .filter(reply -> !unreadOnly || reply.isUnread())
            .sorted((r1, r2) -> r1.getCreatedAt().compareTo(r2.getCreatedAt())) // Oldest first
            .collect(Collectors.toList());
    }

    /*****
     * <p> Method: getRepliesByAuthor(String authorUsername) </p>
     * 
     * <p> Description: Retrieves all replies authored by the specified user. 
     * Supports the student feature that allows viewing one’s own replies. </p>
     * 
     * @param authorUsername the username of the author
     * @return a list of replies created by that author, newest first
     */
    public List<Reply> getRepliesByAuthor(String authorUsername) {
        return replies.values().stream()
            .filter(reply -> authorUsername.equals(reply.getAuthorUsername()))
            .sorted((r1, r2) -> r2.getCreatedAt().compareTo(r1.getCreatedAt())) // Newest first
            .collect(Collectors.toList());
    }

    /*****
     * <p> Method: getUnreadRepliesForPost(String postId, String currentUsername) </p>
     * 
     * <p> Description: Retrieves all unread replies for a given post that were written by 
     * other users. Supports the student feature that helps track unread messages in discussions. </p>
     * 
     * @param postId the ID of the parent post
     * @param currentUsername the username of the current user
     * @return a list of unread replies for that post
     */
    public List<Reply> getUnreadRepliesForPost(String postId, String currentUsername) {
        return replies.values().stream()
            .filter(reply -> postId.equals(reply.getParentPostId()))
            .filter(reply -> !reply.getAuthorUsername().equals(currentUsername)) // Don't count own replies
            .filter(Reply::isUnread)
            .sorted((r1, r2) -> r1.getCreatedAt().compareTo(r2.getCreatedAt())) // Oldest first
            .collect(Collectors.toList());
    }

    /*****
     * <p> Method: getReplyCountForPost(String postId) </p>
     * 
     * <p> Description: Returns the total number of replies associated with a specific post. </p>
     * 
     * @param postId the parent post ID
     * @return the number of replies for that post
     */
    public int getReplyCountForPost(String postId) {
        return (int) replies.values().stream()
            .filter(reply -> postId.equals(reply.getParentPostId()))
            .count();
    }

    /*****
     * <p> Method: getUnreadReplyCountForPost(String postId, String currentUsername) </p>
     * 
     * <p> Description: Returns the number of unread replies for a post written by other users. </p>
     * 
     * @param postId the parent post ID
     * @param currentUsername the logged-in user’s username
     * @return the count of unread replies
     */
    public int getUnreadReplyCountForPost(String postId, String currentUsername) {
        return getUnreadRepliesForPost(postId, currentUsername).size();
    }

    /*****
     * <p> Method: updateReply(String replyId, String newBody, String currentUsername) </p>
     * 
     * <p> Description: Updates the text of an existing reply after validation and permission checks. 
     * Supports the student feature that allows editing one’s own replies. </p>
     * 
     * @param replyId the ID of the reply to update
     * @param newBody the new body text
     * @param currentUsername the username of the user requesting the edit
     * @return an empty string if successful, or an error message if validation fails
     */
    public String updateReply(String replyId, String newBody, String currentUsername) {
        Reply reply = replies.get(replyId);
        
        if (reply == null) {
            return "Reply not found.";
        }
        
        if (!reply.canEdit(currentUsername)) {
            return "You can only edit your own replies.";
        }
        
        // Create temporary reply to validate new content
        Reply tempReply = new Reply(replyId, newBody, currentUsername, reply.getParentPostId());
        String validationError = tempReply.validateReply();
        
        if (!validationError.isEmpty()) {
            return validationError;
        }
        
        reply.updateContent(newBody);
        return ""; // Success
    }

    /*****
     * <p> Method: deleteReply(String replyId, String currentUsername) </p>
     * 
     * <p> Description: Marks a reply as deleted after verifying ownership. 
     * Supports the student feature that allows removing one’s own replies. </p>
     * 
     * @param replyId the ID of the reply to delete
     * @param currentUsername the username of the user requesting deletion
     * @return an empty string if successful, or an error message if not permitted
     */
    public String deleteReply(String replyId, String currentUsername) {
        Reply reply = replies.get(replyId);
        
        if (reply == null) {
            return "Reply not found.";
        }
        
        if (!reply.canDelete(currentUsername)) {
            return "You can only delete your own replies.";
        }
        
        reply.markAsDeleted();
        return ""; // Success
    }

    /*****
     * <p> Method: getReplyById(String replyId) </p>
     * 
     * <p> Description: Retrieves a reply object by its unique ID. </p>
     * 
     * @param replyId the reply’s unique ID
     * @return the corresponding Reply object, or null if not found
     */
    public Reply getReplyById(String replyId) {
        return replies.get(replyId);
    }

    /*****
     * <p> Method: replyExists(String replyId) </p>
     * 
     * <p> Description: Determines whether a reply with the specified ID exists in the collection. </p>
     * 
     * @param replyId the reply ID to check
     * @return true if the reply exists; false otherwise
     */
    public boolean replyExists(String replyId) {
        return replies.containsKey(replyId);
    }

    /*****
     * <p> Method: markRepliesAsRead(String postId, String currentUsername) </p>
     * 
     * <p> Description: Marks all replies for a given post as read for the specified user. 
     * Supports the staff and student feature that tracks unread message indicators. </p>
     * 
     * @param postId the parent post ID
     * @param currentUsername the user marking replies as read
     */
    public void markRepliesAsRead(String postId, String currentUsername) {
        replies.values().stream()
            .filter(reply -> postId.equals(reply.getParentPostId()))
            .filter(reply -> !reply.getAuthorUsername().equals(currentUsername)) // Don't mark own replies
            .forEach(Reply::markAsRead);
    }

    /*****
     * <p> Method: markReplyAsRead(String replyId) </p>
     * 
     * <p> Description: Marks a specific reply as read. </p>
     * 
     * @param replyId the ID of the reply to mark as read
     */
    public void markReplyAsRead(String replyId) {
        Reply reply = replies.get(replyId);
        if (reply != null) {
            reply.markAsRead();
        }
    }

    /*****
     * <p> Method: getReplyCount() </p>
     * 
     * <p> Description: Returns the total number of replies stored in the collection. </p>
     * 
     * @return the total reply count
     */
    public int getReplyCount() {
        return replies.size();
    }

    /*****
     * <p> Method: getActiveReplyCount() </p>
     * 
     * <p> Description: Returns the number of replies that are not marked as deleted. </p>
     * 
     * @return the count of active (non-deleted) replies
     */
    public int getActiveReplyCount() {
        return (int) replies.values().stream()
            .filter(reply -> !reply.isDeleted())
            .count();
    }

    /*****
     * <p> Method: generateReplyId() </p>
     * 
     * <p> Description: Generates a unique reply ID using the format "REPLY_n". 
     * Increments the ID counter to prevent duplication. </p>
     * 
     * @return the generated reply ID string
     */
    private String generateReplyId() {
        String replyId = "REPLY_" + nextReplyId;
        nextReplyId++;
        return replyId;
    }

    /*****
     * <p> Method: getRecentReplies(int count) </p>
     * 
     * <p> Description: Retrieves the most recent replies based on creation time. 
     * Supports dashboards and “Recent Activity” views. </p>
     * 
     * @param count the number of recent replies to return
     * @return a list of recent Reply objects
     */
    public List<Reply> getRecentReplies(int count) {
        return replies.values().stream()
            .filter(reply -> !reply.isDeleted())
            .sorted((r1, r2) -> r2.getCreatedAt().compareTo(r1.getCreatedAt()))
            .limit(count)
            .collect(Collectors.toList());
    }

    /*****
     * <p> Method: getReplyCountsByPost() </p>
     * 
     * <p> Description: Generates a map of post IDs to their corresponding active reply counts. 
     * Supports staff analytics and dashboard statistics. </p>
     * 
     * @return a map pairing post IDs with their reply counts
     */
    public Map<String, Integer> getReplyCountsByPost() {
        Map<String, Integer> replyCounts = new HashMap<>();
        
        for (Reply reply : replies.values()) {
            if (!reply.isDeleted()) {
                String postId = reply.getParentPostId();
                replyCounts.put(postId, replyCounts.getOrDefault(postId, 0) + 1);
            }
        }
        
        return replyCounts;
    }
}