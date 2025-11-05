import { expect, Locator, Page } from '@playwright/test';
import { log } from './logger';
import { join } from 'path';
import { TIMEOUTS } from './timeouts';

/**
 * Dismisses announcement popup if visible on the page
 * @param page - Playwright Page object
 * @returns Promise<void>
 */
export async function dismissAnnouncementPopup(page: Page): Promise<void> {
  const announcementPopup = page.locator('.gl-pop-up');
  const acknowledgeButton = announcementPopup.getByRole('button', { name: 'Acknowledge' });
  if (await announcementPopup.isVisible()) {
    await acknowledgeButton.click();
    await expect(announcementPopup).not.toBeVisible();
  }
}

/**
 * Closes privacy dialog by trying multiple OneTrust privacy dialog patterns
 * Handles various OneTrust banner and container configurations
 * @param page - Playwright Page object
 * @returns Promise<void>
 */
export async function closePrivacyDialog(page: Page): Promise<void> {
  // Try multiple privacy dialog patterns
  const privacyDialogPatterns = [
    {
      name: 'OneTrust Container',
      dialog: page.locator('div.ot-sdk-container[role="alertdialog"]'),
      button: page.getByRole('button', { name: 'Decline optional cookies' }),
    },
    {
      name: 'OneTrust Banner',
      dialog: page.locator('div.onetrust-banner-sdk').locator('div.ot-sdk-container'),
      button: page.getByRole('button', { name: 'Decline optional cookies' }),
    },
  ];

  for (const pattern of privacyDialogPatterns) {
    try {
      log.debug(`Checking for ${pattern.name} privacy dialog...`);

      // Wait for dialog to be visible with a reasonable timeout
      await pattern.dialog.waitFor({ state: 'visible', timeout: TIMEOUTS.SHORT });
      log.debug(`${pattern.name} privacy dialog is visible`);

      // Wait for button to be visible AND enabled (actionable)
      await pattern.button.waitFor({ state: 'visible', timeout: TIMEOUTS.SHORT });

      // Ensure the button is actually clickable by checking if it's enabled
      await expect(pattern.button).toBeEnabled({ timeout: TIMEOUTS.SHORT });

      // Click the button
      await pattern.button.click();

      // Wait for dialog to disappear
      await expect(pattern.dialog).not.toBeVisible({ timeout: TIMEOUTS.SHORT });
      log.debug(`${pattern.name} privacy dialog closed successfully`);
      return; // Exit once we successfully close a dialog
    } catch (error) {
      log.debug(`${pattern.name} pattern failed: ${error}`);
      // Try alternative close methods for this pattern
      try {
        if (await pattern.dialog.isVisible({ timeout: TIMEOUTS.SHORT })) {
          log.debug(`${pattern.name} dialog visible, trying alternative close methods`);
          const closeBtn = pattern.dialog
            .getByRole('button', { name: /close|×|decline|reject/i })
            .first();

          await closeBtn.waitFor({ state: 'visible', timeout: TIMEOUTS.SHORT });
          await expect(closeBtn).toBeEnabled({ timeout: TIMEOUTS.SHORT });
          await closeBtn.click();
          await expect(pattern.dialog).not.toBeVisible({ timeout: TIMEOUTS.SHORT });
          log.debug(`${pattern.name} privacy dialog closed with alternative button`);
          return;
        }
      } catch (altError) {
        log.debug(`${pattern.name} alternative method also failed: ${altError}`);
      }
      continue; // Try next pattern
    }
  }
  log.debug('No privacy dialog found or successfully closed');
}

/**
 * Closes OneTrust Privacy Center that may be blocking user interactions
 * Tries multiple approaches: buttons, clicking outside, and keyboard escape
 * @param page - Playwright Page object
 * @returns Promise<void>
 */
export async function closeOneTrustPrivacyCenter(page: Page): Promise<void> {
  log.debug('Checking for OneTrust Privacy Center that may be blocking interactions...');

  const oneTrustPatterns = [
    {
      name: 'OneTrust Consent SDK',
      container: page.locator('#onetrust-consent-sdk'),
      filter: page.locator('div.onetrust-pc-dark-filter'),
    },
    {
      name: 'OneTrust PC Modal',
      container: page.locator('div[class*="onetrust-pc-sdk"]'),
      filter: page.locator('div.onetrust-pc-dark-filter'),
    },
  ];

  for (const pattern of oneTrustPatterns) {
    try {
      // Check if the dark filter is visible (blocking interactions)
      const filterVisible = await pattern.filter.isVisible({ timeout: TIMEOUTS.SHORT });
      if (filterVisible) {
        log.debug(`${pattern.name} dark filter is blocking interactions`);

        // Try to find and click close/reject buttons with more comprehensive selectors
        const closeButtons = [
          // Specific OneTrust buttons
          pattern.container.locator('button[id*="reject"], button[class*="reject"]').first(),
          pattern.container.locator('button.ot-pc-refuse-all-handler').first(),
          pattern.container.locator('button.save-preference-btn-handler').first(),
          pattern.container
            .getByRole('button', { name: /reject all|decline all|refuse all/i })
            .first(),
          pattern.container.getByRole('button', { name: /close|×|done|save/i }).first(),
          pattern.container.locator('button[aria-label*="Close"]').first(),
          pattern.container.locator('button.ot-close-icon').first(),
          // Fallback to any button in the container
          pattern.container.locator('button').first(),
        ];

        for (const btn of closeButtons) {
          try {
            if (await btn.isVisible({ timeout: TIMEOUTS.SHORT })) {
              log.debug(`Clicking ${pattern.name} close button`);
              await btn.click();
              // Wait for filter to disappear
              //await page.waitForTimeout(1000); // Give it a moment
              const stillVisible = await pattern.filter.isVisible({ timeout: TIMEOUTS.SHORT });
              if (!stillVisible) {
                log.debug(`${pattern.name} privacy center closed successfully`);
                return;
              }
            }
          } catch (btnError) {
            log.debug(`Button click failed: ${btnError}`);
            continue;
          }
        }

        // If no button found or buttons didn't work, try more aggressive approaches
        log.debug(
          `No effective close button found, trying alternative approaches for ${pattern.name}`,
        );

        // Try clicking outside the modal (on the dark filter itself)
        try {
          await pattern.filter.click({ position: { x: 10, y: 10 } });
          //await page.waitForTimeout(1000);
          const stillVisible = await pattern.filter.isVisible({ timeout: TIMEOUTS.SHORT });
          if (!stillVisible) {
            log.debug(`${pattern.name} closed by clicking outside modal`);
            return;
          }
        } catch (error) {
          log.debug(`Clicking outside modal failed: ${error}`);
        }

        // Try pressing Escape key
        log.debug(`Trying Escape key for ${pattern.name}`);
        await page.keyboard.press('Escape');
        //await page.waitForTimeout(1000);
        const stillVisibleAfterEscape = await pattern.filter.isVisible({ timeout: TIMEOUTS.SHORT });
        if (!stillVisibleAfterEscape) {
          log.debug(`${pattern.name} closed with Escape key`);
          return;
        }

        log.debug(`${pattern.name} privacy center could not be closed automatically`);
      }
    } catch (error) {
      log.debug(`${pattern.name} handling failed: ${error}`);
      continue;
    }
  }

  log.debug('No OneTrust Privacy Center found blocking interactions');
}

/**
 * Clicks a locator element while handling OneTrust privacy center interference
 * Automatically retries click action if OneTrust privacy center is blocking interactions
 * @param locator - Playwright Locator to click
 * @param page - Playwright Page object
 * @returns Promise<void>
 */
export async function clickWithPrivacyHandling(locator: Locator, page: Page): Promise<void> {
  // First, check and handle any OneTrust privacy center
  //await closeOneTrustPrivacyCenter(page);
  try {
    await locator.click();
  } catch {
    // If click fails, try handling privacy center again and retry
    log.debug('Click failed, attempting to handle OneTrust privacy center and retry...');
    await closeOneTrustPrivacyCenter(page);
    await locator.click();
  }
}

/**
 * Converts date format from MM/DD/YYYY to YYYY-MM-DD
 * @param dateString - Date string in MM/DD/YYYY format
 * @returns Date string in YYYY-MM-DD format
 */
export async function formatDateForPicker(dateString: string): Promise<string> {
  log.debug(`Converting date format from MM/DD/YYYY to YYYY-MM-DD: ${dateString}`);

  // Split the date string by '/'
  const dateParts = dateString.split('/');

  if (dateParts.length !== 3) {
    throw new Error(`Invalid date format: ${dateString}. Expected MM/DD/YYYY format.`);
  }

  const [month, day, year] = dateParts;

  // Validate the parts
  if (!month || !day || !year) {
    throw new Error(`Invalid date components: ${dateString}`);
  }

  // Pad month and day with leading zeros if needed
  const formattedMonth = month.padStart(2, '0');
  const formattedDay = day.padStart(2, '0');

  const formattedDate = `${year}-${formattedMonth}-${formattedDay}`;
  log.info(`Converted date: ${dateString} → ${formattedDate}`);

  return formattedDate;
}

/**
 * Scrolls the page body into view if needed
 * @param page - Playwright Page object
 * @returns Promise<void>
 */
export async function scrollToPageBody(page: Page) {
  await page.locator('body').scrollIntoViewIfNeeded();
}

/**
 * Scrolls to the end of the page content
 * @param page - Playwright Page object
 * @returns Promise<void>
 */
export async function scrollToPageEnd(page: Page) {
  await page.evaluate('window.scrollTo(0, document.body.scrollHeight)');
}

/**
 * Attaches a document file through file upload dialog
 * Handles file chooser event and sets the specified file
 * @param page - Playwright Page object
 * @param fileBrowseBtn - Locator for the file browse button
 * @param filePath - Path to the file to be uploaded
 * @returns Promise<void>
 */
export async function attachDocument(
  page: Page,
  fileBrowseBtn: Locator,
  filePath: string,
): Promise<void> {
  log.info(`Trying to upload file ${filePath}`);
  const [fileUploadDialog] = await Promise.all([
    // Promise ensures all events within it are completed.
    // below returns the FileChooser object that is assigned to fileUploadDialog
    page.waitForEvent('filechooser'),
    fileBrowseBtn.click(),
  ]);
  log.info(`File chooser is visible and browsing for file: ${filePath} complete.`);
  await fileUploadDialog.setFiles(filePath);
  //await click(addDoc);
}

/**
 * Selects an option from a dropdown by typing and clicking the matching option
 * @param page - Playwright Page object
 * @param clientName - Name of the client to select from dropdown
 * @returns Promise<void>
 */
export async function selectDropdownOption(page: Page, clientName: string) {
  const clientSelector = page.locator('.ant-select-selector');
  const clientCombobox = page.getByRole('combobox');
  const clientOption = (clientName: string) => page.locator(`[title="${clientName}"]`);
  // Click the dropdown arrow or selector area to open dropdown
  await clientSelector.click();
  // Type to filter options
  await clientCombobox.fill(clientName);
  // Wait for options to appear and click the specific option
  await clientOption(clientName).waitFor({ state: 'visible' });
  await clientOption(clientName).click();
}

/**
 * Closes cookies popup if it's visible on the page
 * @param page - Playwright Page object
 * @returns Promise<void>
 */
export async function closeCookiesPopupIfVisible(page: Page): Promise<void> {
  const closeButton = page.getByRole('button', { name: 'Close' });
  try {
    const closeButtonVisible = await closeButton.isVisible({ timeout: TIMEOUTS.SHORT });
    if (closeButtonVisible) {
      log.debug('Cookies popup is visible');
      await closeButton.click();
    } else {
      log.debug('No Cookies popup for action taken.');
    }
  } catch (error) {
    log.warn(`Error while closing Cookies popup: ${error}`);
  }
}

/**
 * Closes privacy popup by clicking the "Decline optional cookies" button
 * Waits for the button to be visible before clicking
 * @param page - Playwright Page object
 * @returns Promise<void>
 */
export async function closePrivacyPopup(page: Page): Promise<void> {
  const closeButton = page.getByRole('button', { name: 'Decline optional cookies' });
  try {
    await expect(closeButton).toBeVisible({ timeout: TIMEOUTS.LONG });
    await closeButton.click();
  } catch (error) {
    log.warn(`Error while closing Privacy popup: ${error}`);
  }
}

/**
 * Closes announcements popup if it's visible on the page
 * Looks for popup with "ANNOUNCEMENTS" header text
 * @param page - Playwright Page object
 * @returns Promise<void>
 */
export async function closeAnnouncementsPopupIfVisible(page: Page): Promise<void> {
  const announcement = page
    .locator('div.gl-pop-up__header span')
    .filter({ hasText: 'ANNOUNCEMENTS' });
  if (await announcement.isVisible()) {
    const closeButton = page.locator('div.gl-pop-up__header span.dds-gl-icon_close__s__stroke');
    try {
      if (await closeButton.isVisible()) {
        log.debug('Announcements popup is visible');
        await closeButton.click();
      } else {
        log.debug('No Announcements popup for action taken.');
      }
    } catch (error) {
      log.warn(`Error while closing Announcements popup: ${error}`);
    }
  }
}

/**
 * Closes disclaimer popup if it's visible on the page
 * Handles modal with "Disclaimer" title and clicks OK button
 * @param page - Playwright Page object
 * @returns Promise<void>
 */
export async function closeDisclaimerPopupIfVisible(page: Page): Promise<void> {
  const disclaimerModal = page.locator('div.modal-content', { hasText: 'Disclaimer' });
  try {
    // Wait up to 3s for modal to be visible; bail out if not
    if (await disclaimerModal.isVisible({ timeout: TIMEOUTS.SHORT }).catch(() => false)) {
      log.debug('Disclaimer modal is visible, attempting to close it.');
      // Defensive: Wait for the modal to be stable (no animation or movement)
      await disclaimerModal.waitFor({ state: 'visible', timeout: TIMEOUTS.SHORT });
      // Chain the OK button to the modal, not the page, for robustness
      const okButton = disclaimerModal.getByRole('button', { name: 'OK', exact: true });
      // Wait for the button to be visible AND enabled AND stable
      await okButton.waitFor({ state: 'visible', timeout: TIMEOUTS.SHORT });
      await expect(okButton).toBeEnabled({ timeout: TIMEOUTS.SHORT });
      if (await okButton.isVisible()) {
        // ensures stability
        await okButton.click({ trial: true });
        // Now click, with retries handled by Playwright
        await click(okButton);
      }
      // Optionally: Wait for the modal to disappear
      await expect(disclaimerModal).not.toBeVisible({ timeout: 5000 });
      log.debug('Clicked OK and closed Disclaimer modal.');
    } else {
      log.debug('No Disclaimer modal for action taken.');
    }
  } catch (error) {
    log.warn(`Error while closing Disclaimer popup: ${error}`);
    return;
    // consuming the error here to avoid test failure
  }
}

/**
 * Closes disclaimer popup by clicking OK button
 * Waits for disclaimer modal to be visible before attempting to close
 * @param page - Playwright Page object
 * @returns Promise<void>
 */
export async function closeDisclaimerPopup(page: Page): Promise<void> {
  const disclaimerModal = page.locator(
    '.modal-dialog.modal-dialog-centered:has(.modal-title:has-text("Disclaimer"))',
  );
  //const disclaimerModal = page.getByRole('dialog', { name: 'Disclaimer' });
  log.info('Check visibility of Disclaimer popup');
  const isVisible: boolean = await disclaimerModal.isVisible({ timeout: TIMEOUTS.LONG });
  if (isVisible) {
    log.debug('Disclaimer modal is visible');
    //const closeButton = page.locator('.info-modal-footer button.btn.btn-primary:has-text("OK")');
    const closeButton = disclaimerModal.getByRole('button', { name: 'OK' });
    await closeButton.click();
  } else {
    log.debug('No Disclaimer modal for action taken.');
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
 * Expands a dropdown by clicking on it based on the dropdown title
 * @param page - Playwright Page object
 * @param dropDownTitle - Title text of the dropdown to expand
 * @returns Promise<void>
 */
export async function expandDropDown(page: Page, dropDownTitle: string): Promise<void> {
  const dropdownLocatorStr = `button:has(.dropdown__text[title="${dropDownTitle}"])`;
  log.debug(`Expanding dropdown with title: ${dropdownLocatorStr}`);
  const dropdown = page.locator(dropdownLocatorStr);
  // Ensure dropdown is clickable
  await expect(dropdown).toBeEnabled({ timeout: TIMEOUTS.SHORT });
  await click(dropdown);
}

/**
 * Clicks on a locator element with error handling and logging
 * @param locator - Playwright Locator to click
 * @returns Promise<void>
 * @throws Error if click operation fails
 */
export async function click(locator: Locator): Promise<void> {
  const locatorContent = await locator.textContent();
  try {
    await locator.click();
  } catch (error) {
    log.error(`Error clicking ${locatorContent} element: ${error}`);
    throw error;
  }
}

/*export async function waitForTimeout(page: Page, timeout: number = 5000): Promise<void> {
  log.debug(`Waiting for ${timeout} milliseconds...`);
  await new Promise((resolve) => setTimeout(resolve, timeout));
  log.debug(`Waited for ${timeout} milliseconds.`);
}*/

/**
 * Fills an input field with the specified value
 * Clicks the field first, then fills it with the provided value
 * @param inputTextField - Locator for the input field to fill
 * @param value - Value to enter into the input field
 * @returns Promise<void>
 */
export async function fillInputField(inputTextField: Locator, value: string): Promise<void> {
  log.debug(`Filling input field '${inputTextField}' with value '${value}'`);
  await click(inputTextField);
  await inputTextField.fill(value);
  log.debug(`Input field '${inputTextField}' filled with value '${value}'`);
}

/**
 * Types text into a locator with character-by-character delay
 * Useful for autocomplete fields that need time to process each character
 * @param page - Playwright Page object
 * @param locator - Input locator to type into
 * @param resultLocator - Locator to check for results/dropdown appearance
 * @param text - Text to type character by character
 * @param delay - Delay between characters in milliseconds (default: 10)
 * @returns Promise<void>
 */
export async function typeWithDelay(
  page: Page,
  locator: Locator,
  resultLocator: Locator,
  text: string,
  delay: number = 10,
): Promise<void> {
  await locator.focus();
  await locator.clear();
  let typed = '';
  for (const char of text) {
    if (page.isClosed()) break;
    await locator.press(char);
    typed += char;
    if (typed.length % 5 === 0) {
      if (await resultLocator.isVisible().catch(() => false)) break;
    }
    await new Promise((resolve) => setTimeout(resolve, delay));
  }
}

/**
 * Handles disclaimer popup by accepting terms and clicking OK
 * Looks for disclaimer popup and accepts it if visible
 * @param page - Playwright Page object
 * @returns Promise<void>
 */
export async function handleDisclaimerPopup(page: Page): Promise<void> {
  const disclaimerPopup = page.locator('.disclaimer-popup');
  if (await disclaimerPopup.isVisible({ timeout: 5000 })) {
    const acceptRadio = page.locator('input[type="radio"][value="accept"]');
    await acceptRadio.check();
    const okButton = page.getByRole('button', { name: 'OK', exact: true });
    await click(okButton);
  }
}

/**
 * Gets the full path to a fixture file in the testdata directory
 * @param fileName - Name of the file in the testdata directory
 * @returns Full path to the fixture file
 */
export function getFixturePath(fileName: string): string {
  return join(process.cwd(), 'testdata', fileName);
}

/**
 * Wait for a specified amount of time. Use only for debugging purposes.
 * This is not recommended for production code as it can lead to flaky tests.
 * @param time Time in milliseconds to wait
 * @returns
 */
export async function waitFor(time: number): Promise<void> {
  return new Promise((resolve) => {
    setTimeout(resolve, time);
  });
}
