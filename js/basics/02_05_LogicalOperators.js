console.log("\n LOGICAL OPERATORS ");
console.log("=======================");
/*
lOICAL OPERATORS
Logical operators are used to combine or invert boolean values. They are essential for controlling the flow of programs and making decisions based on conditions. Here are the main logical operators in JavaScript:
1. Logical AND (&&) - Returns true if both operands are true 
2. Logical OR (||) - Returns true if at least one operand is true
3. Logical NOT (!) - Inverts the boolean value (true becomes false, false becomes true
4. Nullish Coalescing (??) - Returns the right-hand operand when the left-hand operand is null or undefined
5. Chaining Assignment - Assigns the value of the right-hand operand to the left-hand
operand, allowing for multiple variables to be assigned in a single expression
6. Short-circuit Evaluation - Evaluates expressions lazily, meaning it stops evaluating as soon as the result is determined
7. Logical Operators with Non-Boolean Values - Can be used with non-boolean values, treating them as truthy or falsy
8. Logical Operators with Objects - Can be used with objects, returning the object itself or its properties based on truthiness
9. Logical Operators with Arrays - Can be used with arrays, treating them as truthy or falsy
10. Logical Operators with Functions - Can be used with functions, treating them as truthy or falsy
11. Logical Operators with Undefined and Null - Can be used with undefined and null values, treating them as falsy
12. Logical Operators with NaN - Can be used with NaN, treating it as falsy
13. Logical Operators with Symbols - Can be used with symbols, treating them as truthy or falsy
14. Logical Operators with BigInt - Can be used with BigInt, treating it as truthy or falsy
15. Logical Operators with Template Literals - Can be used with template literals, treating them as truthy or falsy
16. Logical Operators with Tagged Templates - Can be used with tagged templates, treating them as truthy or falsy
17. Logical Operators with Regular Expressions - Can be used with regular expressions, treating them as truthy or falsy
18. Logical Operators with Proxies - Can be used with proxies, treating them as truthy or falsy
19. Logical Operators with WeakMaps - Can be used with WeakMaps, treating them as truthy or falsy
20. Logical Operators with WeakSets - Can be used with WeakSets, treating them as truthy or falsy
21. Logical Operators with Iterators - Can be used with iterators, treating them as truth or falsy
22. Logical Operators with Generators - Can be used with generators, treating them as truthy or falsy
23. Logical Operators with Async Functions - Can be used with async functions, treating them as truthy or falsy
24. Logical Operators with Promises - Can be used with promises, treating them as truthy or falsy
25. Logical Operators with Observables - Can be used with observables, treating them as truthy or falsy
26. Logical Operators with Streams - Can be used with streams, treating them as truthy or falsy
27. Logical Operators with Web Workers - Can be used with web workers, treating them as truthy or falsy 
28. Logical Operators with Service Workers - Can be used with service workers, treating them as truthy or falsy
29. Logical Operators with Shared Workers - Can be used with shared workers, treating them as truthy or falsy
30. Logical Operators with WebSockets - Can be used with WebSockets, treating them as truthy or falsy
31. Logical Operators with WebRTC - Can be used with WebRTC, treating them as truthy or falsy
32. Logical Operators with WebAssembly - Can be used with WebAssembly, treating them as truthy or falsy
33. Logical Operators with WebGL - Can be used with WebGL, treating them as truthy or falsy
34. Logical Operators with WebGPU - Can be used with WebGPU, treating them as truthy or falsy
*/

// 1. Logical AND (&&)
console.log("\n1. Logical AND (&&):");
console.log("true && true =", true && true); // true
console.log("true && false =", true && false); // false
console.log("false && true =", false && true); // false
console.log("false && false =", false && false); // false
console.log("5 > 3 && 10 > 5 =", 5 > 3 && 10 > 5); // true
console.log("5 > 3 && 10 < 5 =", 5 > 3 && 10 < 5); // false

// 2. Logical OR (||)
console.log("\n2. Logical OR (||):");
console.log("true || true =", true || true); // true
console.log("true || false =", true || false); // true
console.log("false || true =", false || true); // true
console.log("false || false =", false || false); // false
console.log("5 > 3 || 10 < 5 =", 5 > 3 || 10 < 5); // true
console.log("5 < 3 || 10 < 5 =", 5 < 3 || 10 < 5); // false
// 3. Logical NOT (!)
console.log("\n3. Logical NOT (!):");
console.log("!true =", !true); // false
console.log("!false =", !false); // true
console.log("!5 =", !5); // false (non-zero number is truthy)
console.log("!0 =", !0); // true (0 is falsy)
console.log("!null =", !null); // true (null is falsy)
console.log("!undefined =", !undefined); // true (undefined is falsy)
console.log("!'' =", !""); // true (empty string is falsy)
// 4. Nullish Coalescing (??)
console.log("\n4. Nullish Coalescing (??):");
console.log("null ?? 'default' =", null ?? "default"); // 'default' (null is nullish)
console.log("undefined ?? 'default' =", undefined ?? "default"); // 'default' (undefined is nullish)
console.log("5 ?? 'default' =", 5 ?? "default"); // 5 (5 is not nullish)
console.log("'' ?? 'default' =", "" ?? "default"); // '' (empty string is not nullish)
// 5. Chaining Assignment
console.log("\n5. Chaining Assignment:");
let a, b, c;
a = b = c = 10; // All variables assigned the value 10
console.log("a =", a, ", b =", b, ", c =", c); // a = 10, b = 10, c = 10
// 6. Short-circuit Evaluation
console.log("\n6. Short-circuit Evaluation:");
console.log("true && (5 > 3) =", true && 5 > 3); // true (evaluates second operand)
console.log("false && (5 > 3) =", false && 5 > 3); // false (does not evaluate second operand)
console.log("true || (5 < 3) =", true || 5 < 3); // true (does not evaluate second operand)
console.log("false || (5 < 3) =", false || 5 < 3); // false (evaluates second operand)
console.log("5 > 3 && (10 < 5 || 20 > 15) =", 5 > 3 && (10 < 5 || 20 > 15)); // true (evaluates both sides)
// 7. Logical Operators with Non-Boolean Values
console.log("\n7. Logical Operators with Non-Boolean Values:");
console.log("5 && 'Hello' =", 5 && "Hello"); // 'Hello' (5 is truthy, returns second operand)
console.log("0 && 'Hello' =", 0 && "Hello"); // 0 (0 is falsy, returns first operand)
console.log("'Hello' || 5 =", "Hello" || 5); // 'Hello' ('Hello' is truthy, returns first operand)
console.log("'' || 5 =", "" || 5); // 5 (empty string is falsy, returns second operand)
console.log("null || 'default' =", null || "default"); // 'default' (null is falsy, returns second operand)
// 8. Logical Operators with Objects
console.log("\n8. Logical Operators with Objects:");
let obj = { name: "John" };
console.log("obj && obj.name =", obj && obj.name); // 'John' (obj is truthy, returns obj.name)
console.log("null && obj.name =", null && obj.name); // null (null is falsy, returns first operand)
console.log("obj || 'default' =", obj || "default"); // { name: 'John' } (obj is truthy, returns obj)
console.log("undefined || 'default' =", undefined || "default"); // 'default' (undefined is falsy, returns second operand)
// 9. Logical Operators with Arrays
console.log("\n9. Logical Operators with Arrays:");
let arr = [1, 2, 3];
console.log("arr && arr.length =", arr && arr.length); // 3 (arr is truthy, returns arr.length)
console.log("[] && 'Hello' =", [] && "Hello"); // 'Hello' (empty array is truthy, returns second operand)
console.log("null || arr =", null || arr); // [1, 2, 3] (null is falsy, returns arr)
console.log("undefined || arr =", undefined || arr); // [1, 2, 3] (undefined is falsy, returns arr)
// 10. Logical Operators with Functions
console.log("\n10. Logical Operators with Functions:");
function greet() {
  return "Hello, World!";
}
console.log("greet && greet() =", greet && greet()); // 'Hello, World!' (greet is truthy, calls function)
console.log("null && greet() =", null && greet()); // null (null is falsy, does not call function)
console.log("greet || 'default' =", greet || "default"); // 'Hello, World!' (greet is truthy, returns greet)
console.log("undefined || greet() =", undefined || greet()); // 'Hello, World!' (undefined is falsy, calls function)
// 11. Logical Operators with Undefined and Null
console.log("\n11. Logical Operators with Undefined and Null:");
console.log("undefined && 'Hello' =", undefined && "Hello"); // undefined (undefined is falsy, returns first operand)
console.log("null || 'default' =", null || "default"); // 'default' (null is falsy, returns second operand)
console.log("undefined || null =", undefined || null); // null (both are falsy, returns last operand)
console.log("null && undefined =", null && undefined); // null (null is falsy,  returns first operand)
// 12. Logical Operators with NaN
console.log("\n12. Logical Operators with NaN:");
console.log("NaN && 'Hello' =", NaN && "Hello"); // NaN (NaN is falsy, returns first operand)
console.log("NaN || 'default' =", NaN || "default"); // 'default' (NaN is falsy, returns second operand)
console.log("5 || NaN =", 5 || NaN); // 5 (5 is truthy, returns first operand)
console.log("NaN && 0 =", NaN && 0); // 0 ( NaN is falsy, returns first operand)
// 13. Logical Operators with Symbols
console.log("\n13. Logical Operators with Symbols:");
let sym = Symbol("test");
console.log("sym && 'Hello' =", sym && "Hello"); // 'Hello' (sym is truthy, returns second operand)
console.log("sym || 'default' =", sym || "default"); // Symbol(test) (sym is truthy, returns sym)
console.log("null || sym =", null || sym); // Symbol(test) (null is falsy, returns sym)
console.log("undefined && sym =", undefined && sym); // undefined (undefined is falsy, returns first operand)
// 14. Logical Operators with BigInt
console.log("\n14. Logical Operators with BigInt:");
let bigIntValue = BigInt(123456789012345678901234567890);
console.log("bigIntValue && 'Hello' =", bigIntValue && "Hello"); // 'Hello' (bigIntValue is truthy, returns second operand)
console.log("bigIntValue || 'default' =", bigIntValue || "default"); // bigIntValue (bigIntValue is truthy, returns bigIntValue)
console.log("null || bigIntValue =", null || bigIntValue); // bigIntValue (null is falsy, returns bigIntValue)
console.log("undefined && bigIntValue =", undefined && bigIntValue); // undefined (undefined is falsy, returns first operand)
// 15. Logical Operators with Template Literals
console.log("\n15. Logical Operators with Template Literals:");
let name = "Alice";

console.log("name && `Hello, ${name}` =", name && `Hello, ${name}`); // 'Hello, Alice' (name is truthy, returns template literal)
console.log("'' || `Hello, ${name}` =", "" || `Hello, ${name}`); // 'Hello, Alice' (empty string is falsy, returns template literal)
console.log("null || `Hello, ${name}` =", null || `Hello, ${name}`); // 'Hello, Alice' (null is falsy, returns template literal)
console.log("undefined && `Hello, ${name}` =", undefined && `Hello, ${name}`); // undefined (undefined is falsy, returns first operand)
// 16. Logical Operators with Tagged Templates
console.log("\n16. Logical Operators with Tagged Templates:");
function tag(strings, ...values) {
  return strings.reduce(
    (result, str, i) => result + str + (values[i] || ""),
    ""
  );
}
console.log("tag`Hello, ${name}` && 'World' =", tag`Hello, ${name}` && "World"); // 'Hello, AliceWorld' (tagged template is truthy, returns second operand)
console.log("null || tag`Hello, ${name}` =", null || tag`Hello, ${name}`); // 'Hello, Alice' (null is falsy, returns tagged template)
console.log(
  "undefined && tag`Hello, ${name}` =",
  undefined && tag`Hello, ${name}`
); // undefined (undefined is falsy, returns first operand)
// 17. Logical Operators with Regular Expressions
console.log("\n17. Logical Operators with Regular Expressions:");
let regex = /test/;
console.log("regex && 'Matched' =", regex && "Matched"); // 'Matched' (regex is truthy, returns second operand)
console.log("null || regex.test('test') =", null || regex.test("test")); // true (null is falsy, returns regex test result)
console.log(
  "undefined && regex.test('test') =",
  undefined && regex.test("test")
); // undefined (undefined is falsy, returns first operand)
console.log("regex || 'default' =", regex || "default"); // /test/ (regex is truthy, returns regex)
// 18. Logical Operators with Proxies
console.log("\n18. Logical Operators with Proxies:");
let proxy = new Proxy(
  {},
  {
    get: (target, prop) => `Property ${prop} accessed`,
  }
);
console.log("proxy && 'Hello' =", proxy && "Hello"); // 'Hello' (proxy is truthy, returns second operand)
console.log("null || proxy.name =", null || proxy.name); // 'Property name accessed'
console.log("undefined && proxy.name =", undefined && proxy.name); // undefined (undefined is falsy, returns first operand)
console.log("proxy || 'default' =", proxy || "default"); // Proxy {} (proxy is truthy, returns proxy)
// 19. Logical Operators with WeakMaps
console.log("\n19. Logical Operators with WeakMaps:");
let weakMap = new WeakMap();
let objKey = {};
weakMap.set(objKey, "value");
console.log("weakMap && weakMap.get(objKey) =", weakMap && weakMap.get(objKey)); // 'value' (weakMap is truthy, returns value)
console.log("null || weakMap.get(objKey) =", null || weakMap.get(objKey)); // 'value' (null is falsy, returns value)
console.log(
  "undefined && weakMap.get(objKey) =",
  undefined && weakMap.get(objKey)
); // undefined (undefined is falsy, returns first operand)
console.log("weakMap || 'default' =", weakMap || "default"); // WeakMap {} (weakMap is truthy, returns weakMap)
// 20. Logical Operators with WeakSets
console.log("\n20. Logical Operators with WeakSets:");
let weakSet = new WeakSet();
let objValue = {};
weakSet.add(objValue);
console.log("weakSet && 'Hello' =", weakSet && "Hello");
console.log("null || weakSet.has(objValue) =", null || weakSet.has(objValue)); // true (null is falsy, returns has result)
console.log(
  "undefined && weakSet.has(objValue) =",
  undefined && weakSet.has(objValue)
); // undefined (undefined is falsy, returns first operand)
console.log("weakSet || 'default' =", weakSet || "default"); // WeakSet {} (weakSet is truthy, returns weakSet)
// 21. Logical Operators with Iterators
console.log("\n21. Logical Operators with Iterators:");
let iterator = [1, 2, 3][Symbol.iterator]();
console.log("iterator && 'Hello' =", iterator && "Hello"); // 'Hello' (iterator is truthy, returns second operand)
console.log("null || iterator.next().value =", null || iterator.next().value); // 1 (null is falsy, returns first value)
console.log(
  "undefined && iterator.next().value =",
  undefined && iterator.next().value
); // undefined (undefined is falsy, returns first operand)
console.log("iterator || 'default' =", iterator || "default"); // Iterator { ... }
// 22. Logical Operators with Generators
console.log("\n22. Logical Operators with Generators:");
function* generator() {
  yield 1;
  yield 2;
  yield 3;
}
let gen = generator();
console.log("gen && 'Hello' =", gen && "Hello"); // 'Hello' (gen is truthy, returns second operand)
console.log("null || gen.next().value =", null || gen.next().value); // 1 (null is falsy, returns first value)
console.log("undefined && gen.next().value =", undefined && gen.next().value); // undefined (undefined is falsy, returns first operand)
console.log("gen || 'default' =", gen || "default"); // Generator { ... }
// 23. Logical Operators with Async Functions
console.log("\n23. Logical Operators with Async Functions:");
async function asyncFunc() {
  return "Async Result";
}
asyncFunc().then((result) => {
  console.log("asyncFunc && result =", asyncFunc && result); // 'Async Result' (asyncFunc is truthy, returns result)
});
console.log("null || asyncFunc() =", null || asyncFunc()); // Promise { ... } (null is falsy, returns promise)
console.log("undefined && asyncFunc() =", undefined && asyncFunc()); // undefined (undefined is falsy, returns first operand)
// 24. Logical Operators with Promises
console.log("\n24. Logical Operators with Promises:");
let promise = Promise.resolve("Promise Result");
promise.then((result) => {
  console.log("promise && result =", promise && result); // 'Promise Result' (promise is truthy, returns result)
});
console.log("null || promise =", null || promise); // Promise { ... } (null is falsy, returns promise)
console.log("undefined && promise =", undefined && promise); // undefined (undefined is falsy, returns first operand)
// 25. Logical Operators with Observables
console.log("\n25. Logical Operators with Observables:");
