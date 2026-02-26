package pc.ado.dto;

import java.util.ArrayList;
import java.util.List;

/** Data Transfer Object for pull request information. */
public class PullRequest {

  private final String pullRequestId;
  private final String createdBy;
  private final String creationDate;
  private final List<PullRequestThread> threads = new ArrayList<>();

  public PullRequest(
      final String pullRequestId, final String createdBy, final String creationDate) {
    this.pullRequestId = pullRequestId;
    this.createdBy = createdBy;
    this.creationDate = creationDate;
  }

  public String getPullRequestId() {
    return pullRequestId;
  }

  public String getCreatedBy() {
    return createdBy;
  }

  public String getCreationDate() {
    return creationDate;
  }

  public void addThread(final PullRequestThread thread) {
    this.threads.add(thread);
  }

  public List<PullRequestThread> getThreads() {
    return threads;
  }

  @Override
  public String toString() {
    return "PullRequest{"
        + "pullRequestId='"
        + pullRequestId
        + '\''
        + ", createdBy='"
        + createdBy
        + '\''
        + ", creationDate='"
        + creationDate
        + '\''
        + ", threads="
        + threads
        + '}';
  }
}
