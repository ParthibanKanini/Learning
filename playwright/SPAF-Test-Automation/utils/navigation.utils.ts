import { Page } from '@playwright/test';

/**
 * Scrolls the page body into view if needed
 * @param page - Playwright Page object
 * @returns Promise<void>
 */
export async function scrollToPageBody(page: Page): Promise<void> {
  await page.locator('body').scrollIntoViewIfNeeded();
}

/**
 * Scrolls to the end of the page content
 * @param page - Playwright Page object
 * @returns Promise<void>
 */
export async function scrollToPageEnd(page: Page): Promise<void> {
  await page.evaluate('window.scrollTo(0, document.body.scrollHeight)');
}
