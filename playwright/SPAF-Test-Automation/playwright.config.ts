/*
 * Framework Testing Configuration
 * This configuration file sets up SPAF for end-to-end testing.
 * It includes settings for retries, timeouts, reporters, and browser configurations.
 */

import { defineConfig, devices } from '@playwright/test';
import { appEnv } from './config/env.js';
import { TIMEOUTS } from 'utils/timeouts.js';

export default defineConfig({
  testDir: './tests', // Directory where test files are located
  fullyParallel: true, //Enables tests to run in parallel across all available workers for faster execution
  workers: process.env.CI ? 4 : 2, // 2 workers locally for parallel execution, 4 in CI
  retries: process.env.CI ? 2 : 0, // 2 retries in CI for flaky tests, 0 locally for faster feedback
  forbidOnly: !!process.env.CI, // Fail if a test is marked as 'only' in CI
  timeout: TIMEOUTS.TEST_EXECUTION_TIMEOUT, // 10 mins a test can run before timing out
  expect: { timeout: TIMEOUTS.DEFAULT_ASSERTION_TIMEOUT }, // 1 min for assertions
  outputDir: 'reports/test-results',
  reporter: [
    ['html', { open: 'never', outputFolder: 'reports/html-report' }], // Generates an HTML report after tests run, does not open automatically
    ['junit', { outputFile: 'reports/junit/results.xml' }], // Generates a JUnit report for CI integration
    ['json', { outputFile: 'reports/results.json' }],
    ['allure-playwright', { resultsDir: 'reports/allure-results' }], // Generates Allure reports for detailed test results
    //['lcov', { outputFile: 'reports/coverage/lcov.info' }],
  ],
  use: {
    baseURL: appEnv.baseUrl, // Base URL for the application under test, loaded from environment configuration
    trace: 'on', // Retains traces for debugging only when tests fail
    video: (() => {
      // Flexible video recording configuration
      if (process.env.CI) return 'retain-on-failure'; // CI: only on failures
      if (process.env.RECORD_VIDEO === 'always') return 'on'; // Local: record all tests
      if (process.env.RECORD_VIDEO === 'off') return 'off'; // Local: no videos
      return 'retain-on-failure'; // Default: record only on failures
    })(),
    screenshot: 'only-on-failure', // Takes screenshots only on test failure
    navigationTimeout: TIMEOUTS.DEFAULT_NAVIGATION_TIMEOUT, // Page navigation Timeout
    actionTimeout: TIMEOUTS.DEFAULT_ACTION_TIMEOUT, // User actions like click, fill
  },
  projects: [
    // This determines what type of page object is created
    { name: 'chromium', use: { ...devices['Chrome'] } },
    ...(process.env.CI
      ? [
          { name: 'firefox', use: { ...devices['Firefox'] } },
          { name: 'webkit', use: { ...devices['Safari'] } },
        ]
      : []),
  ],
  globalSetup: './tests/global.setup.ts', // Runs once before all tests to set up the environment
  globalTeardown: './tests/global.teardown.ts', // Runs once after all tests to clean up the environment
});
