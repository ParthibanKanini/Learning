console.log("Scope and closures in JavaScript");
console.log("=================================");

/*
Scope refers to the visibility of variables and functions in different parts of the code.

Closures are functions that have access to their outer function scope, even after the outer function has returned.

*/

console.log("\nFunction Scope & Closure:");
console.log("----------------------------");
/* A closure is created when an inner function is returned from an outer function.
 The inner function retains access to the outer function's variables, creating a closure. */
function outerFunction() {
  let outerVariable = "I'm from outer scope!";
  function innerFunction() {
    // Inner function can access outer function's variable
    console.log(outerVariable);
  }
  // Returning inner function to create a closure
  return innerFunction;
}
const inner = outerFunction(); // Calling outer function
// Prints outer function's variable as it creates a closure
inner(); // Calling inner function

console.log("\nAnother basic closure example:");
console.log("--------------------------------");
// A closure that maintains its own state in private variables
function makeCounter() {
  let count = 0; // Private variable
  return function () {
    count++; // Incrementing the outer function's private variable
    console.log("Count: " + count);
  };
}
const closureCounter = makeCounter(); // Creating a counter instance
closureCounter(); // Count: 1
closureCounter(); // Count: 2
closureCounter(); // Count: 3

console.log("\nClosure with Parameters:");
console.log("--------------------------");
// Closure that uses parameters to create a multiplier function
// This function returns another function that multiplies its argument by a specified multiplier
// The inner function retains access to the multiplier variable from the outer function's scope
function createMultiplier(multiplier) {
  return function (number) {
    return number * multiplier; // Accessing multiplier from outer scope
  };
}
// Creating a multiplier for 2
const double = createMultiplier(2);
console.log(double(5)); // 10

// Creating a multiplier for 3
const triple = createMultiplier(3);
console.log(triple(5)); // 15

console.log("\nClosure with Array:");
console.log("---------------------");
function createArray() {
  const arr = [];
  return function (value) {
    arr.push(value);
    return arr;
  };
}
const addToArray = createArray();
console.log(addToArray(1)); // [1]
console.log(addToArray(2)); // [1, 2]
console.log(addToArray(3)); // [1, 2, 3]

console.log("\nClosure with Private Variables:");
console.log("---------------------------------");
function createCounter() {
  // Private count variable
  let count = 0;
  return {
    // increment function to increase count
    increment: function () {
      count++;
    },
    // getCount function to retrieve the current count
    getCount: function () {
      return count;
    },
  };
}
const counter = createCounter();
counter.increment();
console.log("Current Count: " + counter.getCount()); // Current Count: 1
counter.increment();
console.log("Current Count: " + counter.getCount()); // Current Count: 2
