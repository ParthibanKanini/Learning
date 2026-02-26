package pc.ado.exception;

/**
 * Exception thrown when authentication with Azure DevOps fails.
 *
 * <p>Typically indicates invalid or expired PAT token.
 */
public class AdoAuthenticationException extends AdoException {

  public AdoAuthenticationException(String message) {
    super(message, ErrorCode.AUTH_001);
  }

  public AdoAuthenticationException(String message, Throwable cause) {
    super(message, cause, ErrorCode.AUTH_001);
  }
}
