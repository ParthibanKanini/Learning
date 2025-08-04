console.log(
  "PROMISE - Represents eventual completion of an asynchronous operation"
);
console.log("============================================================");
// PROMISE - Represents eventual completion of an asynchronous operation (ES6+)

let promise1 = new Promise((resolve, reject) => {
  setTimeout(() => resolve("Promise resolved!"), 1000);
});
let promise2 = Promise.resolve("Immediate resolve");
console.log("promise1:", promise1);
console.log("typeof promise1:", typeof promise1); // Returns "object"
console.log("promise2:", promise2);
