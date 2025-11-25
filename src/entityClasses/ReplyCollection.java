package entityClasses;

import java.util.*;
import java.util.stream.Collectors;

/*******
 * <p> Title: ReplyCollection Class </p>
 * 
 * <p> Description: This ReplyCollection class manages a collection of Reply objects. It provides
 * methods for CRUD operations, filtering, and managing replies according to the requirements
 * specified in the document.</p>
 * 
 * <p> Copyright: Joseph © 2025 </p>
 * 
 * @author Joseph
 * 
 * @version 1.00		2025-01-16 Initial version
 */ 

public class ReplyCollection {
    
    /*
     * These are the private attributes for this collection
     */
    private Map<String, Reply> replies;
    private int nextReplyId;
    
    /*****
     * <p> Method: ReplyCollection() </p>
     * 
     * <p> Description: This constructor initializes an empty collection of replies. </p>
     */
    public ReplyCollection() {
        this.replies = new HashMap<>();
        this.nextReplyId = 1;
    }

    /*****
     * <p> Method: String createReply(String body, String authorUsername, String parentPostId) </p>
     * 
     * <p> Description: Creates a new reply with validation. </p>
     * 
     * @param body the body content of the reply
     * @param authorUsername the username of the reply author
     * @param parentPostId the ID of the post this reply belongs to
     * 
     * @return the reply ID if successful, or an error message if validation fails
     * 
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
     * <p> Method: String createFeedback(String body, String authorUsername, String parentPostId) </p>
     * 
     * <p> Description: Creates a new feedback reply (private feedback from staff) with validation. </p>
     * 
     * @param body the body content of the feedback
     * @param authorUsername the username of the staff member providing feedback
     * @param parentPostId the ID of the parent post
     * 
     * @return the reply ID if successful, or an error message if validation fails
     * 
     */
    public String createFeedback(String body, String authorUsername, String parentPostId) {
        Reply newReply = new Reply(generateReplyId(), body, authorUsername, parentPostId, true);
        String validationError = newReply.validateReply();
        
        if (!validationError.isEmpty()) {
            return validationError;
        }
        
        replies.put(newReply.getReplyId(), newReply);
        return newReply.getReplyId();
    }

    /*****
     * <p> Method: void addReply(Reply reply) </p>
     * 
     * <p> Description: Adds an existing reply to the collection (used when loading from database). </p>
     * 
     * @param reply the reply to add
     * 
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
     * <p> Method: List<Reply> getAllReplies() </p>
     * 
     * <p> Description: Returns all replies in the collection. </p>
     * 
     * @return list of all replies
     * 
     */
    public List<Reply> getAllReplies() {
        return new ArrayList<>(replies.values());
    }

    /*****
     * <p> Method: List<Reply> getRepliesForPost(String postId) </p>
     * 
     * <p> Description: Returns all replies for a specific post (excluding feedback). </p>
     * 
     * @param postId the ID of the parent post
     * 
     * @return list of replies for the post (non-feedback replies)
     * 
     */
    public List<Reply> getRepliesForPost(String postId) {
        return replies.values().stream()
            .filter(reply -> postId.equals(reply.getParentPostId()))
            .filter(reply -> !reply.isFeedback()) // Exclude feedback from regular replies
            .sorted((r1, r2) -> r1.getCreatedAt().compareTo(r2.getCreatedAt())) // Oldest first
            .collect(Collectors.toList());
    }
    
    /*****
     * <p> Method: List<Reply> getFeedbackForPost(String postId, String currentUsername, String postAuthorUsername) </p>
     * 
     * <p> Description: Returns feedback replies for a specific post. Only visible to the post author and the feedback author. </p>
     * 
     * @param postId the ID of the parent post
     * @param currentUsername the username of the current user viewing the post
     * @param postAuthorUsername the username of the post author
     * 
     * @return list of feedback replies visible to the current user
     * 
     */
    public List<Reply> getFeedbackForPost(String postId, String currentUsername, String postAuthorUsername) {
        return replies.values().stream()
            .filter(reply -> postId.equals(reply.getParentPostId()))
            .filter(reply -> reply.isFeedback())
            .filter(reply -> reply.getAuthorUsername().equals(currentUsername) || postAuthorUsername.equals(currentUsername))
            .sorted((r1, r2) -> r1.getCreatedAt().compareTo(r2.getCreatedAt())) // Oldest first
            .collect(Collectors.toList());
    }

    /*****
     * <p> Method: List<Reply> getRepliesForPost(String postId, boolean unreadOnly) </p>
     * 
     * <p> Description: Returns replies for a specific post, optionally filtered by read status. </p>
     * 
     * @param postId the ID of the parent post
     * @param unreadOnly if true, only return unread replies
     * 
     * @return list of replies for the post
     * 
     */
    public List<Reply> getRepliesForPost(String postId, boolean unreadOnly) {
        return replies.values().stream()
            .filter(reply -> postId.equals(reply.getParentPostId()))
            .filter(reply -> !unreadOnly || reply.isUnread())
            .sorted((r1, r2) -> r1.getCreatedAt().compareTo(r2.getCreatedAt())) // Oldest first
            .collect(Collectors.toList());
    }

    /*****
     * <p> Method: List<Reply> getRepliesByAuthor(String authorUsername) </p>
     * 
     * <p> Description: Returns all replies by a specific author. </p>
     * 
     * @param authorUsername the username of the author
     * 
     * @return list of replies by the author
     * 
     */
    public List<Reply> getRepliesByAuthor(String authorUsername) {
        return replies.values().stream()
            .filter(reply -> authorUsername.equals(reply.getAuthorUsername()))
            .sorted((r1, r2) -> r2.getCreatedAt().compareTo(r1.getCreatedAt())) // Newest first
            .collect(Collectors.toList());
    }

    /*****
     * <p> Method: List<Reply> getUnreadRepliesForPost(String postId, String currentUsername) </p>
     * 
     * <p> Description: Returns unread replies for a specific post for the current user. </p>
     * 
     * @param postId the ID of the parent post
     * @param currentUsername the username of the current user
     * 
     * @return list of unread replies for the post
     * 
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
     * <p> Method: int getReplyCountForPost(String postId) </p>
     * 
     * <p> Description: Returns the number of replies for a specific post. </p>
     * 
     * @param postId the ID of the parent post
     * 
     * @return the number of replies
     * 
     */
    public int getReplyCountForPost(String postId) {
        return (int) replies.values().stream()
            .filter(reply -> postId.equals(reply.getParentPostId()))
            .count();
    }

    /*****
     * <p> Method: int getUnreadReplyCountForPost(String postId, String currentUsername) </p>
     * 
     * <p> Description: Returns the number of unread replies for a specific post. </p>
     * 
     * @param postId the ID of the parent post
     * @param currentUsername the username of the current user
     * 
     * @return the number of unread replies
     * 
     */
    public int getUnreadReplyCountForPost(String postId, String currentUsername) {
        return getUnreadRepliesForPost(postId, currentUsername).size();
    }

    /*****
     * <p> Method: String updateReply(String replyId, String newBody, String currentUsername) </p>
     * 
     * <p> Description: Updates an existing reply with validation. </p>
     * 
     * @param replyId the ID of the reply to update
     * @param newBody the new body content
     * @param currentUsername the username of the current user
     * 
     * @return empty string if successful, error message if failed
     * 
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
     * <p> Method: String deleteReply(String replyId, String currentUsername) </p>
     * 
     * <p> Description: Marks a reply as deleted with confirmation. </p>
     * 
     * @param replyId the ID of the reply to delete
     * @param currentUsername the username of the current user
     * 
     * @return empty string if successful, error message if failed
     * 
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
     * <p> Method: Reply getReplyById(String replyId) </p>
     * 
     * <p> Description: Retrieves a reply by its ID. </p>
     * 
     * @param replyId the ID of the reply
     * 
     * @return the reply if found, null otherwise
     * 
     */
    public Reply getReplyById(String replyId) {
        return replies.get(replyId);
    }

    /*****
     * <p> Method: boolean replyExists(String replyId) </p>
     * 
     * <p> Description: Checks if a reply exists. </p>
     * 
     * @param replyId the ID of the reply
     * 
     * @return true if the reply exists, false otherwise
     * 
     */
    public boolean replyExists(String replyId) {
        return replies.containsKey(replyId);
    }

    /*****
     * <p> Method: void markRepliesAsRead(String postId, String currentUsername) </p>
     * 
     * <p> Description: Marks all replies for a post as read for the current user. </p>
     * 
     * @param postId the ID of the parent post
     * @param currentUsername the username of the current user
     * 
     */
    public void markRepliesAsRead(String postId, String currentUsername) {
        replies.values().stream()
            .filter(reply -> postId.equals(reply.getParentPostId()))
            .filter(reply -> !reply.getAuthorUsername().equals(currentUsername)) // Don't mark own replies
            .forEach(Reply::markAsRead);
    }

    /*****
     * <p> Method: void markReplyAsRead(String replyId) </p>
     * 
     * <p> Description: Marks a specific reply as read. </p>
     * 
     * @param replyId the ID of the reply
     * 
     */
    public void markReplyAsRead(String replyId) {
        Reply reply = replies.get(replyId);
        if (reply != null) {
            reply.markAsRead();
        }
    }

    /*****
     * <p> Method: int getReplyCount() </p>
     * 
     * <p> Description: Returns the total number of replies in the collection. </p>
     * 
     * @return the number of replies
     * 
     */
    public int getReplyCount() {
        return replies.size();
    }

    /*****
     * <p> Method: int getActiveReplyCount() </p>
     * 
     * <p> Description: Returns the number of non-deleted replies in the collection. </p>
     * 
     * @return the number of active replies
     * 
     */
    public int getActiveReplyCount() {
        return (int) replies.values().stream()
            .filter(reply -> !reply.isDeleted())
            .count();
    }

    /*****
     * <p> Method: String generateReplyId() </p>
     * 
     * <p> Description: Generates a unique reply ID. </p>
     * 
     * @return a unique reply ID
     * 
     */
    private String generateReplyId() {
        String replyId = "REPLY_" + nextReplyId;
        nextReplyId++;
        return replyId;
    }

    /*****
     * <p> Method: List<Reply> getRecentReplies(int count) </p>
     * 
     * <p> Description: Returns the most recent replies. </p>
     * 
     * @param count the number of recent replies to return
     * 
     * @return list of recent replies
     * 
     */
    public List<Reply> getRecentReplies(int count) {
        return replies.values().stream()
            .filter(reply -> !reply.isDeleted())
            .sorted((r1, r2) -> r2.getCreatedAt().compareTo(r1.getCreatedAt()))
            .limit(count)
            .collect(Collectors.toList());
    }

    /*****
     * <p> Method: Map<String, Integer> getReplyCountsByPost() </p>
     * 
     * <p> Description: Returns a map of post IDs to their reply counts. </p>
     * 
     * @return map of post IDs to reply counts
     * 
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
