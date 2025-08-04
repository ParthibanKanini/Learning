console.log("Using var to declare variables:");
console.log("=================================");

/*  

Javascript is a dynamically typed language, meaning variables can hold values of any type and can change types at runtime.

Variables can be declared using 'var', 'let', or 'const' in JavaScript.
Scope of variables declared with 'var' are not limited to the block in which they are defined(unlike let and const).
Instead, they are scoped to the nearest code block/function or the global scope. 
They can be re-declared and updated within the same block/function or globally(if declared outside any function).
*/

function var_variable() {
  var x = 10; // Declaring a variable x using var
  console.log("Initial Value of x:", x); // 10
  var x = 20; // Re-declaring the variable x using var
  console.log("Value of x (after re-declaring):", x); // 20

  if (true) {
    var x = 30; // Redeclaring & assigning the value of x inside a code block { }
    var y = 5; // Declaring a new variable y within the code block { }
    console.log("Value of x (after re-declaring inside block):", x); // 30
  }

  console.log("Final value of x:", x); // 30 x retains the last assigned value 30 from inside if code block.
  console.log("Value of y (outside block):", y); // 5 y is accessible here outside code block as it is function scoped.

  /* Variables declared with 'var' are HOISTED to the top of their scope, meaning they can be accessed before their declaration in the code.
   However, they are initialized with 'undefined' until the line of code where they are assigned a value is executed.
  
   This means that if you try to access a 'var' variable before its declaration, it will return 'undefined' instead of throwing an error.
   Variables declared with 'var' can be re-declared within the same scope without throwing an error. This can lead to confusion, 
   especially in larger codebases, as it allows for unintended variable shadowing or overwriting.
   */
  console.log("Using variable before declaration:", z);
  var z; // Variable z is used above in log before declaring here.
}

var_variable();

// Note: 'var' does not mean the value is immutable, it means the variable reference can be changed.
// You can reassign the value of variables declared with 'var'.
// Summary of 'var':
// - Function-scoped variable declaration
// - Can be re-declared and updated within the same function or globally if declared outside any function
// - Hoisted to the top of their scope, meaning they can be accessed before their declaration in the code
// - Initialized with 'undefined' until the line of code where they are assigned a value is executed
// - Allows for more flexible variable scoping compared to 'let' and 'const'
