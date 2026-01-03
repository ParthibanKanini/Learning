package pc.ado;

import java.util.List;

/**
 * Main entry point for Azure DevOps reporting tool.
 *
 * This class coordinates the retrieval and display of team iterations and
 * capacities. It uses a dependency injection pattern with separate services for
 * configuration, HTTP communication, API interaction, and presentation.
 */
public class AdoTool {

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
            AdoApiClient apiClient = new AdoApiClient(httpClient, config);
            AdoReportFormatter formatter = new AdoReportFormatter();

            // Display team header
            formatter.displayTeamHeader(config.getTeam());

            // Retrieve and display all team iterations with their capacities
            List<Iteration> iterations = apiClient.getTeamIterations();
            formatter.displayTeamIterations(iterations, apiClient);

        } catch (Exception e) {
            System.err.println("Error occurred: " + e.getMessage());
            e.printStackTrace();
        } finally {
            httpClient.close();
        }
    }
}
