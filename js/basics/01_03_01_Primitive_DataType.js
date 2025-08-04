console.log("Primitive Data Types:");
console.log("=====================");

/* Primitive data types are the most basic data types in JavaScript.
 * They are immutable (cannot be changed) and stored by value in memory.
 Passed by value : When you assign a primitive value to a variable, the variable holds the actual value, not a reference to it.
 Primitive value is immutable: Cannot be changed. However, can re-assign a new primitive value. */

// Summary of all primitive types
console.log("\n=== Primitive data types include: ===");
console.log("1. Number - Integers and Floating-point numbers");
console.log("2. String - Text data");
console.log("3. Boolean - true or false");
console.log("4. Undefined - Declared but not assigned");
console.log("5. Null - Intentional absence of value");
console.log("6. BigInt - Large integers");

// typeof is used to determine the type of a variable or value in JavaScript.
console.log("\n=== TYPE CHECKING ===");
console.log("typeof 42:", typeof 42);
console.log("typeof 'hello':", typeof "hello");
console.log("typeof true:", typeof true);
console.log("typeof undefined:", typeof undefined);
console.log("typeof null:", typeof null); // Returns "object" - this is a known bug in JavaScript
console.log("typeof Symbol():", typeof Symbol());
console.log("typeof 123n:", typeof 123n);

// 1. NUMBER - Represents both Integers and Floating-point numbers
/* Numbers in JavaScript can be integers or floating-point numbers.
 Integers are whole numbers, while floating-point numbers can have decimal points.

 Infinity and NaN are special numeric values: 
    NaN (Not a Number) is used to represent a value that is not a legal number, such as the result of an invalid mathematical operation.
    Infinity represents a value that is larger than any finite number, often resulting from division by zero or overflow in calculations. */
let age = 25;
let price = 99.99;
let negative = -42;
let infinity = Infinity;
let notANumber = NaN;
console.log("\nNumber examples:");
console.log("----------------");
console.log("age:", age, typeof age);
console.log("price:", price, typeof price);
console.log("negative:", negative, typeof negative);
console.log("infinity:", infinity, typeof infinity);
console.log("notANumber:", notANumber, typeof notANumber);

// ---------------------------------------------------------------------------------------------------------------------------------------

// 2. STRING - Represents text data
/* Strings can be defined using single quotes, double quotes, or backticks (template literals)
 Template literals allow for multi-line strings and string interpolation (ES6 feature)  */
let usrName = "John";
let greeting = "Hello World";
let template = `Welcome ${usrName}!`;
let emptyString = ""; // Empty string is a string with no characters
console.log("\nString examples:");
console.log("------------------");
console.log("name:", usrName, typeof usrName);
console.log("greeting:", greeting, typeof greeting);
console.log("template:", template, typeof template);
console.log("emptyString:", emptyString, typeof emptyString);

// ---------------------------------------------------------------------------------------------------------------------------------------

// 3. BOOLEAN - Represents true or false
let isActive = true;
let isComplete = false;
console.log("\nBoolean examples:");
console.log("-------------------");
console.log("isActive:", isActive, typeof isActive);
console.log("isComplete:", isComplete, typeof isComplete);

// ---------------------------------------------------------------------------------------------------------------------------------------

// 4. UNDEFINED - Represents a variable that has been declared but not assigned.
/* It is a type of variable that has not been initialized or assigned a value.
 It is different from null, which represents an intentional absence of value.
 Undefined is the default value for variables that have been declared but not assigned a value. 
 Variables can be explicitly set to undefined or left uninitialized.  */
let undefinedVar;
let explicitUndefined = undefined;
console.log("\nUndefined examples:");
console.log("---------------------");
console.log("undefinedVar:", undefinedVar, typeof undefinedVar);
console.log("explicitUndefined:", explicitUndefined, typeof explicitUndefined);

// ---------------------------------------------------------------------------------------------------------------------------------------

// 5. NULL - represents intentional absence of value
let nullValue = null;
console.log("\nNull examples:");
console.log("----------------");
console.log("nullValue:", nullValue, typeof nullValue); // Note: typeof null returns "object" (it is strange javascript behavior!)

// ---------------------------------------------------------------------------------------------------------------------------------------

// 6. BIGINT - Represents integers with arbitrary precision (ES2020+)
/* BigInt allows for representation of integers larger than the Number.MAX_SAFE_INTEGER (2^53 - 1).
 BigInt is useful for working with large integers that exceed the safe range of the Number type */

//  BigInt can be created
//      by appending 'n' to an integer literal or
//      using the BigInt() constructor.
let bigInt1 = 123456789012345678901234567890n;
let bigInt2 = BigInt("123456789012345678901234567890");
console.log("\nBigInt examples:");
console.log("------------------");
console.log("bigInt1:", bigInt1, typeof bigInt1);
console.log("bigInt2:", bigInt2, typeof bigInt2);

// ---------------------------------------------------------------------------------------------------------------------------------------
