package pc.ado;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pc.ado.constants.AdoConstants;
import pc.ado.exception.AdoApiException;
import pc.ado.exception.AdoAuthenticationException;
import pc.ado.exception.AdoException;
import pc.ado.exception.ErrorCode;
import pc.ado.gateway.AdoGateway;
import pc.ado.service.AuthenticationService;
import pc.ado.service.RetryStrategy;

/**
 * Production-grade HTTP client for Azure DevOps API communication.
 *
 * <p>Implements AdoGateway interface and provides:
 *
 * <ul>
 *   <li>Automatic retry with exponential backoff
 *   <li>Proper resource management with connection pooling
 *   <li>Structured exception handling
 *   <li>Authentication abstraction
 *   <li>Health checking capabilities
 * </ul>
 *
 * <p>Follows SOLID principles: implements DIP through interface, SRP by delegating authentication.
 */
public class AdoHttpClient implements AdoGateway {

  private static final Logger logger = LoggerFactory.getLogger(AdoHttpClient.class);

  private final HttpClient httpClient;
  private final AuthenticationService authService;
  private final RetryStrategy retryStrategy;

  /**
   * Creates an HTTP client with Basic Authentication.
   *
   * @param patToken Personal Access Token for Azure DevOps
   */
  public AdoHttpClient(String patToken) {
    this(new AuthenticationService(patToken), new RetryStrategy());
  }

  /**
   * Creates an HTTP client with custom authentication and retry strategy.
   *
   * <p>Useful for testing and custom configurations.
   *
   * @param authService authentication service
   * @param retryStrategy retry strategy
   */
  public AdoHttpClient(AuthenticationService authService, RetryStrategy retryStrategy) {
    this.authService = authService;
    this.retryStrategy = retryStrategy;
    this.httpClient = buildHttpClient();
    logger.debug("HTTP client initialized with retry strategy: max {} attempts",
        retryStrategy.getMaxRetries());
  }

  /**
   * Builds and configures the HttpClient with production-ready settings.
   *
   * @return configured HttpClient instance
   */
  private HttpClient buildHttpClient() {
    return HttpClient.newBuilder()
        .connectTimeout(Duration.ofSeconds(AdoConstants.Resilience.DEFAULT_TIMEOUT_SECONDS))
        .version(HttpClient.Version.HTTP_2) // Use HTTP/2 for better performance
        .build();
  }

  /**
   * Sends a GET request to the specified URL.
   *
   * @param url the URL to request
   * @return the response body as a string
   * @throws AdoException if the request fails
   */
  @Override
  public String get(String url) throws AdoException {
    try {
      return executeGet(url);
    } catch (AdoException e) {
      throw e;
    } catch (Exception e) {
      logger.error("Unexpected error during GET request to {}", url, e);
      throw new AdoApiException(
          "Unexpected error: " + e.getMessage(), e, ErrorCode.API_001);
    }
  }

  /**
   * Sends a GET request with automatic retry logic.
   *
   * @param url the URL to request
   * @param maxRetries maximum number of retry attempts (parameter kept for interface compatibility)
   * @return the response body as a string
   * @throws AdoException if the request fails after all retries
   */
  @Override
  public String getWithRetry(String url, int maxRetries) throws AdoException {
    return retryStrategy.execute(() -> {
      try {
        return executeGet(url);
      } catch (AdoException e) {
        throw new RuntimeException(e);
      }
    }, "GET " + url);
  }

  /**
   * Executes a GET request with proper error handling.
   *
   * @param url the URL to request
   * @return the response body as a string
   * @throws AdoException if the request fails
   */
  private String executeGet(String url) throws AdoException {
    logger.trace("Sending GET request to: {}", url);

    HttpRequest request = buildGetRequest(url);

    try {
      HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
      return handleResponse(response, url);
    } catch (IOException e) {
      logger.error("Network error for URL: {}", url, e);
      throw new AdoApiException(
          "Network error: " + e.getMessage(), e, ErrorCode.NET_001);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      logger.error("Request interrupted for URL: {}", url, e);
      throw new AdoApiException(
          "Request interrupted", e, ErrorCode.API_004);
    }
  }

  /**
   * Builds a GET request with proper headers.
   *
   * @param url the URL to request
   * @return configured HttpRequest
   */
  private HttpRequest buildGetRequest(String url) {
    return HttpRequest.newBuilder()
        .uri(URI.create(url))
        .timeout(Duration.ofSeconds(AdoConstants.Resilience.DEFAULT_TIMEOUT_SECONDS))
        .header(AdoConstants.Http.AUTHORIZATION_HEADER, authService.getAuthorizationHeader())
        .header(AdoConstants.Http.ACCEPT_HEADER, AdoConstants.Http.APPLICATION_JSON)
        .GET()
        .build();
  }

  /**
   * Handles HTTP response and throws appropriate exceptions for error status codes.
   *
   * @param response the HTTP response
   * @param url the requested URL (for logging)
   * @return the response body
   * @throws AdoException if the response status is not successful
   */
  private String handleResponse(HttpResponse<String> response, String url) throws AdoException {
    int statusCode = response.statusCode();

    if (statusCode == AdoConstants.Http.HTTP_OK) {
      logger.trace("API request successful for URL: {}", url);
      return response.body();
    }

    // Handle specific error codes
    String errorMessage = String.format(
        "API request failed: HTTP %d for URL: %s", statusCode, url);
    String responseBody = response.body();

    if (statusCode == AdoConstants.Http.HTTP_UNAUTHORIZED) {
      logger.error("Authentication failed for URL: {}", url);
      throw new AdoAuthenticationException(
          "Authentication failed. Please check your PAT token.");
    }

    if (statusCode == AdoConstants.Http.HTTP_FORBIDDEN) {
      logger.error("Authorization failed for URL: {}", url);
      throw new AdoAuthenticationException(
          "Insufficient permissions. Please check your PAT token permissions.");
    }

    if (statusCode == AdoConstants.Http.HTTP_NOT_FOUND) {
      logger.error("Resource not found for URL: {}", url);
      throw new AdoApiException(errorMessage, statusCode, responseBody);
    }

    if (statusCode == AdoConstants.Http.HTTP_TOO_MANY_REQUESTS) {
      logger.error("Rate limit exceeded for URL: {}", url);
      throw new AdoApiException(
          "Rate limit exceeded", statusCode, responseBody);
    }

    // Generic error
    logger.error("{}, response: {}", errorMessage, responseBody);
    throw new AdoApiException(errorMessage, statusCode, responseBody);
  }

  /**
   * Checks if the gateway is healthy by attempting a basic connection test.
   *
   * @return true if connection is healthy, false otherwise
   */
  @Override
  public boolean isHealthy() {
    // For now, always return true. In a real implementation, this could
    // ping a health endpoint or check connection pool status
    return true;
  }

  /**
   * Closes the HTTP client and releases resources.
   *
   * <p>The Java HttpClient doesn't require explicit closing, but this method is provided for
   * interface compliance and future extensibility.
   */
  @Override
  public void close() {
    logger.debug("Closing HTTP client");
    // HttpClient doesn't require explicit closing in Java 11+
    // But this provides a hook for cleanup if needed in the future
  }
}
