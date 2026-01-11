package pc.ado;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Base64;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handles HTTP communication with Azure DevOps API. Encapsulates authentication
 * and request/response handling.
 */
public class AdoHttpClient {

    private static final Logger logger = LoggerFactory.getLogger(AdoHttpClient.class);
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BASIC_AUTH_PREFIX = "Basic ";
    private static final String ACCEPT_HEADER = "Accept";
    private static final String APPLICATION_JSON = "application/json";
    private static final int HTTP_OK = 200;

    private final HttpClient httpClient;
    private final String encodedCredentials;

    public AdoHttpClient(String patToken) {
        this.httpClient = HttpClient.newHttpClient();
        this.encodedCredentials = encodeBasicAuth(patToken);
    }

    /**
     * Encodes credentials for HTTP Basic Authentication.
     */
    private String encodeBasicAuth(String patToken) {
        String credentials = ":" + patToken;
        return Base64.getEncoder().encodeToString(credentials.getBytes());
    }

    /**
     * Sends a GET request to the specified URL and returns the response body.
     *
     * @param url the URL to request
     * @return the response body as a string
     * @throws Exception if the request fails
     */
    public String get(String url) throws Exception {
        logger.trace("Sending GET request to: {}", url);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header(AUTHORIZATION_HEADER, BASIC_AUTH_PREFIX + encodedCredentials)
                .header(ACCEPT_HEADER, APPLICATION_JSON)
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != HTTP_OK) {
            String errorMsg = "API request failed with status code: " + response.statusCode();
            logger.error(errorMsg);
            throw new Exception(errorMsg);
        }

        logger.trace("API request successful");
        return response.body();
    }

    /**
     * Closes the HTTP client and releases resources.
     */
    public void close() {
        logger.debug("Closing HTTP client");
        // HttpClient doesn't require explicit closure but this method provides cleanup capability
    }
}
