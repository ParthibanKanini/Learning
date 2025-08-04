# Learning JavaScript

## **Basics of JavaScript**

### What is JavaScript?

JavaScript is a high-level, interpreted programming language that was originally designed to make web pages interactive. It runs primarily in web browsers (like Chrome, Firefox, Edge, etc.), but it’s now also used on the server side (Node.js)

- **Client-side**: Runs in the browser to handle user interactions, animations, form validation, etc
- **Server-side**: With Node.js, you can build backend applications and even command-line tools.
- **Versatile**: Used for websites, mobile apps, desktop apps, and even IoT.

### **Adding JS to HTML**

**Internal**:JavaScript code is placed inside a `<script>` tag, usually within the `<head>` or just before the closing `</body>` tag.

```html
<!DOCTYPE html>
<html>
  <head>
    <title>My First JS Page</title>
    <script>
      function sayHello() {
        alert("Hello from JavaScript!");
      }
    </script>
  </head>
  <body>
    <button onclick="sayHello()">Click Me</button>
  </body>
</html>
```

**External**:
Best practice for larger or reusable scripts is to create a separate .js file and link it with the src attribute in the `<script>` tag.

```js
function sayHello() {
  alert("Hello from External JS!");
}
```

```html
<!DOCTYPE html>
<html>
  <head>
    <title>Using External JS</title>
    <script src="script.js"></script>
  </head>
  <body>
    <button onclick="sayHello()">Click Me</button>
  </body>
</html>
```

## Topic Contents:

<ol>
    <li>Variable & Data types
    <ol>
        <li>Comments & Console Log</li>
        <li>Variables
            <ol>
                <li>var</li>
                <li>let</li>
                <li>const</li>
            </ol>
        </li>
        <li>Primitive Data Types
            <ol>
                <li>Number - Integers and Floating-point numbers </li>
                <li>String - Text data</li>
                <li>Boolean - true or false</li>
                <li>Undefined - Declared but not assigned</li>
                <li>Null - Intentional absence of value</li>
                <li>BigInt - Large integers</li>
                <li>Symbol - Unique identifiers (ES6+) </li>
            </ol>
        </li>
        <li>Non-Primitive Data Types
            <ol>
                <li>Object - Key-value pairs collection</li>
                <li>Array - Ordered collection of elements</li>
                <li>Date - Represents dates and times</li>
                <li>RegExp - Regular expressions</li>
                <li>Error - Runtime error objects</li>
                <li>Map - Key-value pairs with any type of keys</li>
                <li>Set - Collection of unique values</li>
                <li>Function - Reusable block of code</li>
            </ol>
        </li>
    </ol>
    </li> <!-- End of Variable & Data types -->
    <li>Operators
        <ol>
            <li>Arithmetic</li>
            <li>Unary</li>
            <li>Assignment</li>
            <li>Comparison</li>
            <li>Logical</li>
        </ol>
    </li>
    <li>Control Structures
        <ol>
            <li>if, else, else if</li>
            <li>switch statements</li>
            <li>Ternary operator (condition ? true : false)</li>
            <li>Loops & Iteration
                <ol>
                    <li>for, while, do...while</li>
                    <li>for...in, for...of (especially for objects and arrays)</li>
                    <li>break and continue</li>
                </ol>
            </li>
        </ol>
    </li>
    <li>Functions
        <ol>
            <li>Function declarations and expressions</li>
            <li>Parameters, arguments, return values</li>
            <li>Arrow functions (=>)</li>
            <li>Scope and closures (basic understanding)</li>
        </ol>
    </li>
    <li>Arrays
        <ol>
            <li>Creating arrays</li>
            <li>Accessing and modifying elements</li>
            <li>Array methods: find, push, pop, shift, unshift, splice, slice</li>
            <li>Iteration: for, forEach, map, filter, reduce</li>
        </ol>
    </li>
    <li>Objects
        <ol>
            <li>Creating objects (literals, constructors)</li>
            <li>Accessing/modifying properties (dot and bracket notation)</li>
            <li>Methods inside objects</li>
            <li>Looping through objects with for...in</li>
        </ol>
    </li>
    <li>DOM Manipulation
        <ol>
            <li>document.getElementById, querySelector, etc.</li>
            <li>Changing content (innerText, innerHTML)</li>
            <li>Changing styles</li>
            <li>Adding/removing elements</li>
        </ol>
    </li>
    <li>Events
        <ol>
            <li>Event types (click, input, submit, etc.)</li>
            <li>addEventListener</li>
            <li>Event object and event.target</li>
            <li>Form validation basics</li>
        </ol>
    </li>
    <li>Error Handling
        <ol>
            <li>try...catch</li>
            <li>throw</li>
            <li>Basic debugging skills in browser dev tools</li>
        </ol>
    </li>
    <li>Asynchronous JavaScript
        <ol>
            <li>setTimeout, setInterval</li>
            <li>Basics of Promises</li>
            <li>Intro to async/await</li>
        </ol>
    </li>
    <li>Modules
        <ol>
            <li>import / export</li>
        </ol>
    </li>
    <li>Others: ES6+ Features:
        <ol>
            <li>String interpolation</li>
            <li>rest</li>
            <li>spread</li>
            <li>template literals</li>
        </ol>
    </li>
</ol>

JSON & API calls (fetch)
Local storage

**Mini Projects**

- Calculator
- To-do list
- Form validation
- Quiz app
