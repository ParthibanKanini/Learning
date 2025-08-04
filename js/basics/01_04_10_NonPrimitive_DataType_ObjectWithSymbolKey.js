console.log("Symbol (as object keys) - Unique identifiers");
console.log("==============================================");

// SYMBOL (when used as object keys)
// This code snippet demonstrates how Symbols can be used as unique identifiers for object keys in JavaScript.
// Symbols are often used to create private properties or unique keys in objects, ensuring that they do not conflict with other keys, even if they have the same description.
// Symbols are not enumerable in for...in loops or Object.keys(), Object.entries(), and are not included in JSON.stringify output.

let sym1 = Symbol("key1");
let sym2 = Symbol("key2");

let objWithSymbols = {
  [sym1]: "value1",
  [sym2]: "value2",
  regularKey: "regularValue",
};

console.log("objWithSymbols:", objWithSymbols);
console.log("objWithSymbols[sym1]:", objWithSymbols[sym1]);
console.log("objWithSymbols[sym2]:", typeof sym2); // Returns "string"
console.log("objWithSymbols[sym2]:", typeof objWithSymbols[sym1]); // Returns "string"
