---
description: 'Playwright test generation instructions'
applyTo: '**'
---

## Test Writing Guidelines

### Code Quality Standards

- **Code Quality Enforcement**: **MANDATORY** - All generated code MUST pass `npm run code:quality` checks before being considered complete. This includes ESLint, Prettier formatting, and TypeScript compilation. Generated code that fails these quality checks is not acceptable and must be corrected.
- **Page Object Model (POM) Usage**: **MANDATORY** - All locators MUST be defined in the Page Object Model's `locators` method. Test spec files MUST NOT contain direct `page.locator()`, `page.getByRole()`, or any other locator definitions. Always use POM methods to interact with elements (e.g., `valuationReconPage.clickExportButton()` instead of `page.getByRole('button', { name: /export/i }).click()`).
- **NO DIRECT PAGE ACCESS**: Test spec files MUST NOT access the `page` object directly. All page interactions must go through POM methods. This includes avoiding `page.keyboard.type()`, `page.waitForEvent()`, and any other direct page API calls.
- **POM EXTENSION REQUIRED**: If existing POM files lack required functionality, EXTEND them with new locators and methods rather than working around limitations in test files.
- **Cognitive Complexity**: Keep individual methods under 15 complexity points. Break complex logic into smaller private helper methods.
- **TypeScript Types**: Use proper TypeScript types and avoid `any`. Create type aliases for union types used in multiple places (e.g., `type ExportFormat = 'excel' | 'csv' | 'pdf'`).
- **Locators**: All locators must be defined in POM files using user-facing, role-based selectors (`getByRole`, `getByLabel`, `getByText`, etc.) for resilience and accessibility. Use `test.step()` to group interactions and improve test readability and reporting.
- **Assertions**: Use auto-retrying web-first assertions. These assertions start with the `await` keyword (e.g., `await expect(locator).toHaveText()`). Avoid `expect(locator).toBeVisible()` unless specifically testing for visibility changes. All assertions should be encapsulated in POM methods.
- **Timeouts**: Rely on Playwright's built-in auto-waiting mechanisms. Avoid hard-coded waits or increased default timeouts.
- **Clarity**: Use descriptive test and step titles that clearly state the intent. Add comments only to explain complex logic or non-obvious interactions.

### Test Structure

- **Imports**: Start with `import { test } from '../fixtures/login.fixtures.js';`.
- **Organization**: Group related tests for a feature under a `test.describe()` block.
- **Hooks**: Use `beforeEach` for setup actions common to all tests in a `describe` block (e.g., navigating to a page).
- **Titles**: Follow a clear naming convention, such as `Feature - Specific action or scenario`.

### File Organization

- **Location**: Store all test files in the `tests/` directory.
- **Naming**: Use the convention `<feature-or-page>.spec.ts` (e.g., `login.spec.ts`, `search.spec.ts`).
- **Scope**: Aim for one test file per major application feature or page.

### Assertion Best Practices

- **UI Structure**: Use `toMatchAriaSnapshot` to verify the accessibility tree structure of a component. This provides a comprehensive and accessible snapshot.
- **Element Counts**: Use `toHaveCount` to assert the number of elements found by a locator.
- **Text Content**: Use `toHaveText` for exact text matches and `toContainText` for partial matches.
- **Navigation**: Use `toHaveURL` to verify the page URL after an action.
- **Visibility**: Use `toBeVisible` only when testing for visibility changes, not as a default assertion.
- **State Changes**: Use `toBeEnabled`, `toBeDisabled`, `toBeChecked`, and `toBeHidden` to assert specific element states.

### **CRITICAL: Page Object Model Requirements**

- **NO DIRECT LOCATORS IN SPEC FILES**: Test specification files MUST NOT contain any direct locator definitions such as:
  - ❌ `page.getByRole('button', { name: /export/i })`
  - ❌ `page.locator('[data-testid="export-button"]')`
  - ❌ `page.getByText('Export Data')`
- **NO DIRECT PAGE ACCESS**: Test files MUST NOT access the `page` object directly:
  - ❌ `const page = valuationReconPage.page;`
  - ❌ `page.keyboard.type('text')`
  - ❌ `page.waitForEvent('download')`
  - ❌ `page.locator('selector').isVisible()`
- **USE POM METHODS ONLY**: All interactions must go through Page Object Model methods:
  - ✅ `valuationReconPage.clickExportButton()`
  - ✅ `valuationReconPage.assertExportDialogVisible()`
  - ✅ `valuationReconPage.selectExportFormat('Excel')`
  - ✅ `valuationReconPage.performCompleteExportFlow()`

- **POM EXTENSION STRATEGY**: When existing POM files lack required functionality:
  1. **EXTEND the POM file** with new locators in the `locators` getter
  2. **ADD business logic methods** for each user action
  3. **CREATE orchestration methods** for complex workflows
  4. **NEVER work around POM limitations** in test files

- **POM LOCATOR DEFINITION**: All locators must be defined in the POM's `locators` getter method and accessed through POM action/assertion methods.

- **POM METHOD PATTERNS**:

  **Basic Actions:**

  ```typescript
  // In POM file
  async clickExportButton(): Promise<void> {
    await this.locators.exportButton.first().click();
  }

  async assertExportDialogVisible(): Promise<void> {
    await expect(this.locators.exportDialog.first()).toBeVisible();
  }
  ```

  **Complex Workflows:**

  ```typescript
  // Orchestration method combining multiple actions
  async performCompleteExportFlow(
    dateFilter?: string,
    clientFilter?: string,
    format: ExportFormat = 'excel'
  ): Promise<{ success: boolean; fileName?: string }> {
    await this.applyFilters(dateFilter, clientFilter);
    await this.clickExportButton();
    await this.selectExportFormat(format);
    return this.generateAndDownloadExport();
  }
  ```

  **Error Handling and Resilience:**

  ```typescript
  private async isElementVisible(locator: LocatorType): Promise<boolean> {
    return locator.first().isVisible().catch(() => false);
  }
  ```

## Test Generation Process

### Pre-Generation Analysis

1. **Examine existing POM files** to understand current capabilities
2. **Identify missing functionality** required for the test scenario
3. **Plan POM extensions** before writing test code
4. **Design business logic methods** for complex workflows

### POM Extension Guidelines

- **Locator Strategy**: Use multiple selector fallbacks for robustness:
  ```typescript
  exportButton: this.page.locator(
    [
      'button:has-text("Export")',
      '[data-testid="export-button"]',
      'button[title*="export" i]',
      '.export-btn',
    ].join(', '),
  );
  ```
- **Type Safety**: Define custom types for parameters:
  ```typescript
  export type ExportFormat = 'excel' | 'csv' | 'pdf';
  ```
- **Complexity Management**: Break complex methods into smaller private helpers
- **Error Handling**: Implement graceful fallbacks for optional elements

### Test Structure Guidelines

- **Simplified Test Logic**: Use orchestration methods to reduce test complexity:
  ```typescript
  await test.step('Perform complete export flow', async () => {
    const result = await valuationReconPage.performCompleteExportFlow(
      '2024-10-01', // date filter
      '3M Company', // client filter
      'excel', // format
      true, // include headers
      true, // include metadata
    );
    expect(result.success).toBe(true);
  });
  ```

### Code Quality Requirements

- **Formatting**: All generated code must be properly formatted with Prettier
- **Linting**: Code must pass ESLint checks without errors
- **TypeScript**: Strict typing with no `any` types
- **Cognitive Complexity**: Individual methods under 15 complexity points
- **Documentation**: Clear method names and parameter documentation

## POM Extension Best Practices

### When to Extend POM Files

**ALWAYS extend POM files** when test scenarios require functionality not currently available. Never work around POM limitations in test files.

### Locator Definition Patterns

```typescript
protected get locators() {
  return {
    ...super.locators,
    // Basic elements
    exportButton: this.page.locator([
      'button:has-text("Export")',
      '[data-testid="export-button"]',
      'button[title*="export" i]',
      '.export-btn'
    ].join(', ')),

    // Dialog/Modal elements
    exportDialog: this.page.locator([
      '.export-dialog',
      '.export-modal',
      '[data-testid="export-dialog"]',
      '.ant-modal:has-text("Export")'
    ].join(', ')),

    // Form options with multiple fallbacks
    formatOptions: {
      excel: this.page.locator([
        'input[value="excel"]',
        'button:has-text("Excel")',
        '[data-testid="excel-format"]'
      ].join(', ')),
      csv: this.page.locator([
        'input[value="csv"]',
        'button:has-text("CSV")',
        '[data-testid="csv-format"]'
      ].join(', '))
    }
  } as const;
}
```

### Method Implementation Patterns

**Basic Action Methods:**

```typescript
async clickExportButton(): Promise<void> {
  await expect(this.locators.exportButton.first()).toBeVisible();
  await this.locators.exportButton.first().click();
  this.pageLogger.info('Export button clicked');
}
```

**Helper Methods for Complexity Reduction:**

```typescript
private async isElementVisible(locator: LocatorLike): Promise<boolean> {
  return locator.first().isVisible().catch(() => false);
}

private async selectOptionSafely(locator: LocatorLike, value: string): Promise<boolean> {
  const exists = await this.isElementVisible(locator);
  if (exists) {
    await locator.first().click();
    return true;
  }
  return false;
}
```

**Orchestration Methods:**

```typescript
async performCompleteWorkflow(
  param1?: string,
  param2?: WorkflowType,
  options: WorkflowOptions = {}
): Promise<WorkflowResult> {
  try {
    await this.stepOne(param1);
    await this.stepTwo(param2);
    const result = await this.stepThree(options);

    return { success: true, data: result };
  } catch (error) {
    this.pageLogger.error(`Workflow failed: ${String(error)}`);
    throw error;
  }
}
```

### Type Definition Requirements

```typescript
// Define type aliases for reusable union types
export type ExportFormat = 'excel' | 'csv' | 'pdf';
export type FilterType = 'date' | 'client' | 'amount';

// Define interfaces for complex return types
export interface ExportResult {
  success: boolean;
  fileName?: string;
  format?: ExportFormat;
  recordCount?: number;
}
```

## Test Simplification Patterns

### Orchestration Over Step-by-Step

**Instead of multiple test steps:**

```typescript
// ❌ AVOID: Multiple manual steps
await test.step('Click export button', async () => {
  await valuationReconPage.clickExportButton();
});
await test.step('Select format', async () => {
  await valuationReconPage.selectExportFormat('excel');
});
await test.step('Configure options', async () => {
  await valuationReconPage.configureExportOptions(true, true);
});
// ... more steps
```

**Use orchestration methods:**

```typescript
// ✅ PREFERRED: Single orchestration method
await test.step('Perform complete export flow', async () => {
  const result = await valuationReconPage.performCompleteExportFlow(
    '2024-10-01', // date filter
    '3M Company', // client filter
    'excel', // format
    true, // include headers
    true, // include metadata
  );
  expect(result.success).toBe(true);
});
```

### Page Factory Usage

Always instantiate page objects once and reuse:

```typescript
// ✅ CORRECT: Instantiate once, reuse throughout test
const dashboardPage = pageFactory.getDashboardPage();
const valuationReconPage = pageFactory.getModuProcValuationReconPage();

await test.step('Navigate', async () => {
  await dashboardPage.navigateTo('modularized procedures', 'valuation recon');
  await valuationReconPage.assertPageLoaded();
});

await test.step('Perform actions', async () => {
  const result = await valuationReconPage.performWorkflow();
  expect(result.success).toBe(true);
});
```

### Error Handling in Tests

```typescript
// Handle expected variations gracefully in POM
const result = await valuationReconPage.performCompleteExportFlow();
if (result.fileName) {
  logger.info(`Downloaded file: ${result.fileName}`);
} else {
  logger.info('Export completed without direct download');
}
expect(result.success).toBe(true);
```

## Quality Checklist

Before finalizing tests, ensure:

- [ ] **All generated code passes `npm run code:quality` checks (ESLint, Prettier, TypeScript)**
- [ ] **POM files have been extended with all required functionality**
- [ ] **NO direct locators exist in spec files - all interactions use POM methods**
- [ ] **NO direct page access in test files - all through POM methods**
- [ ] **POM files contain all locator definitions in the `locators` getter method**
- [ ] **Each user action has a corresponding POM method (click, fill, assert, etc.)**
- [ ] **Complex workflows have orchestration methods that combine multiple actions**
- [ ] **Type aliases are used for union types (e.g., ExportFormat)**
- [ ] **Cognitive complexity is under 15 for all methods**
- [ ] **Error handling and graceful fallbacks are implemented**
- [ ] **All locators use multiple selector strategies for robustness**
- [ ] Tests are grouped logically and follow a clear structure
- [ ] Assertions are meaningful and reflect user expectations
- [ ] Tests follow consistent naming conventions
- [ ] Code is properly formatted and commented

## Test Execution Strategy

1. **Initial Run**: Execute tests with `npx playwright test --project=chromium`
2. **Debug Failures**: Analyze test failures and identify root causes
3. **Iterate**: Refine locators, assertions, or test logic as needed
4. **Validate**: Ensure tests pass consistently and cover the intended functionality
5. **Report**: Provide feedback on test results and any issues discovered
