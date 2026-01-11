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
    private final java.util.List<Task> tasks = new java.util.ArrayList<>();
    private final java.util.List<PullRequest> pullRequests = new java.util.ArrayList<>();

    public WorkItem(int id, String title, String type, String state, String assignedTo) {
        this.id = id;
        this.title = title;
        this.type = type;
        this.state = state;
        this.assignedTo = assignedTo;
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

    /**
     * Inner class representing a task within a work item.
     */
    public static class Task {

        private final String taskType;
        private final String state;
        private final String assignedTo;
        private final String originalEstimate;
        private final String remainingWork;

        public Task(String taskType, String state, String assignedTo, String originalEstimate, String remainingWork) {
            this.taskType = taskType;
            this.state = state;
            this.assignedTo = assignedTo;
            this.originalEstimate = originalEstimate;
            this.remainingWork = remainingWork;
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

        @Override
        public String toString() {
            return "Task{"
                    + "taskType='" + taskType + '\''
                    + ", state='" + state + '\''
                    + ", assignedTo='" + assignedTo + '\''
                    + ", originalEstimate='" + originalEstimate + '\''
                    + ", remainingWork='" + remainingWork + '\''
                    + '}';
        }
    }

}
