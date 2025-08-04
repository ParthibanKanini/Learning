console.log("\n UNARY OPERATORS ");
console.log("=====================");

/*
Unary operators operate on a single operand and perform operations like type conversion, negation, and increment/decrement.

Common unary operators include:
Unary Plus (+) - Converts to number
Unary Minus (-) - Converts to number and negates
Increment (++) - Increases by 1 (pre/post)
Decrement (--) - Decreases by 1 (pre/post)
*/

// 1. UNARY PLUS (+)
console.log("\n1. UNARY PLUS (+):");
console.log("-------------------");
console.log("+5 =", +5); // 5
console.log("+'10' =", +"10"); // 10 (string to number)
console.log("+true =", +true); // 1 (boolean to number)
console.log("+false =", +false); // 0 (boolean to number)
console.log("+null =", +null); // 0 (null to number)
console.log("+undefined =", +undefined); // NaN
console.log("+'hello' =", +"hello"); // NaN

// 2. UNARY MINUS (-)
console.log("\n2. UNARY MINUS (-):");
console.log("-------------------");
console.log("-5 =", -5); // -5 (negation)
console.log("-'10' =", -"10"); // -10 (string to number)
console.log("-true =", -true); // -1 (boolean to number)
console.log("-false =", -false); // -0 (boolean to number)
console.log("-null =", -null); // -0 (null to number)
console.log("-undefined =", -undefined); // NaN
console.log("-'hello' =", -"hello"); // NaN

// 3. INCREMENT (++)
console.log("\n3. INCREMENT (++):");
console.log("------------------");
let counter = 5;
console.log("Initial counter =", counter); // 5
// Pre-increment (++variable)
console.log("Pre-increment ++counter =", ++counter); // 6
console.log("Counter after pre-increment =", counter); // 6
// Post-increment (variable++)
console.log("Post-increment counter++ =", counter++); // 6
console.log("Counter after post-increment =", counter); // 7

// 4. DECREMENT (--)
console.log("\n4. DECREMENT (--):");
console.log("------------------");
let countdown = 10;
console.log("Initial countdown =", countdown); // 10
// Pre-decrement (--variable)
console.log("Pre-decrement --countdown =", --countdown); // 9
console.log("Countdown after pre-decrement =", countdown); // 9
// Post-decrement (variable--)
console.log("Post-decrement countdown-- =", countdown--); // 9
console.log("Countdown after post-decrement =", countdown); // 8

// COMPARISON: Pre vs Post Increment/Decrement
console.log("\n=== PRE vs POST INCREMENT/DECREMENT ===");
console.log("=======================================");
let a = 5;
let b = 5;
console.log("Initial values: a =", a, ", b =", b);
// Pre-increment vs Post-increment
let result1 = ++a; // a is incremented first, then returned
let result2 = b++; // b is returned first, then incremented
console.log("++a result:", result1, ", a is now:", a); // 6, 6
console.log("b++ result:", result2, ", b is now:", b); // 5, 6

// Common pitfall example
let x = 10;
let y = x++ + ++x; // x++ returns 10 and x becomes 11 before ++x, then ++x makes x to 12 and returns 12
console.log(`(x++ + ++x = y) => (${10} + ${12} = ${y}); x is now: ${x}`); // 22, 12

console.log("\n UNARY OPERATRORS EXAMPLES ");
console.log("=============================");

// 1. Converting string to number using unary plus
let userInput = "25";
let age = +userInput;
console.log(
  `User input: "${userInput}"; Converted age: ${age}; Type: ${typeof age}`
);

// 2. Toggle boolean using unary minus
let isActive = true;
let toggledValue = -isActive;
console.log(`Original: ${isActive}, Toggled: ${toggledValue}`);

// 3. Loop counter with increment
console.log("\nLoop counter example:");
let i = 0;
while (i < 5) {
  console.log(`Iteration ${i + 1}: i = ${i++}`);
}

// 4. Array index manipulation
let items = ["apple", "banana", "cherry"];
let index = 0;
console.log(`\nArray navigation:`);
console.log(`Current item: ${items[index]}`);
console.log(`Next item: ${items[++index]}`);
console.log(`Next item: ${items[++index]}`);
