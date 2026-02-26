package pc.ado.service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pc.ado.AdoConfig;
import pc.ado.DateUtils;
import pc.ado.constants.AdoConstants;
import pc.ado.dto.Iteration;
import pc.ado.dto.TeamMemberCapacity;
import pc.ado.exception.AdoParsingException;

/**
 * Service responsible for parsing Azure DevOps API JSON responses.
 *
 * <p>Encapsulates all JSON parsing logic, following Single Responsibility Principle. Uses:
 *
 * <ul>
 *   <li>Constants for JSON field names
 *   <li>Custom exceptions for parsing errors
 *   <li>Builder pattern for complex objects
 * </ul>
 */
public class AdoJsonParserService {

  private static final Logger logger = LoggerFactory.getLogger(AdoJsonParserService.class);

  /**
   * Parses iteration data from JSON response.
   *
   * @param project project name
   * @param team team name
   * @param iterationJson JSON object containing iteration data
   * @return parsed Iteration object
   * @throws AdoParsingException if parsing fails
   */
  public Iteration parseIteration(String project, String team, JSONObject iterationJson)
      throws AdoParsingException {
    try {
      String id = iterationJson.optString(AdoConstants.JsonFields.ID, AdoConstants.Defaults.NOT_AVAILABLE);
      String name = iterationJson.optString(AdoConstants.JsonFields.NAME, AdoConstants.Defaults.NOT_AVAILABLE);
      String startDate = AdoConstants.Defaults.NOT_AVAILABLE;
      String finishDate = AdoConstants.Defaults.NOT_AVAILABLE;

      JSONObject attributes = iterationJson.optJSONObject(AdoConstants.JsonFields.ATTRIBUTES);
      if (attributes != null) {
        startDate = extractAndFormatDate(attributes, AdoConstants.JsonFields.START_DATE);
        finishDate = extractAndFormatDate(attributes, AdoConstants.JsonFields.FINISH_DATE);
        logger.trace("Parsed iteration: {} ({} to {})", name, startDate, finishDate);
      }

      return new Iteration(project, team, id, name, startDate, finishDate);
    } catch (JSONException e) {
      logger.error("Failed to parse iteration JSON", e);
      throw new AdoParsingException(
          "Failed to parse iteration", e, iterationJson.toString());
    }
  }

  /**
   * Parses team member capacity from JSON response.
   *
   * @param teamMemberJson JSON object containing team member data
   * @param teamDaysOff JSON array of team-wide days off
   * @return TeamMemberCapacity or null if no capacity found
   * @throws AdoParsingException if parsing fails
   */
  public TeamMemberCapacity parseTeamMemberCapacity(
      JSONObject teamMemberJson, JSONArray teamDaysOff) throws AdoParsingException {
    try {
      JSONObject teamMember = teamMemberJson.getJSONObject(AdoConstants.JsonFields.TEAM_MEMBER);
      String displayName = teamMember.getString(AdoConstants.JsonFields.DISPLAY_NAME);
      JSONArray activities = teamMemberJson.getJSONArray(AdoConstants.JsonFields.ACTIVITIES);

      // Collect all unique days off (team + member)
      Set<LocalDate> uniqueDaysOff = collectDaysOff(teamDaysOff, teamMemberJson);
      int totalDaysOff = uniqueDaysOff.size();

      for (int i = 0; i < activities.length(); i++) {
        JSONObject activity = activities.getJSONObject(i);
        double capacityPerDay = activity.optDouble(AdoConstants.JsonFields.CAPACITY_PER_DAY, 0);
        if (capacityPerDay > 0) {
          logger.trace("'{}' unique days off: {}", displayName, totalDaysOff);
          return new TeamMemberCapacity(displayName, capacityPerDay, totalDaysOff);
        }
      }
    } catch (JSONException e) {
      logger.debug("Failed to parse team member capacity", e);
      throw new AdoParsingException(
          "Failed to parse team member capacity", e, teamMemberJson.toString());
    }
    return null;
  }

  /**
   * Extracts and formats a date field from JSON.
   *
   * @param jsonObject the JSON object containing the date field
   * @param fieldName the name of the date field
   * @return formatted date string or "N/A"
   */
  private String extractAndFormatDate(JSONObject jsonObject, String fieldName) {
    String dateStr = jsonObject.optString(fieldName, AdoConstants.Defaults.NOT_AVAILABLE);
    if (!dateStr.equals(AdoConstants.Defaults.NOT_AVAILABLE)) {
      try {
        return DateUtils.formatISODate(dateStr);
      } catch (Exception e) {
        logger.warn("Failed to format date: {}", dateStr, e);
      }
    }
    return AdoConstants.Defaults.NOT_AVAILABLE;
  }

  /**
   * Collects all unique days off for a team member.
   *
   * @param teamDaysOff team-wide days off
   * @param teamMemberJson team member's individual days off
   * @return set of unique days off
   */
  private Set<LocalDate> collectDaysOff(JSONArray teamDaysOff, JSONObject teamMemberJson) {
    Set<LocalDate> uniqueDaysOff = new HashSet<>();

    // Add team holidays
    if (teamDaysOff != null) {
      for (int i = 0; i < teamDaysOff.length(); i++) {
        JSONObject dayOff = teamDaysOff.getJSONObject(i);
        addDaysOffRange(uniqueDaysOff, dayOff);
      }
    }

    // Add member PTO
    JSONArray memberDaysOff = teamMemberJson.optJSONArray(AdoConstants.JsonFields.DAYS_OFF);
    if (memberDaysOff != null) {
      for (int i = 0; i < memberDaysOff.length(); i++) {
        JSONObject dayOff = memberDaysOff.getJSONObject(i);
        addDaysOffRange(uniqueDaysOff, dayOff);
      }
    }

    return uniqueDaysOff;
  }

  /**
   * Adds a range of days off to the set.
   *
   * @param uniqueDaysOff set to add days to
   * @param dayOffJson JSON object with start and end dates
   */
  private void addDaysOffRange(Set<LocalDate> uniqueDaysOff, JSONObject dayOffJson) {
    LocalDate start = DateUtils.formatISODateToLocalDate(
        dayOffJson.getString(AdoConstants.JsonFields.START));
    LocalDate end = DateUtils.formatISODateToLocalDate(
        dayOffJson.getString(AdoConstants.JsonFields.END));
    uniqueDaysOff.addAll(DateUtils.getWeekDaysBetween(start, end));
  }

  /**
   * Parses iterations array from JSON response.
   *
   * @param response JSON response string
   * @param project project name
   * @param team team name
   * @param config ADO configuration
   * @param includeOnlyIterationNames filter for iteration names (empty = all)
   * @return list of parsed iterations
   * @throws AdoParsingException if parsing fails
   */
  public List<Iteration> parseIterations(
      String response,
      String project,
      String team,
      AdoConfig config,
      List<String> includeOnlyIterationNames) throws AdoParsingException {
    try {
      JSONObject jsonResponse = new JSONObject(response);
      JSONArray iterationsArray = jsonResponse.getJSONArray(AdoConstants.JsonFields.VALUE);
      List<Iteration> iterations = new ArrayList<>();

      for (int i = 0; i < iterationsArray.length(); i++) {
        JSONObject iterationJson = iterationsArray.getJSONObject(i);
        Iteration iteration = parseIteration(project, team, iterationJson);

        if (shouldIncludeIteration(iteration, iterationJson, config, includeOnlyIterationNames)) {
          iterations.add(iteration);
        }
      }

      logger.debug("Successfully parsed {} team iterations", iterations.size());
      return iterations;
    } catch (JSONException e) {
      logger.error("Failed to parse iterations response", e);
      throw new AdoParsingException("Failed to parse iterations response", e, response);
    }
  }

  /**
   * Determines if an iteration should be included based on filters.
   *
   * @param iteration the parsed iteration
   * @param iterationJson original JSON for date extraction
   * @param config ADO configuration
   * @param includeOnlyIterationNames iteration name filter
   * @return true if iteration should be included
   */
  private boolean shouldIncludeIteration(
      Iteration iteration,
      JSONObject iterationJson,
      AdoConfig config,
      List<String> includeOnlyIterationNames) {

    // Check date filter
    JSONObject attributes = iterationJson.optJSONObject(AdoConstants.JsonFields.ATTRIBUTES);
    if (attributes != null) {
      String rawFinishDate = attributes.optString(
          AdoConstants.JsonFields.FINISH_DATE,
          AdoConstants.Defaults.NOT_AVAILABLE);

      if (!rawFinishDate.equals(AdoConstants.Defaults.NOT_AVAILABLE)) {
        LocalDate finishDate = DateUtils.formatISODateToLocalDate(rawFinishDate);
        LocalDate ignoreBeforeDate = config.getIgnoreIterationsEndedBefore();

        if (ignoreBeforeDate != null && finishDate.isBefore(ignoreBeforeDate)) {
          logger.trace(
              "Ignoring iteration '{}:{}' as it ended before {}",
              iteration.getName(),
              finishDate,
              ignoreBeforeDate);
          return false;
        }
      }
    }

    // Check name filter
    if (!includeOnlyIterationNames.isEmpty()
        && !includeOnlyIterationNames.contains(iteration.getName())) {
      return false;
    }

    return true;
  }

  /**
   * Builds team URI with proper URL encoding.
   *
   * @param config ADO configuration
   * @param project project name
   * @param team team name
   * @return properly encoded team URI
   */
  public String buildTeamUri(AdoConfig config, String project, String team) {
    try {
      String baseUri = config.getBaseUri() + config.getOrganization() + "/";
      String encodedProject = URLEncoder.encode(project, StandardCharsets.UTF_8.toString())
          .replaceAll(AdoConstants.Defaults.PLUS_SIGN_REGEX, AdoConstants.Defaults.SPACE_ENCODED);
      String projectUri = baseUri + encodedProject + "/";

      if (team != null) {
        String encodedTeam = URLEncoder.encode(team, StandardCharsets.UTF_8.toString())
            .replaceAll(AdoConstants.Defaults.PLUS_SIGN_REGEX, AdoConstants.Defaults.SPACE_ENCODED);
        return projectUri + encodedTeam + "/";
      }

      return projectUri;
    } catch (Exception e) {
      logger.error("Failed to build team URI", e);
      throw new RuntimeException("Failed to build team URI", e);
    }
  }
}
