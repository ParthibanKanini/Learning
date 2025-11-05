import js from '@eslint/js';
import typescript from '@typescript-eslint/eslint-plugin';
import typescriptParser from '@typescript-eslint/parser';
import playwright from 'eslint-plugin-playwright';
import prettier from 'eslint-plugin-prettier';
import prettierConfig from 'eslint-config-prettier';

export default [
  // Apply to all TypeScript and JavaScript files
  {
    files: ['**/*.{ts,js,mjs,cjs}'],
    ignores: [
      'node_modules/**',
      'dist/**',
      'reports/**',
      'allure-results/**',
      'trash/**',
      'coverage/**',
      '.eslintrc.cjs', // Ignore old config file
      'eslint.config.js', // Ignore this config file
    ],
    languageOptions: {
      parser: typescriptParser,
      parserOptions: {
        project: ['./tsconfig.json'],
        tsconfigRootDir: process.cwd(),
        sourceType: 'module',
        ecmaVersion: 2022,
      },
      globals: {
        // Node.js globals
        process: 'readonly',
        Buffer: 'readonly',
        console: 'readonly',
        setTimeout: 'readonly',
        clearTimeout: 'readonly',
        setInterval: 'readonly',
        clearInterval: 'readonly',
        // Browser globals for Playwright
        window: 'readonly',
        document: 'readonly',
      },
    },
    plugins: {
      '@typescript-eslint': typescript,
      playwright: playwright,
      prettier: prettier,
    },
    rules: {
      // ESLint recommended rules
      ...js.configs.recommended.rules,

      // TypeScript recommended rules
      ...typescript.configs.recommended.rules,

      // Prettier integration
      ...prettierConfig.rules,

      // Custom rules
      'no-console': ['warn', { allow: ['warn', 'error'] }],
      'playwright/no-wait-for-timeout': 'error',
      'prettier/prettier': 'error',
      '@typescript-eslint/no-unused-vars': ['error', { argsIgnorePattern: '^_' }],
    },
  },

  // Test files and workflows - disable expect-expect
  {
    files: ['**/*.spec.ts', '**/workflows/**/*.ts'],
    rules: {
      'playwright/expect-expect': 'off',
    },
  },

  // Utility files - allow waitForTimeout for specific use cases
  {
    files: ['**/utils/**/*.ts'],
    rules: {
      'playwright/no-wait-for-timeout': 'warn', // Allow but warn
      '@typescript-eslint/no-unused-vars': ['error', { argsIgnorePattern: '^_|^error$' }],
    },
  },

  // Keep expect-expect rules active for POM files
  {
    files: ['**/pages/**/*.ts'],
    rules: {
      'playwright/expect-expect': 'error',
    },
  },

  // Special rules for configuration files
  {
    files: ['*.config.{ts,js}', 'eslint.config.js'],
    rules: {
      'no-console': 'off',
    },
  },
];
