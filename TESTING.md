# Testing Guide for Bookshelf API

This document describes the comprehensive testing strategy implemented for the Bookshelf API.

## Test Coverage

The project includes three main types of tests:

### 1. Repository (DAO) Tests ✅
**Location**: `src/test/java/app/repository/`

- `BookRepositoryTest.java` - Tests for book data access operations
- `UserRepositoryTest.java` - Tests for user data access operations

**Features**:
- Uses TestContainers for real PostgreSQL database
- Tests all CRUD operations
- Tests complex queries (findByUserId, findByIdAndUserId)
- Ensures data isolation between users

**Running repository tests**:
```bash
mvn test -Dtest=*RepositoryTest
```

### 2. Service Layer Tests ✅
**Location**: `src/test/java/app/service/`

- `BookServiceTest.java` - Tests for book business logic
- `UserServiceTest.java` - Tests for user and authentication logic

**Features**:
- Unit tests with mocked dependencies
- Tests business logic and validation
- Tests error handling

**Running service tests**:
```bash
mvn test -Dtest=*ServiceTest
```

### 3. Endpoint (Integration) Tests ✅
**Location**: `src/test/java/app/controller/`

- `EndpointTest.java` - Comprehensive tests for all 7 API endpoints

**Features**:
- Uses REST Assured for HTTP testing
- Uses TestContainers for real database
- Tests all HTTP methods (GET, POST, PUT, DELETE)
- Tests authentication and authorization
- Tests error scenarios (400, 401, 404)
- Tests security (unauthorized access, wrong user access)

**Test Coverage**:

#### Authentication Endpoints (2 endpoints)
1. `POST /api/auth/register`
   - ✅ Successful registration
   - ✅ Duplicate username (400)
   - ✅ Missing username (400)
   - ✅ Missing password (400)

2. `POST /api/auth/login`
   - ✅ Successful login with JWT token
   - ✅ Wrong password (401)
   - ✅ Non-existent user (401)

#### Book Endpoints (5 endpoints)
3. `POST /api/books`
   - ✅ Create book with valid token
   - ✅ Fail without token (401)
   - ✅ Fail with invalid token (401)
   - ✅ Fail with missing title (400)
   - ✅ Fail with missing author (400)

4. `GET /api/books`
   - ✅ Return all books for authenticated user
   - ✅ Return only user's own books (isolation)
   - ✅ Fail without token (401)

5. `GET /api/books/{id}`
   - ✅ Return book by id
   - ✅ Fail with non-existent book (404)
   - ✅ Fail accessing another user's book (404)
   - ✅ Fail with invalid id format (400)

6. `PUT /api/books/{id}`
   - ✅ Update book successfully
   - ✅ Fail updating another user's book (404)
   - ✅ Fail with missing title (400)

7. `DELETE /api/books/{id}`
   - ✅ Delete book successfully
   - ✅ Fail deleting another user's book (404)
   - ✅ Fail with non-existent book (404)

**Running endpoint tests**:
```bash
mvn test -Dtest=EndpointTest
```

### 4. Unit Tests ✅
**Location**: `src/test/java/app/`

Additional unit tests for:
- `JwtUtilTest.java` - JWT token generation and validation
- `PasswordUtilTest.java` - Password hashing and verification
- `DTOTest.java` - Data Transfer Objects
- `BookTest.java` - Book entity tests
- `UserTest.java` - User entity tests

## Running All Tests

```bash
# Run all tests
mvn test

# Run tests with coverage report
mvn test jacoco:report

# Run specific test class
mvn test -Dtest=EndpointTest

# Run specific test method
mvn test -Dtest=EndpointTest#testRegisterSuccess

# Skip tests during build
mvn package -DskipTests
```

## Test Technologies

- **JUnit 5** - Testing framework
- **Hamcrest** - Assertion library for readable tests
- **REST Assured** - HTTP client for API testing
- **TestContainers** - Docker containers for integration testing
- **Mockito** - Mocking framework for unit tests

## Manual Testing

### Using test.http file

The project includes a `test.http` file for manual testing with:
- IntelliJ IDEA HTTP Client
- VS Code REST Client extension

**Steps**:
1. Start the application: `java -jar target/app.jar`
2. Open `test.http`
3. Execute requests in order:
   - Register users
   - Login to get JWT tokens
   - Test book CRUD operations
   - Test error cases

### Using Postman/Thunder Client

1. Import the endpoints from `test.http`
2. Set up environment variables for tokens
3. Test each endpoint manually

## Test Database

### Development
Tests use TestContainers to spin up temporary PostgreSQL databases:
- Automatically starts before tests
- Automatically cleaned up after tests
- Each test class gets a fresh database
- No configuration needed

### CI/CD
GitHub Actions workflow runs all tests:
- Uses PostgreSQL service container
- Runs tests in isolated environment
- Fails build if any test fails

## Best Practices Implemented

1. ✅ **Isolation**: Each test is independent
2. ✅ **Cleanup**: Database cleaned after each test
3. ✅ **Real Dependencies**: Uses real database via TestContainers
4. ✅ **Security Testing**: Tests authentication and authorization
5. ✅ **Error Testing**: Tests all error cases (400, 401, 404)
6. ✅ **Happy Path**: Tests successful operations
7. ✅ **Edge Cases**: Tests boundary conditions
8. ✅ **User Isolation**: Tests data access restrictions

## Test Execution Order

For endpoint tests, tests are ordered to ensure:
1. Authentication tests run first
2. CRUD operations tested in logical order
3. Complex scenarios tested last

## Continuous Integration

The GitHub Actions workflow (`deploy.yml`) automatically:
1. Runs all tests on every push
2. Fails the build if tests fail
3. Only deploys if all tests pass

## Test Data

The `DatabasePopulator` class provides sample data:
- 3 test users (alice, bob, admin)
- 7 sample books
- Only populates in development mode
- Can be used for manual testing

## Troubleshooting Tests

### Tests failing locally
```bash
# Clean and rebuild
mvn clean install

# Check Docker is running (for TestContainers)
docker ps

# Check PostgreSQL port is not in use
lsof -i :5432
```

### TestContainers issues
- Ensure Docker is running
- Check Docker has enough resources
- Pull PostgreSQL image manually: `docker pull postgres:15-alpine`

### Port conflicts
- Ensure port 7071 (test port) is available
- Kill any processes using the port: `kill -9 $(lsof -t -i:7071)`

## Coverage Goals

Current test coverage:
- ✅ All 7 API endpoints
- ✅ Repository layer (all CRUD methods)
- ✅ Service layer (key business logic)
- ✅ Security (JWT and password utilities)
- ✅ Error handling
- ✅ DTOs and Entities

## Next Steps

To further improve testing:
1. Add performance tests (load testing)
2. Add integration tests with external APIs (if applicable)
3. Add contract tests (Pact)
4. Measure and improve code coverage percentage
5. Add mutation testing (PIT)

---

Happy Testing! 🧪
