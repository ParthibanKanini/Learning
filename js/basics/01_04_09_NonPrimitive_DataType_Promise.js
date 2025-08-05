console.log(
  "PROMISE - Represents eventual completion of an asynchronous operation"
);
console.log("================================================================");
/**
 * A JavaScript Promise is an object representing the eventual successful completion or failure of an
 * asynchronous operation and its resulting value.
 * Imagine a Promise as a placeholder for a value that will be available in the future.
 * It can be in one of three states:
 *  Pending: The task is in the initial state.
 *  Fulfilled: The task was completed successfully, and the result is available.
 *  Rejected: The task failed, and an error is provided.
 *
 */

console.log("\n Basic Promise Creation:");
console.log("----------------------------");

let basicPromise = new Promise((resolve, reject) => {
  try {
    // Simulating an asynchronous operation using setTimeout
    setTimeout(() => {
      // calling resolve() after 1 second to simulate success
      resolve("Promise resolved after 1 second!");
    }, 1000);
    //throw new Error("Simulate promise rejection by throwing this error");
  } catch (error) {
    // If an error occurs, we reject the promise
    reject("Promise rejected due to an error: " + error.message);
  }
});
// The above & following statements are asynchronous
// so they will execute immediately after the promise is created.
console.log("basicPromise (initial state):", basicPromise);

// Explicitly waiting for 1 second for the promise's state to change to fulfilled.
setTimeout(() => {
  console.log("basicPromise (later state):", basicPromise);
}, 1100);

console.log("typeof basicPromise:", typeof basicPromise); // Returns "object"
