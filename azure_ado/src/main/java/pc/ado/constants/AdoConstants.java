package pc.ado.constants;

/**
 * Central repository for all Azure DevOps API constants.
 *
 * <p>Eliminates magic strings and numbers throughout the codebase, improving maintainability and
 * reducing errors.
 */
public final class AdoConstants {

  private AdoConstants() {
    throw new UnsupportedOperationException("Utility class - cannot be instantiated");
  }

  /** HTTP related constants. */
  public static final class Http {
    private Http() {}

    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String ACCEPT_HEADER = "Accept";
    public static final String CONTENT_TYPE_HEADER = "Content-Type";
    public static final String APPLICATION_JSON = "application/json";
    public static final String BASIC_AUTH_PREFIX = "Basic ";
    public static final int HTTP_OK = 200;
    public static final int HTTP_UNAUTHORIZED = 401;
    public static final int HTTP_FORBIDDEN = 403;
    public static final int HTTP_NOT_FOUND = 404;
    public static final int HTTP_TOO_MANY_REQUESTS = 429;
    public static final int HTTP_SERVER_ERROR = 500;
  }

  /** Retry and timeout configuration. */
  public static final class Resilience {
    private Resilience() {}

    public static final int DEFAULT_TIMEOUT_SECONDS = 60;
    public static final int DEFAULT_MAX_RETRIES = 3;
    public static final long INITIAL_RETRY_DELAY_MS = 1000;
    public static final int CONNECT_TIMEOUT_MILLIS = 10000;
  }

  /** Connection pool configuration. */
  public static final class ConnectionPool {
    private ConnectionPool() {}

    public static final int MAX_CONNECTIONS = 100;
    public static final int MAX_CONNECTIONS_PER_ROUTE = 50;
    public static final long IDLE_CONNECTION_TIMEOUT_SECONDS = 30;
    public static final long CONNECTION_KEEP_ALIVE_SECONDS = 60;
  }

  /** JSON field names. */
  public static final class JsonFields {
    private JsonFields() {}

    public static final String VALUE = "value";
    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String ATTRIBUTES = "attributes";
    public static final String START_DATE = "startDate";
    public static final String FINISH_DATE = "finishDate";
    public static final String TEAM_MEMBER = "teamMember";
    public static final String DISPLAY_NAME = "displayName";
    public static final String ACTIVITIES = "activities";
    public static final String CAPACITY_PER_DAY = "capacityPerDay";
    public static final String DAYS_OFF = "daysOff";
    public static final String START = "start";
    public static final String END = "end";
    public static final String TEAM_MEMBERS = "teamMembers";
    public static final String WORK_ITEM_RELATIONS = "workItemRelations";
    public static final String TARGET = "target";
    public static final String URL = "url";
    public static final String REL = "rel";
    public static final String FIELDS = "fields";
  }

  /** Default values and fallbacks. */
  public static final class Defaults {
    private Defaults() {}

    public static final String NOT_AVAILABLE = "N/A";
    public static final String EMPTY_STRING = "";
    public static final String PLUS_SIGN_REGEX = "\\+";
    public static final String SPACE_ENCODED = "%20";
  }

  /** System field names in Azure DevOps. */
  public static final class SystemFields {
    private SystemFields() {}

    public static final String TITLE = "System.Title";
    public static final String WORK_ITEM_TYPE = "System.WorkItemType";
    public static final String STATE = "System.State";
    public static final String ASSIGNED_TO = "System.AssignedTo";
    public static final String CREATED_DATE = "System.CreatedDate";
    public static final String CHANGED_DATE = "System.ChangedDate";
  }

  /** Work item types. */
  public static final class WorkItemTypes {
    private WorkItemTypes() {}

    public static final String BUG = "Bug";
    public static final String TASK = "Task";
    public static final String USER_STORY = "User Story";
    public static final String FEATURE = "Feature";
    public static final String EPIC = "Epic";
  }
}
