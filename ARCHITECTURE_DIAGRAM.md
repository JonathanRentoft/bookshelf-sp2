# 🏗️ API Architecture - Visual Guide

## 🎯 The Big Picture

```
┌─────────────────────────────────────────────────────────────┐
│                        CLIENT                                │
│  (Browser, Postman, Mobile App, Another Server)             │
└────────────────────┬────────────────────────────────────────┘
                     │
                     │ HTTP Request (JSON)
                     ↓
┌─────────────────────────────────────────────────────────────┐
│                    JAVALIN SERVER                            │
│                  (Port 7070)                                 │
│                                                              │
│  ┌────────────────────────────────────────────────────┐    │
│  │           AUTHENTICATION LAYER                      │    │
│  │         (JwtAuthFilter.java)                        │    │
│  │  • Checks "Authorization: Bearer <token>"          │    │
│  │  • Validates token signature                       │    │
│  │  • Extracts username & role                        │    │
│  │  • Returns 401 if invalid                          │    │
│  └──────────────────┬─────────────────────────────────┘    │
│                     │                                        │
│                     ↓                                        │
│  ┌────────────────────────────────────────────────────┐    │
│  │           CONTROLLER LAYER                          │    │
│  │    (AuthController, BookController)                 │    │
│  │  • Handles HTTP requests                           │    │
│  │  • Validates input data                            │    │
│  │  • Calls authentication filter                     │    │
│  │  • Sets HTTP status codes                          │    │
│  │  • Returns JSON responses                          │    │
│  └──────────────────┬─────────────────────────────────┘    │
│                     │                                        │
│                     ↓                                        │
│  ┌────────────────────────────────────────────────────┐    │
│  │            SERVICE LAYER                            │    │
│  │      (UserService, BookService)                     │    │
│  │  • Business logic                                  │    │
│  │  • Password hashing/verification                   │    │
│  │  • JWT token generation                            │    │
│  │  • Data transformation (Entity ↔ DTO)              │    │
│  └──────────────────┬─────────────────────────────────┘    │
│                     │                                        │
│                     ↓                                        │
│  ┌────────────────────────────────────────────────────┐    │
│  │          REPOSITORY LAYER                           │    │
│  │    (UserRepository, BookRepository)                 │    │
│  │  • Database operations (CRUD)                      │    │
│  │  • JPA queries                                     │    │
│  │  • Entity management                               │    │
│  └──────────────────┬─────────────────────────────────┘    │
│                     │                                        │
└─────────────────────┼────────────────────────────────────────┘
                      │
                      ↓
            ┌─────────────────────┐
            │     DATABASE        │
            │  (PostgreSQL/H2)    │
            │                     │
            │  ┌───────────────┐ │
            │  │ users table   │ │
            │  │ books table   │ │
            │  └───────────────┘ │
            └─────────────────────┘
```

---

## 🔄 Request Flow Example: "Create a Book"

### Step-by-Step Journey:

```
1️⃣  CLIENT SENDS REQUEST
    ↓
    POST http://localhost:7070/api/books
    Authorization: Bearer eyJhbGc...
    Body: {"title": "The Hobbit", "author": "Tolkien"}

2️⃣  JAVALIN RECEIVES REQUEST
    ↓
    Routes to: BookController.createBook()

3️⃣  AUTHENTICATION FILTER
    ↓
    JwtAuthFilter.authenticate(ctx)
    • Extracts token from header
    • Validates signature & expiration
    • Extracts username: "alice"
    • Stores in context: ctx.attribute("username", "alice")
    • If invalid → STOP, return 401

4️⃣  CONTROLLER VALIDATION
    ↓
    • Checks if authentication passed
    • Validates title is not empty
    • Validates author is not empty
    • Gets username from context
    • If invalid → STOP, return 400

5️⃣  SERVICE LAYER
    ↓
    BookService.createBook(bookDTO, user)
    • Creates Book entity
    • Associates with user
    • Calls repository to save

6️⃣  REPOSITORY LAYER
    ↓
    BookRepository.save(book)
    • Begins transaction
    • Persists to database
    • Commits transaction
    • Returns saved entity with ID

7️⃣  RESPONSE FLOWS BACK
    ↓
    Service → Controller → Client
    Status: 201 Created
    Body: {"id": 1, "title": "The Hobbit", "author": "Tolkien"}

✅  CLIENT RECEIVES RESPONSE
```

---

## 🔐 Authentication Flow: Login & Token

```
┌──────────┐
│  CLIENT  │
└────┬─────┘
     │
     │ 1. POST /api/auth/login
     │    {"username": "alice", "password": "pass123"}
     ↓
┌────────────────┐
│ AuthController │
└────┬───────────┘
     │
     │ 2. Validates input
     ↓
┌────────────────┐
│  UserService   │
└────┬───────────┘
     │
     │ 3. Finds user in database
     │ 4. Verifies password (bcrypt)
     │ 5. Generates JWT token with username & role
     ↓
┌──────────┐
│  JwtUtil │  Creates token:
└──────────┘  {
                "sub": "alice",      // username
                "role": "USER",      // user role
                "iat": 1698400000,   // issued at
                "exp": 1698486400    // expires at
              }
              Signed with secret key ✍️
     │
     │ 6. Returns token
     ↓
┌──────────┐
│  CLIENT  │  Receives:
└──────────┘  {
                "username": "alice",
                "token": "eyJhbGciOi..."
              }
              
              🎫 CLIENT SAVES TOKEN
              (Will use for all future requests)
```

---

## 🛡️ Security Check Flow

```
PROTECTED ENDPOINT REQUEST
         │
         ↓
    ┌─────────────────┐
    │ Has Auth Header?│ ─── NO ──→ ❌ 401 Unauthorized
    └────────┬────────┘
             YES
             ↓
    ┌─────────────────┐
    │ Format Correct? │ ─── NO ──→ ❌ 401 Invalid header
    └────────┬────────┘        (Must be "Bearer <token>")
             YES
             ↓
    ┌─────────────────┐
    │ Valid Signature?│ ─── NO ──→ ❌ 401 Invalid token
    └────────┬────────┘
             YES
             ↓
    ┌─────────────────┐
    │  Not Expired?   │ ─── NO ──→ ❌ 401 Token expired
    └────────┬────────┘
             YES
             ↓
    ┌─────────────────┐
    │ Extract username│
    └────────┬────────┘
             │
             ↓
         ✅ PROCEED TO ENDPOINT
         Username available in context
```

---

## 📊 Data Flow: Entity vs DTO

```
DATABASE                    SERVICE                   CLIENT
┌─────────┐                ┌─────┐                   ┌──────┐
│  Book   │                │DTO  │                   │ JSON │
│ Entity  │                │     │                   │      │
├─────────┤                ├─────┤                   ├──────┤
│ id      │ ─────────────→ │ id  │ ─────────────→   │ "id" │
│ title   │ ─────────────→ │title│ ─────────────→   │"title"│
│ author  │ ─────────────→ │author────────────────→ │"author"│
│ user_id │ ╳ (hidden)                               │      │
│ user    │ ╳ (hidden)                               │      │
└─────────┘                └─────┘                   └──────┘

Why DTO?
• Hides database structure (security)
• Prevents circular references (user → books → user)
• Cleaner API responses
• Easy JSON conversion
```

---

## 🎭 Role-Based Access (Future Feature)

```
                    REQUEST + TOKEN
                          │
                          ↓
                    ┌──────────┐
                    │ Validate │
                    │  Token   │
                    └─────┬────┘
                          │
                    Extract Role
                          │
              ┌───────────┴──────────┐
              │                      │
         role="USER"            role="ADMIN"
              │                      │
              ↓                      ↓
    ┌─────────────────┐    ┌─────────────────┐
    │ USER ENDPOINTS  │    │ ADMIN ENDPOINTS │
    ├─────────────────┤    ├─────────────────┤
    │ • Own books     │    │ • All books     │
    │ • Own profile   │    │ • All users     │
    │ • Create book   │    │ • Delete any    │
    └─────────────────┘    │ • Manage system │
                           └─────────────────┘
```

---

## 🗂️ File Structure

```
/workspace
├── src/main/java/app
│   ├── Main.java                    🚀 Application entry point
│   ├── controller/
│   │   ├── AuthController.java      🔓 Login/Register (PUBLIC)
│   │   └── BookController.java      🔒 Book CRUD (PROTECTED)
│   ├── service/
│   │   ├── UserService.java         🧠 User logic + JWT generation
│   │   └── BookService.java         🧠 Book logic
│   ├── repository/
│   │   ├── UserRepository.java      💾 User database ops
│   │   └── BookRepository.java      💾 Book database ops
│   ├── security/
│   │   ├── JwtAuthFilter.java       ✨ NEW! Authentication filter
│   │   ├── JwtUtil.java             🔐 Token generation/validation
│   │   ├── PasswordUtil.java        🔑 Password hashing
│   │   └── Role.java                👤 USER, ADMIN roles
│   ├── entities/
│   │   ├── User.java                📋 Database user model
│   │   └── Book.java                📋 Database book model
│   └── dto/
│       ├── BookDTO.java             📦 API book response
│       ├── UserDTO.java             📦 API user request/response
│       ├── AuthResponseDTO.java     📦 Login response with token
│       └── ErrorDTO.java            📦 Error response
│
├── test.http                        ✨ NEW! Manual API tests
├── JWT_USAGE_GUIDE.md               ✨ NEW! Usage documentation
├── EXAMPLE_USAGE.md                 ✨ NEW! Real-world examples
└── JWT_IMPLEMENTATION_SUMMARY.md    ✨ NEW! Implementation details
```

---

## 💡 Why This Architecture?

### Separation of Concerns:

| Layer | Responsibility | Can Change Without Affecting |
|-------|----------------|------------------------------|
| Controller | HTTP handling | Service, Repository |
| Service | Business logic | Controller, Repository |
| Repository | Database ops | Service, Controller |
| Entity | DB structure | DTO, Controller |
| DTO | API format | Entity, Repository |

### Benefits:

✅ **Testable** - Test each layer independently  
✅ **Maintainable** - Change one layer without breaking others  
✅ **Scalable** - Easy to add new features  
✅ **Secure** - Authentication layer protects everything  
✅ **Clean** - Each file has one job  

---

## 🎯 Quick Reference

### Public Endpoints (No Token):
```
POST /api/auth/register  → Create account
POST /api/auth/login     → Get token
```

### Protected Endpoints (Need Token):
```
GET    /api/books        → List my books
GET    /api/books/1      → Get book #1
POST   /api/books        → Create book
PUT    /api/books/1      → Update book #1
DELETE /api/books/1      → Delete book #1
```

### Token Format:
```
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

---

**Now you understand how everything connects!** 🧩✨
