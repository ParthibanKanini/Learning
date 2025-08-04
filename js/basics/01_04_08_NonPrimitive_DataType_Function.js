console.log("Function - Reusable block of code");
console.log("==================================");

// Three ways to create functions in JavaScript
// Function declarations, function expressions, and arrow functions

// 1. Function Declaration
// Function declarations are hoisted, meaning they can be called before they are defined in the code.
testfn_declared();

function testfn_declared() {
  console.log("Function declaration called before definition.");
  console.log("----------------------------------------------");
}

// 2. Function Expression
// Function expressions are not hoisted, so they must be defined before they are called.
const testfn_expression = () => {
  console.log("Function expression called after definition.");
  console.log("--------------------------------------------");
};
testfn_expression(); // Calling the function expression after it has been defined

// 3. FUNCTION - Reusable block of code
console.log("\n3. FUNCTION:");
console.log("-------------");
// Function declarations are defined using the `function` keyword followed by the function name and parentheses.
function greet(name) {
  return `Hello, ${name}!`;
}
// function assigned to variables
let add = function (a, b) {
  return a + b;
};
// function passed as an argument
function executeAddFunction(fn, arg1, arg2) {
  return fn(arg1, arg2);
}
// function returned from other functions.
function retAddFunction() {
  return function (a, b) {
    return a + b;
  };
}
// Arrow function syntax (ES6+)
// More concise syntax for writing functions.
let multiply = (a, b) => a * b;

console.log("greet function:", greet);
console.log("typeof greet:", typeof greet); // Returns "function"
console.log("greet('Parthiban'):", greet("Parthiban"));
console.log("add(5, 3):", add(5, 3));
console.log("executeAddFunction(add, 2, 2):", executeAddFunction(add, 2, 2));
// callling the function returned from retAddFunction
let addReturnedFunction = retAddFunction();
console.log("addReturnedFunction(2, 3):", addReturnedFunction(2, 3));
console.log("multiply(4, 2):", multiply(4, 2));
