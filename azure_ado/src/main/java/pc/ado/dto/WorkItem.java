package pc.ado.dto;

/** Data Transfer Object for work item information. */
public class WorkItem {

  private final int id;
  private final String title;
  private final String type;
  private final String state;
  private final String assignedTo;
  private final String plannedVersion;
  private final String storyPoints;
  private final String qaStoryPoints;
  private final String originalStoryPoints;
  private final String priority;
  private final String severity;
  private final String createdDate;
  private final String createdBy;
  private final String devEndDate;
  private final String qaReadyDate;
  private final String qaEndDate;
  private final String tags;
  private final boolean hasImplementationDetails;
  private final java.util.List<Task> tasks = new java.util.ArrayList<>();
  private final java.util.List<PullRequest> pullRequests = new java.util.ArrayList<>();

  public WorkItem(
      int id,
      String title,
      String type,
      String state,
      String assignedTo,
      String plannedVersion,
      String storyPoints,
      String qaStoryPoints,
      String originalStoryPoints,
      String priority,
      String severity,
      String createdDate,
      String createdBy,
      String devEndDate,
      String qaReadyDate,
      String qaEndDate,
      boolean hasImplementationDetails,
      String tags) {
    this.id = id;
    this.title = title;
    this.type = type;
    this.state = state;
    this.assignedTo = assignedTo;
    this.plannedVersion = plannedVersion;
    this.storyPoints = storyPoints;
    this.qaStoryPoints = qaStoryPoints;
    this.originalStoryPoints = originalStoryPoints;
    this.priority = priority;
    this.severity = severity;
    this.createdDate = createdDate;
    this.createdBy = createdBy;
    this.devEndDate = devEndDate;
    this.qaReadyDate = qaReadyDate;
    this.qaEndDate = qaEndDate;
    this.hasImplementationDetails = hasImplementationDetails;
    this.tags = tags;
  }

  public int getId() {
    return id;
  }

  public String getTitle() {
    return title;
  }

  public String getType() {
    return type;
  }

  public String getState() {
    return state;
  }

  public String getAssignedTo() {
    return assignedTo;
  }

  public void addTask(Task task) {
    this.tasks.add(task);
  }

  public java.util.List<Task> getTasks() {
    return tasks;
  }

  public void addPullRequest(PullRequest pullRequest) {
    this.pullRequests.add(pullRequest);
  }

  public java.util.List<PullRequest> getPullRequests() {
    return pullRequests;
  }

  public String getPlannedVersion() {
    return plannedVersion;
  }

  public String getStoryPoints() {
    return storyPoints;
  }

  public String getQaStoryPoints() {
    return qaStoryPoints;
  }

  public String getOriginalStoryPoints() {
    return originalStoryPoints;
  }

  public String getPriority() {
    return priority;
  }

  public String getSeverity() {
    return severity;
  }

  public String getCreatedDate() {
    return createdDate;
  }

  public String getCreatedBy() {
    return createdBy;
  }

  public String getDevEndDate() {
    return devEndDate;
  }

  public String getQaReadyDate() {
    return qaReadyDate;
  }

  public String getQaEndDate() {
    return qaEndDate;
  }

  public boolean isHasImplementationDetails() {
    return hasImplementationDetails;
  }

  public String getTags() {
    return tags;
  }

  /**
   * Creates a new Builder for constructing WorkItem instances.
   *
   * @return a new WorkItem.Builder
   */
  public static Builder builder() {
    return new Builder();
  }

  /**
   * Builder class for constructing WorkItem instances with a fluent API.
   *
   * <p>Implements the Builder pattern to handle the complexity of WorkItem construction with 17+
   * fields. This improves readability and maintainability by:
   *
   * <ul>
   *   <li>Providing clear, self-documenting construction code
   *   <li>Allowing optional fields to be set easily
   *   <li>Avoiding telescoping constructor anti-pattern
   *   <li>Enabling immutability of WorkItem instances
   * </ul>
   */
  public static class Builder {
    private int id;
    private String title = "";
    private String type = "";
    private String state = "";
    private String assignedTo = "";
    private String plannedVersion = "";
    private String storyPoints = "";
    private String qaStoryPoints = "";
    private String originalStoryPoints = "";
    private String priority = "";
    private String severity = "";
    private String createdDate = "";
    private String createdBy = "";
    private String devEndDate = "";
    private String qaReadyDate = "";
    private String qaEndDate = "";
    private String tags = "";
    private boolean hasImplementationDetails = false;

    private Builder() {}

    public Builder id(int id) {
      this.id = id;
      return this;
    }

    public Builder title(String title) {
      this.title = title;
      return this;
    }

    public Builder type(String type) {
      this.type = type;
      return this;
    }

    public Builder state(String state) {
      this.state = state;
      return this;
    }

    public Builder assignedTo(String assignedTo) {
      this.assignedTo = assignedTo;
      return this;
    }

    public Builder plannedVersion(String plannedVersion) {
      this.plannedVersion = plannedVersion;
      return this;
    }

    public Builder storyPoints(String storyPoints) {
      this.storyPoints = storyPoints;
      return this;
    }

    public Builder qaStoryPoints(String qaStoryPoints) {
      this.qaStoryPoints = qaStoryPoints;
      return this;
    }

    public Builder originalStoryPoints(String originalStoryPoints) {
      this.originalStoryPoints = originalStoryPoints;
      return this;
    }

    public Builder priority(String priority) {
      this.priority = priority;
      return this;
    }

    public Builder severity(String severity) {
      this.severity = severity;
      return this;
    }

    public Builder createdDate(String createdDate) {
      this.createdDate = createdDate;
      return this;
    }

    public Builder createdBy(String createdBy) {
      this.createdBy = createdBy;
      return this;
    }

    public Builder devEndDate(String devEndDate) {
      this.devEndDate = devEndDate;
      return this;
    }

    public Builder qaReadyDate(String qaReadyDate) {
      this.qaReadyDate = qaReadyDate;
      return this;
    }

    public Builder qaEndDate(String qaEndDate) {
      this.qaEndDate = qaEndDate;
      return this;
    }

    public Builder tags(String tags) {
      this.tags = tags;
      return this;
    }

    public Builder hasImplementationDetails(boolean hasImplementationDetails) {
      this.hasImplementationDetails = hasImplementationDetails;
      return this;
    }

    /**
     * Builds and returns an immutable WorkItem instance.
     *
     * @return a new WorkItem
     */
    public WorkItem build() {
      return new WorkItem(
          id,
          title,
          type,
          state,
          assignedTo,
          plannedVersion,
          storyPoints,
          qaStoryPoints,
          originalStoryPoints,
          priority,
          severity,
          createdDate,
          createdBy,
          devEndDate,
          qaReadyDate,
          qaEndDate,
          hasImplementationDetails,
          tags);
    }
  }

  /** Inner class representing a task within a work item. */
  public static class Task {

    private final String taskId;
    private final String taskType;
    private final String state;
    private final String assignedTo;
    private final String originalEstimate;
    private final String remainingWork;
    private final String completedWork;

    public Task(
        String taskId,
        String taskType,
        String state,
        String assignedTo,
        String originalEstimate,
        String remainingWork,
        String completedWork) {
      this.taskId = taskId;
      this.taskType = taskType;
      this.state = state;
      this.assignedTo = assignedTo;
      this.originalEstimate = originalEstimate;
      this.remainingWork = remainingWork;
      this.completedWork = completedWork;
    }

    public String getTaskId() {
      return taskId;
    }

    public String getTaskType() {
      return taskType;
    }

    public String getState() {
      return state;
    }

    public String getAssignedTo() {
      return assignedTo;
    }

    public String getOriginalEstimate() {
      return originalEstimate;
    }

    public String getRemainingWork() {
      return remainingWork;
    }

    public String getCompletedString() {
      return completedWork;
    }

    public String getCompletedWork() {
      return completedWork;
    }

    @Override
    public String toString() {
      return "Task{"
          + "taskId='"
          + taskId
          + '\''
          + ", taskType='"
          + taskType
          + '\''
          + ", state='"
          + state
          + '\''
          + ", assignedTo='"
          + assignedTo
          + '\''
          + ", originalEstimate='"
          + originalEstimate
          + '\''
          + ", remainingWork='"
          + remainingWork
          + '\''
          + ", completedWork='"
          + completedWork
          + '\''
          + '}';
    }
  }
}
