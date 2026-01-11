package pc.ado;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pc.ado.dto.Iteration;
import pc.ado.dto.TeamMemberAllocation;
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

    //private static final String TAB = "\t";
    public static void main(String[] args) {
        AdoTool tool = new AdoTool();
        tool.run();
    }

    /**
     * Runs the Azure DevOps reporting workflow.
     */
    public void run() {
        AdoConfig config = AdoConfig.getInstance();
        AdoHttpClient httpClient = new AdoHttpClient(config.getPatToken());
        try {
            AdoApiClient apiClient = new AdoApiClient(config, httpClient);
            List<Iteration> projectTeamItr = getTeamSprints(config.getProject(), config.getTeam(), apiClient);
            populateTeamCapacitiesForIterations(config.getProject(), config.getTeam(), apiClient, projectTeamItr);

            //TODO: For testing, extract details for a specific sprint. make it null later.
            String sprintName = null;
            populateIterationWorkItemDetails(config, apiClient, projectTeamItr, sprintName);

            // Write details to file as JSON
            logger.info("Generating report for project '{}' and team '{} - {}'", config.getProject(), config.getTeam(), projectTeamItr);
            AdoReportFormatter formatter = new AdoReportFormatter(config);
            formatter.writeSprintCapacitiesToJsonFile(projectTeamItr);
            logger.info("Report generation completed successfully.");

        } catch (Exception e) {
            logger.error("Error occurred during report generation", e);
            System.exit(1);
        } finally {
            httpClient.close();
        }
    }

    private void populateIterationWorkItemDetails(AdoConfig config, AdoApiClient apiClient, List<Iteration> iterations, String sprintName) throws Exception {
        final String project = config.getProject();
        final String team = config.getTeam();
        //List<Iteration> iterations = apiClient.getTeamSprint(project, team);
        for (Iteration iteration : iterations) {
            // If no print name provided or a specific sprint name is given, process it
            if (sprintName == null || iteration.getName().equals(sprintName)) {
                logger.debug("Fetching story report for sprint: {} -> {} -> {}", project, team, sprintName);
                // Retrieve and process work items for the specified sprint
                //final String itrId = iteration.getId();
                apiClient.getSprintWorkItems(project, team, iteration);
            }
        }
        /* TODO: Fetch the below details for each work item
                Impl details present(Y/N) - fields.(Custom.ImplementationDetails)
                Dependancy
                    successorOf []
                    predecessorOf []
                tasks []
                    ID
                    remaining hrs
                    actual hrs
                    assigned to
                    Dependancy
                        successorOf []
                        predecessorOf []               */
        logger.info("Story report generation completed successfully.");
    }

    /**
     * Retrieves all team sprints for a given project and team.
     */
    private List<Iteration> getTeamSprints(String project, String team, AdoApiClient apiClient) throws Exception {
        logger.debug("Retrieving team sprints for project '{}' and team '{}'", project, team);
        List<Iteration> iterations = apiClient.getTeamSprint(project, team);
        logger.debug("Retrieved {} sprints", iterations.size());
        return iterations;
    }

    /**
     * Processes team sprints to retrieve their capacity details.
     */
    private void populateTeamCapacitiesForIterations(String project, String team, AdoApiClient apiClient, List<Iteration> iterations) {
        for (Iteration iteration : iterations) {
            populateIterationTeamCapacity(project, team, apiClient, iteration);
        }
    }

    /**
     * Retrieves formatted team members with capacity for a specific iteration.
     */
    private void populateIterationTeamCapacity(String project, String team, AdoApiClient apiClient, Iteration iteration) {
        try {
            logger.debug("Fetching capacities for iteration '{}'", iteration.getName());
            List<TeamMemberCapacity> capacities = apiClient.getIterationCapacities(project, team, iteration.getId());
            logger.trace("Found {} team members for iteration", capacities.size());
            //logger.trace("---" + iteration.getName() + " - " + iteration.getStartDate() + " - " + iteration.getFinishDate() + "---");
            for (TeamMemberCapacity capacity : capacities) {
                int workedDays = DateUtils.calculateWeekDays(DateUtils.formatStringToLocalDate(iteration.getStartDate()), DateUtils.formatStringToLocalDate(iteration.getFinishDate()), capacity.getDaysOff());
                double workedHours = workedDays * capacity.getCapacityPerDay();
                // Populate TeamMemberAllocation and add to iteration
                TeamMemberAllocation allocation = new TeamMemberAllocation(
                        capacity.getDisplayName(),
                        capacity.getCapacityPerDay(),
                        capacity.getDaysOff(),
                        workedDays,
                        workedHours
                );
                iteration.addAllocation(allocation);
            }
        } catch (Exception e) {
            logger.error("Error occurred while fetching sprint capacities: {}", iteration.getName(), e);
        }
    }

}
