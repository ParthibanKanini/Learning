// JavaScript Primitive vs Non-Primitive Data Types
// =================================================

console.log("Primitive vs Non-Primitive key differences ");
console.log("===========================================");

// Primitive types are stored by value
let a = 5;
let b = a;
a = 10;
console.log("Primitive - a:", a, "b:", b); // b is still 5

// Non-primitive types are stored by reference
let obj1 = { value: 5 };
let obj2 = obj1;
obj1.value = 10;
// obj2.value is also updated because both obj1 and obj2 reference the same object
console.log(
  "Non-primitive - obj1.value:",
  obj1.value,
  "obj2.value:",
  obj2.value
); // Both are 10

// Comparison
console.log("\nComparison:");
console.log("5 === 5:", 5 === 5); // true (same value)
console.log("{} === {}:", {} === {}); // false (different references)
console.log("obj1 === obj2:", obj1 === obj2); // true (same reference)

// Note: Non-primitive types can be more complex and can hold multiple values or properties, while primitive types are simpler and represent single values.
