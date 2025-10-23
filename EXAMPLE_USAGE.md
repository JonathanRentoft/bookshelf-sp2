# 🚀 Complete Example: How Your API Works

This is a **step-by-step real-world example** of how someone would use your API.

---

## 📖 Scenario: Alice wants to manage her book collection

### Step 1: Alice Creates an Account

**Request:**
```bash
curl -X POST http://localhost:7070/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username": "alice", "password": "mypassword"}'
```

**Response:**
```json
{
  "username": "alice",
  "message": "User created"
}
```

✅ Alice now has an account!

---

### Step 2: Alice Logs In

**Request:**
```bash
curl -X POST http://localhost:7070/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "alice", "password": "mypassword"}'
```

**Response:**
```json
{
  "username": "alice",
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhbGljZSIsInJvbGUiOiJVU0VSIiwiaWF0IjoxNjk4NDAwMDAwLCJleHAiOjE2OTg0ODY0MDB9.abcd1234..."
}
```

✅ Alice receives her JWT token! She saves it for the next steps.

---

### Step 3: Alice Adds Her First Book

**Request:**
```bash
curl -X POST http://localhost:7070/api/books \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhbGljZSIsInJvbGUiOiJVU0VSIiwiaWF0IjoxNjk4NDAwMDAwLCJleHAiOjE2OTg0ODY0MDB9.abcd1234..." \
  -d '{"title": "The Hobbit", "author": "J.R.R. Tolkien"}'
```

**Response:**
```json
{
  "id": 1,
  "title": "The Hobbit",
  "author": "J.R.R. Tolkien"
}
```

✅ Book created! Notice the `Authorization` header with the token.

---

### Step 4: Alice Adds Another Book

**Request:**
```bash
curl -X POST http://localhost:7070/api/books \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9..." \
  -d '{"title": "1984", "author": "George Orwell"}'
```

**Response:**
```json
{
  "id": 2,
  "title": "1984",
  "author": "George Orwell"
}
```

---

### Step 5: Alice Views All Her Books

**Request:**
```bash
curl -X GET http://localhost:7070/api/books \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9..."
```

**Response:**
```json
[
  {
    "id": 1,
    "title": "The Hobbit",
    "author": "J.R.R. Tolkien"
  },
  {
    "id": 2,
    "title": "1984",
    "author": "George Orwell"
  }
]
```

✅ Alice can see all her books!

---

### Step 6: Alice Updates a Book

**Request:**
```bash
curl -X PUT http://localhost:7070/api/books/1 \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9..." \
  -d '{"title": "The Hobbit - Special Edition", "author": "J.R.R. Tolkien"}'
```

**Response:**
```json
{
  "id": 1,
  "title": "The Hobbit - Special Edition",
  "author": "J.R.R. Tolkien"
}
```

---

### Step 7: Alice Deletes a Book

**Request:**
```bash
curl -X DELETE http://localhost:7070/api/books/2 \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9..."
```

**Response:**
```
204 No Content
```

✅ Book deleted!

---

## 🔒 What Happens Behind the Scenes?

### When Alice sends a request with her token:

```
1. Request arrives at BookController
   ↓
2. JwtAuthFilter.authenticate() is called
   ↓
3. Token is extracted from "Authorization" header
   ↓
4. Token is validated (signature + expiration check)
   ↓
5. Username "alice" is extracted from token
   ↓
6. Username is stored in context for the endpoint to use
   ↓
7. BookController gets Alice's username from context
   ↓
8. BookService fetches books belonging to Alice's user ID
   ↓
9. Response is sent back to Alice
```

---

## 🛡️ Security in Action

### ❌ What happens if Alice forgets her token?

**Request:**
```bash
curl -X GET http://localhost:7070/api/books
```

**Response:**
```json
{
  "error": "Missing or invalid Authorization header. Use: Bearer <token>"
}
```

**Status Code:** `401 Unauthorized`

---

### ❌ What happens if Bob tries to access Alice's books?

Bob logs in and gets his own token. Then he tries to see books:

**Request:**
```bash
curl -X GET http://localhost:7070/api/books \
  -H "Authorization: Bearer <BOB_TOKEN>"
```

**Response:**
```json
[]
```

Bob gets an empty list because he doesn't have any books! He can't see Alice's books because:
1. Bob's token contains "bob" as username
2. The API only fetches books where `user_id = bob's user ID`
3. Alice's books have `user_id = alice's user ID`

✅ **Each user's data is completely isolated!**

---

### ❌ What happens with an expired token?

After 24 hours, Alice's token expires. If she tries to use it:

**Response:**
```json
{
  "error": "Invalid or expired token"
}
```

**Status Code:** `401 Unauthorized`

Alice needs to login again to get a new token!

---

## 🎯 Key Takeaways

1. **Authentication = Login** → Proves you are who you say you are
2. **Token = Your Pass** → Like a movie ticket, proves you're authenticated
3. **Authorization Header** → How you show your pass
4. **User Isolation** → You can only see YOUR data
5. **24 Hour Expiry** → Tokens don't last forever (security!)

---

## 🧪 Try It Yourself!

1. Start your server: Run `Main.java`
2. Open the `test.http` file
3. Execute requests from top to bottom
4. Watch the magic happen! ✨

---

## 💡 Why This Matters

**Without JWT (Insecure):**
```
GET /api/books?username=alice
```
Anyone can pretend to be Alice by just typing her username!

**With JWT (Secure):**
```
GET /api/books
Authorization: Bearer <signed-token-that-only-Alice-has>
```
You need Alice's token, which requires her password to get!

---

**Your API is production-ready!** 🎉🔒
