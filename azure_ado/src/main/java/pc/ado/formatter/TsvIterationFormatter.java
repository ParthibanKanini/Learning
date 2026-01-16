package pc.ado.formatter;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pc.ado.dto.Iteration;
import pc.ado.dto.PullRequest;
import pc.ado.dto.PullRequestThread;
import pc.ado.dto.TeamMemberAllocation;
import pc.ado.dto.WorkItem;

/**
 * Formats iterations data to tab-separated values output. Explodes hierarchy
 * into individual row items with parent fields repeated for each child.
 */
public class TsvIterationFormatter implements IterationFormatter {

    private static final Logger logger = LoggerFactory.getLogger(TsvIterationFormatter.class);
    private static final String TAB = "\t";
    private static final String NEWLINE = "\n";

    @Override
    public String format(List<Iteration> iterations) {
        try {
            StringBuilder sb = new StringBuilder();

            // Write header
            sb.append("Project Name").append(TAB)
                    .append("Team Name").append(TAB)
                    .append("Iteration Name").append(TAB)
                    .append("Start Date").append(TAB)
                    .append("Finish Date").append(TAB)
                    .append("Member Name").append(TAB)
                    .append("Capacity").append(TAB)
                    .append("Days Off").append(TAB)
                    .append("Worked Days").append(TAB)
                    .append("Worked Hours").append(TAB)
                    .append("Planned Release Ver").append(TAB)
                    .append("Work Item ID").append(TAB)
                    .append("Work Item Type").append(TAB)
                    .append("Work Item State").append(TAB)
                    .append("Assigned To").append(TAB)
                    .append("Pull Request ID").append(TAB)
                    .append("PR Created By").append(TAB)
                    .append("PR Creation Date").append(TAB)
                    .append("PR Thread ID").append(TAB)
                    .append("PR Thread Status").append(TAB)
                    //.append("PR Thread Deleted").append(TAB)
                    .append("Commenter").append(TAB)
                    .append("Comment Count").append(NEWLINE);

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
                for (TeamMemberAllocation allocation : iteration.getAllocations()) {
                    sb.append(projectName).append(TAB)
                            .append(teamName).append(TAB)
                            .append(iterationName).append(TAB)
                            .append(startDate).append(TAB)
                            .append(finishDate).append(TAB)
                            .append(allocation.getName()).append(TAB)
                            .append(allocation.getCapacity()).append(TAB)
                            .append(allocation.getDaysOff()).append(TAB)
                            .append(allocation.getWorkedDays()).append(TAB)
                            .append(allocation.getWorkedHours()).append(TAB)
                            .append(TAB)
                            .append(TAB).append(TAB).append(TAB).append(TAB)
                            .append(TAB).append(TAB).append(TAB)
                            .append(TAB).append(TAB).append(TAB)
                            .append(TAB).append(TAB)
                            .append(NEWLINE);
                }

                // Write work item data with their associated PRs
                for (WorkItem workItem : iteration.getWorkItems()) {
                    String workItemId = String.valueOf(workItem.getId());
                    String workItemType = workItem.getType();
                    String workItemState = workItem.getState();
                    String assignedTo = workItem.getAssignedTo();
                    String plannedVersion = workItem.getPlannedVersion();

                    // Check if work item has pull requests
                    if (workItem.getPullRequests().isEmpty()) {
                        // Print work item row without PRs
                        sb.append(projectName).append(TAB)
                                .append(teamName).append(TAB)
                                .append(iterationName).append(TAB)
                                .append(startDate).append(TAB)
                                .append(finishDate).append(TAB)
                                .append(TAB).append(TAB).append(TAB).append(TAB).append(TAB)
                                .append(plannedVersion).append(TAB)
                                .append(workItemId).append(TAB)
                                .append(workItemType).append(TAB)
                                .append(workItemState).append(TAB)
                                .append(assignedTo).append(TAB)
                                .append(TAB).append(TAB).append(TAB)
                                .append(TAB).append(TAB).append(TAB)
                                .append(TAB).append(TAB)
                                .append(NEWLINE);
                    } else {
                        // Print work item with each of its PRs
                        for (PullRequest pr : workItem.getPullRequests()) {
                            String prId = pr.getPullRequestId();
                            String prCreatedBy = pr.getCreatedBy();
                            String prCreationDate = pr.getCreationDate();

                            if (pr.getThreads().isEmpty()) {
                                // PR with no threads
                                sb.append(projectName).append(TAB)
                                        .append(teamName).append(TAB)
                                        .append(iterationName).append(TAB)
                                        .append(startDate).append(TAB)
                                        .append(finishDate).append(TAB)
                                        .append(TAB).append(TAB).append(TAB).append(TAB).append(TAB)
                                        .append(plannedVersion).append(TAB)
                                        .append(workItemId).append(TAB)
                                        .append(workItemType).append(TAB)
                                        .append(workItemState).append(TAB)
                                        .append(assignedTo).append(TAB)
                                        .append(prId).append(TAB)
                                        .append(prCreatedBy).append(TAB)
                                        .append(prCreationDate).append(TAB)
                                        .append(TAB).append(TAB).append(TAB)
                                        .append(TAB).append(TAB)
                                        .append(NEWLINE);
                            } else {
                                // PR with threads
                                for (PullRequestThread thread : pr.getThreads()) {
                                    String threadId = thread.getThreadId();
                                    String threadStatus = thread.getStatus();
                                    //String isDeleted = String.valueOf(thread.isDeleted());

                                    if (thread.getCommenters().isEmpty()) {
                                        // Thread with no commenters
                                        sb.append(projectName).append(TAB)
                                                .append(teamName).append(TAB)
                                                .append(iterationName).append(TAB)
                                                .append(startDate).append(TAB)
                                                .append(finishDate).append(TAB)
                                                .append(TAB).append(TAB).append(TAB).append(TAB).append(TAB)
                                                .append(plannedVersion).append(TAB)
                                                .append(workItemId).append(TAB)
                                                .append(workItemType).append(TAB)
                                                .append(workItemState).append(TAB)
                                                .append(assignedTo).append(TAB)
                                                .append(prId).append(TAB)
                                                .append(prCreatedBy).append(TAB)
                                                .append(prCreationDate).append(TAB)
                                                .append(threadId).append(TAB)
                                                .append(threadStatus).append(TAB)
                                                .append(TAB).append(TAB)
                                                .append(NEWLINE);
                                    } else {
                                        // Thread with commenters
                                        for (Map.Entry<String, String[]> commenter : thread.getCommenters().entrySet()) {
                                            String commenterName = commenter.getKey();
                                            int commentCount = commenter.getValue().length;
                                            sb.append(projectName).append(TAB)
                                                    .append(teamName).append(TAB)
                                                    .append(iterationName).append(TAB)
                                                    .append(startDate).append(TAB)
                                                    .append(finishDate).append(TAB)
                                                    .append(TAB).append(TAB).append(TAB).append(TAB).append(TAB)
                                                    .append(plannedVersion).append(TAB)
                                                    .append(workItemId).append(TAB)
                                                    .append(workItemType).append(TAB)
                                                    .append(workItemState).append(TAB)
                                                    .append(assignedTo).append(TAB)
                                                    .append(prId).append(TAB)
                                                    .append(prCreatedBy).append(TAB)
                                                    .append(prCreationDate).append(TAB)
                                                    .append(threadId).append(TAB)
                                                    .append(threadStatus).append(TAB)
                                                    //.append(isDeleted).append(TAB)
                                                    .append(commenterName).append(TAB)
                                                    .append(commentCount)
                                                    .append(NEWLINE);
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
                        sb.append(projectName).append(TAB)
                                .append(teamName).append(TAB)
                                .append(iterationName).append(TAB)
                                .append(startDate).append(TAB)
                                .append(finishDate).append(TAB)
                                .append(TAB).append(TAB).append(TAB).append(TAB).append(TAB)
                                .append(TAB).append(TAB).append(TAB).append(TAB).append(TAB)
                                .append(prId).append(TAB)
                                .append(prCreatedBy).append(TAB)
                                .append(prCreationDate).append(TAB)
                                .append(TAB).append(TAB).append(TAB)
                                .append(TAB).append(TAB)
                                .append(NEWLINE);
                    } else {
                        // PR with threads
                        for (PullRequestThread thread : pr.getThreads()) {
                            String threadId = thread.getThreadId();
                            String threadStatus = thread.getStatus();
                            String isDeleted = String.valueOf(thread.isDeleted());

                            if (thread.getCommenters().isEmpty()) {
                                // Thread with no commenters
                                sb.append(projectName).append(TAB)
                                        .append(teamName).append(TAB)
                                        .append(iterationName).append(TAB)
                                        .append(startDate).append(TAB)
                                        .append(finishDate).append(TAB)
                                        .append(TAB).append(TAB).append(TAB).append(TAB).append(TAB)
                                        .append(TAB).append(TAB).append(TAB).append(TAB).append(TAB)
                                        .append(prId).append(TAB)
                                        .append(prCreatedBy).append(TAB)
                                        .append(prCreationDate).append(TAB)
                                        .append(threadId).append(TAB)
                                        .append(threadStatus).append(TAB)
                                        //.append(isDeleted).append(TAB)
                                        .append(TAB).append(TAB)
                                        .append(NEWLINE);
                            } else {
                                // Thread with commenters
                                for (Map.Entry<String, String[]> commenter : thread.getCommenters().entrySet()) {
                                    String commenterName = commenter.getKey();
                                    int commentCount = commenter.getValue().length;

                                    sb.append(projectName).append(TAB)
                                            .append(teamName).append(TAB)
                                            .append(iterationName).append(TAB)
                                            .append(startDate).append(TAB)
                                            .append(finishDate).append(TAB)
                                            .append(TAB).append(TAB).append(TAB).append(TAB).append(TAB)
                                            .append(TAB).append(TAB).append(TAB).append(TAB).append(TAB)
                                            .append(prId).append(TAB)
                                            .append(prCreatedBy).append(TAB)
                                            .append(prCreationDate).append(TAB)
                                            .append(threadId).append(TAB)
                                            .append(threadStatus).append(TAB)
                                            .append(isDeleted).append(TAB)
                                            .append(commenterName).append(TAB)
                                            .append(commentCount)
                                            .append(NEWLINE);
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
}
