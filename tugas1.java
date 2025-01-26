const express = require('express');
const bodyParser = require('body-parser');
const app = express();
const port = 3000;
// In-memory user store (replace this with a proper user database in a production environment)
const users = [
  { username: 'user1', password: 'password1' },
  { username: 'user2', password: 'password2' },
];
// Middleware to parse JSON in the request body
app.use(bodyParser.json());
// Logging Middleware
const loggingMiddleware = (req, res, next) => {
  console.log(`[${new Date().toISOString()}] ${req.method} ${req.url}`);
  next(); // Call the next middleware in the stack
};
// Authentication Middleware
const authenticationMiddleware = (req, res, next) => {
  // Check if the request has a valid username and password
  const { username, password } = req.body;
  console.log(`[${new Date().toISOString()}] Attempted login with username: ${username}`);
  // Query the in-memory user store for the user
  const user = users.find((u) => u.username === username && u.password === password);
  if (user) {
    // If valid, set the user in the request for future middleware/route access
    req.user = user;
    console.log(`[${new Date().toISOString()}] Authentication successful for ${username}`);
    next();
  } else {
    // If not valid, invoke the error handling middleware
    console.log(`[${new Date().toISOString()}] Authentication failed for ${username}`);
    next(new Error('Unauthorized'));
  }
};
// Error Handling Middleware
const errorHandlerMiddleware = (err, req, res, next) => {
  console.error(err.stack);
  // Handle authentication errors
  if (err.message === 'Unauthorized') {
    res.status(401).send('Unauthorized');
  } else {
    // Handle other errors with a generic 500 status
    res.status(500).send('Internal Server Error');
  }
};
// Use the logging middleware, authentication middleware, and error handling middleware
app.use(loggingMiddleware);
// Route accessible without authentication
app.get('/', (req, res) => {
  res.send('Hello, Express with In-Memory Authentication Middleware!');
});
// Route for login using POST
app.post('/login', authenticationMiddleware, (req, res) => {
  res.send(`Hello, ${req.user.username}! Login successful`);
});
app.use(errorHandlerMiddleware);
// Start the server
app.listen(port, () => {
  console.log(`Server is running at http://localhost:${port}`);
});