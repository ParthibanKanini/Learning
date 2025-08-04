/* Comments are used to explain code, making it easier to understand for others or for future reference.
 Comments in JavaScript can be, */

// - Single-line : start with `//` and continue to the end of the line. This is a single-line comment.
// - Multi-line : enclosed between `/*` and `*/`, allowing for longer explanations or notes.
/* This is  a Multi-line comment that extends across multiple lines.
It can be used to provide detailed explanations or to temporarily disable code without deleting it. */

////////////////////////////////////////////////////////////////

/*Console logging in JavaScript. 
==============================*/

console.log("Console logging:");
console.log("=================");

//Variable usr with a string value "Parthiban"
var usr = "Parthiban";

// Multiple arguments are separated by commas are printed in the same line with a space between them.
console.log("\nHello", usr, "Welcome to JavaScript!");
console.log(usr, ":Thank you!", "Lets rock!!");

// String inerpolation using template literals (ES6+)
// Using template literal (`) with a placeholder ${ } interpolates the value of the variable name into the string.
console.log(`\n${usr} : Learning JavaScript is fun!`);

const multiLineString = `\nThis is a multi-line string.
      It can span multiple lines without the need for concatenation or escape characters.
      Multi line string using template literals (\`) will print the string as it is, 
        preserving the line breaks and indentation.
      This is cool!!! `;
console.log(multiLineString);
