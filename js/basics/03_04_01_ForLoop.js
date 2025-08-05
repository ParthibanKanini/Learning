console.log("for, while, do...while");
console.log("======================");

/*
Loops in JavaScript are used to reduce repetitive tasks by repeatedly executing a block of code as 
long as a specified condition is true. 

`for (Statement 1 ; Statement 2 ; Statement 3){  code here... }`
Statement 1: Initialization of the counter. It is executed once before the execution of the code block.
Statement 2: Testing condition for executing the code block
Statement 3: Increment or Decrement of the counter. Executed (every time) after the code block has been executed.


--- LOOP BEST PRACTICES ---
Use FOR loops when you know the number of iterations
Always ensure loop conditions will eventually become false
Use break and continue statements wisely
*/

console.log("\n--- FOR LOOP EXAMPLES ---");
console.log("----------------------------");

// Example 1: Basic for loop
console.log("1. Basic counting:");
for (let i = 1; i <= 5; i++) {
  console.log("Count: " + i);
}

// Example 2: For loop with step increment
console.log("\n2. Even numbers from 0 to 10:");
for (let i = 0; i <= 10; i += 2) {
  console.log("Even: " + i);
}

// Example 3: Reverse for loop
console.log("\n3. Countdown from 5 to 1:");
for (let i = 5; i >= 1; i--) {
  console.log("Countdown: " + i);
}

// Example 4: For loop with array
console.log("\n4. Iterating through array:");
let fruits = ["apple", "banana", "orange", "mango"];
for (let i = 0; i < fruits.length; i++) {
  console.log("Fruit " + (i + 1) + ": " + fruits[i]);
}

// Example 5: For loop with break
console.log("\n5. For loop with break (find first even number):");
for (let i = 1; i <= 10; i++) {
  if (i % 2 === 0) {
    console.log("First even number found: " + i);
    break;
  }
  console.log("Checking: " + i);
}

// Example 6: For loop with continue
console.log("\n6. For loop with continue (skip odd numbers):");
for (let i = 1; i <= 10; i++) {
  if (i % 2 !== 0) {
    continue; // Skip odd numbers
  }
  console.log("Even number: " + i);
}

// Example 7: Nested for loops
console.log("\n7. Nested for loops (multiplication table):");
for (let i = 1; i <= 3; i++) {
  let row = "";
  for (let j = 1; j <= 3; j++) {
    row += i * j + "\t";
  }
  console.log("Row " + i + ": " + row);
}
