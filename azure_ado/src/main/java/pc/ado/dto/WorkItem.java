package pc.ado.dto;

/**
 * Data Transfer Object for work item information.
 */
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

    public WorkItem(int id, String title, String type, String state, String assignedTo, String plannedVersion,
            String storyPoints, String qaStoryPoints, String originalStoryPoints,
            String priority, String severity, String createdDate, String createdBy,
            String devEndDate, String qaReadyDate, String qaEndDate, boolean hasImplementationDetails, String tags) {
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
     * Inner class representing a task within a work item.
     */
    public static class Task {

        private final String taskType;
        private final String state;
        private final String assignedTo;
        private final String originalEstimate;
        private final String remainingWork;
        private final String completedWork;

        public Task(String taskType, String state, String assignedTo, String originalEstimate, String remainingWork, String completedWork) {
            this.taskType = taskType;
            this.state = state;
            this.assignedTo = assignedTo;
            this.originalEstimate = originalEstimate;
            this.remainingWork = remainingWork;
            this.completedWork = completedWork;
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

        @Override
        public String toString() {
            return "Task{"
                    + "taskType='" + taskType + '\''
                    + ", state='" + state + '\''
                    + ", assignedTo='" + assignedTo + '\''
                    + ", originalEstimate='" + originalEstimate + '\''
                    + ", remainingWork='" + remainingWork + '\''
                    + ", completedWork='" + completedWork + '\''
                    + '}';
        }
    }

}
