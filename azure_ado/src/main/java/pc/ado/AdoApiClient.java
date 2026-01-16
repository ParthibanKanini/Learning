package pc.ado;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pc.ado.dto.Iteration;
import pc.ado.dto.PullRequest;
import pc.ado.dto.PullRequestThread;
import pc.ado.dto.TeamMemberCapacity;
import pc.ado.dto.WorkItem;

/**
 * Handles interaction with Azure DevOps API. Encapsulates all API calls and
 * response parsing.
 */
public class AdoApiClient {

    private static final Logger logger = LoggerFactory.getLogger(AdoApiClient.class);
    private static final String PLUS_SIGN = "\\+";
    private static final String SPACE_ENCODED = "%20";

    private final AdoHttpClient httpClient;
    private final AdoConfig config;

    public AdoApiClient(AdoConfig config, AdoHttpClient httpClient) {
        this.config = config;
        this.httpClient = httpClient;
    }

    /**
     * Retrieves all team iterations for the configured team and project.
     *
     * @return list of Iteration objects
     * @throws Exception if the API call fails
     */
    public List<Iteration> getTeamSprint(String project, String team, List<String> itrNames) throws Exception {
        logger.trace("Fetching team iterations");
        String teamUri = buildTeamUri(project, team);
        String itrUrl = teamUri + config.getIterationsApiPath() + "?api-version=" + config.getApiVersion();
        try {
            String response = httpClient.get(itrUrl);
            JSONObject jsonResponse = new JSONObject(response);
            JSONArray iterationsArray = jsonResponse.getJSONArray("value");
            List<Iteration> iterations = new ArrayList<>();
            for (int i = 0; i < iterationsArray.length(); i++) {
                JSONObject iteration = iterationsArray.getJSONObject(i);
                Iteration itr = parseIteration(project, team, iteration);
                // Filter by Iteration name if provided else add all iterations
                if (itrNames == null || itrNames.isEmpty() || itrNames.contains(itr.getName())) {
                    iterations.add(itr);
                }
            }
            logger.info("Successfully retrieved {} team iterations", iterations.size());
            return iterations;
        } catch (JSONException e) {
            logger.error("Failed to parse iterations response", e);
            throw new Exception("Failed to parse iterations response", e);
        }
    }

    /**
     * Retrieves team member capacities for a specific iteration.
     *
     * @param iterationId the ID of the iteration
     * @return list of TeamMemberCapacity objects with capacity > 0
     * @throws Exception if the API call fails
     */
    public List<TeamMemberCapacity> getIterationCapacities(String project, String team, String iterationId) throws Exception {
        logger.trace("Fetching capacities for iteration: {}", iterationId);
        String teamUri = buildTeamUri(project, team);
        String capacitiesPath = config.getCapacitiesApiPath().replace("{iterationId}", iterationId);
        try {
            // Get team holidays for the iteration
            String url = teamUri + "/" + (config.getIterationDayOffPath().replace("{iterationId}", iterationId)) + "?api-version=" + config.getApiVersion();
            String response = httpClient.get(url);
            //logger.trace("Iteration Day Off response: {}", response);
            JSONObject jsonResponse = new JSONObject(response);

            int teamHolidays = 0;
            JSONArray teamDaysOff = jsonResponse.getJSONArray("daysOff");
            for (int i = 0; i < teamDaysOff.length(); i++) {
                JSONObject dayOff = teamDaysOff.getJSONObject(i);
                String startDate = dayOff.getString("start");
                String endDate = dayOff.getString("end");
                int weekDayHoliday = DateUtils.calculateWeekDays(DateUtils.formatISODateToLocalDate(startDate), DateUtils.formatISODateToLocalDate(endDate));
                //logger.debug("Team Day Off from {} to {} : {} days", startDate, endDate, days);
                teamHolidays += weekDayHoliday;
            }
            logger.trace("teamHolidays: {} - {}", teamHolidays, jsonResponse.toString());

            // Team members details
            url = teamUri + "/" + capacitiesPath + "?api-version=" + config.getApiVersion();
            response = httpClient.get(url);
            jsonResponse = new JSONObject(response);
            JSONArray teamMembersArray = jsonResponse.getJSONArray("teamMembers");

            List<TeamMemberCapacity> capacities = new ArrayList<>();
            for (int i = 0; i < teamMembersArray.length(); i++) {
                JSONObject teamMember = teamMembersArray.getJSONObject(i);
                TeamMemberCapacity capacity = parseTeamMemberCapacity(teamMember, teamHolidays);
                if (capacity != null && capacity.getCapacityPerDay() > 0) {
                    capacities.add(capacity);
                }
            }
            logger.info("Retrieved {} team members with capacity for iteration: {}", capacities.size(), iterationId);
            return capacities;
        } catch (JSONException e) {
            logger.error("Failed to parse capacities response for iteration: {}", iterationId, e);
            throw new Exception("Failed to parse capacities response", e);
        }
    }

    /**
     * Parses iteration data from JSON response.
     */
    private Iteration parseIteration(String project, String team, JSONObject iterationJson) {
        String id = iterationJson.optString("id", "N/A");
        String name = iterationJson.optString("name", "N/A");
        String startDate = "N/A";
        String finishDate = "N/A";
        try {
            JSONObject attributes = iterationJson.optJSONObject("attributes");
            if (attributes != null) {
                startDate = attributes.optString("startDate", "N/A");
                startDate = !(startDate.equals("N/A")) ? DateUtils.formatISODate(startDate) : "N/A";
                finishDate = attributes.optString("finishDate", "N/A");
                finishDate = !(finishDate.equals("N/A")) ? DateUtils.formatISODate(finishDate) : "N/A";
                logger.trace("Parsed iteration : {} ({} to {})", name, startDate, finishDate);
            }
        } catch (Exception e) {
            logger.error(iterationJson.toString(), e);
        }
        return new Iteration(project, team, id, name, startDate, finishDate);
    }

    /**
     * Parses team member capacity data from JSON response. Returns null if no
     * capacity is found.
     *
     * @param teamMemberJson
     * @param teamHolidays
     * @return
     */
    private TeamMemberCapacity parseTeamMemberCapacity(JSONObject teamMemberJson, int teamHolidays) {
        try {
            JSONObject teamMember = teamMemberJson.getJSONObject("teamMember");
            String displayName = teamMember.getString("displayName");
            JSONArray activities = teamMemberJson.getJSONArray("activities");
            int teamMemberPTO = 0;
            // Iterate daysoff array and extract start and end date to calculate total holidays using daysbetween function
            JSONArray teamMemberSprintdaysOff = teamMemberJson.getJSONArray("daysOff");
            for (int i = 0; i < teamMemberSprintdaysOff.length(); i++) {
                JSONObject dayOff = teamMemberSprintdaysOff.getJSONObject(i);
                String startDate = dayOff.getString("start");
                String endDate = dayOff.getString("end");
                teamMemberPTO += DateUtils.calculateWeekDays(DateUtils.formatISODateToLocalDate(startDate), DateUtils.formatISODateToLocalDate(endDate));
                logger.trace("Team Member '{}' Day Off from {} to {} = {} days.", displayName, startDate, endDate, teamMemberPTO);
            }
            for (int i = 0; i < activities.length(); i++) {
                JSONObject activity = activities.getJSONObject(i);
                double capacityPerDay = activity.optDouble("capacityPerDay", 0);
                if (capacityPerDay > 0) {
                    logger.trace("'{}' had PTO {} days & Team Holiday {} days.", displayName, teamMemberPTO, teamHolidays);
                    return new TeamMemberCapacity(displayName, capacityPerDay, teamMemberPTO + teamHolidays);
                }
            }
        } catch (JSONException e) {
            logger.debug("Failed to parse team member capacity", e);
        }
        return null;
    }

    /**
     * Builds the team URI from configuration.
     */
    private String buildTeamUri(String project, String team) throws Exception {
        String baseUri = config.getBaseUri() + config.getOrganization() + "/";
        String projectName = URLEncoder.encode(project, StandardCharsets.UTF_8.toString())
                .replaceAll(PLUS_SIGN, SPACE_ENCODED);
        String projUri = baseUri + projectName + "/";
        if (team != null) {
            String teamName = URLEncoder.encode(team, StandardCharsets.UTF_8.toString())
                    .replaceAll(PLUS_SIGN, SPACE_ENCODED);
            projUri = projUri + teamName + "/";
        }
        return projUri;
    }

    /**
     * Retrieves sprint work items for a specific iteration.
     *
     * @param project
     * @param team
     * @param sprintId
     * @return
     * @throws Exception
     */
    public List<TeamMemberCapacity> getSprintWorkItems(String project, String team, Iteration iteration) throws Exception {
        String teamUri = buildTeamUri(project, team);
        try {
            // Get team holidays for the iteration
            String url = teamUri + "/" + (config.getWorkItemsApiPath().replace("{iterationId}", iteration.getId())) + "?api-version=" + config.getApiVersion();
            String response = httpClient.get(url);
            JSONObject jsonResponse = new JSONObject(response);
            JSONArray workItemsArray = jsonResponse.getJSONArray("workItemRelations");
            int count = 0;
            for (int i = 0; i < workItemsArray.length(); i++) {
                JSONObject workItem = workItemsArray.getJSONObject(i);
                // When work item relation is null then get the target object
                Object relObj = workItem.opt("rel");
                if (relObj == null || relObj == JSONObject.NULL) {
                    count++;
                    JSONObject target = workItem.optJSONObject("target");
                    String workItemLink = target.getString("url");
                    getWorkItemFields(project, workItemLink, iteration);
                }
            }
            logger.debug("Total work items processed: {}", count);
            //logger.debug("Sprint Work Items response: {}", response);
        } catch (JSONException e) {
            logger.error("Failed to parse work items response for iteration: {}", iteration.getId(), e);
            throw new Exception("Failed to parse work items response", e);
        }
        return null;
    }

    /**
     * Retrieves and logs fields for a given work item.
     *
     * @param project
     * @param team
     * @param workItemLink
     * @throws Exception
     */
    private void getWorkItemFields(String project, String workItemLink, Iteration iteration) throws Exception {
        String workItemResponse = httpClient.get(workItemLink);
        JSONObject workItemJsonResponse = new JSONObject(workItemResponse);
        //logger.debug("Work Item Response: {}", workItemJsonResponse.toString());
        String id = workItemJsonResponse.optString("id", "N/A");
        JSONObject fields = workItemJsonResponse.optJSONObject("fields");
        if (fields != null) {
            // Do not extract any project specific details
            //String title = fields.optString("System.Title", "N/A");
            String title = "";
            String workItemType = fields.optString("System.WorkItemType"); // Bug/Story
            String state = fields.optString("System.State");
            // Check if the work item state is in the ignored list
            List<String> ignoredStates = (config.getIgnoredWorkItemStates() != null) ? config.getIgnoredWorkItemStates() : List.of();
            if (!ignoredStates.contains(state.trim())) {
                logger.trace("Fetching Work item ID: {} in state: {}", id, state);
                String storyPoints = fields.optString("Microsoft.VSTS.Scheduling.StoryPoints");
                JSONObject assignedToObj = fields.optJSONObject("System.AssignedTo");
                String assignedTo = assignedToObj != null ? assignedToObj.optString("displayName", "Unassigned") : "Unassigned";
                String priority = fields.optString("Microsoft.VSTS.Common.Priority");
                String severity = fields.optString("Microsoft.VSTS.Common.Severity");
                String createdDate = fields.optString("System.CreatedDate");
                JSONObject createdByObj = fields.optJSONObject("System.CreatedBy");
                String createdBy = createdByObj != null ? createdByObj.optString("displayName") : "Unknown";
                String devEndDate = fields.optString("Custom.DevEndDate");
                String qaEndDate = fields.optString("Custom.QACompletionDate");
                String tags = fields.optString("System.Tags");
                logger.trace("ID: {} | Title: {} | Type: {} | Story Points: {} | Assigned To: {} | State: {} | Priority: {} | Severity: {} | Created Date: {} | Created By: {} | Dev End Date: {} | QA End Date: {} | Tags: {}",
                        id, title, workItemType, storyPoints, assignedTo, state, priority, severity, createdDate, createdBy, devEndDate, qaEndDate, tags);

                // Custom field for project
                String plannedReleaseVersion = fields.optString("Custom.SYMPlannedReleaseVersion", "");

                // Create and add WorkItem to iteration
                WorkItem workItem = new WorkItem(Integer.parseInt(id), title, workItemType, state, assignedTo, plannedReleaseVersion);

                populateWorkItemChildren(project, id, workItem);
                iteration.addWorkItem(workItem);
                logger.debug("  Added work item {} in {} state to iteration {}", id, state, iteration.getName());
            }
        } else {
            logger.warn("No fields found for work item ID: {}", id);
        }
    }

    private void populateWorkItemChildren(String project, String id, WorkItem workItem) throws Exception {
        // Retrieve work item tasks based on configuration
        if (config.isFetchWorkItemTasks()) {
            logger.trace("      Fetching tasks for Work item ID: {}", id);
            populateTasks(project, Integer.parseInt(id), workItem);
        }
        // Retrieve work item pull requests based on configuration
        if (config.isFetchWorkItemPullRequests()) {
            logger.trace("      Fetching pull requests for Work item ID: {}", id);
            populatePullRequests(project, Integer.parseInt(id), workItem);
        }
    }

    /**
     * Retrieves and processes child tasks for a given work item.
     *
     * @param project
     * @param workItemId
     * @throws Exception
     */
    private void populateTasks(String project, int workItemId, WorkItem workItem) throws Exception {
        String teamUri = buildTeamUri(project, null);
        int totalTasksAdded = 0;
        JSONArray relations = fetchWorkItemRelations(teamUri, workItemId);
        if (relations != null) {
            for (int i = 0; i < relations.length(); i++) {
                JSONObject relation = relations.getJSONObject(i);
                if (isTaskRelation(relation)) {
                    String taskUrl = relation.optString("url");
                    totalTasksAdded += processTaskRelation(taskUrl, workItem);
                }
            }
        }
        logger.debug("      Total tasks added to work item {}: {}", workItemId, totalTasksAdded);
    }

    /**
     * Retrieves and processes child pull requests for a given work item.
     *
     * @param project
     * @param workItemId
     * @throws Exception
     */
    private void populatePullRequests(String project, int workItemId, WorkItem workItem) throws Exception {
        String teamUri = buildTeamUri(project, null);
        int totalPullRequestsAdded = 0;
        try {
            JSONArray relations = fetchWorkItemRelations(teamUri, workItemId);
            if (relations != null) {
                for (int i = 0; i < relations.length(); i++) {
                    JSONObject relation = relations.getJSONObject(i);
                    if (isPullRequestRelation(relation)) {
                        //logger.trace("{} -{}", workItemId, relation.toString());
                        String urlLink = relation.optString("url");
                        try {
                            totalPullRequestsAdded += processPullRequestRelation(teamUri, urlLink, workItem);
                        } catch (Exception e) {
                            logger.warn("Failed to process pull request for work item {}: {}", workItemId, e.getMessage());
                            logger.debug("PR details: {}", urlLink, e);
                            // Continue processing other PRs even if one fails
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.warn("Failed to fetch work item relations for work item {}: {}", workItemId, e.getMessage());
            logger.debug("Error details", e);
            // Don't fail entirely if PR retrieval fails
        }
        logger.debug("      Total PR added to work item {}: {}", workItemId, totalPullRequestsAdded);
    }

    /**
     * Fetches work item relations from Azure DevOps API.
     *
     * @param teamUri
     * @param workItemId
     * @return JSONArray of relations
     * @throws Exception
     */
    private JSONArray fetchWorkItemRelations(String teamUri, int workItemId) throws Exception {
        String url = teamUri + "/" + (config.getWorkItemRelationsApiPath().replace("{parentId}", String.valueOf(workItemId))) + "&api-version=" + config.getApiVersion();
        String response = httpClient.get(url);
        JSONObject jsonResponse = new JSONObject(response);
        return jsonResponse.optJSONArray("relations");
    }

    /**
     * Checks if a relation represents a Task.
     *
     * @param relation
     * @return true if relation is a task hierarchy link
     */
    private boolean isTaskRelation(JSONObject relation) {
        String relType = relation.optString("rel", "");
        if (!relType.equals("System.LinkTypes.Hierarchy-Forward")) {
            return false;
        }
        JSONObject attributes = relation.optJSONObject("attributes");
        if (attributes == null) {
            return false;
        }
        String name = attributes.optString("name");
        return name.equals("Child");
    }

    /**
     * Checks if a relation represents a Pull Request.
     *
     * @param relation
     * @return true if relation is a pull request artifact link
     */
    private boolean isPullRequestRelation(JSONObject relation) {
        String relType = relation.optString("rel", "");
        if (!relType.equals("ArtifactLink")) {
            return false;
        }
        JSONObject attributes = relation.optJSONObject("attributes");
        if (attributes == null) {
            return false;
        }
        String name = attributes.optString("name");
        return name.equals("Pull Request");
    }

    /**
     * Processes a task relation and logs its details.
     *
     * @param taskUrl
     * @throws Exception
     */
    private int processTaskRelation(String taskUrl, WorkItem workItem) throws Exception {
        int taskAdded = 0;
        String taskResponse = httpClient.get(taskUrl);
        JSONObject taskJsonResponse = new JSONObject(taskResponse);
        //logger.trace(taskJsonResponse.toString());
        JSONObject fields = taskJsonResponse.optJSONObject("fields");
        if (fields == null) {
            return taskAdded;
        }
        String workItemType = fields.optString("System.WorkItemType");
        if (workItemType.equals("Task")) {
            String taskState = fields.optString("System.State");
            String taskType = fields.optString("Microsoft.VSTS.Common.Activity");
            JSONObject assignedToObj = fields.optJSONObject("System.AssignedTo");
            String assignedTo = assignedToObj != null ? assignedToObj.optString("displayName", "Unassigned") : "Unassigned";
            String originalEstimate = fields.optString("Microsoft.VSTS.Scheduling.OriginalEstimate");
            //TODO: Test Remaining hrs
            String remainingHrs = fields.optString("Microsoft.VSTS.Scheduling.RemainingWork");

            logger.trace("    Task Type: {} - State: {} - Assigned To: {} - Original Estimate: {} hrs - Remaining Hrs: {} hrs",
                    taskType, taskState, assignedTo, originalEstimate, remainingHrs);

            // Create Task DTO and add to WorkItem
            WorkItem.Task task = new WorkItem.Task(taskType, taskState, assignedTo, originalEstimate, remainingHrs);
            workItem.addTask(task);
            taskAdded = 1;
        }
        return taskAdded;
    }

    /**
     * Processes a pull request relation and logs its details including threads
     * and commenters.
     *
     * @param teamUri
     * @param prUrlLink
     * @throws Exception
     */
    private int processPullRequestRelation(String teamUri, String prUrlLink, WorkItem workItem) throws Exception {
        int prAdded;
        try {
            // Parse PR URL to extract IDs
            String decodedUrl = URLDecoder.decode(prUrlLink.replace("vstfs:///Git/PullRequestId/", ""), StandardCharsets.UTF_8.toString());
            String[] parts = decodedUrl.split("/");

            String projectId = parts[0];
            String repositoryId = parts[1];
            String pullRequestId = parts[2];

            // Fetch PR details
            String prDetailsUrl = buildPullRequestDetailsUrl(teamUri, repositoryId, pullRequestId);
            logger.trace("Pull PR for {} {} {} from: {}", projectId, repositoryId, pullRequestId, prDetailsUrl);

            String prDetailsResponse = httpClient.get(prDetailsUrl);
            JSONObject prDetailsJsonResponse = new JSONObject(prDetailsResponse);
            String createdBy = prDetailsJsonResponse.optJSONObject("createdBy").optString("displayName");
            String creationDate = prDetailsJsonResponse.optString("creationDate");
            logger.trace("  Pull request: {} - {} by {} on {} : {}", workItem, pullRequestId, createdBy, creationDate, prDetailsJsonResponse);
            // Create PullRequest object
            PullRequest pullRequest = new PullRequest(pullRequestId, createdBy, creationDate);

            // Fetch PR threads
            String prThreadUrl = buildPullRequestThreadUrl(teamUri, repositoryId, pullRequestId);
            String prThreadResponse = httpClient.get(prThreadUrl);
            JSONObject prThreadJsonResponse = new JSONObject(prThreadResponse);

            processPullRequestThreads(prThreadJsonResponse, pullRequest);

            // Add PullRequest to WorkItem
            workItem.addPullRequest(pullRequest);
            prAdded = 1;
            return prAdded;
        } catch (Exception e) {
            logger.error("Failed to process pull request relation from URL: {}", prUrlLink, e);
            throw e;
        }
    }

    /**
     * Builds the PR details API URL.
     *
     * @param teamUri
     * @param repositoryId
     * @param pullRequestId
     * @return formatted URL
     */
    private String buildPullRequestDetailsUrl(String teamUri, String repositoryId, String pullRequestId) {
        return teamUri + "/" + (config.getPullRequestApiPath())
                .replace("{repositoryId}", repositoryId)
                .replace("{pullRequestId}", pullRequestId)
                + "?api-version=" + config.getApiVersion();
    }

    /**
     * Builds the PR thread API URL.
     *
     * @param teamUri
     * @param repositoryId
     * @param pullRequestId
     * @return formatted URL
     */
    private String buildPullRequestThreadUrl(String teamUri, String repositoryId, String pullRequestId) {
        return teamUri + "/" + (config.getPRThreadApiPath())
                .replace("{repositoryId}", repositoryId)
                .replace("{pullRequestId}", pullRequestId)
                + "?api-version=" + config.getApiVersion();
    }

    /**
     * Processes pull request threads and extracts commenter information.
     *
     * @param prThreadJsonResponse
     * @param pullRequest
     */
    private void processPullRequestThreads(JSONObject prThreadJsonResponse, PullRequest pullRequest) {
        JSONArray prThreadArray = prThreadJsonResponse.optJSONArray("value");
        if (prThreadArray == null) {
            return;
        }
        for (int j = 0; j < prThreadArray.length(); j++) {
            JSONObject prObject = prThreadArray.getJSONObject(j);
            String prThreadId = prObject.optString("id");
            String prThreadStatus = prObject.optString("status");
            Boolean prThreadIsDeleted = prObject.optBoolean("isDeleted");

            if (!prThreadIsDeleted && !prThreadStatus.equals("abandoned")) {
                // Ignores abandoned & Deleted PR threads
                // Accepts not deleted PRs in notSet, active, completed status
                PullRequestThread thread = new PullRequestThread(prThreadId, prThreadStatus, prThreadIsDeleted);
                Map<String, String[]> prThreadCommenters = extractThreadCommenters(prObject, thread);

                if (!prThreadCommenters.isEmpty()) {
                    logger.trace("      PR {} (Text)Thread {} in '{}' state with {} collaborators: {}",
                            pullRequest.getPullRequestId(), prThreadId, prThreadStatus, prThreadCommenters.size(), prThreadCommenters);
                }
                pullRequest.addThread(thread);
            }
        }
    }

    /**
     * Extracts commenter information from a PR thread and populates the thread
     * object.
     *
     * @param prThreadObject
     * @param thread
     * @return Map of author name to array of commented dates
     */
    private Map<String, String[]> extractThreadCommenters(JSONObject prThreadObject, PullRequestThread thread) {
        Map<String, String[]> prThreadCommenters = new HashMap<>();
        JSONArray prThreadComments = prThreadObject.optJSONArray("comments");
        if (prThreadComments == null) {
            return prThreadCommenters;
        }
        for (int k = 0; k < prThreadComments.length(); k++) {
            JSONObject commentObj = prThreadComments.getJSONObject(k);
            String prThreadCommentType = commentObj.optString("commentType");
            if (prThreadCommentType.equals("text")) {
                String commentPublishedDate = commentObj.optString("publishedDate");
                String author = commentObj.optJSONObject("author").optString("displayName");
                // NOTE: DO NOT get content to avoid any accidental exposure of sensitive data.
                //Uncoment only for debugging
                //logger.trace("      {} : {} : {}", commentPublishedDate, author, commentObj.optString("content", ""));
                String[] commentedDates = prThreadCommenters.getOrDefault(author, new String[]{});
                String[] updatedDates = new String[commentedDates.length + 1];
                System.arraycopy(commentedDates, 0, updatedDates, 0, commentedDates.length);
                updatedDates[commentedDates.length] = commentPublishedDate;
                prThreadCommenters.put(author, updatedDates);
            }
        }

        // Add all commenters to the thread object
        for (Map.Entry<String, String[]> entry : prThreadCommenters.entrySet()) {
            thread.addCommenter(entry.getKey(), entry.getValue());
        }

        return prThreadCommenters;
    }

}
