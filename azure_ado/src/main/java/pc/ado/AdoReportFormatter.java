package pc.ado;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handles presentation/formatting of ADO data. Separates display logic from
 * business logic.
 */
public class AdoReportFormatter {

    private static final Logger logger = LoggerFactory.getLogger(AdoReportFormatter.class);

    private final String outputFilePath;

    public AdoReportFormatter(AdoConfig config) {
        this.outputFilePath = config.getOutputFilePath();
        logger.info("Report formatter initialized with output file: {}", outputFilePath);
    }

    /**
     * Writes iteration capacity details to file.
     */
    public void writeSprintCapacitiesToFile(List<String> details) {
        try {
            logger.info("Writing {} lines to report file", details.size());
            try (PrintWriter writer = new PrintWriter(new FileWriter(outputFilePath, true))) {
                for (String line : details) {
                    writer.println(line);
                }
                writer.flush();
            } catch (IOException e) {
                logger.error("Failed to write to output file: {}", outputFilePath, e);
            }
        } catch (Exception e) {
            logger.error("Error occurred while writing iteration capacities to file", e);
        }
    }

    /**
     * Displays team header.
     */
    public void logTeamName(String project, String teamName) {
        logger.debug("Fetching Iteration details for '{} - {}'", project, teamName);
    }

}
