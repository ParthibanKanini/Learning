package pc.ado.exception;

/**
 * Exception thrown when parsing Azure DevOps API responses fails.
 *
 * <p>Indicates unexpected response format or missing required fields.
 */
public class AdoParsingException extends AdoException {

  private final String jsonContent;

  public AdoParsingException(String message, String jsonContent) {
    super(message, ErrorCode.API_002);
    this.jsonContent = jsonContent;
  }

  public AdoParsingException(String message, Throwable cause, String jsonContent) {
    super(message, cause, ErrorCode.API_002);
    this.jsonContent = jsonContent;
  }

  public String getJsonContent() {
    return jsonContent;
  }

  @Override
  public String getFormattedMessage() {
    return String.format(
        "[%s] %s - JSON: %s",
        getErrorCode().getCode(), getMessage(), jsonContent != null ? jsonContent : "N/A");
  }
}
