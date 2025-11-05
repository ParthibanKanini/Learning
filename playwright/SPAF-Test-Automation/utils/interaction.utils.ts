import { Locator, Page } from '@playwright/test';
import { log } from './logger';

/**
 * Clicks on a locator element with error handling and logging.
 * Typically used while debugging test scripts to provide consistent click behavior.
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

/**
 * Fills an input field with the specified value
 * Clicks the field first, then fills it with the provided value.
 * Typically used while debugging test scripts to provide consistent click behavior.
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
 * Selects an option from a dropdown by typing and clicking the matching option
 * @param page - Playwright Page object
 * @param optionName - Name of the option to select from dropdown
 * @param dropdownSelector - Locator for the dropdown arrow or selector area to open dropdown (default: '.ant-select-selector')
 * @param clientCombobox - Locator for the combobox input (default: role 'combobox')
 * @returns Promise<void>
 */
export async function selectDropdownOption(
  page: Page,
  optionName: string,
  dropdownSelector: Locator = page.locator('.ant-select-selector'),
  clientCombobox: Locator = page.getByRole('combobox'),
): Promise<void> {
  await dropdownSelector.click();
  // Type to filter options
  await clientCombobox.fill(optionName);
  const selectionOption = page.locator(`[title="${optionName}"]`);
  // Wait for options to appear and click the specific option
  await selectionOption.waitFor({ state: 'visible' });
  await selectionOption.click();
}
