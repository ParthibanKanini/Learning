import { test } from '../fixtures/login.fixtures.js';
import { logger } from 'utils/logger';
import { TAG_SANITY } from './test-tags';

test.describe(`Sample Test Scenario`, () => {
  test(`${TAG_SANITY} Sample home page testcase`, async ({ pageFactory }): Promise<void> => {
    logger.info(`Sample Test starts`);
    const homePage = pageFactory.getHomePage();
    await homePage.assertPageLoaded();

    /*await test.step('Setup test data', async () => {
      const testData = TestDataFactory.getTestData('sample.test-data.json');
      // Prepare test data and initial state
    });*/
  });
});
