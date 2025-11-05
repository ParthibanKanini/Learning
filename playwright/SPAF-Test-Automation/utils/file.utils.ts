import { Page, Locator } from '@playwright/test';
import { log } from './logger';
import { join } from 'path';

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
 * Gets the full path to the file
 * @param fileName - Name of the file in the directory
 * @param parentDirectory - Optional parent directory name
 * @returns Full path to the fixture file
 */
export function getFilePath(fileName: string, parentDirectory?: string): string {
  return join(process.cwd(), parentDirectory || '', fileName);
}
