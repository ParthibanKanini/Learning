console.log("=== ARITHMETIC OPERATORS ===");
console.log("============================");

/*
Arithmetic operators perform mathematical operations on numbers. They are fundamental in calculations and data manipulation.
Common arithmetic operators include:
1. Addition (+) - Adds numbers or concatenates strings
2. Subtraction (-) - Subtracts numbers
3. Multiplication (*) - Multiplies numbers
4. Division (/) - Divides numbers
5. Modulus (%) - Returns remainder of division
6. Exponentiation ()** - Raises to power (ES2016)
*/

// 1. ADDITION (+)
console.log("\n1. ADDITION (+):");
console.log("----------------");
console.log("5 + 3 =", 5 + 3); // 8
console.log("10.5 + 2.5 =", 10.5 + 2.5); // 13
console.log("'Hello' + ' World' =", "Hello" + " World"); // "Hello World"
console.log("'5' + 3 + 1 =", "5" + 3 + 1); // "531" (string concatenation)
console.log("true + 5 =", true + 5); // 6 (boolean converted to number)

// 2. SUBTRACTION (-)
console.log("\n2. SUBTRACTION (-):");
console.log("-------------------");
console.log("10 - 4 =", 10 - 4); // 6
console.log("15.7 - 3.2 =", 15.7 - 3.2); // 12.5
console.log("'20' - 5 - 1 =", "20" - 5 - 1); // 14 (string converted to number)
console.log("100 - 150 =", 100 - 150); // -50
console.log("true - false =", true - false); // 1
console.log("false - true =", false - true); // -1

// 3. MULTIPLICATION (*)
console.log("\n3. MULTIPLICATION (*):");
console.log("----------------------");
console.log("6 * 7 =", 6 * 7); // 42
console.log("3.5 * 2 =", 3.5 * 2); // 7
console.log("'4' * 5 =", "4" * 5); // 20 (string converted to number)
console.log("0 * 1000 =", 0 * 1000); // 0
console.log("Infinity * 2 =", Infinity * 2); // Infinity

// 4. DIVISION (/)
console.log("\n4. DIVISION (/):");
console.log("----------------");
console.log("20 / 4 =", 20 / 4); // 5
console.log("15 / 4 =", 15 / 4); // 3.75
console.log("'12' / 3 =", "12" / 3); // 4 (string converted to number)
console.log("10 / 0 =", 10 / 0); // Infinity
console.log("0 / 0 =", 0 / 0); // NaN

// 5. MODULUS (%)
console.log("\n5. MODULUS (%):");
console.log("---------------");
console.log("17 % 5 =", 17 % 5); // 2
console.log("15 % 3 =", 15 % 3); // 0
console.log("10 % 2 =", 10 % 2); // 0 (even number)
console.log("11 % 2 =", 11 % 2); // 1 (odd number)

// 6. EXPONENTIATION (**)
console.log("\n6. EXPONENTIATION (**):");
console.log("-----------------------");
console.log("2 ** 3 =", 2 ** 3); // 8
console.log("5 ** 2 =", 5 ** 2); // 25
console.log("3 ** 4 =", 3 ** 4); // 81
console.log("9 ** 0.5 =", 9 ** 0.5); // 3 (square root)
console.log("(-2) ** 3 =", (-2) ** 3); // -8

// OPERATOR PRECEDENCE
console.log("\n=== OPERATOR PRECEDENCE ===");
console.log("============================");
console.log("2 + 3 * 4 =", 2 + 3 * 4); // 14 (multiplication first)
console.log("(2 + 3) * 4 =", (2 + 3) * 4); // 20 (parentheses first)
console.log("2 ** 3 ** 2 =", 2 ** (3 ** 2)); // 512 (exponentiation is right-associative)
console.log("(2 ** 3) ** 2 =", (2 ** 3) ** 2); // 64

// TYPE COERCION WITH ARITHMETIC OPERATORS
/* Process of automatically converting one data type to another during arithmetic operations
 This can lead to unexpected results if not understood properly. */
console.log("\n TYPE COERCION WITH ARITHMETIC OPERATORS");
console.log("===========================================");
console.log("'5' - 2 =", "5" - 2); // 3 (string to number)
console.log("'5' * 2 =", "5" * 2); // 10 (string to number)
console.log("'5' / 2 =", "5" / 2); // 2.5 (string to number)
console.log("'5' + 2 =", "5" + 2); // "52" (number to string)
console.log("true + 5 =", true + 5); // 6 (boolean to number)
console.log("false * 10 =", false * 10); // 0 (boolean to number)
console.log("null + 5 =", null + 5); // 5 (null to number)
console.log("undefined + 5 =", undefined + 5); // NaN (undefined to number)
console.log("NaN + 5 =", NaN + 5); // NaN (NaN remains NaN)
console.log("Infinity + 5 =", Infinity + 5); // Infinity (Infinity remains Infinity)

// PRACTICAL EXAMPLES
console.log("\n PRACTICAL EXAMPLES ");
console.log("========================");

// 1. Calculate total price with tax
let price = 100;
let taxRate = 0.08;
let totalPrice = price + price * taxRate;
console.log(`Price: ${price}, Tax: ${taxRate * 100}%, Total: ${totalPrice}`);

// 2. Check if number is even or odd
let number = 17;
console.log(`${number} is ${number % 2 === 0 ? "even" : "odd"}`);

// 3. Calculate compound interest
let principal = 1000;
let rate = 0.05;
let time = 3;
let amount = principal * (1 + rate) ** time;
console.log(
  `Investment: ${principal}, Rate: ${
    rate * 100
  }%, Time: ${time} years, Amount: ${amount.toFixed(2)}`
);

// 4. Loop counter with increment
console.log("\nLoop counter example:");
let i = 0;
while (i < 5) {
  console.log(`Iteration ${i + 1}: i = ${i}`);
  i++;
}
