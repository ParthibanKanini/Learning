console.log("Switch operator");
console.log("================");
/*
The switch statement evaluates an expression and executes code based on matching cases. It’s an 
efficient alternative to multiple if-else statements, improving readability when handling many conditions.
The expression is evaluated once. The value of the expression is compared against each case.
The block under the matching case runs. If no match, the default block executes (if present).
`break` statements prevents execution of subsequent cases.
*/
let grade = "B";
switch (grade) {
  case "A":
    console.log("Excellent work!");
    break;
  case "B":
    console.log("Good job!");
    break;
  case "C":
    console.log("Average performance");
    break;
  case "D":
    console.log("Needs improvement");
    break;
  case "F":
    console.log("Failed - try again");
    break;
  default:
    console.log("Invalid grade");
}
