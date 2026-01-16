package pc.ado;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
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
    private static final int TIMEOUT_SECONDS = 60;
    private static final int MAX_RETRIES = 3;
    private static final long INITIAL_RETRY_DELAY_MS = 1000;

    private final HttpClient httpClient;
    private final String encodedCredentials;

    public AdoHttpClient(String patToken) {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(TIMEOUT_SECONDS))
                .build();
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
     * Includes retry logic with exponential backoff for transient failures.
     *
     * @param url the URL to request
     * @return the response body as a string
     * @throws Exception if the request fails after all retries
     */
    public String get(String url) throws Exception {
        return getWithRetry(url, 0);
    }

    /**
     * Internal method to handle GET requests with retry logic.
     *
     * @param url the URL to request
     * @param retryCount the current retry attempt number
     * @return the response body as a string
     * @throws Exception if the request fails after all retries
     */
    private String getWithRetry(String url, int retryCount) throws Exception {
        logger.trace("Sending GET request to: {} (attempt {}/{})", url, retryCount + 1, MAX_RETRIES + 1);
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofSeconds(TIMEOUT_SECONDS))
                    .header(AUTHORIZATION_HEADER, BASIC_AUTH_PREFIX + encodedCredentials)
                    .header(ACCEPT_HEADER, APPLICATION_JSON)
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != HTTP_OK) {
                String errorMsg = "API request failed with status code: " + response.statusCode();
                logger.error(errorMsg + " for URL: {}", url);
                throw new Exception(errorMsg);
            }

            logger.trace("API request successful");
            return response.body();
        } catch (java.io.IOException e) {
            // Handle transient network errors (including HttpTimeoutException) with retry logic
            if (retryCount < MAX_RETRIES) {
                long delayMs = INITIAL_RETRY_DELAY_MS * (long) Math.pow(2, retryCount);
                logger.warn("Transient network error occurred ({}), retrying after {} ms: {}",
                        e.getClass().getSimpleName(), delayMs, e.getMessage());
                try {
                    Thread.sleep(delayMs);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new Exception("Retry interrupted", ie);
                }
                return getWithRetry(url, retryCount + 1);
            } else {
                logger.error("API request failed after {} retries. URL: {}", MAX_RETRIES, url, e);
                throw new Exception("API request failed after " + (MAX_RETRIES + 1) + " attempts: " + e.getMessage(), e);
            }
        }
    }

    /**
     * Closes the HTTP client and releases resources.
     */
    public void close() {
        logger.debug("Closing HTTP client");
        // HttpClient doesn't require explicit closure but this method provides cleanup capability
    }
}
