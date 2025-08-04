console.log("Symbol (as object keys) - Unique identifiers");
console.log("===============================================");

/* Symbol - Represents a unique identifier (ES6+)
 Symbol is built-in Object with constructor returning symbol primitive.
 Used to create private properties or unique keys in objects. Each symbol is guaranteed to be unique, even if they have the same description.
 
 Symbols are not enumerable in for...in loops or Object.keys(), Object.entries(), Not included in JSON.stringify output.
 Can still be accessed using Object.getOwnPropertySymbols() -> static method returns an array of all symbol properties found directly on a given object.
 Symbols can be used to hide internal/private implemenation details or data. Avoids accidental exposure of sensitive data.
 Libraries can add properties without interfering with user code. */

let symbol1 = Symbol();
let symbol2 = Symbol("description");
let symbol3 = Symbol("description");

console.log("\nSymbol examples:");
console.log("------------------");
console.log("symbol1:", symbol1, typeof symbol1);
console.log("symbol2:", symbol2, typeof symbol2);
console.log("symbol3:", symbol3, typeof symbol3);
console.log("symbol2 === symbol3:", symbol2 === symbol3); // false - each symbol is unique

//NOTE Usecases for Symbols include and not restricted to:
// 1. Creating unique object keys
// 2. Hiding properties from JSON serialization
// 3. Implementing private properties in classes
// 4. Avoiding name clashes in libraries
// 5. Customizing object behavior with well-known symbols (e.g., Symbol.iterator for iteration)

console.log("Symbol (as object keys) - Unique identifiers");

console.log("\n10. SYMBOL as Object Keys:");
console.log("---------------------------");

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
console.log(
  "Object.getOwnPropertySymbols(objWithSymbols):",
  Object.getOwnPropertySymbols(objWithSymbols)
);
console.log("Object.keys(objWithSymbols):", Object.keys(objWithSymbols)); // Regular keys only, symbols are not included
console.log("Object.entries(objWithSymbols):", Object.entries(objWithSymbols)); // Regular keys only, symbols are not included
console.log("JSON.stringify(objWithSymbols):", JSON.stringify(objWithSymbols)); // Symbols are not included in JSON output
console.log(
  "Object.getOwnPropertyNames(objWithSymbols):",
  Object.getOwnPropertyNames(objWithSymbols)
); // Regular keys only, symbols are not included
