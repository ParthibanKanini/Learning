import { randomInt } from 'crypto';

/**
 * Generates a random string with optional prefix and date suffix.
 * The generated string follows the pattern: [prefix][separator][4-char-alphanumeric][separator][DDMMYYYY]
 *
 * @param prefix - Optional prefix to prepend to the generated string
 * @param separator - Character(s) to use as separator between components (default: '_')
 * @returns Promise resolving to a formatted random string
 *
 * @example
 * ```typescript
 * // Generate with prefix: "TEST_A1B2_05112025"
 * const result = await generateRandString('TEST');
 *
 * // Generate without prefix: "A1B2_05112025"
 * const result = await generateRandString();
 *
 * // Generate with custom separator: "TEST-A1B2-05112025"
 * const result = await generateRandString('TEST', '-');
 * ```
 */
export async function generateRandString(
  prefix?: string,
  separator: string = '_',
): Promise<string> {
  return `${prefix ? prefix + separator : ''}${await alphaNumericString(4)}${separator}${await formatDate(new Date())}`;
}

/**
 * Selects a random element from the provided array.
 *
 * @param options - Array of elements to choose from
 * @returns Promise resolving to a randomly selected element from the options array
 *
 * @example
 * ```typescript
 * const colors = ['red', 'blue', 'green', 'yellow'];
 * const randomColor = await getRandomOption(colors); // e.g., 'blue'
 *
 * const numbers = [1, 2, 3, 4, 5];
 * const randomNumber = await getRandomOption(numbers); // e.g., 3
 * ```
 */
export async function getRandomOption<T>(options: T[]): Promise<T> {
  return options[Math.floor(Math.random() * options.length)];
}

/**
 * Selects multiple random elements from the provided array without replacement.
 * The returned array will contain unique elements in random order.
 *
 * @param options - Array of elements to choose from
 * @param count - Number of elements to select (should not exceed options.length)
 * @returns Promise resolving to an array of randomly selected elements
 *
 * @example
 * ```typescript
 * const fruits = ['apple', 'banana', 'cherry', 'date', 'elderberry'];
 * const randomFruits = await getRandomOptions(fruits, 3); // e.g., ['cherry', 'apple', 'elderberry']
 *
 * const testUsers = ['user1', 'user2', 'user3', 'user4'];
 * const selectedUsers = await getRandomOptions(testUsers, 2); // e.g., ['user3', 'user1']
 * ```
 */
export async function getRandomOptions<T>(options: T[], count: number): Promise<T[]> {
  const shuffled = [...options].sort(() => 0.5 - Math.random());
  return shuffled.slice(0, count);
}

async function alphaNumericString(length: number): Promise<string> {
  const chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789';
  let result = '';
  for (let i = 0; i < length; i++) {
    const randomIndex = randomInt(0, chars.length);
    result += chars[randomIndex];
  }
  return result;
}

async function formatDate(date: Date): Promise<string> {
  const day = date.getDate().toString().padStart(2, '0');
  const month = (date.getMonth() + 1).toString().padStart(2, '0');
  const year = date.getFullYear().toString();
  return `${day}${month}${year}`;
}
