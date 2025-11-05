import * as fs from 'fs';
import * as path from 'path';
import { fileURLToPath } from 'node:url';
import { log } from 'utils/logger.js';
import { SampleTestData, TestData } from './DataTypes';

// ESM equivalent of __dirname
const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);

export type EnvType = 'dev' | 'qa' | 'stage';

/**
 * Test Data Factory - Centralized way to manage test data across different environments
 */
export class TestDataFactory {
  /**
   * Get test data for the current environment (from environment variable or default to 'dev')
   */
  static getTestData(testDataFileName: string): TestData {
    // APP_ENV is not set then default to 'dev'
    const envValue = process.env.APP_ENV ?? 'dev';
    const testEnv: EnvType = this.validateEnvironment(envValue.toLowerCase());
    log.debug(`Creating test data for test ${testDataFileName} in environment: ${testEnv}`);
    //return createTestDataForEnvironment(testEnv.toLowerCase(), testDataFileName);
    return loadTestDataFromFile(testEnv, testDataFileName);
  }

  /**
   * Validates that the environment value is a valid EnvType
   * If not 'dev' or 'qa' or 'stage', defaults to 'dev'
   * @param env - Environment string to validate
   * @returns Validated EnvType
   */
  private static validateEnvironment(env: string): EnvType {
    const validEnvs: EnvType[] = ['dev', 'qa', 'stage'];
    if (validEnvs.includes(env as EnvType)) {
      return env as EnvType;
    }
    log.warn(`Invalid environment '${env}', defaulting to 'dev'`);
    return 'dev';
  }
}

/**
 * Factory function to create TestData from a JSON file
 * @param testEnv - Environment name (dev, qa, stage, etc.)
 * @param testDataFileName - Name of the JSON file (relative to data directory)
 * @returns TestData instance
 */
function loadTestDataFromFile(testEnv: string, testDataFileName: string): TestData {
  const jsonPath = path.join(path.dirname(__dirname), 'testdata', testEnv, testDataFileName);
  log.debug(`Looking for test data file at: ${jsonPath}`);
  if (!fs.existsSync(jsonPath)) {
    throw new Error(`Test data file not found: ${jsonPath}`);
  }
  const rawData = fs.readFileSync(jsonPath, 'utf-8');
  const parsedData = JSON.parse(rawData);

  return new TestData(
    // Add other project specific test data classes as needed based on the testcase requirements.
    new SampleTestData(parsedData.sampleKey, parsedData.sampleValue, parsedData.sampleList),
  );
}

// Export for backward compatibility and convenience
export default TestDataFactory;
