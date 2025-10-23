# 🔒 JWT Security Implementation Guide

## How It Works (Simple Explanation)

Your API is now **secured with JWT tokens**! Think of it like a movie ticket:

1. **Register/Login** → Get your ticket (JWT token)
2. **Use the ticket** → Show it every time you want to access protected endpoints
3. **Token expires** → Get a new ticket after 24 hours

## 🎯 How to Use Your Secured API

### Step 1: Register a New User

**Request:**
```http
POST http://localhost:7070/api/auth/register
Content-Type: application/json

{
  "username": "john",
  "password": "password123"
}
```

**Response:**
```json
{
  "username": "john",
  "message": "User created"
}
```

### Step 2: Login to Get Your Token

**Request:**
```http
POST http://localhost:7070/api/auth/login
Content-Type: application/json

{
  "username": "john",
  "password": "password123"
}
```

**Response:**
```json
{
  "username": "john",
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJqb2huIiwicm9sZSI6IlVTRVIiLCJpYXQiOjE2OTg0..."
}
```

**SAVE THIS TOKEN!** You need it for all other requests.

### Step 3: Use the Token to Access Protected Endpoints

Now for **ALL book endpoints**, you must include the token in the `Authorization` header:

**Example: Get All Books**
```http
GET http://localhost:7070/api/books
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJqb2huIiwicm9sZSI6IlVTRVIiLCJpYXQiOjE2OTg0...
```

**Example: Create a Book**
```http
POST http://localhost:7070/api/books
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJqb2huIiwicm9sZSI6IlVTRVIiLCJpYXQiOjE2OTg0...
Content-Type: application/json

{
  "title": "The Hobbit",
  "author": "J.R.R. Tolkien"
}
```

## 🔑 What Changed?

### Before (INSECURE ❌):
```http
GET http://localhost:7070/api/books?username=john
```
*Anyone could pretend to be "john"!*

### After (SECURE ✅):
```http
GET http://localhost:7070/api/books
Authorization: Bearer <your-jwt-token>
```
*Only someone with a valid token can access their data!*

## 🛡️ Security Features

1. **Token Validation**: Every protected endpoint checks if your token is valid
2. **User Identification**: The token contains your username, so you can only see YOUR books
3. **Expiration**: Tokens expire after 24 hours (you need to login again)
4. **Role-Based Access**: Tokens include user roles (USER, ADMIN) for future permissions

## 📝 All Protected Endpoints

These endpoints **REQUIRE** the `Authorization: Bearer <token>` header:

- `GET /api/books` - Get all your books
- `GET /api/books/{id}` - Get a specific book
- `POST /api/books` - Create a new book
- `PUT /api/books/{id}` - Update a book
- `DELETE /api/books/{id}` - Delete a book

## 🌐 Public Endpoints

These endpoints **DO NOT** require authentication:

- `POST /api/auth/register` - Register a new user
- `POST /api/auth/login` - Login and get a token

## ⚠️ Common Errors

### 401 Unauthorized
```json
{
  "error": "Missing or invalid Authorization header. Use: Bearer <token>"
}
```
**Fix**: Make sure you include the `Authorization` header with your token

### 401 Invalid Token
```json
{
  "error": "Invalid or expired token"
}
```
**Fix**: Your token expired or is invalid. Login again to get a new one.

### 404 User Not Found
```json
{
  "error": "User not found"
}
```
**Fix**: The user in your token doesn't exist in the database.

## 🎓 Technical Details (For Learning)

### How JWT Authentication Works:

1. **User logs in** → Server validates credentials
2. **Server generates JWT** → Contains username, role, expiration
3. **Server sends token** → Client stores it (localStorage, memory, etc.)
4. **Client uses token** → Includes it in `Authorization` header
5. **Server validates token** → Decodes and checks signature/expiration
6. **Server extracts user info** → Uses it to fetch user-specific data

### Token Structure:
A JWT has 3 parts separated by dots (`.`):
```
header.payload.signature
```

**Example decoded payload:**
```json
{
  "sub": "john",           // username
  "role": "USER",          // user role
  "iat": 1698400000,       // issued at (timestamp)
  "exp": 1698486400        // expires at (timestamp)
}
```

## 🔧 How to Add Admin Role Support (Future Enhancement)

If you want admin-only endpoints later:

```java
private void adminOnlyEndpoint(Context ctx) {
    // Authenticate AND check for admin role
    JwtAuthFilter.authenticateWithRole(ctx, "ADMIN");
    if (ctx.status() == 403) return; // Stop if not admin
    
    // Admin-only logic here
}
```

## 📱 Testing with Postman/Insomnia

1. Create a new request
2. Set method and URL
3. Go to "Headers" tab
4. Add header:
   - Key: `Authorization`
   - Value: `Bearer <paste-your-token-here>`
5. Send request!

---

**Your API is now secured!** 🎉
