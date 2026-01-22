package pc.ado.dto;

/**
 * Data Transfer Object for a thread comment containing the date and content.
 */
public class ThreadComment {

    private final String commentedDate;
    private final String commentContent;

    public ThreadComment(String commentedDate, String commentContent) {
        this.commentedDate = commentedDate;
        this.commentContent = commentContent;
    }

    public String getCommentedDate() {
        return commentedDate;
    }

    public String getCommentContent() {
        return commentContent;
    }

    @Override
    public String toString() {
        return "ThreadComment{"
                + "commentedDate='" + commentedDate + '\''
                + ", commentContent='" + commentContent + '\''
                + '}';
    }
}
