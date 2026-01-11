package pc.ado.dto;

import java.util.HashMap;
import java.util.Map;

/**
 * Data Transfer Object for pull request thread information.
 */
public class PullRequestThread {

    private final String threadId;
    private final String status;
    private final boolean isDeleted;
    private final Map<String, String[]> commenters;

    public PullRequestThread(String threadId, String status, boolean isDeleted) {
        this.threadId = threadId;
        this.status = status;
        this.isDeleted = isDeleted;
        this.commenters = new HashMap<>();
    }

    public String getThreadId() {
        return threadId;
    }

    public String getStatus() {
        return status;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public Map<String, String[]> getCommenters() {
        return commenters;
    }

    public void addCommenter(String author, String[] commentedDates) {
        this.commenters.put(author, commentedDates);
    }

    @Override
    public String toString() {
        return "PullRequestThread{"
                + "threadId='" + threadId + '\''
                + ", status='" + status + '\''
                + ", isDeleted=" + isDeleted
                + ", commenters=" + commenters
                + '}';
    }
}
