console.log("Arrow Functions");
console.log("================");

/* Arrow functions can be used with or without parameters.
 Arrow function do not have their own 'this', 'arguments', 'super', or 'new.target'.
 They are best suited for non-method functions, and they cannot be used as constructors.*/

const addFunction = (a, b) => {
  console.log("\nUsing arrow function to add two numbers:");
  console.log("------------------------------------------");
  return a + b;
};
console.log(addFunction(2, 3)); // 5

console.log("\nSingle line arrow function :");
console.log("-------------------------------");
/* One Arrow function has single line of code without curly braces, which implicitly can return or does not return a result.
 They are often used for short functions or callbacks, making the code more concise.
 */
const test = () => console.log("Single line arrow function.");
test();

console.log("\nUsing arrow function with forEach:");
console.log("------------------------------------");
const ids = [1, 2, 3, 4, 5];
/* Arrow functions can also be used with array methods like forEach, map, filter, etc.
 Iterate over arrays and perform operations on each element.
 */
ids.forEach((id) => {
  id *= 10;
  console.log(`ID: ${id}`);
});
