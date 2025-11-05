/**
 * Centralized timeout configurations for the DNAV Playwright framework
 * All timeout values are in milliseconds
 */
export const TIMEOUTS = {
  /** Quick interactions */
  SHORT: 5_000,

  /** Standard page loads */
  MEDIUM: 15_000,

  /** Complex operations */
  LONG: 30_000,

  /** API calls */
  API_RESPONSE: 90_000,

  /** Long-running searches */
  SEARCH_RESULTS: 180_000,

  /** Timeout for actions like click, fill, etc. */
  DEFAULT_ACTION_TIMEOUT: 15_000,

  /** Timeout for assertions */
  DEFAULT_ASSERTION_TIMEOUT: 60_000,

  /** Timeout for page navigations */
  DEFAULT_NAVIGATION_TIMEOUT: 60_000,

  /** Max Test can run before */
  TEST_EXECUTION_TIMEOUT: 600_000,
} as const;

/** Type for timeout values */
export type TimeoutValues = (typeof TIMEOUTS)[keyof typeof TIMEOUTS];
