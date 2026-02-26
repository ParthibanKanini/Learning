package pc.ado.gateway;

import pc.ado.exception.AdoException;

/**
 * Gateway interface for Azure DevOps API communication.
 *
 * <p>Defines the contract for HTTP operations against Azure DevOps. This abstraction allows for:
 *
 * <ul>
 *   <li>Easy testing with mock implementations
 *   <li>Swapping HTTP client implementations without affecting business logic
 *   <li>Clear separation between API gateway and business logic layers
 * </ul>
 *
 * <p>Follows Dependency Inversion Principle - high-level modules depend on this abstraction, not
 * on concrete implementations.
 */
public interface AdoGateway {

  /**
   * Sends a GET request to the specified URL.
   *
   * @param url the full URL to request
   * @return the response body as a string
   * @throws AdoException if the request fails or returns non-success status
   */
  String get(String url) throws AdoException;

  /**
   * Sends a GET request with auto-retry logic.
   *
   * @param url the full URL to request
   * @param maxRetries maximum number of retry attempts
   * @return the response body as a string
   * @throws AdoException if the request fails after all retries
   */
  String getWithRetry(String url, int maxRetries) throws AdoException;

  /**
   * Checks if the gateway is healthy and can communicate with Azure DevOps.
   *
   * @return true if connection is healthy
   */
  boolean isHealthy();

  /**
   * Closes any underlying resources.
   *
   * <p>Should be called when the gateway is no longer needed.
   */
  void close();
}
