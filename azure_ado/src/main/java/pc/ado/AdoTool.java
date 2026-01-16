package pc.ado;

import java.util.ArrayList;
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
        long startTime = System.currentTimeMillis();
        logger.info("<<<--- Start Execution --->>>");
        AdoTool tool = new AdoTool();
        tool.run();
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        logger.info("<<<--- Execution Completed in {} ms ({} seconds) --->>>", duration, duration / 1000.0);
    }

    /**
     * Runs the Azure DevOps reporting workflow.
     */
    public void run() {
        long runStartTime = System.currentTimeMillis();
        AdoConfig config = AdoConfig.getInstance();
        AdoHttpClient httpClient = new AdoHttpClient(config.getPatToken());
        try {
            AdoApiClient apiClient = new AdoApiClient(config, httpClient);
            List<Iteration> projectTeamIterations = new ArrayList<>();
            String project = config.getProject();
            int totalTeamsProcessed = 0;
            int totalIterationsCollected = 0;

            for (String team : config.getTeams()) {
                long teamStartTime = System.currentTimeMillis();
                logger.info("Processing team: {}", team);

                // Read iteration filter from config, or null to fetch all sprints
                List<String> includeOnlyIterationWithNames = config.getIncludeOnlyIterationWithNames();
                List<Iteration> projectTeamItr = getTeamSprints(project, team, apiClient, includeOnlyIterationWithNames);

                if (config.isFetchCapacities()) {
                    populateTeamCapacitiesForIterations(project, team, apiClient, projectTeamItr);
                }
                if (config.isFetchWorkItemDetails()) {
                    populateIterationWorkItemDetails(project, team, apiClient, projectTeamItr);
                }

                // Write details to file using configured formatter
                projectTeamIterations.addAll(projectTeamItr);
                totalTeamsProcessed++;
                totalIterationsCollected += projectTeamItr.size();

                long teamDuration = System.currentTimeMillis() - teamStartTime;
                logger.info("Collected data for project '{}' team '{}' with {} Iterations in {} ms",
                        project, team, projectTeamItr.size(), teamDuration);
            }

            AdoReportFormatter formatter = new AdoReportFormatter(config);
            formatter.writeSprintCapacitiesToFormattedFile(projectTeamIterations);

            long runEndTime = System.currentTimeMillis();
            long totalDuration = runEndTime - runStartTime;
            logger.info("Report generation completed successfully.");
            logger.info("=== Execution Statistics ===");
            logger.info("Total Teams Processed: {}", totalTeamsProcessed);
            logger.info("Total Iterations Collected: {}", totalIterationsCollected);
            logger.info("Total Execution Time: {} ms ({} seconds)", totalDuration, totalDuration / 1000.0);
            logger.info("============================");
        } catch (Exception e) {
            logger.error("Error occurred during report generation", e);
            System.exit(1);
        } finally {
            httpClient.close();
        }
    }

    private void populateIterationWorkItemDetails(String project, String team, AdoApiClient apiClient, List<Iteration> iterations) throws Exception {
        int totalIterations = iterations.size();
        int currentIteration = 0;
        for (Iteration iteration : iterations) {
            currentIteration++;
            logger.debug("Fetching Workitems for '{}' : '{}' : '{}' ({} of {})", project, team, iteration.getName(), currentIteration, totalIterations);
            // Retrieve and process work items for the specified sprint
            apiClient.getSprintWorkItems(project, team, iteration);
        }
        logger.info("Fetching iteration work items completed successfully for project '{}' team '{}' for {} iterations", project, team, totalIterations);
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

    }

    /**
     * Retrieves all team sprints for a given project and team.
     */
    private List<Iteration> getTeamSprints(String project, String team, AdoApiClient apiClient, List<String> sprintNames) throws Exception {
        logger.debug("Retrieving team sprints for project '{}' and team '{}'", project, team);
        List<Iteration> iterations = apiClient.getTeamSprint(project, team, sprintNames);
        logger.debug("Retrieved {} sprints", iterations.size());
        return iterations;
    }

    /**
     * Processes team sprints to retrieve their capacity details.
     */
    private void populateTeamCapacitiesForIterations(String project, String team, AdoApiClient apiClient, List<Iteration> iterations) {
        int totalIterations = iterations.size();
        int currentIteration = 0;
        for (Iteration iteration : iterations) {
            currentIteration++;
            logger.debug("Fetching Capacities for '{}' : '{}' : '{}' ({} of {})", project, team, iteration.getName(), currentIteration, totalIterations);
            populateIterationTeamCapacity(project, team, apiClient, iteration);
        }
        logger.info("Fetching capacities completed successfully for project '{}' team '{}' for {} iterations", project, team, totalIterations);
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
