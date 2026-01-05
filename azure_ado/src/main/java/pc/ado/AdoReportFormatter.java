package pc.ado;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pc.ado.dto.Iteration;
import pc.ado.dto.TeamMemberCapacity;

/**
 * Handles presentation/formatting of ADO data. Separates display logic from
 * business logic.
 */
public class AdoReportFormatter {

    private static final Logger logger = LoggerFactory.getLogger(AdoReportFormatter.class);
    private static final String TAB = "\t";

    private final String outputFilePath;

    public AdoReportFormatter(AdoConfig config) {
        this.outputFilePath = config.getOutputFilePath();
        logger.info("Report formatter initialized with output file: {}", outputFilePath);
    }

    /**
     * Displays team iterations and their details.
     */
    public void displayTeamIterations(List<Iteration> iterations, AdoApiClient apiClient) {
        for (Iteration iteration : iterations) {
            outputIterationCapacitiesResult(iteration, apiClient);
        }
    }

    /**
     * Returns iteration details as a formatted string.
     */
    private String getIterationDetails(Iteration iteration) {
        StringBuilder sb = new StringBuilder(iteration.getName());
        sb.append(TAB);
        sb.append(iteration.getStartDate());
        sb.append(TAB);
        sb.append(iteration.getFinishDate());
        sb.append(TAB);
        return sb.toString();
    }

    /**
     * Displays team members with capacity for a specific iteration.
     */
    private void outputIterationCapacitiesResult(Iteration iteration, AdoApiClient apiClient) {
        try {
            List<TeamMemberCapacity> capacities = apiClient.getIterationCapacities(iteration.getId());
            String itrDetails = getIterationDetails(iteration);
            logger.debug("Writing {} team members to report file", capacities.size());
            try (PrintWriter writer = new PrintWriter(new FileWriter(outputFilePath, true))) {
                for (TeamMemberCapacity capacity : capacities) {
                    StringBuilder sb = new StringBuilder(itrDetails);
                    sb.append(capacity.getDisplayName());
                    sb.append(TAB);
                    sb.append(capacity.getCapacityPerDay());
                    writer.println(sb.toString());
                }
                writer.flush();
            } catch (IOException e) {
                logger.error("Failed to write to output file: {}", outputFilePath, e);
            }
            System.out.println(System.lineSeparator());
        } catch (Exception e) {
            logger.error("Error occurred while fetching iteration capacities: {}", iteration.getName(), e);
        }
    }

    /**
     * Displays team header.
     */
    public void displayTeamHeader(String teamName) {
        System.out.println("  << " + teamName + "  >> ");
    }
}
