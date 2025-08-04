console.log("Using let to declare variables:");
console.log("================================");

/* 'let' is used to declare block-scoped variables in JavaScript.
  Variables declared with let can be updated but not re-declared within the same block scope.

  They are limited to the block in which they are defined, they cannot be accessed outside of that block.
  This helps prevent issues with variable shadowing and unintended overwriting of variables.
  Variables declared with let are not HOISTED to the top of their scope, they cannot be accessed before their declaration in the code.
 */

function let_variable() {
  let x = 10; // Declaring a variable x using let
  console.log("Initial Value of x:", x); // 10

  // Uncommenting this line will cause an error because x is already declared in the same scope
  // let x = 20;

  if (true) {
    let x = 30; // Declaring a new variable x inside the block scope
    let y = 5; // Declaring a new variable y inside the block scope
    console.log("Value of x (inside block):", x); // 30
    x = 40; // Reassigning the value of x inside the block
    console.log("Reassigned value of x (inside block):", x); // 40
    console.log("Value of y (inside block):", y); // 5
  }

  console.log("Final value of x:", x); // x retains the value from the outer scope 10
  // console.log("Value of y (outside block):", y); // Uncommenting this line will cause an error because y is not defined outside the block
}

let_variable();

// Note: 'let' does not mean the value is immutable, it means the variable reference can be changed.
// You can reassign the value of variables declared with 'let'.
// Summary of 'let':
// - Block-scoped variable declaration
// - Can be updated but not re-declared within the same block scope
// - Helps prevent issues with variable shadowing and unintended overwriting of variables
// - Not hoisted, cannot be accessed before declaration in the code
// - Allows for more controlled variable scoping compared to 'var'
// - Can be used to declare variables that are limited to the block in which they are defined
// - Variables declared with 'let' can be reassigned, but not redeclared in the same scope
// - Helps prevent issues with variable shadowing and unintended overwriting of variables
// - Variables declared with 'let' are not hoisted, meaning they cannot be accessed before their declaration in the code
// - Allows for more controlled variable scoping compared to 'var'
