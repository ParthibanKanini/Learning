package pc.ado;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pc.ado.dto.Iteration;
import pc.ado.formatter.IterationFormatter;
import pc.ado.formatter.IterationFormatterFactory;

/**
 * Handles presentation/formatting of ADO data. Separates display logic from
 * business logic.
 */
public class AdoReportFormatter {

    private static final Logger logger = LoggerFactory.getLogger(AdoReportFormatter.class);

    private final String outputFilePath;
    private final IterationFormatter iterationFormatter;

    public AdoReportFormatter(AdoConfig config) {
        this.outputFilePath = config.getSprintCapacityDetailsFilePath();
        String formatterType = config.getOutputFormatterType();
        this.iterationFormatter = IterationFormatterFactory.createFormatter(formatterType);
        logger.info("Report formatter initialized with output file: {} and formatter type: {}",
                outputFilePath, formatterType);
    }

    /**
     * Writes iteration capacity details to file.
     */
    public void writeSprintCapacitiesToFile(List<String> details) {
        logger.info("Writing {} lines to report file", details.size());
        try (PrintWriter writer = new PrintWriter(new FileWriter(outputFilePath, true))) {
            for (String line : details) {
                writer.println(line);
            }
            writer.flush();
        } catch (IOException e) {
            logger.error("Failed to write to output file: {}", outputFilePath, e);
        }
    }

    /**
     * Displays team header.
     */
    public void logTeamName(String project, String teamName) {
        logger.debug("Fetching Iteration details for '{} - {}'", project, teamName);
    }

    /**
     * Converts a list of iterations to formatted output and writes to file.
     */
    public void writeSprintCapacitiesToFormattedFile(List<Iteration> iterations) {
        try {
            String formattedContent = iterationFormatter.format(iterations);
            writeFormattedContentToFile(formattedContent);
        } catch (Exception e) {
            logger.error("Error occurred while converting iterations to formatted output", e);
        }
    }

    /**
     * Writes formatted content to file.
     */
    private void writeFormattedContentToFile(String content) {
        try {
            logger.info("Writing formatted data to report file");
            try (FileWriter writer = new FileWriter(outputFilePath, false)) {
                writer.write(content);
                writer.flush();
                logger.info("Formatted data successfully written to {}", outputFilePath);
            }
        } catch (IOException e) {
            logger.error("Failed to write formatted content to output file: {}", outputFilePath, e);
        }
    }

    /**
     * Writes iteration capacity details to file.
     */
}
