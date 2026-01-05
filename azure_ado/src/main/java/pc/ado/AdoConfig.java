package pc.ado;

import java.io.IOException;
import java.io.InputStream;
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
            logger.info("Configuration loaded successfully");
        } catch (IOException e) {
            logger.error("Error loading configuration", e);
            throw new RuntimeException("Failed to load configuration: " + e.getMessage(), e);
        }
        return props;
    }

    private void validateConfiguration() {
        String[] requiredProperties = {"team", "organization", "project", "patToken", "apiVersion", "baseUri"};
        for (String prop : requiredProperties) {
            if (!config.containsKey(prop) || config.getProperty(prop).isBlank()) {
                String error = "Required configuration property is missing or empty: " + prop;
                logger.error(error);
                throw new RuntimeException(error);
            }
        }
        logger.info("Configuration validation passed");
    }

    public String getTeam() {
        return config.getProperty("team");
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

    public String getCapacitiesApiPath() {
        return config.getProperty("capacitiesApiPath");
    }

    public String getOutputFilePath() {
        return config.getProperty("outputFilePath", "output.txt");
    }
}
