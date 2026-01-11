package pc.ado;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

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
 * Handles presentation/formatting of ADO data. Separates display logic from
 * business logic.
 */
public class AdoReportFormatter {

    private static final Logger logger = LoggerFactory.getLogger(AdoReportFormatter.class);

    private final String outputFilePath;

    public AdoReportFormatter(AdoConfig config) {
        this.outputFilePath = config.getSprintCapacityDetailsFilePath();
        logger.info("Report formatter initialized with output file: {}", outputFilePath);
    }

    /**
     * Writes iteration capacity details to file.
     */
    public void writeSprintCapacitiesToFile(List<String> details) {
        logger.info("Writing {} lines to report file", details.size());
        try (PrintWriter writer = new PrintWriter(new FileWriter(outputFilePath, true))) {
            for (String line : details) {
                writer.println(line);
            }
            writer.flush();
        } catch (IOException e) {
            logger.error("Failed to write to output file: {}", outputFilePath, e);
        }
    }

    /**
     * Displays team header.
     */
    public void logTeamName(String project, String teamName) {
        logger.debug("Fetching Iteration details for '{} - {}'", project, teamName);
    }

    /**
     * Converts a list of iterations to JSON and writes to file.
     */
    public void writeSprintCapacitiesToJsonFile(List<Iteration> iterations) {
        try {
            JSONArray jsonArray = new JSONArray();

            for (Iteration iteration : iterations) {
                JSONObject iterationJson = new JSONObject();
                iterationJson.put("projectName", iteration.getProjName());
                iterationJson.put("teamName", iteration.getTeamName());
                //iterationJson.put("id", iteration.getId());
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
                    //workItemJson.put("title", workItem.getTitle());
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
                            //threadJson.put("isDeleted", thread.isDeleted());
                            JSONObject commentersJson = new JSONObject();
                            for (java.util.Map.Entry<String, String[]> commenter : thread.getCommenters().entrySet()) {
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
                        for (java.util.Map.Entry<String, String[]> commenter : thread.getCommenters().entrySet()) {
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

            writeJsonToFile(jsonArray.toString(2));
        } catch (Exception e) {
            logger.error("Error occurred while converting iterations to JSON", e);
        }
    }

    /**
     * Writes JSON string to file.
     */
    private void writeJsonToFile(String jsonContent) {
        try {
            logger.info("Writing JSON data to report file");
            try (FileWriter writer = new FileWriter(outputFilePath, false)) {
                writer.write(jsonContent);
                writer.flush();
                logger.info("JSON data successfully written to {}", outputFilePath);
            }
        } catch (IOException e) {
            logger.error("Failed to write JSON to output file: {}", outputFilePath, e);
        }
    }

}
