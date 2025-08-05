console.log("Function declarations and expressions");
console.log("======================================");

/*
Functions in JavaScript are reusable blocks of code that can be executed when called.
They can be defined using function declarations or function expressions.
Function declarations are hoisted, meaning they can be called before they are defined in the code.
Function expressions are not hoisted, so they must be defined before they are called.
*/

console.log("\nFunction Declaration:");
console.log("-----------------------");
greet("Alice"); // Calling the function before its definition
// Function definition
function greet(name) {
  console.log(`Hello, ${name}!`);
}

console.log("\nFunction Expression:");
console.log("-----------------------");
// Function expression assigned to a variable and can be called after its definition
const add = function (a, b) {
  return a + b;
};
console.log(add(5, 10)); // Calling the function expression
