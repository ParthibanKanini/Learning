console.log("if-else & if-else if-else statements");
console.log("============================");

/* The if, else, and else if statements are used to control the flow of execution based on 
certain conditions.
*/

console.log("if-else statement example");
console.log("-------------------------");

let age = 20;
// Condition in the if statement evaluates to true (or) false
if (age >= 18) {
  console.log("You are an adult.");
} else {
  console.log("You are a minor.");
}

console.log("\n--- Chained if-else if-else example with String comparison ---");
console.log("----------------------------------------------------------------");
// Multiple conditions can be checked using else-if the flow breaks when a condition is true
let weather = "sunny";
if (weather === "sunny") {
  console.log("Great day for a picnic!");
} else if (weather === "rainy") {
  console.log("Don't forget your umbrella!");
} else if (weather === "cloudy") {
  console.log("Perfect weather for a walk.");
} else {
  console.log("Weather is unpredictable today.");
}

console.log("\n--- Chained if-else example with logical operators ---");
console.log("--------------------------------------------------------");
let temperature = 25;
let humidity = 60;
// Logical operators can be used to combine multiple conditions
// && (AND), || (OR), ! (NOT)
if (temperature > 30 && humidity > 70) {
  console.log("It's hot and humid - stay hydrated!");
} else if (temperature > 30 || humidity > 80) {
  console.log("Either hot or very humid - be careful!");
} else if (temperature < 10) {
  console.log("It's cold - wear warm clothes!");
} else {
  console.log("Nice comfortable weather!");
}

console.log("\n--- Chained if-else example with Boolean conditions ---");
console.log("--------------------------------------------------------");
let isLoggedIn = true;
let isAdmin = false;
if (isLoggedIn && isAdmin) {
  console.log("Welcome Admin! You have full access.");
} else if (isLoggedIn && !isAdmin) {
  console.log("Welcome User! You have limited access.");
} else {
  console.log("Please log in to continue.");
}

console.log("\n--- if-else example with Type Checking ---");
console.log("---------------------------------------------");
let value = "hello";
if (typeof value === "string") {
  console.log("Value is a string: " + value);
} else if (typeof value === "number") {
  console.log("Value is a number: " + value);
} else if (typeof value === "boolean") {
  console.log("Value is a boolean: " + value);
} else {
  console.log("Value is of type: " + typeof value);
}

console.log("\n--- if-else example with Null and Undefined ---");
console.log("--------------------------------------------------");
let userInput = null;
if (userInput === null) {
  console.log("Input is null");
} else if (userInput === undefined) {
  console.log("Input is undefined");
} else if (userInput === "") {
  console.log("Input is empty string");
} else {
  console.log("Input has value: " + userInput);
}

console.log("\n--- Complex condition with nested if example ---");
console.log("--------------------------------------------------");
// Nested ifs can be used for more complex conditions
// This example checks for weekend and holiday conditions
let dayOfWeek = "Saturday";
let isHoliday = false;
if (dayOfWeek === "Saturday" || dayOfWeek === "Sunday") {
  if (isHoliday) {
    console.log("It's a holiday weekend - double rest!");
  } else {
    console.log("It's weekend - but not a holiday!");
  }
} else {
  if (isHoliday) {
    console.log("It's a weekday holiday - lucky you, Holiday!");
  } else {
    console.log("It's a regular weekday - time to work!");
  }
}
