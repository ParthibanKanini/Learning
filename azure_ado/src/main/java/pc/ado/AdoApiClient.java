package pc.ado;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pc.ado.dto.Iteration;
import pc.ado.dto.TeamMemberCapacity;

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

    public AdoApiClient(AdoHttpClient httpClient, AdoConfig config) {
        this.httpClient = httpClient;
        this.config = config;
    }

    /**
     * Retrieves all team iterations for the configured team and project.
     *
     * @return list of Iteration objects
     * @throws Exception if the API call fails
     */
    public List<Iteration> getTeamSprint(String project, String team) throws Exception {
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
                iterations.add(parseIteration(project, iteration));
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

            //int totalCapacityPerDay = jsonResponse.getInt("totalCapacityPerDay");
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
    private Iteration parseIteration(String project, JSONObject iterationJson) {
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
                logger.debug("Parsed iteration : {} ({} to {})", name, startDate, finishDate);
            }
        } catch (Exception e) {
            logger.error(iterationJson.toString(), e);
        }
        return new Iteration(project, id, name, startDate, finishDate);
    }

    /**
     * Parses team member capacity data from JSON response. Returns null if no
     * capacity is found.
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
                logger.debug("Team Member '{}' Day Off from {} to {} = {} days.", displayName, startDate, endDate, teamMemberPTO);
            }

            for (int i = 0; i < activities.length(); i++) {
                JSONObject activity = activities.getJSONObject(i);
                double capacityPerDay = activity.optDouble("capacityPerDay", 0);
                if (capacityPerDay > 0) {
                    //todo: dont use teamMemberSprintdaysoff length directly, consider start and end date
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
        String teamName = URLEncoder.encode(team, StandardCharsets.UTF_8.toString())
                .replaceAll(PLUS_SIGN, SPACE_ENCODED);
        return projUri + teamName + "/";
    }
}
