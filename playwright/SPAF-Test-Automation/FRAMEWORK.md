# Framework README

## Framework files

> **WARNING**: Do not modify any of the following files.

**package.json** : Describes the project metadata, dependencies, version, scripts, and other required configurations. It lists the packages and their versions being used in the project, and categorizes packages based on their usage, such as dependencies for production and devDependencies for the development environment. Versioning follows the [semantic versioning spec](https://docs.npmjs.com/about-semantic-versioning).

**package-lock.json** : An auto-generated file that provides a detailed, deterministic record of the dependency tree. It locks down the specific versions of every installed package, preventing unintended updates and ensures consistency, stability, and reproducibility in your project's dependency management.

**tsconfig.json** : Indicates the root of TypeScript project. It includes set of options that control how the TypeScript compiler behaves when it transpiles TypeScript code to JavaScript. More details in [config reference](https://www.typescriptlang.org/tsconfig/).

**.prettierrc** : Allows developers to customize how Prettier formats their code, ensuring consistency across a project. More on [prettier configuration](https://prettier.io/docs/configuration).

**.nvmrc** : Instruct NVM to switch to the appropriate Node version on navigating to that directory. Ensures version consistency in a collaborative project environment. It also serves as easy look up to the node version supported in project.

**.editorconfig** : file contains a list of rules that can be applied to any IDE's or code editors for proper formatting of code. More on [EditorConfig](https://editorconfig.org/).

---

## Custom ESLint Rules

Key enforcements rules added:

- No arbitrary timeouts (`playwright/no-wait-for-timeout`)
- Warn on console usage

---

## Framework folder structure

```
| Folder              | Purpose                               | Notes                                |
| ------------------- | ------------------------------------- | ------------------------------------ |
| `.github/workflows` | CI pipelines (smoke PR, nightly full) | Chromium on PR; all 3 nightly        |
| `config`            | Env loader + per-env JSON             | Selected via `APP_ENV` (dev/qa/stage) |
| `pages`             | Page-model classes (one per screen)   | Use `data-testid` locators only      |
| `components`        | Reusable UI widgets                   | Used by page models & workflows      |
| `workflows`         | Business flows across pages           | Eg. loginAndCheckout()               |
| `fixtures`          | Playwright fixtures & context helpers | Auth storage, seeded users           |
| `data`              | Static JSON + factories               | Test users, orders, etc.             |
| `tests`             | Specs + global setup/teardown         | Tag smoke vs regression              |
| `docs`              | Project documentation                 |                                      |
| `.husky`            | Pre-commit lint/format hook           | Runs lint-staged                     |
```

---

---
