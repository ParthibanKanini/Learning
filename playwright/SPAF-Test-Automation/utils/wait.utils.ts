import { Page, Locator } from '@playwright/test';
import { log } from './logger';
import { TIMEOUTS } from './timeouts';

/**
 * Waits for the DOMContentLoaded event to ensure the initial HTML document
 * has been completely loaded and parsed.
 * External resources (like stylesheets/images) may still be loading.
 * @param page
 */
export async function waitForDOMContentLoaded(page: Page): Promise<void> {
  await page.waitForLoadState('domcontentloaded', { timeout: TIMEOUTS.LONG });
}

/**
 * Iterate & wait for any loading spinner to disappear from the page
 * @param loaderLocator
 */
export async function waitForLoaderToDisappear(loaderLocator: Locator): Promise<void> {
  const loaderCount = await loaderLocator.count();
  if (loaderCount > 0) {
    log.debug(`Found ${loaderCount} loading indicators, waiting for them to disappear`);
    for (let i = 0; i < loaderCount; i++) {
      if (loaderLocator.nth(i) && (await loaderLocator.nth(i).isVisible())) {
        log.info(`Waiting for loader ${i + 1} of ${loaderCount} to disappear`);
        await loaderLocator.nth(i).waitFor({ state: 'detached', timeout: TIMEOUTS.LONG });
      }
    }
    log.info(`All ${loaderCount} loading indicators have disappeared`);
  }
}

/**
 * Waits for a specific API response to complete successfully
 * @param page - Playwright Page object
 * @param url - URL pattern to wait for in the response
 * @param timeout - Maximum time to wait in milliseconds (default: TIMEOUTS.API_RESPONSE)
 * @returns Promise<void>
 */
export async function waitForAPIResponse(
  page: Page,
  url: string,
  timeout: number = TIMEOUTS.API_RESPONSE,
): Promise<void> {
  try {
    await page.waitForResponse(
      (response) => {
        return response.url().includes(url) && response.ok();
      }, // Fixed filter
      { timeout },
    );
    log.debug(`API response for ${url} received successfully.`);
  } catch (error) {
    log.warn(`Failed to receive API response for ${url} within ${timeout}ms: ${error}`);
  }
}

/**
 * Waits for all spinner elements to disappear from the page
 * Handles both generic loader div and specific Deloitte spinner images
 * @param page - Playwright Page object
 * @param timeout - Maximum time to wait in milliseconds (default: TIMEOUTS.API_RESPONSE)
 * @returns Promise<void>
 */
export async function waitForSpinnerToDisappear(
  page: Page,
  timeout: number = TIMEOUTS.API_RESPONSE,
): Promise<void> {
  const spinnerPlaceholder = 'div.loader';
  let spinnerLocator: Array<Locator> = await page.locator(spinnerPlaceholder).all();
  if (spinnerLocator.length === 0) {
    const spinnerIcon = 'div.loader > img[src*="Deloitte-Spinner.svg"]'; // Specific spinner icon
    spinnerLocator = await page.locator(spinnerIcon).all();
  }
  log.debug(`Spinner locators count: ${spinnerLocator.length}`);
  if (spinnerLocator.length > 0) {
    // loop through all spinner locators and wait for each to disappear
    for (const spinner of spinnerLocator) {
      try {
        if (await spinner.isVisible({ timeout: 5000 })) {
          log.debug(`Waiting ${timeout}ms for ${String(spinner)} spinner to disappear.`);
          try {
            // wait until the spinner Element is not visible
            await spinner.waitFor({ state: 'hidden', timeout });
          } catch (error) {
            log.warn(`Spinner is still visible : ${error}`);
          }
        } else {
          log.debug(`Spinner is not visible, no need to wait: ${String(spinner)}`);
        }
      } catch (error) {
        // Helpful debug logging
        if (page.isClosed()) {
          log.error(
            `Page closed! waiting for spinner to disappear for ${timeout}ms errored ${error}`,
          );
        }
      }
      log.debug(`Spinner ${String(spinner)} handling completed.`);
    }
  }
}

/**
 * Wait for a specified amount of time. Use only for debugging purposes.
 * This is not recommended for production code as it can lead to flaky tests.
 * @param time Time in milliseconds to wait
 * @returns Promise<void>
 */
export async function waitFor(time: number): Promise<void> {
  return new Promise((resolve) => {
    setTimeout(resolve, time);
  });
}
