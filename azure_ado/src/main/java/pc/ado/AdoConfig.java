package pc.ado;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Manages ADO configuration loaded from properties file. Singleton pattern
 * ensures single instance of configuration throughout the application.
 */
public class AdoConfig {

    private static final AdoConfig INSTANCE = new AdoConfig();
    private final Properties config;

    private AdoConfig() {
        this.config = loadConfiguration();
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
        } catch (IOException e) {
            System.err.println("Error loading configuration: " + e.getMessage());
            e.printStackTrace();
        }
        return props;
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
}
