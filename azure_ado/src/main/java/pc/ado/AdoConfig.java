package pc.ado;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Manages ADO configuration loaded from properties file. Singleton pattern
 * ensures single instance of configuration throughout the application.
 */
public class AdoConfig {

    private static final Logger logger = LoggerFactory.getLogger(AdoConfig.class);
    private static final AdoConfig INSTANCE = new AdoConfig();
    private final Properties config;

    private AdoConfig() {
        this.config = loadConfiguration();
        validateConfiguration();
    }

    public static AdoConfig getInstance() {
        return INSTANCE;
    }

    private Properties loadConfiguration() {
        Properties props = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                throw new IOException("config.properties file not found in resources");
            }
            props.load(input);
            logger.debug("Configuration loaded successfully");
        } catch (IOException e) {
            logger.error("Error loading configuration", e);
            throw new RuntimeException("Failed to load configuration: " + e.getMessage(), e);
        }
        return props;
    }

    private void validateConfiguration() {
        String[] requiredProperties = {"teams", "organization", "project", "patToken", "apiVersion", "baseUri"};
        for (String prop : requiredProperties) {
            if (!config.containsKey(prop) || config.getProperty(prop).isBlank()) {
                String error = "Required configuration property is missing or empty: " + prop;
                logger.error(error);
                throw new RuntimeException(error);
            }
        }
        logger.debug("Configuration validation passed");
    }

    public String[] getTeams() {
        String teams = config.getProperty("teams");
        return teams.split(",");
    }

    public String getOrganization() {
        return config.getProperty("organization");
    }

    public String getProject() {
        return config.getProperty("project");
    }

    public String getPatToken() {
        return config.getProperty("patToken");
    }

    public String getApiVersion() {
        return config.getProperty("apiVersion");
    }

    public String getBaseUri() {
        return config.getProperty("baseUri");
    }

    public String getIterationsApiPath() {
        return config.getProperty("iterationsApiPath");
    }

    public String getIterationDayOffPath() {
        return config.getProperty("iterationDayOffPath");
    }

    public String getSprintCapacityDetailsFilePath() {
        return config.getProperty("sprintCapacityDetailsFilePath");
    }

    public String getCapacitiesApiPath() {
        return config.getProperty("capacitiesApiPath");
    }

    public String getWorkItemsApiPath() {
        return config.getProperty("workitemsApiPath");
    }

    public String getWorkItemRelationsApiPath() {
        return config.getProperty("workItemRelationsAPIPath");
    }

    public String getPullRequestApiPath() {
        return config.getProperty("pullRequestApiPath");
    }

    public String getPRThreadApiPath() {
        return config.getProperty("PRThreadApiPath");
    }

    public String getOutputFormatterType() {
        return config.getProperty("outputFormatterType", "json");
    }

    public List<String> getIgnoredWorkItemStates() {
        String states = config.getProperty("ignoredWorkItemStates", "");
        return List.of(states.split(","));
    }

    public List<String> getIncludeOnlyIterationWithNames() {
        String includeOnlyIterationWithNames = config.getProperty("includeOnlyIterationWithNames");
        return List.of(includeOnlyIterationWithNames.split(","));
    }

    public boolean isFetchCapacities() {
        return Boolean.parseBoolean(config.getProperty("fetchCapacities", "false"));
    }

    public boolean isFetchWorkItemDetails() {
        return Boolean.parseBoolean(config.getProperty("fetchWorkItemDetails", "false"));
    }

    public boolean isFetchWorkItemTasks() {
        return Boolean.parseBoolean(config.getProperty("fetchWorkItemDetails.tasks", "false"));
    }

    public boolean isFetchWorkItemPullRequests() {
        return Boolean.parseBoolean(config.getProperty("fetchWorkItemDetails.pullRequests", "false"));
    }

    public boolean isExecutionTrackingEnabled() {
        return Boolean.parseBoolean(config.getProperty("enableExecutionTracking", "false"));
    }

    public boolean isExecutionStatsEnabled() {
        return Boolean.parseBoolean(config.getProperty("enableExecutionStats", "true"));
    }

    public boolean isIgnoreSubmitterPRComments() {
        return Boolean.parseBoolean(config.getProperty("ignoreSubmitterPRComments", "false"));
    }

    public boolean isIgnoreSingleWordPRComment() {
        return Boolean.parseBoolean(config.getProperty("ignoreSingleWordPRComment", "false"));
    }

    public List<String> getIgnoreCommentsWith() {
        String comments = config.getProperty("ignoreCommentsWith", "").trim();
        return List.of(comments.split(","));
    }

    public LocalDate getIgnoreIterationsEndedBefore() {
        String dateStr = config.getProperty("ignoreIterationsEndedBefore", "").trim();
        if (dateStr.isEmpty()) {
            return null;
        }
        try {
            return DateUtils.formatStringToLocalDate(dateStr);
        } catch (Exception e) {
            logger.warn("Failed to parse ignoreIterationsEndedBefore date: {}", dateStr, e);
            return null;
        }
    }

}
