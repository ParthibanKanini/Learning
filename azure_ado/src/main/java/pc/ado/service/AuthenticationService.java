package pc.ado.service;

import java.util.Base64;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pc.ado.constants.AdoConstants;

/**
 * Handles authentication concerns for Azure DevOps API.
 *
 * <p>Encapsulates credential encoding and authorization header generation, following Single
 * Responsibility Principle.
 */
public class AuthenticationService {

  private static final Logger logger = LoggerFactory.getLogger(AuthenticationService.class);

  private final String encodedCredentials;

  /**
   * Creates an authentication service with the provided PAT token.
   *
   * @param patToken Personal Access Token for Azure DevOps
   * @throws IllegalArgumentException if PAT token is null or empty
   */
  public AuthenticationService(String patToken) {
    validatePatToken(patToken);
    this.encodedCredentials = encodeBasicAuth(patToken);
    logger.debug("Authentication service initialized");
  }

  /**
   * Validates that the PAT token is not null or empty.
   *
   * @param patToken the PAT token to validate
   * @throws IllegalArgumentException if validation fails
   */
  private void validatePatToken(String patToken) {
    if (patToken == null || patToken.isBlank()) {
      logger.error("PAT token cannot be null or empty");
      throw new IllegalArgumentException("PAT token cannot be null or empty");
    }
  }

  /**
   * Encodes credentials for HTTP Basic Authentication.
   *
   * <p>Azure DevOps uses Basic Auth with PAT token as the password and empty username.
   *
   * @param patToken the personal access token
   * @return Base64 encoded credentials
   */
  private String encodeBasicAuth(String patToken) {
    String credentials = ":" + patToken; // Empty username, PAT as password
    return Base64.getEncoder().encodeToString(credentials.getBytes());
  }

  /**
   * Gets the complete Authorization header value.
   *
   * @return Authorization header value with "Basic " prefix
   */
  public String getAuthorizationHeader() {
    return AdoConstants.Http.BASIC_AUTH_PREFIX + encodedCredentials;
  }

  /**
   * Gets the encoded credentials without the "Basic " prefix.
   *
   * @return Base64 encoded credentials
   */
  public String getEncodedCredentials() {
    return encodedCredentials;
  }
}
