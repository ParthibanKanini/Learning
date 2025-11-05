# **SPAF** (**S**mart **P**laywright **A**utomation **F**ramework)

---

## About the Automation Test Framework

This is a **production-ready, enterprise-grade** Playwright automation framework built for **Web based application test automation**. The framework implements sophisticated architectural patterns for scalable, maintainable test automation. It includes tools that will enable a quick start to the test case automation journey.

Playwright + TypeScript test automation framework for rapid kick-starter of QA automation with built-in support for Reporting, Logging, multi-environment support(dev|qa|stage), Test tagging (Sanity|Smoke|Regression) and many more features.

Framework also enforces best practices for engineers for Test case defintion and their structuring by laying out rules for aligning to design priciples like SRP & DRY.

Framework also incorporates tools to ensures code quality, consistency, readability and maintainability.

Version 0.0.1 is focusing primarly for Test Engineers and stakholders. Upcomming releases to focus on Seamless CI-CD Integration, co-pilot support, etc..

### **Domain Context**

The framework is specifically designed for **Web application workflows**.

-

### Test Framework Design Principles

- **Scalable** Growing testscript to expand coverage of application should not slow down performance
  - Targetted execution for Smoke, Sanity & Regression
  - Ability for parallel execution
- **Maintainable** Easy updates and modifications to test scripts, data and configurations
  - use ESLint, Prettier,
- **Reusable** Reuse tests and components to save time and effort.
  - Seperation of concerns with designated folders for test layers
- **Ease of Use** Users with different level of expertice should be able to use and maintain it
  - Test commands for specific executions
  - Sample test data are configurable in json files

---

## Getting Started

### Setup Dependencies

#### Recommended development setup(Pre-requisites):

- Node version v22.x.x or higher\*. Check with command `node -v`
- Node Package Manager version 10.x.x or higher\*. Check with command `npm-version`
- IDE: Visual Studio Code version 1.101 or higher\*
- git version 2.x.x or higher\*

Disclaimer: \*versions are recommended not fully tested

#### One-time code setup

Following instructions allow the code setup(one-time) in local environment for test case development.

```bash
# uses Node version in .nvmrc The command can fail if nvm package is not present
nvm use
# install all required dependencies
npm ci

# install browsers required for Playwright testing
npm run install:browsers

# create a local secrets file
cp .env.example .env.local
```

Framework supports `dev`, `qa`, `stage` environments. It is select from `APP_ENV=<env>` environment variable.
Environment and Credentials are override via `.env.local` file. Local development/test secrets go in (git-ignored) `.env.local` file.

Following instructions allow the clean up local setup. Typically used when any library dependancy issues or upgrading package versions or re-install project dependancies.

```bash
npm run clean:install
```

Following instructions execute Project Test cases

```bash
# Executes smoke test suite without launching browser
npm run test:smoke
# Executes smoke test suite in headed mode
npm run test:smoke:headed
# Executes smoke test suite in headed debug mode
npm run test:smoke:debug
```

Following insruction Launches a html report in browser. All test cases shall include video evidence and detailed trace of errors. When report review is complete, kill the report with `ctrl + c` in the terminal.

```bash
`npm run report`

```

Following instructions allow Verification of the project code base

```bash
# Check project dependencies & its transitive dependencies for known scruity vulnerabilities
npm audit

# Verify types defined in project and report errors(if any)
npm run code:typecheck

# Performs quality checks like code linting fixes, prettier & type checking
npm run code:quality


```

Following instructions help to check the framework dependencies versions

```bash
npm outdated # check the current state of dependencies

node --version; npm --version # check the current Node.js and npm versions

```

#### .env.local configuration

Edit .env.local to setup environment specific configuration like credintials & secrets. This file will be ignored by framework for git changes.

```properties
APP_ENV # test environent (dev | qa | stage)
APP_EMAIL # test user login email
APP_PWD # base 64 encoded test user login password
CONSOLE_LOGGING # true, by default. outputs logs to console.
FILE_LOGGING # false, by default. allows log to output in a file directory.
```

#### How to execute tests?

- Refer to section [Project Test execution](#project-test-execution)

---

## Test Project Folder Structure

Follow this defined folder structure for test case definition to ensure Scalability & Maintainablity of any growing project features test coverage.

```
└── .github
└── .husky
└── .playwright-mcp
└── .vscode
├── config/         # Environment configuration
├── data/           # Test data representation Types used by test cases
├── fixtures/       # Test fixture is a fixed state of a set of objects used as a base for running tests.
├── pages/          # Page Objects (reusable UI abstractions)
├── prd/            # Product Requirements Documents of the application feature to be automated.
├── testdata/       # Test data and factories
├── tests/          # Test specifications for project
├── trash/          # Recycle bin for unsed/unwanted files
├── utils/          # Utilities used across project
└── workflows/      # Business logic flows that could touch across project screens. Orchistrates flows that navigate across pages.
```

### Detailed Test code Layer Descriptions

- **`/pages/`** - Page Object Models (POMs) following a consistent architectural pattern:
  - Represent each screen/page in the application. Encapsulate page-specific interactions and locators. Expose business-intent methods rather than raw Playwright actions. Pages use Playwright's typed `Page` object. Reusable UI infrastructure. Adviced to have one class per top-level screen/route.
  - All extend `BasePage` abstract class for common functionality
  - Use centralized `locators` getter pattern for element selectors
  - All page interactions and assertions should be within POM files
  - Implement `assertPageLoaded()` validation method for sucessful page load after navigation
  - Follow business-intent method naming (e.g., `login()`, `createEngagement()`)
  - Include contextual logging with `pageLogger` instance
  - Project UI changes should only require page object updates.
  - Multiple tests can use the same page methods.
  - Tests focus on business logic. Expose _business intent_ (`login()`, `searchFor()`)

- **`/workflows/`** - Business Logic Orchestration Layer:
  - Class-based workflows that encapsulate complete user journeys
  - Use dependency injection pattern for page objects
  - Implement structured logging with `createWorkflowLogger()`
  - Follow `test.step()` pattern for granular reporting of related operation
  - Handle error scenarios with try-catch blocks and descriptive logging

- **`/fixtures/`** - Test Setup and Page Initialization:
  - Extend Playwright's base test with custom fixtures
  - Pre-initialized page objects with navigation and validation
  - Eliminates repetitive setup code in test files
  - Supports both authenticated and non-authenticated scenarios

- **`/testdata/`** - Structured Test Data Management:
  - Static JSON seed data within Environment-specific(dev|qa|stage) folders
  - Supports data-driven testing with consistent structure

- **`/data/`** - Structured Test Data Management:
  - TypeScript interfaces for type safety (`TestDataTypes.ts`)
  - Factory pattern implementation (`TestData.factory.ts`)

- **`/utils/`** - Cross-cutting Concerns and Utilities:
  - Centralized wait strategies and UI interaction helpers
  - Popup handling utilities (cookies, disclaimers)
  - File path utilities and screenshot functions
  - Defensive coding patterns for robust element interactions

- **`/tests/`** - Cross-cutting Concerns and Utilities:
  - Test specifications (.spec.ts files) defines `What to test?`
  -

---

## Use Test tagging:

Any tests description or test can have one or more test tags to categorize them.

- `${TAG_SMOKE} ` quick checks (run on PR). High-level check to ensure a build stability. Fast, stable, minimal dependencies. Gate pull requests.
- `${TAG_SANITY} ` focused verification after a new feature addition.
- `${TAG_REG} ` broader coverage (nightly).
- `${TAG_P1} ` Priority 1 test cases.
- `${TAG_P2} ` Priority 2 test cases.

---

## Framework Architectural Patterns

### **Page Object Model (POM) Architecture**

All page objects follow a consistent architectural pattern:

```typescript
// BasePage Pattern - All page objects extend this abstract class
export abstract class BasePage {
  protected page: Page;
  protected pageLogger: ReturnType<typeof createPageLogger>;

  constructor(protected readonly currentPage: Page) {
    this.page = currentPage;
    this.pageLogger = createPageLogger(this.constructor.name);
  }
}

// Centralized Locators Pattern - Single source of truth for element selectors
private get locators() {
  return {
    loginButton: this.page.getByRole('button', { name: 'Login' }),
    emailInput: this.page.getByTestId('email-input'),
    // ... other locators
  } as const;
}

// Business Intent Methods - Express what the user wants to accomplish
async login(user: TestUser): Promise<void> {
  await this.locators.emailInput.fill(user.email);
  await this.locators.passwordInput.fill(user.password);
  await this.locators.loginButton.click();
}
```

#### POM files Locator Strategy guidelines

✅ Recommended - data-testid (stable)

```typescript
await this.page.getByTestId('login-button');
```

⚠️ Acceptable - semantic locators

```typescript
await this.page.getByRole('button', { name: 'Login' });
```

❌ Avoid - CSS/XPath selectors (brittle)

```typescript
await this.page.locator('#login-btn');
await this.page.locator('xpath=//button[@class="submit"]');
```

#### POM files Test Assertions best practices

❌ Wrong - test assertions in page objects

```typescript
async login(user: TestUser): Promise<void> {
  await this.usernameInput.fill(user.username);
  await this.passwordInput.fill(user.password);
  await this.loginButton.click();

  //Don't do this in page objects
  expect(this.page.url()).toContain('/dashboard');
}
```

✅ Correct - separate validation method

```typescript
async login(user: TestUser): Promise<void> {
  await this.usernameInput.fill(user.username);
  await this.passwordInput.fill(user.password);
  await this.loginButton.click();
}

async assertLoginSuccess(): Promise<void> {
  await expect(this.page.getByText('Welcome')).toBeVisible();
}
```

### **Workflow Orchestration Pattern**

Business workflows coordinate multiple page interactions:
Covers the Higher-level business flows that users would perform in the application that can span multiple pages to complete real user journeys.

Workflows represent complete end-to-end scenarios. Business logic that orchestrates the infrastructure.

Reusable business logic functions that orchestrate multiple actions to accomplish business tasks.It can be called by tests(`What to do?`) or other workflows (`How to do things?`)

#### Purpose

Workflows are reusable functions that:

- **Combine multiple page interactions** into meaningful business flows
- **Reduce test code duplication** by providing common user journeys
- **Make tests more readable** by abstracting complex multi-step processes
- **Improve maintainability** by centralizing business logic

#### Examples

- `loginAndLand` - Complete login process and navigation to dashboard
- `registerNewUser` - Full user registration workflow
- `completeShoppingJourney` - End-to-end shopping from login to order completion
- `submitContactForm` - Contact form submission process
- `updateUserProfile` - User account/profile update process

#### Best Practices

1. **Keep workflows pure**: Accept dependencies (pages, data) as parameters
2. **Return meaningful results**: Return order numbers, user data, success indicators
3. **Use TypeScript interfaces**: Define clear input/output types
4. **Handle assertions within workflows**: Verify critical success states
5. **Make workflows composable**: Allow workflows to call other workflows
6. **Include error handling**: Handle expected failure scenarios gracefully

```typescript
export class SustainabilityReportingWorkflow {
  private wfLogger = createWorkflowLogger(SustainabilityReportingWorkflow.name);

  constructor(
    private engagementPage: EngagementPage,
    private page: Page,
  ) {
    // Dependency injection of page objects
    this.checklistPage = new ChecklistPage(page);
    this.scopingPage = new SustainabilityReportingScopingPage(page);
  }

  async sustainabilityReportingWorkFlow(): Promise<void> {
    await test.step('Create Engagement', async () => {
      const engagementName = await this.engagementPage.createNewEngagement(testData.engagement);
      this.wfLogger.info(`Engagement created: ${engagementName}`);
    });

    await test.step('Create Checklist', async () => {
      // Orchestrate multiple page interactions
    });
  }
}
```

### **Test Data Factory Pattern**

Environment-specific data management with type safety:

```typescript
// Type-safe data structures
export class EngagementTestData {
  constructor(
    public namePrefix: string,
    public serviceType: string,
    public engagementType: string,
    // ...
  ) {}
}

// Factory pattern for environment-specific data
export class TestDataFactory {
  static getTestData(testcaseName: string): TestData {
    const environment = process.env.APP_ENV ?? 'dev';
    return createTestDataForEnvironment(testcaseName, environment);
  }
}

// Usage in workflows
private testData: TestData = TestDataFactory.getTestData('sustainability.reporting');
```

## Log Contexts

Supports structured, contextual logging that can scale well with large test suites.

Each logger includes contextual information that helps identify where the log message originated. It allows enabling granular log levels for different components. Easy debugging to filter logs to interested sections.

- **General logs** General project logging
- **Test loggers** Test case execution logging
- **Workflow loggers** Business process orchestration layer logging
- **Page loggers** UI interaction layer logging
- **Component loggers** Individual UI component logging

### Granular Log Level Control

Control logging levels for different sections using environment variables:

```bash
# Global log level (affects all loggers)
LOG_LEVEL=info

# Section-specific levels (override global for that section)
LOG_LEVEL_TEST=debug      # Detailed test execution logs
LOG_LEVEL_PAGE=info       # Standard page interaction logs
LOG_LEVEL_WORKFLOW=warn   # Only warnings for workflows
LOG_LEVEL_COMPONENT=error # Only errors for components
```

## Log Levels

- **trace**: Very detailed information, typically only of interest when diagnosing problems
- **debug**: Detailed information on the flow through the system
- **info**: Interesting runtime events (startup/shutdown)
- **warn**: Use of deprecated APIs, poor use of API, 'almost' errors
- **error**: Runtime errors or unexpected conditions
- **fatal**: Very severe error events that will presumably lead the application to abort

### Basic Usage

```typescript
import { log, logger } from '../utils/logger';

// General logs with different levels
log.info('Application started');
log.warn('This is a warning message');
log.error('An error occurred', { errorCode: 500 });
log.debug('Debug information', { userId: 123, action: 'login' });
```

### **Contextual Logging Architecture**

Layer-specific loggers provide structured, filterable logs:

```typescript
// Test-level logging
const testLogger = createTestLogger('sustainability-reporting-test');

// Workflow-level logging
const wfLogger = createWorkflowLogger('SustainabilityReportingWorkflow');
wfLogger.info('Starting user registration and shopping workflow');

// Page-level logging (automatic in BasePage)
this.pageLogger = createPageLogger(this.constructor.name);

// Granular control via environment variables
LOG_LEVEL_TEST=debug      # Detailed test execution logs
LOG_LEVEL_WORKFLOW=info   # Standard workflow logs
LOG_LEVEL_PAGE=warn       # Only warnings for page interactions
```

---

## Coding Standards to follow

- **Strict TypeScript** to avoid unpredicted code behavour and better IDE support.
- **async/await everywhere** Readable & Managiable way of asynchronious coding. This avoids test flakyness by avoiding waitTime.
- **One assertion of page load per page model** Pattern ensures Playwright tests are more reliable, maintainable, and easier to debug when page loading issues occur.
- **Prefer `data-testid` selectors** Request developer to add it in project code for all missing components/elements.
- **No `waitForTimeout( )`** This avoid flakiness in the test results.
- **Centralized locators pattern** All locators defined in a single `locators` getter for maintainability.
- **Business-intent methods** Page objects expose business-focused methods rather than technical UI actions.
- **Dependency Injection** Workflows receive page objects as constructor parameters for better testability.
- **Factory Pattern** Test data created through factory pattern for environment-specific configurations.
- **Fixture-based Setup** Pre-initialized page objects eliminate repetitive setup code in tests.

---
