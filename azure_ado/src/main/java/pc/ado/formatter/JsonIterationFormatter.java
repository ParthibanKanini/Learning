package pc.ado.formatter;

import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pc.ado.dto.Iteration;
import pc.ado.dto.PullRequest;
import pc.ado.dto.PullRequestThread;
import pc.ado.dto.TeamMemberAllocation;
import pc.ado.dto.WorkItem;

/**
 * Formats iterations data to JSON output.
 */
public class JsonIterationFormatter implements IterationFormatter {

    private static final Logger logger = LoggerFactory.getLogger(JsonIterationFormatter.class);

    @Override
    public String format(List<Iteration> iterations) {
        try {
            JSONArray jsonArray = new JSONArray();

            for (Iteration iteration : iterations) {
                JSONObject iterationJson = new JSONObject();
                iterationJson.put("projectName", iteration.getProjName());
                iterationJson.put("teamName", iteration.getTeamName());
                iterationJson.put("name", iteration.getName());
                iterationJson.put("startDate", iteration.getStartDate());
                iterationJson.put("finishDate", iteration.getFinishDate());

                JSONArray allocationsArray = new JSONArray();
                for (TeamMemberAllocation allocation : iteration.getAllocations()) {
                    JSONObject allocationJson = new JSONObject();
                    allocationJson.put("name", allocation.getName());
                    allocationJson.put("capacity", allocation.getCapacity());
                    allocationJson.put("daysOff", allocation.getDaysOff());
                    allocationJson.put("workedDays", allocation.getWorkedDays());
                    allocationJson.put("workedHours", allocation.getWorkedHours());
                    allocationsArray.put(allocationJson);
                }
                iterationJson.put("allocations", allocationsArray);

                JSONArray workItemsArray = new JSONArray();
                for (WorkItem workItem : iteration.getWorkItems()) {
                    JSONObject workItemJson = new JSONObject();
                    workItemJson.put("id", workItem.getId());
                    workItemJson.put("type", workItem.getType());
                    workItemJson.put("state", workItem.getState());
                    workItemJson.put("assignedTo", workItem.getAssignedTo());

                    JSONArray tasksArray = new JSONArray();
                    for (WorkItem.Task task : workItem.getTasks()) {
                        JSONObject taskJson = new JSONObject();
                        taskJson.put("taskType", task.getTaskType());
                        taskJson.put("state", task.getState());
                        taskJson.put("assignedTo", task.getAssignedTo());
                        taskJson.put("originalEstimate", task.getOriginalEstimate());
                        taskJson.put("remainingWork", task.getRemainingWork());
                        tasksArray.put(taskJson);
                    }
                    workItemJson.put("tasks", tasksArray);

                    JSONArray wiPullRequestsArray = new JSONArray();
                    for (PullRequest prFromWi : workItem.getPullRequests()) {
                        JSONObject prJson = new JSONObject();
                        prJson.put("pullRequestId", prFromWi.getPullRequestId());
                        prJson.put("createdBy", prFromWi.getCreatedBy());
                        prJson.put("creationDate", prFromWi.getCreationDate());

                        JSONArray threadsArray = new JSONArray();
                        for (PullRequestThread thread : prFromWi.getThreads()) {
                            JSONObject threadJson = new JSONObject();
                            threadJson.put("threadId", thread.getThreadId());
                            threadJson.put("status", thread.getStatus());
                            JSONObject commentersJson = new JSONObject();
                            for (Map.Entry<String, String[]> commenter : thread.getCommenters().entrySet()) {
                                commentersJson.put(commenter.getKey(), new JSONArray(commenter.getValue()));
                            }
                            threadJson.put("commenters", commentersJson);
                            threadsArray.put(threadJson);
                        }
                        prJson.put("threads", threadsArray);
                        wiPullRequestsArray.put(prJson);
                    }
                    workItemJson.put("pullRequests", wiPullRequestsArray);

                    workItemsArray.put(workItemJson);
                }
                iterationJson.put("workItems", workItemsArray);

                JSONArray pullRequestsArray = new JSONArray();
                for (PullRequest pullRequest : iteration.getPullRequests()) {
                    JSONObject prJson = new JSONObject();
                    prJson.put("pullRequestId", pullRequest.getPullRequestId());
                    prJson.put("createdBy", pullRequest.getCreatedBy());
                    prJson.put("creationDate", pullRequest.getCreationDate());

                    JSONArray threadsArray = new JSONArray();
                    for (PullRequestThread thread : pullRequest.getThreads()) {
                        JSONObject threadJson = new JSONObject();
                        threadJson.put("threadId", thread.getThreadId());
                        threadJson.put("status", thread.getStatus());
                        threadJson.put("isDeleted", thread.isDeleted());

                        JSONObject commentersJson = new JSONObject();
                        for (Map.Entry<String, String[]> commenter : thread.getCommenters().entrySet()) {
                            commentersJson.put(commenter.getKey(), new JSONArray(commenter.getValue()));
                        }
                        threadJson.put("commenters", commentersJson);
                        threadsArray.put(threadJson);
                    }
                    prJson.put("threads", threadsArray);
                    pullRequestsArray.put(prJson);
                }
                iterationJson.put("pullRequests", pullRequestsArray);

                jsonArray.put(iterationJson);
            }

            return jsonArray.toString(2);
        } catch (Exception e) {
            logger.error("Error occurred while formatting iterations to JSON", e);
            return "{}";
        }
    }
}
