import { Page } from '@playwright/test';
import { createPageLogger } from 'utils/logger';
import { clickWithPrivacyHandling } from '../utils/testUtil.js';
import { TIMEOUTS } from '../utils/timeouts.js';
/**
 * BasePage class that all page objects should extend.
 * This class provides a common interface for page objects and enforces
 * the implementation of the assertPageLoaded method.
 */
export abstract class BasePage {
  protected page: Page;
  protected pageLogger: ReturnType<typeof createPageLogger>;

  constructor(protected readonly currentPage: Page) {
    this.page = currentPage;
    /* The logger will include the page name in its context for better organization
       and filtering of logs related to specific pages.
       Automatically gets the subclass name (e.g., 'LoginPage', 'DashboardPage', etc.)
    */
    this.pageLogger = createPageLogger(this.constructor.name);
  }

  /**
   * Locators for the Engagement Page.
   * These locators are used to interact with elements on the page.
   */
  protected get locators() {
    return {
      defaultLandingPage: this.page.locator(
        'span.ant-page-header-heading-title[title="Dashboard"]',
      ),
      dashboardMenu: this.page.getByRole('link', { name: 'Dashboard' }),
      fullDnavMenu: this.page.getByRole('img', { name: 'Satchel' }),
      auditDirLink: this.page.getByRole('link', { name: 'Audit Directory', exact: true }),
      statusMonitorLink: this.page.locator('a[href="/US/status-monitor"]'),
      proceduresLink: this.page.locator('a[href="/US/procedures"]'),
      modularizedProceduresMenu: this.page.getByRole('menuitem', {
        name: 'Modularized Procedures',
      }),
      valuationReconLink: this.page.getByRole('link', {
        name: 'Valuation & Reconciliation',
        exact: true,
      }),
      privateInvestmentModuleLink: this.page.getByRole('link', {
        name: 'Private Investment Module',
        exact: true,
      }),
      privateDebtCalculatorLink: this.page.locator('a[href="/US/private-debt-calculator"]'),

      pdfExtractionLink: this.page.getByRole('menuitem', { name: 'Pdf Extraction' }),
      searchModuleMenu: this.page
        .getByRole('menuitem', { name: 'omnia-asset-searchSearch' })
        .getByLabel('omnia-asset-search'),

      searchInvestmentsModuleLink: this.page.getByRole('menuitem', { name: 'Investments' }),
      searchFxModuleLink: this.page.getByRole('menuitem', { name: 'FX' }),
      clientManagementLink: this.page.getByRole('menuitem', { name: 'Client Management' }),
      financialStatementWorkroomLink: this.page.getByRole('menuitem', {
        name: 'Financial Statement Workroom',
      }),
      marketDataMonitorLink: this.page.getByRole('menuitem', {
        name: 'Market Data Monitor',
      }),

      auditsLink: this.page.locator('a[href="/US/audits"]'),
      loaderLocator: this.page.locator('#main-anatomy-content div').filter({ hasText: 'Loading' }),
    } as const;
  }

  public async navigateTo(section: string, subSection?: string): Promise<void> {
    this.pageLogger.debug(`Navigating to section: '${section}' >> ${subSection ?? ''}`);
    try {
      switch (section.toLowerCase()) {
        case 'dashboard':
          return await this.locators.dashboardMenu.click();
        case 'full dnav':
          await this.locators.fullDnavMenu.click();
          switch (subSection?.toLowerCase()) {
            case 'audit directory':
              return await this.locators.auditDirLink.click();
            case 'status monitor':
              return await this.locators.statusMonitorLink.click();
            case 'procedures':
              return await this.locators.proceduresLink.click();
          }
          return;
        case 'modularized procedures':
          // Use click with privacy handling to handle OneTrust interference
          await clickWithPrivacyHandling(this.locators.modularizedProceduresMenu, this.page);
          switch (subSection?.toLowerCase()) {
            case 'valuation recon':
              return await this.locators.valuationReconLink.click();
            case 'private investment module':
              // Use click with privacy handling to handle OneTrust interference
              return await clickWithPrivacyHandling(
                this.locators.privateInvestmentModuleLink,
                this.page,
              );
            case 'private debt calculator':
              return await this.locators.privateDebtCalculatorLink.click();
          }
          return;
        case 'pdf extraction':
          return await this.locators.pdfExtractionLink.click();
        case 'search module':
          await this.locators.searchModuleMenu.click();
          switch (subSection?.toLowerCase()) {
            case 'investments':
              return await this.locators.searchInvestmentsModuleLink.click();
            case 'fx':
              return await this.locators.searchFxModuleLink.click();
          }
          return;
        case 'client management':
          return await this.locators.clientManagementLink.click();
        case 'financial statement workroom':
          return await this.locators.financialStatementWorkroomLink.click();
        case 'market data monitor':
          return await this.locators.marketDataMonitorLink.click();
        case 'audits':
          return await this.locators.auditsLink.click();

        default:
          this.pageLogger.error(`Unknown section: ${section}. Cannot navigate.`);
          return Promise.reject(new Error(`Unknown section: ${section}. Cannot navigate.`));
      }
    } catch (error) {
      this.pageLogger.error(
        `Navigation to section: '${section}' >> ${subSection ?? ''} failed: ${
          error instanceof Error ? error.message : String(error)
        }`,
      );
      throw error;
    }
  }
  // Force all page classes to implement assertPageLoaded method such that
  // they assert based on the page's specific elements.
  public async assertPageLoaded(): Promise<void> {
    //DOM is fully parsed, but external resources (like stylesheets/images) may still be loading.
    await this.page.waitForLoadState('domcontentloaded', { timeout: TIMEOUTS.LONG });

    //iterate to wait for any loading spinner to disappear
    const loaderCount = await this.locators.loaderLocator.count();
    if (loaderCount > 0) {
      this.pageLogger.debug(
        `Found ${loaderCount} loading indicators, waiting for them to disappear`,
      );
      for (let i = 0; i < loaderCount; i++) {
        if (
          this.locators.loaderLocator.nth(i) &&
          (await this.locators.loaderLocator.nth(i).isVisible())
        ) {
          this.pageLogger.info(`Waiting for loader ${i + 1} of ${loaderCount} to disappear`);
          await this.locators.loaderLocator
            .nth(i)
            .waitFor({ state: 'detached', timeout: TIMEOUTS.LONG });
        }
      }
      this.pageLogger.info(`All ${loaderCount} loading indicators have disappeared`);
    }
  }
}
