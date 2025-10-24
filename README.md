# Bookshelf API 📚

A RESTful API for managing personal book collections with user authentication and authorization using JWT tokens.

## Features

- 🔐 User registration and authentication with JWT
- 📖 Full CRUD operations for books
- 🔒 Secure endpoints with role-based access control
- 🐘 PostgreSQL database with JPA/Hibernate
- 🧪 Comprehensive testing with JUnit, REST Assured, and TestContainers
- 🐳 Docker containerization for easy deployment
- 🚀 CI/CD pipeline with GitHub Actions
- 🔒 HTTPS support with Caddy reverse proxy

## Technology Stack

- **Framework**: Javalin 6.x
- **Database**: PostgreSQL 15
- **ORM**: Hibernate/JPA
- **Security**: JWT (JSON Web Tokens), BCrypt
- **Testing**: JUnit 5, REST Assured, Hamcrest, TestContainers
- **Build Tool**: Maven
- **Deployment**: Docker, Docker Compose, GitHub Actions, Caddy

## API Endpoints

### Authentication (Public)
- `POST /api/auth/register` - Register a new user
- `POST /api/auth/login` - Login and receive JWT token

### Books (Protected - Requires JWT)
- `GET /api/books` - Get all books for authenticated user
- `GET /api/books/{id}` - Get a specific book by ID
- `POST /api/books` - Create a new book
- `PUT /api/books/{id}` - Update an existing book
- `DELETE /api/books/{id}` - Delete a book

### Documentation (Public)
- `GET /api/routes` - View all available routes (Javalin route overview)

## Prerequisites

- Java 17 or higher
- Maven 3.6 or higher
- PostgreSQL 15 (or use Docker)
- Docker & Docker Compose (for deployment)

## Getting Started

### 1. Clone the repository
```bash
git clone https://github.com/yourusername/bookshelf-api.git
cd bookshelf-api
```

### 2. Set up PostgreSQL Database
```bash
# Using Docker
docker run --name bookshelf-postgres \
  -e POSTGRES_DB=bookshelf \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=postgres \
  -p 5432:5432 \
  -d postgres:15-alpine

# Or install PostgreSQL locally and create database
createdb bookshelf
```

### 3. Configure database connection
The application uses `src/main/resources/META-INF/persistence.xml` for local development.
For production, environment variables will be used.

### 4. Build the project
```bash
mvn clean package
```

### 5. Run the application
```bash
java -jar target/app.jar
```

The API will be available at `http://localhost:7070`

## Testing

### Run all tests
```bash
mvn test
```

### Test coverage includes:
- ✅ Repository (DAO) tests with TestContainers
- ✅ Service layer tests
- ✅ Endpoint tests with REST Assured
- ✅ Security tests (authentication, authorization)
- ✅ Error handling tests (400, 401, 404, etc.)

### Manual testing with .http file
Use the `test.http` file with IntelliJ IDEA HTTP Client or VS Code REST Client extension:
1. Start the application
2. Open `test.http`
3. Execute requests in order

## Example Usage

### Register a user
```bash
curl -X POST http://localhost:7070/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username": "alice", "password": "password123"}'
```

### Login
```bash
curl -X POST http://localhost:7070/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "alice", "password": "password123"}'
```

Response:
```json
{
  "username": "alice",
  "token": "eyJhbGciOiJIUzI1NiJ9..."
}
```

### Create a book (requires JWT token)
```bash
curl -X POST http://localhost:7070/api/books \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"title": "The Hobbit", "author": "J.R.R. Tolkien"}'
```

### Get all books
```bash
curl -X GET http://localhost:7070/api/books \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

## Deployment

See [DEPLOYMENT.md](DEPLOYMENT.md) for detailed deployment instructions.

### Quick Docker deployment
```bash
# Build the application
mvn clean package

# Build Docker image
docker build -t bookshelf-api .

# Run with Docker Compose
docker-compose up -d
```

## Project Structure

```
src/
├── main/
│   ├── java/app/
│   │   ├── config/         # Configuration classes
│   │   ├── controller/     # REST controllers
│   │   ├── dto/           # Data Transfer Objects
│   │   ├── entities/      # JPA entities
│   │   ├── exceptions/    # Custom exceptions
│   │   ├── repository/    # Data access layer
│   │   ├── security/      # JWT and security utilities
│   │   ├── service/       # Business logic layer
│   │   ├── utils/         # Utility classes
│   │   └── Main.java      # Application entry point
│   └── resources/
│       └── META-INF/
│           └── persistence.xml
└── test/
    └── java/app/
        ├── controller/    # Endpoint tests
        ├── dto/          # DTO tests
        ├── entities/     # Entity tests
        ├── repository/   # Repository tests
        ├── security/     # Security tests
        └── service/      # Service tests
```

## Security Features

- 🔐 Password hashing with BCrypt
- 🎫 JWT token-based authentication
- 🛡️ Role-based authorization (USER, ADMIN)
- 🔒 Protected endpoints
- 🚫 User isolation (users can only access their own books)

## Database Schema

### Users Table
- `id` (Primary Key)
- `username` (Unique)
- `password` (BCrypt hashed)
- `role` (USER/ADMIN)

### Books Table
- `id` (Primary Key)
- `title`
- `author`
- `user_id` (Foreign Key to Users)

## Environment Variables

For Docker/Production deployment:

- `DB_HOST` - Database host (default: localhost)
- `DB_PORT` - Database port (default: 5432)
- `DB_NAME` - Database name (default: bookshelf)
- `DB_USER` - Database user (default: postgres)
- `DB_PASSWORD` - Database password (default: postgres)
- `PORT` - Application port (default: 7070)
- `ENVIRONMENT` - Environment mode (development/production)

## CI/CD Pipeline

GitHub Actions workflow automatically:
1. ✅ Runs all tests
2. 🔨 Builds the application
3. 🐳 Creates Docker image
4. 📤 Pushes to Docker Hub
5. 🚀 Deploys to Digital Ocean droplet

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License.

## Author

Your Name - [@yourhandle](https://github.com/yourhandle)

## Acknowledgments

- Built as part of a backend development course
- Uses Javalin framework for REST API
- Inspired by modern microservices architecture

---

⭐ Star this repo if you find it helpful!
