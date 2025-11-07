import { Page } from '@playwright/test';

// TODO: Import all POM classes as needed for the project
import LoginPage from './Login.page.js';
import HomePage from './Home.page.js';

/**
 * PageFactory - Centralized factory for creating Page Object Model instances
 *
 * This factory provides a consistent way to instantiate POM classes with the Playwright page object.
 * Benefits:
 * - Centralized POM instantiation
 * - Type-safe factory methods
 * - Easy to maintain and extend
 * - Consistent pattern across tests
 * - Instance caching for better performance and shared state
 */
export class PageFactory {
  private readonly pageInstances = new Map<string, unknown>();

  constructor(private readonly page: Page) {}

  /**
   * Gets the underlying Playwright Page object
   * Provides access to the page instance for advanced operations
   * @returns The Playwright Page object
   */
  // Utility method to access the underlying page object
  getPage(): Page {
    return this.page;
  }

  /**
   * Generic method to get or create a page instance with caching
   * This ensures the same page instance is reused throughout the test
   * @param key - Unique identifier for the page instance
   * @param factory - Function that creates the page instance
   * @returns Cached or newly created page instance
   * @template T - The type of the page object being created
   */
  private getOrCreatePage<T>(key: string, factory: () => T): T {
    if (!this.pageInstances.has(key)) {
      this.pageInstances.set(key, factory());
    }
    return this.pageInstances.get(key) as T;
  }

  /**
   * Creates or retrieves a cached LoginPage instance
   * @returns LoginPage instance for authentication operations
   */
  getLoginPage(): LoginPage {
    return this.getOrCreatePage('login', () => new LoginPage(this.page));
  }

  getHomePage(): HomePage {
    return this.getOrCreatePage('home', () => new HomePage(this.page));
  }

  // Convenience method to get multiple commonly used pages
  // Method to get pages by grouping - extend as needed

  /**
   * Gets a collection of commonly used page instances
   * Provides convenient access to frequently needed pages in a single call
   * @returns Object containing common page instances (login, dashboard, etc.)
   */
  getCommonPages() {
    return {
      login: this.getLoginPage(),
      home: this.getHomePage(),
      //dashboard: this.getDashboardPage(),
    };
  }

  /**
   * Clears all cached page instances for test isolation
   * This should rarely be needed as fixtures handle test isolation automatically
   * Use this method when you need to force recreation of page instances
   * @returns void
   */
  clearCache(): void {
    this.pageInstances.clear();
  }

  /**
   * Checks if a specific page type is already cached
   * Useful for debugging or conditional page creation logic
   * @param pageType - The key/identifier for the page type to check
   * @returns true if the page instance is cached, false otherwise
   */
  isCached(pageType: string): boolean {
    return this.pageInstances.has(pageType);
  }
}

/**
 * Static factory method for convenient PageFactory creation
 * Provides a functional approach to creating PageFactory instances
 * @param page - The Playwright Page object to use for page creation
 * @returns A new PageFactory instance
 */
// Static factory method for convenience
export function createPageFactory(page: Page): PageFactory {
  return new PageFactory(page);
}

// Export type for TypeScript intellisense
export type PageFactoryType = PageFactory;
