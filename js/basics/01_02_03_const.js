console.log("Using const to declare constant variables:");
console.log("==========================================");

/*
Constants declared with 'const' cannot be reassigned or redeclared within the same block scope.
They are limited to the block in which they are defined, they cannot be accessed outside of that block.
This helps prevent issues with variable shadowing and unintended overwriting of variables.
*/

function const_variable() {
  const x_const = 10; // Declaring a constant x_const
  console.log("Initial Value of x:", x_const); // 10
  // Uncommenting this line will cause an error because x_const is a constant and cannot be reassigned
  //x_const = 20;
  if (true) {
    const x_const = 30; // Declaring a new x_const inside the block scope
    console.log("Value of x (inside block):", x_const); // 30
  }
}

/*
  Constants declared with 'const' can hold objects or arrays, but the reference to the object or array cannot be changed.
  However, properties of the object or elements of the array can be modified.
  This means that while you cannot reassign the entire object or array, you can change its contents.
*/
function const_obj() {
  console.log("\nUsing const to declare objects:");
  console.log("================================");
  const book = { titile: "JavaScript Basics" }; // Declaring a constant object book using const
  console.log("Initial Book Object:", book); // {titile: "JavaScript Basics"}
  //book = { title: "Headfirst JavaScript"}; // Reassigning the object reference is not allowed,
  book.titile = "Advanced JavaScript"; // However, properties of the object can be modified
  console.log("Book Object title modified:", book); // {titile: "Advanced JavaScript"}
}

function const_arr() {
  console.log("\nUsing const to declare arrays:");
  console.log("================================");
  const numbers = [1, 2, 3]; // Declaring a constant array numbers using const
  console.log("Initial Numbers Array:", numbers); // [1, 2, 3]
  // Uncommenting this line will cause an error because numbers is a constant and cannot be reassigned
  //numbers = [4, 5, 6]; // Reassigning the array reference is not allowed,
  numbers.push(4); // However, elements of the array can be modified
  console.log("Numbers Array after push:", numbers); // [1, 2, 3, 4]
}

const_variable();
const_obj();
const_arr();

// Note: 'const' does not mean the value is immutable, it means the variable reference cannot be changed.
// You can still modify the contents of objects or arrays declared with 'const'.
// Summary of 'const':
// - Block-scoped constant declaration
// - Cannot be reassigned or redeclared within the same block scope
// - Can hold objects or arrays, but reference cannot be changed
// - Properties of objects or elements of arrays can be modified
// - Helps prevent issues with variable shadowing and unintended overwriting of variables
