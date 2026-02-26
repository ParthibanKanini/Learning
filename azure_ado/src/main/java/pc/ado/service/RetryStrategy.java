package pc.ado.service;

import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pc.ado.constants.AdoConstants;
import pc.ado.exception.AdoException;
import pc.ado.exception.ErrorCode;

/**
 * Implements retry logic with exponential backoff for resilient API communication.
 *
 * <p>Follows Strategy pattern to encapsulate retry behavior.
 */
public class RetryStrategy {

  private static final Logger logger = LoggerFactory.getLogger(RetryStrategy.class);

  private final int maxRetries;
  private final long initialDelayMs;

  /**
   * Creates a retry strategy with default configuration.
   *
   * <p>Uses: - Max retries: 3 - Initial delay: 1000ms - Exponential backoff
   */
  public RetryStrategy() {
    this(
        AdoConstants.Resilience.DEFAULT_MAX_RETRIES,
        AdoConstants.Resilience.INITIAL_RETRY_DELAY_MS);
  }

  /**
   * Creates a retry strategy with custom configuration.
   *
   * @param maxRetries maximum number of retry attempts
   * @param initialDelayMs initial retry delay in milliseconds
   */
  public RetryStrategy(int maxRetries, long initialDelayMs) {
    this.maxRetries = maxRetries;
    this.initialDelayMs = initialDelayMs;
  }

  /**
   * Executes the given operation with retry logic.
   *
   * @param <T> the return type of the operation
   * @param operation the operation to execute
   * @param operationName name of the operation for logging
   * @return the result of the operation
   * @throws AdoException if all retry attempts fail
   */
  public <T> T execute(Supplier<T> operation, String operationName) throws AdoException {
    int attempt = 0;
    Exception lastException = null;

    while (attempt <= maxRetries) {
      try {
        if (attempt > 0) {
          logger.debug("Retry attempt {}/{} for {}", attempt, maxRetries, operationName);
        }
        return operation.get();
      } catch (Exception e) {
        lastException = e;
        attempt++;

        if (attempt > maxRetries) {
          logger.error(
              "All {} retry attempts failed for {}", maxRetries, operationName, lastException);
          break;
        }

        long delayMs = calculateDelay(attempt);
        logger.warn(
            "Attempt {}/{} failed for {}. Retrying in {} ms",
            attempt,
            maxRetries,
            operationName,
            delayMs,
            e);

        sleep(delayMs);
      }
    }

    throw new AdoException(
        "Failed after " + maxRetries + " retries: " + operationName,
        lastException,
        ErrorCode.NET_003);
  }

  /**
   * Calculates retry delay using exponential backoff.
   *
   * @param attempt the current attempt number (1-based)
   * @return delay in milliseconds
   */
  private long calculateDelay(int attempt) {
    return initialDelayMs * (long) Math.pow(2, attempt - 1);
  }

  /**
   * Sleeps for the specified duration.
   *
   * @param milliseconds duration to sleep
   */
  private void sleep(long milliseconds) {
    try {
      Thread.sleep(milliseconds);
    } catch (InterruptedException e) {
      logger.warn("Retry sleep interrupted", e);
      Thread.currentThread().interrupt();
    }
  }

  public int getMaxRetries() {
    return maxRetries;
  }

  public long getInitialDelayMs() {
    return initialDelayMs;
  }
}
