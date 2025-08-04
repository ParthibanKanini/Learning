console.log("JavaScript Non-Primitive Data Types");
console.log("=====================================");

/*
 Non-primitive data types are more complex data structures that can hold multiple values or properties.
 They are mutable, meaning their contents can be changed after creation.
 They are stored by reference, meaning that when you assign a non-primitive value to a variable, 
 the variable holds a reference to the value in memory, not the actual value itself.
*/

console.log("\n=== Summary of all non-primitive types ===");
console.log("1. Object - Key-value pairs collection");
console.log("2. Array - Ordered collection of elements");
console.log("3. Date - Represents dates and times");
console.log("4. RegExp - Regular expressions");
console.log("5. Error - Runtime error objects");
console.log("6. Map - Key-value pairs with any type of keys");
console.log("7. Set - Collection of unique values");

// 1. OBJECT - Key-value pairs collection
console.log("\n1. OBJECT:");
console.log("----------");
let person = {
  name: "Parthiban",
  height: 6.1,
  isStudent: false,
  address: {
    street: "Vilankurichi",
    city: "Coimbatore",
  },
};
console.log("person:", person);
console.log("typeof person:", typeof person);
// Accessing properties using dot notation
console.log("person.name:", person.name);
// Accessing properties using bracket notation
console.log("person['height']:", person["height"]);
// Accessing nested properties using dot notation
console.log("person.address.city:", person.address.city);
// Objects are mutable. Changing a property value.
person.name = "Neo";
console.log("After changing name:", person.name);

// ---------------------------------------------------------------------------------------------------------------------------------------

// 2. ARRAY - Ordered collection of elements
console.log("\n2. ARRAY:");
console.log("----------");
let numbers = [1, 2, 3, 4, 5];
let mixedArray = [1, "hello", true, null, { name: "test" }];
let emptyArray = [];
console.log("numbers:", numbers);
console.log("typeof numbers:", typeof numbers); // Returns "object"
// Checking if it's an array
console.log("Array.isArray(numbers):", Array.isArray(numbers)); //Returns "true". Better way to check
// Accessing elements of array using index
console.log("numbers[2]:", numbers[2]); // Returns 3
// Length of the array can be checked using length property
console.log("numbers.length:", numbers.length); // Returns 5
// Arrays are mutable. Can add elements using push method
numbers.push(6);
console.log("After push:", numbers);

// ---------------------------------------------------------------------------------------------------------------------------------------

// 3. DATE - Represents dates and times
console.log("\n4. DATE:");
console.log("--------");
let now = new Date();
let specificDate = new Date("2025-07-28");
let customDate = new Date(2025, 1, 20, 10, 15, 0); // Year, Month (0-indexed), Day, Hour, Minute, Second
// format date to a custom format
let formattedDate = customDate.toISOString().split("T")[0]; // YYYY-MM-DD
// format date and time to a custom format
let formattedDateTime = customDate
  .toISOString()
  .replace("T", " ")
  .split(".")[0]; // YYYY-MM-DD HH:mm:ss

// format date and time to a custom format DD/MM/YYYY HH:mm:ss
let formattedDateTimeCustom = customDate.toLocaleString("en-GB", {
  year: "numeric",
  month: "2-digit", // 2-digit month
  day: "2-digit", // 2-digit day
  hour: "2-digit", // 2-digit hour
  minute: "2-digit", // 2-digit minute
  second: "2-digit", // 2-digit second
});

console.log("now:", now);
console.log("typeof now:", typeof now); // Returns "object"
console.log("now.getFullYear():", now.getFullYear());
console.log("specificDate:", specificDate);
console.log("formattedDate:", formattedDate);
console.log("formattedDateTime:", formattedDateTime);
console.log("formattedDateTimeCustom", formattedDateTimeCustom);

// ---------------------------------------------------------------------------------------------------------------------------------------

// 4. REGEXP - Regular expressions for pattern matching
console.log("\n5. REGEXP:");
console.log("----------");
let pattern1 = /hello/i; // Case-insensitive search for "hello"
let pattern2 = new RegExp("\\d+", "g"); // Global search for digits
console.log("pattern1:", pattern1);
console.log("typeof pattern1:", typeof pattern1); // Returns "object"
console.log("pattern1.test('Hello World'):", pattern1.test("Hello World"));
console.log("'123abc456'.match(pattern2):", "123abc456".match(pattern2));

// ---------------------------------------------------------------------------------------------------------------------------------------

// 5. ERROR - Represents runtime errors
console.log("\n6. ERROR:");
console.log("---------");
let customError = new Error("Something went wrong!");
let typeError = new TypeError("Invalid type provided");
console.log("customError:", customError);
console.log("typeof customError:", typeof customError); // Returns "object"
console.log("customError.message:", customError.message);

// ---------------------------------------------------------------------------------------------------------------------------------------

// 6. MAP - Collection of key-value pairs (ES6+)
console.log("\n7. MAP:");
console.log("-------");
let userRoles = new Map();
// The keys are unique, and can be of any type
// values can be of any type.
userRoles.set("admin", "Parthiban");
userRoles.set("user", "Neo");
userRoles.set("guest", 1);
console.log("userRoles:", userRoles);
console.log("typeof userRoles:", typeof userRoles); // Returns "object"
console.log("userRoles.get('admin'):", userRoles.get("admin"));
console.log("userRoles.size:", userRoles.size);

// ---------------------------------------------------------------------------------------------------------------------------------------

// 7. SET - Collection of unique values (ES6+)
console.log("\n8. SET:");
console.log("-------");
let uniqueNumbers = new Set([1, 2, 2, 3, 3, 4, 5]);
uniqueNumbers.add(6);
uniqueNumbers.add(2); // Won't be added as it already exists
console.log("uniqueNumbers:", uniqueNumbers); // { size:6, 1,2,3,4,5,6}
console.log("typeof uniqueNumbers:", typeof uniqueNumbers); // Returns "object"
console.log("uniqueNumbers.has(3):", uniqueNumbers.has(3)); // Returns true
console.log("uniqueNumbers.size:", uniqueNumbers.size); // Returns 6

// ---------------------------------------------------------------------------------------------------------------------------------------

// Note: Non-primitive types can be more complex and can hold multiple values or properties, while primitive types are simpler and represent single values.
// Characteristics of Non-Primitive Types:
// - Mutable (can be changed)
// - Stored by reference
// - typeof returns 'object' (except functions)
// - Complex data structures
