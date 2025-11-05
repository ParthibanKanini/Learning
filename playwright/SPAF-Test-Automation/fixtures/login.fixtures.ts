import { test as baseTest, Page } from '@playwright/test';
import { createTestLogger } from 'utils/logger.js';
import LoginPage from '@pages/Login.page.js';
import HomePage from '@pages/Home.page.js';
import { PageFactory } from '@pages/PageFactory.js';
import { getUser } from 'data/TestUser.factory.js';

// Test-scoped fixtures (available to each test)
type TestFixtures = {
  pageFactory: PageFactory;
};

// Worker-scoped fixtures (shared across tests in same worker)
type WorkerFixtures = {
  authenticatedPage: Page;
};

export const test = baseTest.extend<TestFixtures, WorkerFixtures>({
  // Worker-scoped: Authenticate ONCE per worker and reuse the same page
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
        const dashboardPage = new HomePage(page);
        await dashboardPage.assertPageLoaded();
        fixtureLogger.info(
          `Worker ${workerInfo.workerIndex}: Authentication completed successfully - page ready for reuse`,
        );
        // Provide the same authenticated page to all tests in this worker
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

  // Test-scoped: Reuse the same authenticated page for each test
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
