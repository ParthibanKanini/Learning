import { log } from 'utils/logger.js';

/**
 * Global setup function to run before any tests are executed.
 * This function is executed once before all test files are run.
 */

async function globalSetup() {
  log.info('Test execution started');
}
export default globalSetup;
