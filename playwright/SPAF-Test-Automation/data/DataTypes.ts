// TestData and related class types for sustainable reporting

export class TestData {
  constructor(public sampleTestData: SampleTestData) {}
}

/**
 * Represents test users.
 * Contains properties for user type, username, and password.
 * This interface is used to define the structure of user objects in the test data.
 *
 */
export class TestUser {
  constructor(
    public type: string,
    public email: string,
    public password: string,
    public username: string,
  ) {}
}

export class SampleTestData {
  constructor(
    public testKey: string,
    public testValue: number,
    public testTags: string[],
  ) {}
}
