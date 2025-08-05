console.log("while, do...while");
console.log("======================");

/*
The while loop executes a block of code as long as a specified condition is true. 
The loop evaluates the condition before each iteration and continues running 
as long as the condition remains true.


--- LOOP BEST PRACTICES ---
Use WHILE loops when the condition is checked before execution
Use DO...WHILE loops when you need at least one execution
Always ensure loop conditions will eventually become false
Use break and continue statements wisely
*/

// ===================
// WHILE LOOP EXAMPLES
// ===================

console.log("\n--- WHILE LOOP EXAMPLES ---");
console.log("==========================");

// Example 1: Basic while loop
console.log("1. Basic while loop counting:");
let count = 1;
while (count <= 5) {
  console.log("While count: " + count);
  count++;
}

// Example 2: While loop with condition
console.log("\n2. While loop - find power of 2:");
let number = 1;
while (number < 100) {
  console.log("Power of 2: " + number);
  number *= 2;
}

// Example 3: While loop with array processing
console.log("\n3. While loop processing array:");
let colors = ["red", "green", "blue", "yellow"];
let index = 0;
while (index < colors.length) {
  console.log("Color " + (index + 1) + ": " + colors[index]);
  index++;
}

// Example 4: While loop with user input simulation
console.log("\n4. While loop with condition checking:");
let attempts = 0;
let maxAttempts = 3;
let success = false;
while (attempts < maxAttempts && !success) {
  attempts++;
  console.log("Attempt " + attempts + " of " + maxAttempts);
  // Simulate random success (50% chance)
  success = Math.random() > 0.5;
  if (success) {
    console.log("Success on attempt " + attempts + "!");
  } else if (attempts < maxAttempts) {
    console.log("Failed, trying again...");
  } else {
    console.log("All attempts failed.");
  }
}

// Example 5: While loop with break
console.log("\n5. While loop with break:");
let num = 1;
while (true) {
  if (num > 5) {
    console.log("Breaking at: " + num);
    break;
  }
  console.log("Number: " + num);
  num++;
}

// ========================
// DO...WHILE LOOP EXAMPLES
// ========================

console.log("\n--- DO...WHILE LOOP EXAMPLES ---");
console.log("===============================");

// Example 1: Basic do...while loop
console.log("1. Basic do...while loop:");
let doCount = 1;
do {
  console.log("Do-while count: " + doCount);
  doCount++;
} while (doCount <= 5);

// Example 2: Do...while vs while comparison
console.log("\n2. Do...while executes at least once:");
let falseCondition = 10;
do {
  console.log(
    "This executes once even though condition is false: " + falseCondition
  );
  falseCondition++;
} while (falseCondition < 10);

// Example 3: Do...while with user input simulation
console.log("\n3. Do...while menu simulation:");
let choice = 0;
let menuAttempts = 0;
do {
  menuAttempts++;
  choice = Math.floor(Math.random() * 4) + 1; // Random choice 1-4
  console.log("Menu attempt " + menuAttempts + " - User selected: " + choice);

  if (choice === 1) {
    console.log("Action: View Profile");
  } else if (choice === 2) {
    console.log("Action: Edit Settings");
  } else if (choice === 3) {
    console.log("Action: View Reports");
  } else if (choice === 4) {
    console.log("Action: Exit");
  }
} while (choice !== 4 && menuAttempts < 5);

// Example 4: Do...while with validation
console.log("\n4. Do...while input validation simulation:");
let validInput = false;
let inputAttempts = 0;
do {
  inputAttempts++;
  let simulatedInput = Math.floor(Math.random() * 15) + 1; // Random 1-15
  console.log("Input attempt " + inputAttempts + ": " + simulatedInput);

  if (simulatedInput >= 1 && simulatedInput <= 10) {
    console.log("Valid input received: " + simulatedInput);
    validInput = true;
  } else {
    console.log("Invalid input. Please enter a number between 1 and 10.");
  }
} while (!validInput && inputAttempts < 3);

// ===================
// COMPARISON EXAMPLES
// ===================

console.log("\n--- LOOP COMPARISON ---");
console.log("======================");

// Example: Same task with different loops
console.log("Same task - sum numbers 1 to 5:");

// Using for loop
let forSum = 0;
for (let i = 1; i <= 5; i++) {
  forSum += i;
}
console.log("For loop sum: " + forSum);

// Using while loop
let whileSum = 0;
let whileI = 1;
while (whileI <= 5) {
  whileSum += whileI;
  whileI++;
}
console.log("While loop sum: " + whileSum);

// Using do...while loop
let doWhileSum = 0;
let doWhileI = 1;
do {
  doWhileSum += doWhileI;
  doWhileI++;
} while (doWhileI <= 5);
console.log("Do-while loop sum: " + doWhileSum);
