package pc.ado.formatter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pc.ado.dto.Iteration;
import pc.ado.dto.PullRequest;
import pc.ado.dto.PullRequestThread;
import pc.ado.dto.TeamMemberAllocation;
import pc.ado.dto.ThreadComment;
import pc.ado.dto.WorkItem;

/**
 * Formats iterations data to tab-separated values output. Explodes hierarchy
 * into individual row items with parent fields repeated for each child.
 */
public class TsvIterationFormatter implements IterationFormatter {

    private static final Logger logger = LoggerFactory.getLogger(TsvIterationFormatter.class);
    private static final String TAB = "\t";
    private static final String NEWLINE = "\n";

    private final boolean includeCapacities;
    private final boolean includeWorkItems;
    private final boolean includePullRequests;

    public TsvIterationFormatter() {
        this(true, true, true);
    }

    public TsvIterationFormatter(boolean includeCapacities, boolean includeWorkItems, boolean includePullRequests) {
        this.includeCapacities = includeCapacities;
        this.includeWorkItems = includeWorkItems;
        this.includePullRequests = includePullRequests;
    }

    @Override
    public String format(List<Iteration> iterations) {
        try {
            StringBuilder sb = new StringBuilder();

            // Write header
            appendHeader(sb);

            for (Iteration iteration : iterations) {
                String projectName = iteration.getProjName();
                String teamName = iteration.getTeamName();
                String iterationName = iteration.getName();
                String startDate = iteration.getStartDate();
                String finishDate = iteration.getFinishDate();

                boolean hasAllocations = !iteration.getAllocations().isEmpty();
                boolean hasWorkItems = !iteration.getWorkItems().isEmpty();
                boolean hasPullRequests = !iteration.getPullRequests().isEmpty();

                // Write allocation data
                if (includeCapacities) {
                    for (TeamMemberAllocation allocation : iteration.getAllocations()) {
                        List<String> row = new ArrayList<>();
                        addBaseColumns(row, projectName, teamName, iterationName, startDate, finishDate);
                        addCapacityColumnsForAllocation(row, allocation);
                        addWorkItemBlanks(row);
                        addPullRequestBlanks(row);
                        addTaskBlanks(row);
                        appendRow(sb, row);
                    }
                }

                // Write work item data with their associated PRs
                if (includeWorkItems) {
                    for (WorkItem workItem : iteration.getWorkItems()) {
                        String workItemId = String.valueOf(workItem.getId());
                        String workItemTitle = workItem.getTitle();
                        String workItemType = workItem.getType();
                        String workItemState = workItem.getState();
                        String assignedTo = workItem.getAssignedTo();
                        String plannedVersion = workItem.getPlannedVersion();
                        String storyPoints = workItem.getStoryPoints();
                        String qaStoryPoints = workItem.getQaStoryPoints();
                        String originalStoryPoints = workItem.getOriginalStoryPoints();
                        String priority = workItem.getPriority();
                        String severity = workItem.getSeverity();
                        String createdDate = workItem.getCreatedDate();
                        String createdBy = workItem.getCreatedBy();
                        String devEndDate = workItem.getDevEndDate();
                        String qaReadyDate = workItem.getQaReadyDate();
                        String qaEndDate = workItem.getQaEndDate();
                        boolean hasImplDetails = workItem.isHasImplementationDetails();
                        String tags = workItem.getTags();
                        boolean hasTasks = !workItem.getTasks().isEmpty();

                        if (hasTasks) {
                            for (WorkItem.Task task : workItem.getTasks()) {
                                List<String> row = new ArrayList<>();
                                addBaseColumns(row, projectName, teamName, iterationName, startDate, finishDate);
                                addCapacityColumnsForWorkItem(row, plannedVersion);
                                addWorkItemColumns(row, workItemId, workItemTitle, workItemType, workItemState, assignedTo,
                                        storyPoints, qaStoryPoints, originalStoryPoints, priority, severity, createdDate,
                                        createdBy, devEndDate, qaReadyDate, qaEndDate, tags, hasImplDetails);
                                addPullRequestBlanks(row);
                                addTaskColumns(row, task);
                                appendRow(sb, row);
                            }
                        }

                        if (!includePullRequests || workItem.getPullRequests().isEmpty()) {
                            if (!hasTasks) {
                                // Print work item row without PRs
                                List<String> row = new ArrayList<>();
                                addBaseColumns(row, projectName, teamName, iterationName, startDate, finishDate);
                                addCapacityColumnsForWorkItem(row, plannedVersion);
                                addWorkItemColumns(row, workItemId, workItemTitle, workItemType, workItemState, assignedTo,
                                        storyPoints, qaStoryPoints, originalStoryPoints, priority, severity, createdDate,
                                        createdBy, devEndDate, qaReadyDate, qaEndDate, tags, hasImplDetails);
                                addPullRequestBlanks(row);
                                addTaskBlanks(row);
                                appendRow(sb, row);
                            }
                        } else {
                            // Print work item with each of its PRs
                            for (PullRequest pr : workItem.getPullRequests()) {
                                String prId = pr.getPullRequestId();
                                String prCreatedBy = pr.getCreatedBy();
                                String prCreationDate = pr.getCreationDate();

                                if (pr.getThreads().isEmpty()) {
                                    // PR with no threads
                                    List<String> row = new ArrayList<>();
                                    addBaseColumns(row, projectName, teamName, iterationName, startDate, finishDate);
                                    addCapacityColumnsForWorkItem(row, plannedVersion);
                                    addWorkItemColumns(row, workItemId, workItemTitle, workItemType, workItemState,
                                            assignedTo, storyPoints, qaStoryPoints, originalStoryPoints, priority,
                                            severity, createdDate, createdBy, devEndDate, qaReadyDate, qaEndDate, tags,
                                            hasImplDetails);
                                    addPullRequestColumns(row, prId, prCreatedBy, prCreationDate, "", "", "", "", "");
                                    addTaskBlanks(row);
                                    appendRow(sb, row);
                                } else {
                                    // PR with threads
                                    for (PullRequestThread thread : pr.getThreads()) {
                                        String threadId = thread.getThreadId();
                                        String threadStatus = thread.getStatus();

                                        if (thread.getCommenters().isEmpty()) {
                                            // Thread with no commenters
                                            List<String> row = new ArrayList<>();
                                            addBaseColumns(row, projectName, teamName, iterationName, startDate, finishDate);
                                            addCapacityColumnsForWorkItem(row, plannedVersion);
                                            addWorkItemColumns(row, workItemId, workItemTitle, workItemType, workItemState,
                                                    assignedTo, storyPoints, qaStoryPoints, originalStoryPoints, priority,
                                                    severity, createdDate, createdBy, devEndDate, qaReadyDate, qaEndDate,
                                                    tags, hasImplDetails);
                                            addPullRequestColumns(row, prId, prCreatedBy, prCreationDate, threadId,
                                                    threadStatus, "", "", "");
                                            addTaskBlanks(row);
                                            appendRow(sb, row);
                                        } else {
                                            // Thread with commenters
                                            for (Map.Entry<String, List<ThreadComment>> commenter : thread.getCommenters().entrySet()) {
                                                String commenterName = commenter.getKey();
                                                List<ThreadComment> comments = commenter.getValue();
                                                int commentCount = comments.size();

                                                // Format comments: enclose each in quotes and join with " - "
                                                StringBuilder commentsBuilder = new StringBuilder();
                                                for (int i = 0; i < comments.size(); i++) {
                                                    if (i > 0) {
                                                        commentsBuilder.append(" - ");
                                                    }
                                                    commentsBuilder.append("\"")
                                                            .append(comments.get(i).getCommentContent())
                                                            .append("\"");
                                                }
                                                String formattedComments = commentsBuilder.toString();

                                                List<String> row = new ArrayList<>();
                                                addBaseColumns(row, projectName, teamName, iterationName, startDate, finishDate);
                                                addCapacityColumnsForWorkItem(row, plannedVersion);
                                                addWorkItemColumns(row, workItemId, workItemTitle, workItemType,
                                                        workItemState, assignedTo, storyPoints, qaStoryPoints,
                                                        originalStoryPoints, priority, severity, createdDate, createdBy,
                                                        devEndDate, qaReadyDate, qaEndDate, tags, hasImplDetails);
                                                addPullRequestColumns(row, prId, prCreatedBy, prCreationDate, threadId,
                                                        threadStatus, commenterName, String.valueOf(commentCount),
                                                        formattedComments);
                                                addTaskBlanks(row);
                                                appendRow(sb, row);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Write any PRs that are not associated with work items (at iteration level)
                // NOTE: Since we now collect PRs from work items and add them to iteration,
                // this section handles orphaned PRs only (if any exist at iteration level)
                if (includePullRequests) {
                    for (PullRequest pr : iteration.getPullRequests()) {
                        // Skip PRs that are already processed from work items
                        boolean isProcessed = false;
                        for (WorkItem wi : iteration.getWorkItems()) {
                            if (wi.getPullRequests().contains(pr)) {
                                isProcessed = true;
                                break;
                            }
                        }
                        if (isProcessed) {
                            continue;
                        }

                        String prId = pr.getPullRequestId();
                        String prCreatedBy = pr.getCreatedBy();
                        String prCreationDate = pr.getCreationDate();

                        if (pr.getThreads().isEmpty()) {
                            // PR with no threads
                            List<String> row = new ArrayList<>();
                            addBaseColumns(row, projectName, teamName, iterationName, startDate, finishDate);
                            addCapacityBlanks(row);
                            addWorkItemBlanks(row);
                            addPullRequestColumns(row, prId, prCreatedBy, prCreationDate, "", "", "", "", "");
                            addTaskBlanks(row);
                            appendRow(sb, row);
                        } else {
                            // PR with threads
                            for (PullRequestThread thread : pr.getThreads()) {
                                String threadId = thread.getThreadId();
                                String threadStatus = thread.getStatus();

                                if (thread.getCommenters().isEmpty()) {
                                    // Thread with no commenters
                                    List<String> row = new ArrayList<>();
                                    addBaseColumns(row, projectName, teamName, iterationName, startDate, finishDate);
                                    addCapacityBlanks(row);
                                    addWorkItemBlanks(row);
                                    addPullRequestColumns(row, prId, prCreatedBy, prCreationDate, threadId, threadStatus,
                                            "", "", "");
                                    addTaskBlanks(row);
                                    appendRow(sb, row);
                                } else {
                                    // Thread with commenters
                                    for (Map.Entry<String, List<ThreadComment>> commenter : thread.getCommenters().entrySet()) {
                                        String commenterName = commenter.getKey();
                                        List<ThreadComment> comments = commenter.getValue();
                                        int commentCount = comments.size();

                                        // Format comments: enclose each in quotes and join with " - "
                                        StringBuilder commentsBuilder = new StringBuilder();
                                        for (int i = 0; i < comments.size(); i++) {
                                            if (i > 0) {
                                                commentsBuilder.append(" - ");
                                            }
                                            commentsBuilder.append("\"").append(comments.get(i).getCommentContent()).append("\"");
                                        }
                                        String formattedComments = commentsBuilder.toString();

                                        List<String> row = new ArrayList<>();
                                        addBaseColumns(row, projectName, teamName, iterationName, startDate, finishDate);
                                        addCapacityBlanks(row);
                                        addWorkItemBlanks(row);
                                        addPullRequestColumns(row, prId, prCreatedBy, prCreationDate, threadId,
                                                threadStatus, commenterName, String.valueOf(commentCount),
                                                formattedComments);
                                        addTaskBlanks(row);
                                        appendRow(sb, row);
                                    }
                                }
                            }
                        }
                    }
                }

                // Skip iteration if it has no children (allocations, work items, or pull requests)
                if (!hasAllocations && !hasWorkItems && !hasPullRequests) {
                    // Skip empty iterations
                }
            }

            return sb.toString();
        } catch (Exception e) {
            logger.error("Error occurred while formatting iterations to TSV", e);
            return "";
        }
    }

    private void appendHeader(StringBuilder sb) {
        List<String> header = new ArrayList<>();
        addBaseColumns(header, "Project Name", "Team Name", "Iteration Name", "Start Date", "Finish Date");
        if (includeCapacities) {
            header.add("Member Name");
            header.add("Capacity");
            header.add("Days Off");
            header.add("Worked Days");
            header.add("Worked Hours");
            if (includeWorkItems) {
                header.add("Planned Release Ver");
            }
        }
        if (includeWorkItems) {
            header.add("Work Item ID");
            header.add("Work Item Title");
            header.add("Work Item Type");
            header.add("Work Item State");
            header.add("Assigned To");
            header.add("Story Points");
            header.add("QA Story Points");
            header.add("Original Story Points");
            header.add("Priority");
            header.add("Severity");
            header.add("Created Date");
            header.add("Created By");
            header.add("Dev End Date");
            header.add("QA Ready Date");
            header.add("QA End Date");
            header.add("Tags");
            header.add("Has Impl");
        }
        if (includePullRequests) {
            header.add("Pull Request ID");
            header.add("PR Created By");
            header.add("PR Creation Date");
            header.add("PR Thread ID");
            header.add("PR Thread Status");
            header.add("Commenter");
            header.add("Comment Count");
            header.add("Comments");
        }
        if (includeWorkItems) {
            header.add("Task Work item ID");
            header.add("Task Type");
            header.add("State");
            header.add("Assigned To");
            header.add("Original Estimate");
            header.add("Remaining Hrs");
            header.add("Completed Hrs");
        }
        appendRow(sb, header);
    }

    private void appendRow(StringBuilder sb, List<String> columns) {
        sb.append(String.join(TAB, columns)).append(NEWLINE);
    }

    private void addBaseColumns(List<String> columns, String projectName, String teamName, String iterationName,
            String startDate, String finishDate) {
        columns.add(String.valueOf(projectName));
        columns.add(String.valueOf(teamName));
        columns.add(String.valueOf(iterationName));
        columns.add(String.valueOf(startDate));
        columns.add(String.valueOf(finishDate));
    }

    private void addCapacityColumnsForAllocation(List<String> columns, TeamMemberAllocation allocation) {
        if (!includeCapacities) {
            return;
        }
        columns.add(String.valueOf(allocation.getName()));
        columns.add(String.valueOf(allocation.getCapacity()));
        columns.add(String.valueOf(allocation.getDaysOff()));
        columns.add(String.valueOf(allocation.getWorkedDays()));
        columns.add(String.valueOf(allocation.getWorkedHours()));
        if (includeWorkItems) {
            columns.add("");
        }
    }

    private void addCapacityColumnsForWorkItem(List<String> columns, String plannedVersion) {
        if (!includeCapacities) {
            return;
        }
        columns.add("");
        columns.add("");
        columns.add("");
        columns.add("");
        columns.add("");
        if (includeWorkItems) {
            columns.add(String.valueOf(plannedVersion));
        }
    }

    private void addCapacityBlanks(List<String> columns) {
        if (!includeCapacities) {
            return;
        }
        columns.add("");
        columns.add("");
        columns.add("");
        columns.add("");
        columns.add("");
        if (includeWorkItems) {
            columns.add("");
        }
    }

    private void addWorkItemColumns(List<String> columns, String workItemId, String workItemTitle, String workItemType,
            String workItemState, String assignedTo, String storyPoints, String qaStoryPoints,
            String originalStoryPoints, String priority, String severity, String createdDate, String createdBy,
            String devEndDate, String qaReadyDate, String qaEndDate, String tags, boolean hasImplDetails) {
        if (!includeWorkItems) {
            return;
        }
        columns.add(String.valueOf(workItemId));
        columns.add(String.valueOf(workItemTitle));
        columns.add(String.valueOf(workItemType));
        columns.add(String.valueOf(workItemState));
        columns.add(String.valueOf(assignedTo));
        columns.add(String.valueOf(storyPoints));
        columns.add(String.valueOf(qaStoryPoints));
        columns.add(String.valueOf(originalStoryPoints));
        columns.add(String.valueOf(priority));
        columns.add(String.valueOf(severity));
        columns.add(String.valueOf(createdDate));
        columns.add(String.valueOf(createdBy));
        columns.add(String.valueOf(devEndDate));
        columns.add(String.valueOf(qaReadyDate));
        columns.add(String.valueOf(qaEndDate));
        columns.add(String.valueOf(tags));
        columns.add(String.valueOf(hasImplDetails));
    }

    private void addWorkItemBlanks(List<String> columns) {
        if (!includeWorkItems) {
            return;
        }
        columns.add("");
        columns.add("");
        columns.add("");
        columns.add("");
        columns.add("");
        columns.add("");
        columns.add("");
        columns.add("");
        columns.add("");
        columns.add("");
        columns.add("");
        columns.add("");
        columns.add("");
        columns.add("");
        columns.add("");
        columns.add("");
        columns.add("");
    }

    private void addPullRequestColumns(List<String> columns, String prId, String prCreatedBy, String prCreationDate,
            String threadId, String threadStatus, String commenterName, String commentCount, String comments) {
        if (!includePullRequests) {
            return;
        }
        columns.add(String.valueOf(prId));
        columns.add(String.valueOf(prCreatedBy));
        columns.add(String.valueOf(prCreationDate));
        columns.add(String.valueOf(threadId));
        columns.add(String.valueOf(threadStatus));
        columns.add(String.valueOf(commenterName));
        columns.add(String.valueOf(commentCount));
        columns.add(String.valueOf(comments));
    }

    private void addPullRequestBlanks(List<String> columns) {
        if (!includePullRequests) {
            return;
        }
        columns.add("");
        columns.add("");
        columns.add("");
        columns.add("");
        columns.add("");
        columns.add("");
        columns.add("");
        columns.add("");
    }

    private void addTaskColumns(List<String> columns, WorkItem.Task task) {
        if (!includeWorkItems) {
            return;
        }
        columns.add(String.valueOf(task.getTaskId()));
        columns.add(String.valueOf(task.getTaskType()));
        columns.add(String.valueOf(task.getState()));
        columns.add(String.valueOf(task.getAssignedTo()));
        columns.add(String.valueOf(task.getOriginalEstimate()));
        columns.add(String.valueOf(task.getRemainingWork()));
        columns.add(String.valueOf(task.getCompletedWork()));
    }

    private void addTaskBlanks(List<String> columns) {
        if (!includeWorkItems) {
            return;
        }
        columns.add("");
        columns.add("");
        columns.add("");
        columns.add("");
        columns.add("");
        columns.add("");
        columns.add("");
    }
}
