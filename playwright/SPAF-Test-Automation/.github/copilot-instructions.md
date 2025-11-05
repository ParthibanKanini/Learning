# GitHub Copilot Instructions - DNAV Playwright Framework

This is an enterprise-grade Playwright automation framework for **Deloitte Net Asset Value (DNAV)** workflows, implementing sophisticated architectural patterns for scalable E2E testing.

## Framework Architecture & Patterns

### Page Object Model (POM) Convention

All page objects **must** extend `BasePage` and follow this strict pattern:

```typescript
export class YourPage extends BasePage {
  constructor(page: Page) {
    super(page);
  }

  // Centralized locators pattern - single source of truth
  protected get locators() {
    return {
      ...super.locators, // Inherit base navigation locators
      yourElement: this.page.getByRole('button', { name: 'Submit' }),
    } as const;
  }

  // Business intent methods, not raw Playwright actions
  async submitForm(data: FormData): Promise<void> {
    this.pageLogger.info('Submitting form with validation');
    await this.locators.yourElement.click();
  }

  // Required implementation
  async assertPageLoaded(): Promise<void> {
    await expect(this.locators.mainHeading).toBeVisible();
    this.pageLogger.info('Page loaded successfully');
  }
}
```

### PageFactory Pattern

**Always** use `PageFactory` for page instantiation with instance caching:

```typescript
const dashboardPage = pageFactory.getDashboardPage(); // Cached instances
```

### Workflow Pattern for Complex Flows

Business logic spanning multiple pages goes in `/workflows/`:

```typescript
export class YourWorkflow {
  wfLogger = createWorkflowLogger(YourWorkflow.name);

  async complexBusinessFlow(): Promise<void> {
    await test.step('Step description', async () => {
      // Orchestrate multiple page interactions
    });
  }
}
```

## Test Organization & Tagging

### Test Structure

- Tests use custom fixtures from `/fixtures/login.fixtures.ts` for pre-authenticated pages
- All tests **must** include tags: `${TAG_SMOKE}`, `${TAG_SANITY}`, `${TAG_REG}`, `${TAG_P1}`, `${TAG_P2}`
- Use `test.step()` for granular reporting of complex operations

### Critical NPM Commands

```bash
# Test execution by category
npm run test:smoke:headed      # Smoke tests with browser UI
npm run test:sanity:debug      # Sanity tests with debugging
npm run test:regression        # Full regression suite

# Development workflow
npm run clean:install          # Clean dependency reinstall
npm run code:quality           # Lint, prettier, type checking
npm run install:browsers       # Playwright browser setup
```

## Environment & Configuration

### Multi-Environment Setup

- Environments: `dev|qa|stage` controlled by `APP_ENV` variable
- Configuration files: `/config/appEnv.{env}.json`
- Local overrides in `.env.local` (git-ignored)
- Environment loading via `/config/env.ts` with caching

### User Management

- Use `getUser('default_user')` from `/data/TestUser.factory.ts`
- Credentials loaded from environment configuration
- Test users defined with TypeScript interfaces

## Development Conventions

### Logging Strategy

- Page objects: `this.pageLogger` (auto-named by class)
- Workflows: `createWorkflowLogger(ClassName.name)`
- Test utilities: Standard `logger` import

### File Naming & Location Rules

- Page objects: `{Feature}.page.ts` in `/pages/`
- Workflows: `{Feature}.workflow.ts` in `/workflows/`
- Tests: `{feature}.spec.ts` in `/tests/`
- Test data: JSON files in `/testdata/{env}/`

### Critical Integration Points

- **Authentication**: Worker-scoped fixtures for performance (`/fixtures/login.fixtures.ts`)
- **Navigation**: Centralized in `BasePage.locators` for cross-page navigation
- **Reporting**: Allure, HTML, JUnit configured in `playwright.config.ts`
- **Error Handling**: Structured logging with try-catch in workflows

## Code Quality & Build

- **ESLint + Prettier**: Enforced via `npm run code:quality`
- **TypeScript**: Strict mode with path mappings (`@pages/`, `@workflows/`)
- **CI/CD**: GitHub Actions for smoke (PR) and regression (nightly)
- **Parallel Execution**: 2 workers locally, 4 in CI

## Key Files to Reference

- `/pages/Base.page.ts` - Page object foundation
- `/fixtures/login.fixtures.ts` - Authentication pattern
- `/config/env.ts` - Environment configuration
- `/pages/PageFactory.ts` - Page instantiation pattern
- `playwright.config.ts` - Test execution configuration
