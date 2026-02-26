package pc.ado.exception;

/**
 * Exception thrown when Azure DevOps API calls fail.
 *
 * <p>Includes HTTP status code and response details for debugging.
 */
public class AdoApiException extends AdoException {

  private final int statusCode;
  private final String responseBody;

  public AdoApiException(String message, int statusCode, String responseBody) {
    super(message, ErrorCode.API_001);
    this.statusCode = statusCode;
    this.responseBody = responseBody;
  }

  public AdoApiException(String message, Throwable cause, ErrorCode errorCode) {
    super(message, cause, errorCode);
    this.statusCode = -1;
    this.responseBody = null;
  }

  public int getStatusCode() {
    return statusCode;
  }

  public String getResponseBody() {
    return responseBody;
  }

  @Override
  public String getFormattedMessage() {
    if (statusCode != -1) {
      return String.format(
          "[%s] HTTP %d: %s - %s",
          getErrorCode().getCode(), statusCode, getMessage(), responseBody);
    }
    return super.getFormattedMessage();
  }
}
