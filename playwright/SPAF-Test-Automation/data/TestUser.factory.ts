import { log } from 'utils/logger.js';
import { appEnv } from '../config/env.js';
import { TestUser } from './DataTypes.js';

export type UserType = 'default_user' | 'nonexisting_user' | 'wrong_password_user' | 'register_new';

/**
 * Function to get a user object based on the provided test user type.
 * @param testUserType - The type of user to retrieve (default is 'default_user').
 * @returns A TestUser object with the specified type, username, and password.
 * @throws Error if the user type is unknown.
 * This function is used to retrieve user data for testing purposes, allowing for different user scenarios (e.g., valid, invalid, admin, etc.).
 * It can be extended to include more user types as needed.
 */
export function getUser(testUserType: UserType = 'default_user'): TestUser {
  // Use environment configuration for default login user
  log.info(`Using ${testUserType} from environment configuration `);
  // TODO: Extend support for additinal user types as needed
  if (testUserType !== 'default_user') {
    throw new Error(`Unknown test user type: ${testUserType}`);
  }
  return {
    type: 'default_user',
    email: appEnv.defaultUser.email,
    password: appEnv.defaultUser.password,
    username: appEnv.defaultUser.username ?? '',
  };
}
