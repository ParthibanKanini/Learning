package pc.ado;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pc.ado.dto.Iteration;
import pc.ado.dto.TeamMemberCapacity;

/**
 * Main entry point for Azure DevOps reporting tool.
 *
 * This class coordinates the retrieval and display of team iterations and
 * capacities. It uses a dependency injection pattern with separate services for
 * configuration, HTTP communication, API interaction, and presentation.
 */
public class AdoTool {

    private static final Logger logger = LoggerFactory.getLogger(AdoTool.class);

    private static final String TAB = "\t";

    public static void main(String[] args) {
        AdoTool tool = new AdoTool();
        tool.run();
    }

    /**
     * Runs the Azure DevOps reporting workflow.
     */
    public void run() {
        try {
            AdoConfig config = AdoConfig.getInstance();
            AdoHttpClient httpClient = new AdoHttpClient(config.getPatToken());
            try {
                AdoApiClient apiClient = new AdoApiClient(httpClient, config);
                AdoReportFormatter formatter = new AdoReportFormatter(config);
                final String project = config.getProject();
                final String team = config.getTeam();
                formatter.logTeamName(project, team);
                // Retrieve and display all team iterations with their capacities
                List<Iteration> iterations = apiClient.getTeamSprint(project, team);
                // Retrieve iteration details 
                List<String> itrDetails = collectTeamSprintDetails(project, team, iterations, apiClient);
                // Write details to file
                formatter.writeSprintCapacitiesToFile(itrDetails);
                logger.info("Report generation completed successfully.");
            } finally {
                httpClient.close();
            }
        } catch (Exception e) {
            logger.error("Error occurred during report generation", e);
            System.exit(1);
        }
    }

    /**
     * Collects team iterations and their details, then writes them to file.
     */
    public List<String> collectTeamSprintDetails(String project, String team, List<Iteration> iterations, AdoApiClient apiClient) {
        List<String> allDetails = new java.util.ArrayList<>();
        // Add header on first iteration
        allDetails.add("Project" + TAB + "Team Name" + TAB + "Iteration Name" + TAB + "Start Date" + TAB + "Finish Date" + TAB + "Team Member" + TAB + "Capacity(Hrs) Per Day" + TAB + "PTO + Holidays" + TAB + "worked Days" + TAB + "worked(accounted) hrs");
        for (Iteration iteration : iterations) {
            //if (iteration.getName().equals("ESG Rel 6.1 Sprint 1")) {
            allDetails.addAll(getSprintCapacityDetails(project, team, iteration, apiClient));
            //}
        }
        return allDetails;
    }

    /**
     * Retrieves formatted team members with capacity for a specific iteration.
     */
    private List<String> getSprintCapacityDetails(String project, String team, Iteration iteration, AdoApiClient apiClient) {
        List<String> details = new java.util.ArrayList<>();
        try {
            logger.debug("Fetching capacities for iteration '{}'", iteration.getName());
            List<TeamMemberCapacity> capacities = apiClient.getIterationCapacities(project, team, iteration.getId());
            logger.debug("Retrieved {} team members for iteration", capacities.size());
            //logger.trace("---" + iteration.getName() + " - " + iteration.getStartDate() + " - " + iteration.getFinishDate() + "---");
            String itrDetails = getSprintDetails(project, team, iteration);
            for (TeamMemberCapacity capacity : capacities) {
                StringBuilder sb = new StringBuilder(itrDetails);
                sb.append(capacity.getDisplayName());
                sb.append(TAB);
                sb.append(capacity.getCapacityPerDay());
                sb.append(TAB);
                sb.append(capacity.getDaysOff());
                sb.append(TAB);
                int workedDays = DateUtils.calculateWeekDays(DateUtils.formatStringToLocalDate(iteration.getStartDate()), DateUtils.formatStringToLocalDate(iteration.getFinishDate()), capacity.getDaysOff());
                sb.append(workedDays);
                sb.append(TAB);
                sb.append(workedDays * capacity.getCapacityPerDay());
                details.add(sb.toString());
            }
        } catch (Exception e) {
            logger.error("Error occurred while fetching sprint capacities: {}", iteration.getName(), e);
        }
        return details;
    }

    /**
     * Returns sprint details as a formatted string.
     */
    private String getSprintDetails(String project, String team, Iteration iteration) {
        StringBuilder sb = new StringBuilder(project);
        sb.append(TAB);
        sb.append(team);
        sb.append(TAB);
        sb.append(iteration.getName());
        sb.append(TAB);
        sb.append(iteration.getStartDate());
        sb.append(TAB);
        sb.append(iteration.getFinishDate());
        sb.append(TAB);
        return sb.toString();
    }

}
