import { log } from './logger';

/**
 * Converts date string from one format to another
 * @param dateString - Date string in MM/DD/YYYY or DD/MM/YYYY format
 * @param targetFormat - Target date format (e.g., 'YYYY-MM-DD', 'DD-MM-YYYY', 'MM/DD/YYYY', 'DD/MM/YYYY')
 * @param sourceFormat - Source date format, defaults to 'MM/DD/YYYY'
 * @returns Date string in the specified target format
 * @throws Error if date format is invalid or conversion fails
 */
export async function formatDateForPicker(
  dateString: string,
  targetFormat: string = 'YYYY-MM-DD',
  sourceFormat: string = 'MM/DD/YYYY',
): Promise<string> {
  log.debug(`Converting date format from ${sourceFormat} to ${targetFormat}: ${dateString}`);

  // Parse the input date based on source format
  const { month, day, year } = parseDateString(dateString, sourceFormat);

  // Validate the parsed components
  if (!month || !day || !year) {
    throw new Error(`Invalid date components parsed from: ${dateString}`);
  }

  // Validate date values
  const monthNum = parseInt(month, 10);
  const dayNum = parseInt(day, 10);
  const yearNum = parseInt(year, 10);

  if (monthNum < 1 || monthNum > 12) {
    throw new Error(`Invalid month: ${month}. Must be between 1-12.`);
  }
  if (dayNum < 1 || dayNum > 31) {
    throw new Error(`Invalid day: ${day}. Must be between 1-31.`);
  }
  if (yearNum < 1900 || yearNum > 2100) {
    throw new Error(`Invalid year: ${year}. Must be between 1900-2100.`);
  }

  // Format the date according to target format
  const formattedDate = formatDateString(month, day, year, targetFormat);
  log.info(`Converted date: ${dateString} (${sourceFormat}) → ${formattedDate} (${targetFormat})`);

  return formattedDate;
}

/**
 * Parses a date string based on the specified format
 * @param dateString - Date string to parse
 * @param format - Format pattern (e.g., 'MM/DD/YYYY', 'DD/MM/YYYY')
 * @returns Object with month, day, and year components
 */
function parseDateString(
  dateString: string,
  format: string,
): { month: string; day: string; year: string } {
  // Determine the separator used in the date string
  const separators = ['/', '-', '.', ' '];
  let separator = '';

  for (const sep of separators) {
    if (dateString.includes(sep)) {
      separator = sep;
      break;
    }
  }

  if (!separator) {
    throw new Error(`No valid separator found in date string: ${dateString}`);
  }

  // Split both the date string and format by the separator
  const dateParts = dateString.split(separator);
  const formatParts = format.split(separator);

  if (dateParts.length !== formatParts.length || dateParts.length !== 3) {
    throw new Error(`Date format mismatch. Expected ${format}, got: ${dateString}`);
  }

  let month = '';
  let day = '';
  let year = '';

  // Map parts based on format pattern
  for (let i = 0; i < formatParts.length; i++) {
    const formatPart = formatParts[i].toUpperCase();
    const datePart = dateParts[i];

    if (formatPart.includes('MM')) {
      month = datePart.padStart(2, '0');
    } else if (formatPart.includes('DD')) {
      day = datePart.padStart(2, '0');
    } else if (formatPart.includes('YYYY') || formatPart.includes('YY')) {
      year = datePart;
      // Convert 2-digit year to 4-digit if needed
      if (year.length === 2) {
        const currentYear = new Date().getFullYear();
        const century = Math.floor(currentYear / 100) * 100;
        year = String(century + parseInt(year, 10));
      }
    }
  }

  return { month, day, year };
}

/**
 * Formats date components into the specified target format
 * @param month - Month component (1-12)
 * @param day - Day component (1-31)
 * @param year - Year component (4 digits)
 * @param targetFormat - Target format pattern
 * @returns Formatted date string
 */
function formatDateString(month: string, day: string, year: string, targetFormat: string): string {
  // Ensure components are properly padded
  const paddedMonth = month.padStart(2, '0');
  const paddedDay = day.padStart(2, '0');

  // Replace format tokens with actual values
  return targetFormat
    .replace(/YYYY/g, year)
    .replace(/YY/g, year.slice(-2))
    .replace(/MM/g, paddedMonth)
    .replace(/DD/g, paddedDay)
    .replace(/M/g, month.replace(/^0+/, '') || '0') // Remove leading zeros for single digit
    .replace(/D/g, day.replace(/^0+/, '') || '0'); // Remove leading zeros for single digit
}
