import { expect, Page } from '@playwright/test';
import { log } from './logger';
import { TIMEOUTS } from './timeouts';
import { click } from './interaction.utils';

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
        await okButton.click();
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
