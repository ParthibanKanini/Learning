console.log("Ternary operator");
console.log("================");

/* Ternary operator (shorthand if-else)
 Conditional operator that evaluates a condition and returns one of two values based on whether 
 the condition is true or false.
    
 `condition ? trueExpression : falseExpression`
A condition that evaluates to true or false. 
trueExpression: The value or expression is returned if the condition is true.
expressionIfFalse: The value or expression returned if the condition is false.
*/

console.log("\n--- Ternary operator example ---");
console.log("-----------------------------------");
let userAge = 16;
let message = userAge >= 18 ? "Can vote" : "Cannot vote yet";
console.log("Voting eligibility: " + message);
