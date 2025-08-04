console.log("\n ASSIGNMENT OPERATORS ");
console.log("=======================");

/*
Assignment operators are used to assign values to variables. They can also perform arithmetic operations and assign the
result back to the variable. Here are some common assignment operators:
1. Simple Assignment (=)
2. Addition Assignment (+=)
3. Subtraction Assignment (-=)
4. Multiplication Assignment (*=)
5. Division Assignment (/=)
6. Modulus Assignment (%=)
7. Exponentiation Assignment (**=) - ES2016
8. Bitwise AND Assignment (&=)
9. Bitwise OR Assignment (|=)
10. Bitwise XOR Assignment (^=)
11. Signed Left Shift Assignment (<<=)
12. Signed Right Shift Assignment (>>=)
13. chaining assignment (e.g., x = y = z) - Assigns the value of z to y, then y to x
*/

// 1. Simple Assignment (=)
let x = 10;
console.log("\n1. Simple Assignment: x =", x); // 10

// 2. Addition Assignment (+=)
//x += 5;  Equivalent to x = x + 5
console.log(`2. Addition Assignment: (${x} += 5) =`, (x += 5)); // 15

// 3. Subtraction Assignment (-=)
// x -= 3; Equivalent to x = x - 3
console.log(`3. Subtraction Assignment: (${x} -= 3) =`, (x -= 3)); // 12

// 4. Multiplication Assignment (*=)
//x *= 2; // Equivalent to x = x * 2
console.log(`4. Multiplication Assignment: (${x} *= 2) =`, (x *= 2)); // 24

// 5. Division Assignment (/=) : Returns the quotient of the division
//x /= 4; // Equivalent to x = x / 4
console.log(`5. Division Assignment: (${x} /= 4) =`, (x /= 4)); // 6

// 6. Modulus Assignment (%=) :  Returns the remainder of the division
//x %= 5; // Equivalent to x = x % 5
console.log(`6. Modulus Assignment: (${x} %= 5) =`, (x %= 5)); // 1

// 7. Exponentiation Assignment (**=) - ES2016 : Raises to power
//x **= 3; // Equivalent to x = x ** 3 which is (1^3 = 1)
console.log(`7. Exponentiation Assignment: (${x} **= 3) =`, (x **= 3)); // 1

// 8. Bitwise AND Assignment (&=) : Performs bitwise AND operation and assigns the result
let y = 6; // Binary of 6: 110
//y &= 3; // Binary of 3: 011,
// Result bits are 1 when both bits is 1. So (110 & 011) => 010 (2 in decimal)
console.log(`8. Bitwise AND Assignment: (${y} &= 3) =`, (y &= 3)); // 2

// 9. Bitwise OR Assignment (|=)
//y |= 5; // Binary of 2 is 010, Binary of 5 is 101
// Result bits are 1 when either bit is 1. So (010 | 101) => 111 (7 in decimal)
console.log(`9. Bitwise OR Assignment: (${y} |= 5) =`, (y |= 5)); // 7

// 10. Bitwise XOR Assignment (^=)
//y ^= 4; // Binary of 7 is 111, Binary of 4 is 100
// Result bits are 1 when bits are different. So (111 ^ 100) => 011 (3 in decimal)
console.log(`10. Bitwise XOR Assignment: (${y} ^= 4) =`, (y ^= 4)); // 3

// 11. Signed Left Shift Assignment (<<=)
//y <<= 1; // Binary of 3 is 011, shifted left becomes 110 (Adds 0 to end).
// Result is 110 which is 6 in decimal
console.log(`11. Left Shift Assignment: (${y} <<= 1) =`, (y <<= 1)); // 6

// 12. Signed Right Shift Assignment (>>=)
//y >>= 1; // Binary of 6 is 110, shifted right becomes 011 (removing last bit and add 0 to start if Positive number).
//  110 shifted right becomes 011 (3 in decimal)
console.log(`12. Right Shift Assignment: (${y} >>= 1) =`, (y >>= 1)); // 3

//NOTE: Unsightned right and left shift assignment operators (>>>=, <<<=) are not commonly used in JavaScript.
//TODO : Add examples for Unsigned Right Shift (>>>=) and Unsigned Left Shift (<<=) if needed.

// 13. Chaining Assignment
let a, b, c;
a = b = c = 42; // Assigns 42 to c, then b, then a
console.log(
  `13. Chaining Assignment (a = b = c = 42): a =", a, ", b =", b, ", c =`,
  c
); // 42, 42, 42

// ---------------------------------------------------------------------------------------------------------------------------------------
