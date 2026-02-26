package pc.ado;

import java.net.URLDecoder;
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

import pc.ado.constants.AdoConstants;
import pc.ado.dto.Iteration;
import pc.ado.dto.PullRequest;
import pc.ado.dto.PullRequestThread;
import pc.ado.dto.TeamMemberCapacity;
import pc.ado.dto.ThreadComment;
import pc.ado.dto.WorkItem;
import pc.ado.exception.AdoException;
import pc.ado.exception.AdoParsingException;
import pc.ado.gateway.AdoGateway;
import pc.ado.service.AdoJsonParserService;

/**
 * Production-grade Azure DevOps API client.
 *
 * <p>Refactored to follow SOLID principles:
 *
 * <ul>
 *   <li>Single Responsibility: Focuses on orchestrating API calls and business logic
 *   <li>Dependency Inversion: Depends on AdoGateway abstraction, not concrete HttpClient
 *   <li>Delegates parsing to AdoJsonParserService
 *   <li>Uses structured exceptions for better error handling
 *   <li>Uses constants to eliminate magic strings
 * </ul>
 */
public class AdoApiClient {

  private static final Logger logger = LoggerFactory.getLogger(AdoApiClient.class);

  private final AdoGateway gateway;
  private final AdoConfig config;
  private final AdoJsonParserService parserService;

  /**
   * Creates an API client with the given configuration and HTTP client.
   *
   * <p>For backward compatibility with existing code.
   */
  public AdoApiClient(AdoConfig config, AdoHttpClient httpClient) {
    this(config, (AdoGateway) httpClient, new AdoJsonParserService());
  }

  /**
   * Creates an API client with full dependency injection.
   *
   * <p>Recommended constructor for testability.
   *
   * @param config ADO configuration
   * @param gateway gateway for API communication
   * @param parserService service for parsing JSON responses
   */
  public AdoApiClient(AdoConfig config, AdoGateway gateway, AdoJsonParserService parserService) {
    this.config = config;
    this.gateway = gateway;
    this.parserService = parserService;
    logger.debug("API client initialized");
  }

  /**
   * Retrieves team iterations for the specified team and project.
   *
   * <p>Refactored to use parser service and structured exceptions.
   *
   * @param project project name
   * @param team team name
   * @param itrNames iteration names to filter (empty = all)
   * @return list of filtered iterations
   * @throws AdoException if the API call or parsing fails
   */
  public List<Iteration> getTeamSprint(String project, String team, List<String> itrNames)
      throws AdoException {
    logger.trace("Fetching team iterations for project '{}', team '{}'", project, team);

    String teamUri = buildTeamUri(project, team);
    String url = buildIterationsUrl(teamUri);

    try {
      String response = gateway.get(url);
      return parserService.parseIterations(response, project, team, config, itrNames);
    } catch (AdoParsingException e) {
      logger.error("Failed to parse iterations for team: {}", team, e);
      throw e;
    } catch (AdoException e) {
      logger.error("Failed to fetch iterations for team: {}", team, e);
      throw e;
    }
  }

  /**
   * Builds the iterations API URL.
   *
   * @param teamUri base team URI
   * @return complete iterations URL
   */
  private String buildIterationsUrl(String teamUri) {
    return teamUri + config.getIterationsApiPath() + "?api-version=" + config.getApiVersion();
  }

  /**
   * Retrieves team member capacities for a specific iteration.
   *
   * <p>Refactored to separate concerns: API calls, parsing, and filtering.
   *
   * @param project project name
   * @param team team name
   * @param iterationId the ID of the iteration
   * @return list of TeamMemberCapacity objects with capacity > 0
   * @throws AdoException if the API call or parsing fails
   */
  public List<TeamMemberCapacity> getIterationCapacities(
      String project, String team, String iterationId) throws AdoException {
    logger.trace("Fetching capacities for iteration: {}", iterationId);

    String teamUri = buildTeamUri(project, team);

    try {
      // Get team holidays
      JSONArray teamDaysOff = fetchTeamDaysOff(teamUri, iterationId);

      // Get team member capacities
      List<TeamMemberCapacity> capacities = fetchTeamMemberCapacities(
          teamUri, iterationId, teamDaysOff);

      logger.info(
          "Identified {} team members with capacity for iteration: {}",
          capacities.size(),
          iterationId);
      return capacities;
    } catch (JSONException e) {
      logger.error("Failed to parse capacities response for iteration: {}", iterationId, e);
      throw new AdoParsingException(
          "Failed to parse capacities response", e, iterationId);
    }
  }

  /**
   * Fetches team-wide days off for an iteration.
   *
   * @param teamUri base team URI
   * @param iterationId iteration ID
   * @return JSON array of team days off
   * @throws AdoException if the API call fails
   */
  private JSONArray fetchTeamDaysOff(String teamUri, String iterationId) throws AdoException {
    String url = teamUri
        + "/"
        + config.getIterationDayOffPath().replace("{iterationId}", iterationId)
        + "?api-version="
        + config.getApiVersion();

    String response = gateway.get(url);
    JSONObject jsonResponse = new JSONObject(response);
    return jsonResponse.getJSONArray(AdoConstants.JsonFields.DAYS_OFF);
  }

  /**
   * Fetches and parses team member capacities.
   *
   * @param teamUri base team URI
   * @param iterationId iteration ID
   * @param teamDaysOff team-wide days off
   * @return list of team member capacities with capacity > 0
   * @throws AdoException if the API call fails
   */
  private List<TeamMemberCapacity> fetchTeamMemberCapacities(
      String teamUri, String iterationId, JSONArray teamDaysOff) throws AdoException {
    String capacitiesPath = config.getCapacitiesApiPath().replace("{iterationId}", iterationId);
    String url = teamUri + "/" + capacitiesPath + "?api-version=" + config.getApiVersion();

    String response = gateway.get(url);
    JSONObject jsonResponse = new JSONObject(response);
    JSONArray teamMembersArray = jsonResponse.getJSONArray(AdoConstants.JsonFields.TEAM_MEMBERS);

    List<TeamMemberCapacity> capacities = new ArrayList<>();
    for (int i = 0; i < teamMembersArray.length(); i++) {
      JSONObject teamMember = teamMembersArray.getJSONObject(i);
      TeamMemberCapacity capacity = parserService.parseTeamMemberCapacity(teamMember, teamDaysOff);
      if (capacity != null && capacity.getCapacityPerDay() > 0) {
        capacities.add(capacity);
      }
    }
    return capacities;
  }

  /**
   * Parses iteration data from JSON response.
   *
   * <p>Deprecated: Use AdoJsonParserService.parseIteration() instead.
   *
   * @deprecated This method has been moved to AdoJsonParserService for better separation of
   *     concerns. Kept for backward compatibility.
   */
  @Deprecated
  private Iteration parseIteration(String project, String team, JSONObject iterationJson) {
    try {
      return parserService.parseIteration(project, team, iterationJson);
    } catch (AdoParsingException e) {
      logger.error("Failed to parse iteration", e);
      // Return a default iteration for backward compatibility
      return new Iteration(project, team,
          AdoConstants.Defaults.NOT_AVAILABLE,
          AdoConstants.Defaults.NOT_AVAILABLE,
          AdoConstants.Defaults.NOT_AVAILABLE,
          AdoConstants.Defaults.NOT_AVAILABLE);
    }
  }

  /**
   * Parses team member capacity data from JSON response.
   *
   * <p>Deprecated: Use AdoJsonParserService.parseTeamMemberCapacity() instead.
   *
   * @deprecated This method has been moved to AdoJsonParserService for better separation of
   *     concerns. Kept for backward compatibility.
   */
  @Deprecated
  private TeamMemberCapacity parseTeamMemberCapacity(
      JSONObject teamMemberJson, JSONArray teamDaysOff) {
    try {
      return parserService.parseTeamMemberCapacity(teamMemberJson, teamDaysOff);
    } catch (AdoParsingException e) {
      logger.debug("Failed to parse team member capacity", e);
      return null;
    }
  }

  /**
   * REMOVED: The inline parsing code below has been extracted to AdoJsonParserService.
   * This improves testability and follows Single Responsibility Principle.
        uniqueDaysOff.addAll(DateUtils.getWeekDaysBetween(start, end));
      }

      int totalDaysOff = uniqueDaysOff.size();

      for (int i = 0; i < activities.length(); i++) {
        JSONObject activity = activities.getJSONObject(i);
        double capacityPerDay = activity.optDouble("capacityPerDay", 0);
        if (capacityPerDay > 0) {
          logger.trace("'{}' unique days off: {}", displayName, totalDaysOff);
          return new TeamMemberCapacity(displayName, capacityPerDay, totalDaysOff);
        }
      }
    } catch (JSONException e) {
      logger.debug("Failed to parse team member capacity", e);
    }
    return null;
  }

  /**
   * Builds the team URI from configuration.
   *
   * <p>Delegated to AdoJsonParserService for reusability.
   *
   * @param project project name
   * @param team team name (can be null)
   * @return properly encoded team URI
   */
  private String buildTeamUri(String project, String team) {
    return parserService.buildTeamUri(config, project, team);
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
  public List<TeamMemberCapacity> getSprintWorkItems(
      String project, String team, Iteration iteration) throws Exception {
    String teamUri = buildTeamUri(project, team);
    try {
      // Get team holidays for the iteration
      String url =
          teamUri
              + "/"
              + (config.getWorkItemsApiPath().replace("{iterationId}", iteration.getId()))
              + "?api-version="
              + config.getApiVersion();
      String response = gateway.get(url);
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
      // logger.debug("Sprint Work Items response: {}", response);
    } catch (JSONException e) {
      logger.error("Failed to parse work items response for iteration: {}", iteration.getId(), e);
      throw new Exception("Failed to parse work items response", e);
    }
    return null;
  }

  /**
   * Retrieves and logs fields for a given work item(story level).
   *
   * @param project
   * @param team
   * @param workItemLink
   * @throws Exception
   */
  private void getWorkItemFields(String project, String workItemLink, Iteration iteration)
      throws Exception {
    String workItemResponse = gateway.get(workItemLink);
    JSONObject workItemJsonResponse = new JSONObject(workItemResponse);
    // logger.debug("Work Item Response: {}", workItemJsonResponse.toString());
    String id = workItemJsonResponse.optString("id", "N/A");
    JSONObject fields = workItemJsonResponse.optJSONObject("fields");
    if (fields != null) {
      // Do not extract any project specific details
      // String title = fields.optString("System.Title", "N/A");
      String title = "";
      String workItemType = fields.optString("System.WorkItemType"); // Bug/Story
      String state = fields.optString("System.State");
      // Check if the work item state is in the ignored list
      List<String> ignoredStates =
          (config.getIgnoredWorkItemStates() != null)
              ? config.getIgnoredWorkItemStates()
              : List.of();
      if (!ignoredStates.contains(state.trim())) {
        logger.trace("Fetching Work item ID: {} in state: {}", id, state);
        String storyPoints = fields.optString("Microsoft.VSTS.Scheduling.StoryPoints");
        String QAStoryPoints = fields.optString("GAAPChecklistProcess.QAPts");
        String OrigStoryPoints = fields.optString("GAAPChecklistProcess.OriginalStoryPts");
        JSONObject assignedToObj = fields.optJSONObject("System.AssignedTo");
        String assignedTo =
            assignedToObj != null
                ? assignedToObj.optString("displayName", "Unassigned")
                : "Unassigned";
        String priority = fields.optString("Microsoft.VSTS.Common.Priority");
        String severity = fields.optString("Microsoft.VSTS.Common.Severity");
        String createdDate = fields.optString("System.CreatedDate");
        JSONObject createdByObj = fields.optJSONObject("System.CreatedBy");
        String createdBy = createdByObj != null ? createdByObj.optString("displayName") : "Unknown";
        String devEndDate = fields.optString("Custom.DevEndDate");
        String qaReadyDate = fields.optString("Microsoft.VSTS.Scheduling.DueDate");
        // TODO: Test this field with ADO dataset.
        String qaEndDate = fields.optString("Custom.QACompletionDate");
        String implDetails = fields.optString("Custom.ImplementationDetails");

        String tags = fields.optString("System.Tags");
        logger.trace(
            "ID: {} | Title: {} | Type: {} | Story Points: {} | Assigned To: {} | State: {} | Priority: {} | Severity: {} | Created Date: {} | Created By: {} | Dev End Date: {} | QA End Date: {} | Tags: {}",
            id,
            title,
            workItemType,
            storyPoints,
            assignedTo,
            state,
            priority,
            severity,
            createdDate,
            createdBy,
            devEndDate,
            qaEndDate,
            tags);

        // Custom field for project
        String plannedReleaseVersion = fields.optString("Custom.SYMPlannedReleaseVersion", "");

        // Create and add WorkItem to iteration
        WorkItem workItem =
            new WorkItem(
                Integer.parseInt(id),
                title,
                workItemType,
                state,
                assignedTo,
                plannedReleaseVersion,
                storyPoints,
                QAStoryPoints,
                OrigStoryPoints,
                priority,
                severity,
                createdDate,
                createdBy,
                devEndDate,
                qaReadyDate,
                qaEndDate,
                !implDetails.isEmpty(),
                tags);

        populateWorkItemChildren(project, id, workItem);
        iteration.addWorkItem(workItem);
        logger.trace(
            "  Added work item {} in {} state to iteration {}", id, state, iteration.getName());
      }
    } else {
      logger.warn("No fields found for work item ID: {}", id);
    }
  }

  private void populateWorkItemChildren(String project, String id, WorkItem workItem)
      throws Exception {
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
    logger.trace("      Total tasks added to work item {}: {}", workItemId, totalTasksAdded);
  }

  /**
   * Retrieves and processes child pull requests for a given work item.
   *
   * @param project
   * @param workItemId
   * @throws Exception
   */
  private void populatePullRequests(String project, int workItemId, WorkItem workItem)
      throws Exception {
    String teamUri = buildTeamUri(project, null);
    int totalPullRequestsAdded = 0;
    try {
      JSONArray relations = fetchWorkItemRelations(teamUri, workItemId);
      if (relations != null) {
        for (int i = 0; i < relations.length(); i++) {
          JSONObject relation = relations.getJSONObject(i);
          if (isPullRequestRelation(relation)) {
            // logger.trace("{} -{}", workItemId, relation.toString());
            String urlLink = relation.optString("url");
            try {
              totalPullRequestsAdded += processPullRequestRelation(teamUri, urlLink, workItem);
            } catch (Exception e) {
              logger.info("PR details: {}", urlLink, e);
              logger.warn(
                  "Failed to process pull request for work item {}: {}",
                  workItemId,
                  e.getMessage());
              // Continue processing other PRs even if one fails
            }
          }
        }
      }
    } catch (Exception e) {
      logger.warn(
          "Failed to fetch work item relations for work item {}: {}", workItemId, e.getMessage());
      logger.debug("Error details", e);
      // Don't fail entirely if PR retrieval fails
    }
    logger.trace("      Total PR added to work item {}: {}", workItemId, totalPullRequestsAdded);
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
    String url =
        teamUri
            + "/"
            + (config
                .getWorkItemRelationsApiPath()
                .replace("{parentId}", String.valueOf(workItemId)))
            + "&api-version="
            + config.getApiVersion();
    String response = gateway.get(url);
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
    String taskResponse = gateway.get(taskUrl);
    JSONObject taskJsonResponse = new JSONObject(taskResponse);
    // logger.trace(taskJsonResponse.toString());
    JSONObject fields = taskJsonResponse.optJSONObject("fields");
    // log fields for debugging
    logger.trace(
        "    Task URL: {} - Fields: {}",
        taskUrl,
        (fields != null) ? fields.toString() : "No fields found");
    if (fields == null) {
      return taskAdded;
    }
    String taskId = extractWorkItemIdFromUrl(taskUrl);
    String workItemType = fields.optString("System.WorkItemType");
    String tags = fields.optString("System.Tags");
    // Skip tasks with Copilot tag if exclusion is enabled in config
    // Exclude tasks with Copilot tag (case-insensitive, supports variations like co-pilot, co
    // pilot)
    if (config.isFetchWorkItemDetailsTasksCopilotTagExclusion() && tags != null) {
      String tagsLower = tags.toLowerCase();
      if (tagsLower.contains("copilot")
          || tagsLower.contains("co-pilot")
          || tagsLower.contains("co pilot")) {
        logger.debug(
            "    Skipping task {} as it is tagged with {} or a similar variation", taskId, tags);
        return taskAdded;
      }
    }
    if (workItemType.equals("Task")) {
      String taskState = fields.optString("System.State");
      String taskType = fields.optString("Microsoft.VSTS.Common.Activity");
      JSONObject assignedToObj = fields.optJSONObject("System.AssignedTo");
      String assignedTo =
          assignedToObj != null
              ? assignedToObj.optString("displayName", "Unassigned")
              : "Unassigned";
      String originalEstimate = fields.optString("Microsoft.VSTS.Scheduling.OriginalEstimate");
      String completedHrs = fields.optString("Microsoft.VSTS.Scheduling.CompletedWork");
      // TODO: Test Remaining hrs
      String remainingHrs = fields.optString("Microsoft.VSTS.Scheduling.RemainingWork");
      /*tasks []
             ID
             remaining hrs
             actual hrs
             assigned to
             Dependancy
                 successorOf []
                 predecessorOf []
      */
      logger.trace(
          "    Task Type: {} - State: {} - Assigned To: {} - Original Estimate: {} hrs - Remaining Hrs: {} hrs - Completed Hrs: {} hrs",
          taskType,
          taskState,
          assignedTo,
          originalEstimate,
          remainingHrs,
          completedHrs);

      // Create Task DTO and add to WorkItem
      WorkItem.Task task =
          new WorkItem.Task(
              taskId,
              taskType,
              taskState,
              assignedTo,
              originalEstimate,
              remainingHrs,
              completedHrs);
      workItem.addTask(task);
      taskAdded = 1;
    }
    return taskAdded;
  }

  private String extractWorkItemIdFromUrl(String workItemUrl) {
    if (workItemUrl == null || workItemUrl.isBlank()) {
      return "N/A";
    }
    String url = workItemUrl;
    int queryIndex = url.indexOf("?");
    if (queryIndex >= 0) {
      url = url.substring(0, queryIndex);
    }
    int lastSlash = url.lastIndexOf("/");
    if (lastSlash < 0 || lastSlash == url.length() - 1) {
      return "N/A";
    }
    String idPart = url.substring(lastSlash + 1);
    try {
      Integer.parseInt(idPart);
      return idPart;
    } catch (NumberFormatException e) {
      return "N/A";
    }
  }

  /**
   * Processes a pull request relation and logs its details including threads and commenters.
   *
   * @param teamUri
   * @param prUrlLink
   * @throws Exception
   */
  private int processPullRequestRelation(String teamUri, String prUrlLink, WorkItem workItem)
      throws Exception {
    int prAdded;
    try {
      // Parse PR URL to extract IDs
      String decodedUrl =
          URLDecoder.decode(
              prUrlLink.replace("vstfs:///Git/PullRequestId/", ""),
              StandardCharsets.UTF_8.toString());
      String[] parts = decodedUrl.split("/");

      String projectId = parts[0];
      String repositoryId = parts[1];
      String pullRequestId = parts[2];

      // Fetch PR details
      String prDetailsUrl = buildPullRequestDetailsUrl(teamUri, repositoryId, pullRequestId);
      logger.trace(
          "Pull PR for {} {} {} from: {}", projectId, repositoryId, pullRequestId, prDetailsUrl);

      String prDetailsResponse = gateway.get(prDetailsUrl);
      JSONObject prDetailsJsonResponse = new JSONObject(prDetailsResponse);
      String createdBy = prDetailsJsonResponse.optJSONObject("createdBy").optString("displayName");
      String creationDate = prDetailsJsonResponse.optString("creationDate");
      logger.trace(
          "  Pull request: {} - {} by {} on {} : {}",
          workItem,
          pullRequestId,
          createdBy,
          creationDate,
          prDetailsJsonResponse);
      // Create PullRequest object
      PullRequest pullRequest = new PullRequest(pullRequestId, createdBy, creationDate);

      // Fetch PR threads
      String prThreadUrl = buildPullRequestThreadUrl(teamUri, repositoryId, pullRequestId);
      String prThreadResponse = gateway.get(prThreadUrl);
      JSONObject prThreadJsonResponse = new JSONObject(prThreadResponse);

      processPullRequestThreads(prThreadJsonResponse, pullRequest);

      /*if (!pullRequest.getThreads().isEmpty()) {
          logger.debug("  Pull request: {} - {} by {} on {} : {}", workItem, pullRequestId, createdBy, creationDate, prDetailsJsonResponse);
      }*/
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
  private String buildPullRequestDetailsUrl(
      String teamUri, String repositoryId, String pullRequestId) {
    return teamUri
        + "/"
        + (config.getPullRequestApiPath())
            .replace("{repositoryId}", repositoryId)
            .replace("{pullRequestId}", pullRequestId)
        + "?api-version="
        + config.getApiVersion();
  }

  /**
   * Builds the PR thread API URL.
   *
   * @param teamUri
   * @param repositoryId
   * @param pullRequestId
   * @return formatted URL
   */
  private String buildPullRequestThreadUrl(
      String teamUri, String repositoryId, String pullRequestId) {
    return teamUri
        + "/"
        + (config.getPRThreadApiPath())
            .replace("{repositoryId}", repositoryId)
            .replace("{pullRequestId}", pullRequestId)
        + "?api-version="
        + config.getApiVersion();
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
        PullRequestThread thread =
            new PullRequestThread(prThreadId, prThreadStatus, prThreadIsDeleted);
        Map<String, List<ThreadComment>> prThreadCommenters =
            extractThreadCommenters(prObject, thread, pullRequest.getCreatedBy());

        if (!prThreadCommenters.isEmpty()) {
          logger.trace(
              "      PR {} (Text)Thread {} in '{}' state with {} collaborators: {}",
              pullRequest.getPullRequestId(),
              prThreadId,
              prThreadStatus,
              prThreadCommenters.size(),
              prThreadCommenters);
        }
        pullRequest.addThread(thread);
      }
    }
  }

  /**
   * Extracts commenter information from a PR thread and populates the thread object.
   *
   * @param prThreadObject
   * @param thread
   * @return Map of author name to list of ThreadComment objects
   */
  private Map<String, List<ThreadComment>> extractThreadCommenters(
      JSONObject prThreadObject, PullRequestThread thread, String pullRequestCreatedBy) {
    Map<String, List<ThreadComment>> prThreadCommenters = new HashMap<>();
    JSONArray prThreadComments = prThreadObject.optJSONArray("comments");
    if (prThreadComments == null) {
      return prThreadCommenters;
    }
    for (int k = 0; k < prThreadComments.length(); k++) {
      JSONObject commentObj = prThreadComments.getJSONObject(k);
      // logger.trace("PR Comment: {}", commentObj.toString());
      String prThreadCommentType = commentObj.optString("commentType");
      if (prThreadCommentType.equals("text")) {
        String commentPublishedDate = commentObj.optString("publishedDate");
        String commenter = commentObj.optJSONObject("author").optString("displayName");

        // Skip submitter's comments if configured to ignore them
        if (config.isIgnoreSubmitterPRComments() && commenter.equals(pullRequestCreatedBy)) {
          logger.trace("      Ignoring PR submitter comment from: {}", commenter);
          continue;
        }
        String content =
            commentObj
                .optString("content", "")
                .replaceAll("[\r\n]+", "   "); // Replace newlines with three spaces
        // If content is a single word then ignore it based on isIgnoreSingleWordPRComment config
        if (config.isIgnoreSingleWordPRComment() && content.trim().split("\\s+").length == 1) {
          logger.trace("      Ignoring single word PR comment from: {} ", commenter);
          continue;
        }
        // If content contains any of the ignore phrases then skip it
        List<String> ignorePhrases = config.getIgnoreCommentsWith();
        boolean containsIgnorePhrase = false;
        if (ignorePhrases != null && !ignorePhrases.isEmpty()) {
          for (String phrase : ignorePhrases) {
            if (content.toLowerCase().contains(phrase.toLowerCase())) {
              containsIgnorePhrase = true;
              logger.trace(
                  "      Ignoring PR comment from: {} containing phrase: '{}'", commenter, phrase);
              break;
            }
          }
        }
        if (containsIgnorePhrase) {
          continue;
        }
        // Empty comment content to avoid any accidental exposure of sensitive data in reports.
        content = "";
        List<ThreadComment> comments =
            prThreadCommenters.getOrDefault(commenter, new ArrayList<>());
        comments.add(new ThreadComment(commentPublishedDate, content));
        prThreadCommenters.put(commenter, comments);
      }
    }

    // Add all commenters to the thread object
    for (Map.Entry<String, List<ThreadComment>> entry : prThreadCommenters.entrySet()) {
      thread.addCommenter(entry.getKey(), entry.getValue());
    }

    return prThreadCommenters;
  }
}
