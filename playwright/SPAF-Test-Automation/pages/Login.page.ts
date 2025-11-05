import { Page, expect } from '@playwright/test';
import { BasePage } from './Base.page.js';
import { TestUser } from 'data/DataTypes.js';

export default class LoginPage extends BasePage {
  constructor(page: Page) {
    super(page);
  }

  protected get locators() {
    return {
      ...super.locators,
      // Page Elements - Updated to use more robust and semantic locators specific to automationexercise.com
      emailInput: this.page.locator('input[data-qa="login-email"]'),
      passwordInput: this.page.locator('input[data-qa="login-password"]'),
      submitButton: this.page.locator('button[data-qa="login-button"]'),
    } as const;
  }

  async assertPageLoaded(): Promise<void> {
    this.pageLogger.debug('Login flow starting');
    // Assert that the login/signup page is loaded by checking for the presence of the email input
    await expect(this.locators.emailInput).toBeVisible();
    this.pageLogger.info('Login/SignUp page loaded successfully');
  }

  async go(): Promise<void> {
    this.pageLogger.debug('Navigating to login page');
    // Navigate to the login page URL
    await this.page.goto('https://automationexercise.com/login');
  }

  /**
   * Logs in the user with the provided credentials.
   * @param logginUser The user credentials to log in with.
   */
  async login(logginUser: TestUser): Promise<void> {
    this.pageLogger.info(`Starting login process for email: ${logginUser.email}`);
    try {
      this.pageLogger.debug('Filling and navigating login flow');
      await this.locators.emailInput.fill(logginUser.email);
      //await this.locators.nextButton.click();
      await this.locators.passwordInput.fill(logginUser.password);
      //await this.locators.signInButton.click();

      /// <<<< TODO: This is temporary. remove this when working in Del machines
      /*const verificationCode = '437914';
      await this.locators.verificationCodeInput.fill(verificationCode);
      */
      await this.locators.submitButton.click();
      /// >>>>

      this.pageLogger.info(`Login initiated successfully for email: ${logginUser.email}`);
      this.pageLogger.info(`Login initiated successfully for password: ${logginUser.password}`);
    } catch (error) {
      this.pageLogger.error(
        `Login process failed for ${logginUser.email}: ${error instanceof Error ? error.message : String(error)}`,
      );
      throw error;
    }
  }
}
