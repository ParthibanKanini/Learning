import { test as baseTest, expect, Page } from '@playwright/test';
import { createTestLogger } from 'utils/logger.js';
import LoginPage from '@pages/Login.page.js';
import HomePage from '@pages/Home.page.js';
import { PageFactory } from '@pages/PageFactory.js';
import { getUser } from 'data/TestUser.factory.js';

/**
 * Login Fixtures - Authentication Strategy for Playwright Tests
 * This fixture file implements a worker-scoped authentication strategy
 * for Playwright tests, optimizing performance by authenticating
 * once per worker thread rather than per test.
 *
 * This approach reduces the overhead of repeated logins and
 * improves test execution speed. Same page instance across all tests in worker.
 * Context closed when worker finishes.
 */

// The fixture extends Playwright's base test with two types of fixtures:
// a. Test-scoped fixtures (available to each test)
type TestFixtures = {
  pageFactory: PageFactory;
};

// b. Worker-scoped fixtures (shared across tests in same worker)
type WorkerFixtures = {
  authenticatedPage: Page;
};

/*
 * Worker-scoped: Authenticate ONCE per worker and reuse the same page
 * Authenticate once per worker thread instead of before each test.
 * Eliminates repetitive login overhead and improves test performance.
 * The same authenticated page is shared across all tests in a worker with one brwowser context.
 *
 * Worker Setup: Login once
 * ├── Test 1: Use authenticated page
 * ├── Test 2: Use authenticated page
 * └── Test 3: Use authenticated page
 * Worker Cleanup: Close context
 * 1x login overhead per worker
 */
export const test = baseTest.extend<TestFixtures, WorkerFixtures>({
  authenticatedPage: [
    async ({ browser }, use, workerInfo) => {
      const fixtureLogger = createTestLogger('AuthenticatedPage');
      fixtureLogger.info(
        `Worker ${workerInfo.workerIndex}: Starting authentication with single page strategy`,
      );
      const context = await browser.newContext();
      const page = await context.newPage();
      try {
        // Login once per worker thread using the same page that will be reused
        const loginPage = new LoginPage(page);
        await loginPage.go(); // Navigate to login page first
        await loginPage.login(getUser('default_user'));
        // Verify authentication succeeded
        const homePage = new HomePage(page);
        await homePage.assertPageLoaded();
        fixtureLogger.info(
          `Worker ${workerInfo.workerIndex}: Authentication completed successfully - page ready for reuse`,
        );
        // Provide the same authenticated page to all the tests in this worker
        await use(page);
      } catch (error) {
        fixtureLogger.error(
          `Worker ${workerInfo.workerIndex}: Authentication failed: ${error instanceof Error ? error.message : String(error)}`,
        );
        throw error;
      } finally {
        fixtureLogger.info(`Worker ${workerInfo.workerIndex}: Cleaning up authenticated page`);
        await context.close();
      }
    },
    { scope: 'worker' },
  ],

  /*
   * Test-scoped: Reuse the same authenticated page for each test
   * Each test gets a fresh PageFactory instance.
   * Ensures consistent starting point.
   * Provides centralized page object instantiation
   */
  pageFactory: async ({ authenticatedPage }, use, testInfo) => {
    const testLogger = createTestLogger('PageFactory');
    const workerIndex = testInfo.parallelIndex;
    try {
      testLogger.info(`Worker ${workerIndex}: Setting up PageFactory for test: ${testInfo.title}`);
      const pageFactory = new PageFactory(authenticatedPage);
      // Navigate to home to ensure we're in a clean state for each test
      const homePage = pageFactory.getHomePage();
      // Skip navigation since we're already authenticated and on a valid page
      // await homePage.go();
      await homePage.assertPageLoaded();
      // Handle privacy popup if present
      await homePage.dismissPrivacyPopup();
      testLogger.info(`Worker ${workerIndex}: PageFactory ready for test execution`);
      await use(pageFactory);
    } catch (error) {
      testLogger.error(
        `Worker ${workerIndex}: Page factory setup failed: ${error instanceof Error ? error.message : String(error)}`,
      );
      throw error;
    }
    // Note: We don't close the page here since it's shared across tests in the worker
  },
});

export { expect }; // re-export if you want to import from the fixture file
