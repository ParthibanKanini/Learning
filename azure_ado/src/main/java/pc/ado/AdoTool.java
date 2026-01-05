package pc.ado;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pc.ado.dto.Iteration;

/**
 * Main entry point for Azure DevOps reporting tool.
 *
 * This class coordinates the retrieval and display of team iterations and
 * capacities. It uses a dependency injection pattern with separate services for
 * configuration, HTTP communication, API interaction, and presentation.
 */
public class AdoTool {

    private static final Logger logger = LoggerFactory.getLogger(AdoTool.class);

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
                // Display team header
                formatter.displayTeamHeader(config.getTeam());
                // Retrieve and display all team iterations with their capacities
                List<Iteration> iterations = apiClient.getTeamIterations();
                formatter.displayTeamIterations(iterations, apiClient);
                logger.info("Report generation completed successfully");
            } finally {
                httpClient.close();
            }
        } catch (Exception e) {
            logger.error("Error occurred during report generation", e);
            System.exit(1);
        }
    }
}
