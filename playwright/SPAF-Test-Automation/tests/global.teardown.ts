import { log } from 'utils/logger.js';

/**
 * Global teardown function to run after all tests have completed.
 * This function is executed once after all test files have run.
 */
async function globalTeardown() {
  log.info('Test execution Ended');
}
export default globalTeardown;
