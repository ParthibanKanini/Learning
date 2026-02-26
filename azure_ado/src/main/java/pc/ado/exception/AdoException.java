package pc.ado.exception;

/**
 * Base exception for all Azure DevOps operations.
 *
 * <p>Provides a hierarchy of exceptions for better error handling and diagnosis.
 */
public class AdoException extends Exception {

  private final ErrorCode errorCode;

  public AdoException(String message, ErrorCode errorCode) {
    super(message);
    this.errorCode = errorCode;
  }

  public AdoException(String message, Throwable cause, ErrorCode errorCode) {
    super(message, cause);
    this.errorCode = errorCode;
  }

  public ErrorCode getErrorCode() {
    return errorCode;
  }

  public String getFormattedMessage() {
    return String.format("[%s] %s", errorCode.getCode(), getMessage());
  }
}
