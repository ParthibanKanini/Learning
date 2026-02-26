package pc.ado.exception;

/**
 * Enumeration of error codes for Azure DevOps operations.
 *
 * <p>Provides categorized error codes for better error tracking and handling.
 */
public enum ErrorCode {
  // Authentication & Authorization
  AUTH_001("AUTH_001", "Authentication failed - invalid PAT token"),
  AUTH_002("AUTH_002", "Authorization failed - insufficient permissions"),

  // API Communication
  API_001("API_001", "API request failed"),
  API_002("API_002", "API response parsing failed"),
  API_003("API_003", "Invalid API response format"),
  API_004("API_004", "API timeout"),

  // Network & Connectivity
  NET_001("NET_001", "Network connection failed"),
  NET_002("NET_002", "Network timeout"),
  NET_003("NET_003", "Too many retries"),

  // Resource Not Found
  RES_001("RES_001", "Resource not found"),
  RES_002("RES_002", "Project not found"),
  RES_003("RES_003", "Team not found"),
  RES_004("RES_004", "Iteration not found"),

  // Validation
  VAL_001("VAL_001", "Invalid configuration"),
  VAL_002("VAL_002", "Invalid request parameter"),
  VAL_003("VAL_003", "Missing required field"),

  // Rate Limiting
  RATE_001("RATE_001", "Rate limit exceeded"),

  // General
  GEN_001("GEN_001", "Unknown error");

  private final String code;
  private final String description;

  ErrorCode(String code, String description) {
    this.code = code;
    this.description = description;
  }

  public String getCode() {
    return code;
  }

  public String getDescription() {
    return description;
  }

  @Override
  public String toString() {
    return code + ": " + description;
  }
}
