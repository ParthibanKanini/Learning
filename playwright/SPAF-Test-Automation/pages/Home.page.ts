import { expect, Page } from '@playwright/test';

import { BasePage } from './Base.page.js';
import { waitForAPIResponse } from '../utils/testUtil.js';
import { TIMEOUTS } from '../utils/timeouts.js';

export default class HomePage extends BasePage {
  constructor(protected readonly page: Page) {
    super(page);
  }

  protected get locators() {
    return {
      ...super.locators,
      homePageTitle: this.page.locator('span.ant-page-header-heading-title[title="Dashboard"]'),
    } as const;
  }

  /**
   * Asserts that the page under test is loaded successfully.
   * It should use Playwright's expect function to verify visibility.
   * @returns {Promise<void>} A promise that resolves when the assertion is complete.
   */
  async assertPageLoaded(): Promise<void> {
    try {
      await super.assertPageLoaded();
      // Wait for the main page elements to load
      //await expect(this.locators.defaultLandingPage).toBeVisible({ timeout: TIMEOUTS.MEDIUM });
      await waitForAPIResponse(
        this.page,
        '/api/v2/applauncher/menus?languageCode=en-US&appName=Omnia',
        TIMEOUTS.LONG,
      );
      this.pageLogger.info('Dashboard Page loaded successfully & ready for interaction');
    } catch (error) {
      this.pageLogger.error(`Failed to assert Dashboard Page loaded: ${String(error)}`);
      throw error;
    }
  }

  async assertDashboardPageTitle(): Promise<void> {
    this.pageLogger.info('Asserting Dashboard page title is visible');
    await expect(this.locators.homePageTitle).toBeVisible({ timeout: TIMEOUTS.MEDIUM });
  }

  async dismissPrivacyPopup(): Promise<void> {
    // Check for cookies popup visibility
    //await closePrivacyDialog(this.page);
  }

  async go(): Promise<void> {
    // After login on automationexercise.com, user is typically on account page
    // Navigate to the account/dashboard page instead of trying to go to root
    await this.page.goto('https://automationexercise.com/');
  }
}
