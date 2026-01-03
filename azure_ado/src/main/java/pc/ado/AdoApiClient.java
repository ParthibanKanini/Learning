package pc.ado;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Handles interaction with Azure DevOps API. Encapsulates all API calls and
 * response parsing.
 */
public class AdoApiClient {

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
    public List<Iteration> getTeamIterations() throws Exception {
        String teamUri = buildTeamUri();
        String itrUrl = teamUri + config.getIterationsApiPath() + "?api-version=" + config.getApiVersion();

        String response = httpClient.get(itrUrl);
        JSONObject jsonResponse = new JSONObject(response);
        JSONArray iterationsArray = jsonResponse.getJSONArray("value");

        List<Iteration> iterations = new ArrayList<>();
        for (int i = 0; i < iterationsArray.length(); i++) {
            JSONObject iteration = iterationsArray.getJSONObject(i);
            iterations.add(parseIteration(iteration));
        }

        return iterations;
    }

    /**
     * Retrieves team member capacities for a specific iteration.
     *
     * @param iterationId the ID of the iteration
     * @return list of TeamMemberCapacity objects with capacity > 0
     * @throws Exception if the API call fails
     */
    public List<TeamMemberCapacity> getIterationCapacities(String iterationId) throws Exception {
        String teamUri = buildTeamUri();
        String capacitiesPath = config.getCapacitiesApiPath().replace("{iterationId}", iterationId);
        String url = teamUri + "/" + capacitiesPath + "?api-version=" + config.getApiVersion();

        String response = httpClient.get(url);
        JSONObject jsonResponse = new JSONObject(response);
        JSONArray teamMembersArray = jsonResponse.getJSONArray("teamMembers");

        List<TeamMemberCapacity> capacities = new ArrayList<>();
        for (int i = 0; i < teamMembersArray.length(); i++) {
            JSONObject teamMember = teamMembersArray.getJSONObject(i);
            TeamMemberCapacity capacity = parseTeamMemberCapacity(teamMember);
            if (capacity != null && capacity.getCapacityPerDay() > 0) {
                capacities.add(capacity);
            }
        }
        return capacities;
    }

    /**
     * Parses iteration data from JSON response.
     */
    private Iteration parseIteration(JSONObject iterationJson) {
        String id = iterationJson.optString("id", "N/A");
        String name = iterationJson.optString("name", "N/A");
        String startDate = "N/A";
        String finishDate = "N/A";
        try {
            JSONObject attributes = iterationJson.optJSONObject("attributes");
            if (attributes != null) {
                startDate = attributes.optString("startDate", "N/A");
                finishDate = attributes.optString("finishDate", "N/A");
            }
        } catch (Exception e) {
            // Attributes not available for this iteration
        }
        return new Iteration(id, name, startDate, finishDate);
    }

    /**
     * Parses team member capacity data from JSON response. Returns null if no
     * capacity is found.
     */
    private TeamMemberCapacity parseTeamMemberCapacity(JSONObject teamMemberJson) {
        try {
            JSONObject teamMember = teamMemberJson.getJSONObject("teamMember");
            String displayName = teamMember.getString("displayName");
            JSONArray activities = teamMemberJson.getJSONArray("activities");
            for (int i = 0; i < activities.length(); i++) {
                JSONObject activity = activities.getJSONObject(i);
                double capacityPerDay = activity.optDouble("capacityPerDay", 0);
                if (capacityPerDay > 0) {
                    return new TeamMemberCapacity(displayName, capacityPerDay);
                }
            }
        } catch (JSONException e) {
            // Return null if parsing fails
        }
        return null;
    }

    /**
     * Builds the team URI from configuration.
     */
    private String buildTeamUri() throws Exception {
        String baseUri = config.getBaseUri() + config.getOrganization() + "/";
        String projUri = baseUri + config.getProject() + "/";
        String teamName = URLEncoder.encode(config.getTeam(), StandardCharsets.UTF_8.toString())
                .replaceAll("\\+", "%20");
        return projUri + teamName + "/";
    }
}
