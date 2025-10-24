# Quick Start Guide - Bookshelf API

Get up and running in 5 minutes! 🚀

## Prerequisites
- Java 17+
- Docker (for database)
- Maven

## Steps

### 1. Start Database (30 seconds)
```bash
docker run -d \
  --name bookshelf-db \
  -e POSTGRES_DB=bookshelf \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=postgres \
  -p 5432:5432 \
  postgres:15-alpine
```

### 2. Build Application (2 minutes)
```bash
mvn clean package
```

### 3. Run Application (instant)
```bash
java -jar target/app.jar
```

You should see:
```
🚀 Bookshelf API started on port 7070
📚 Route overview available at: http://localhost:7070/api/routes
```

### 4. Test It! (1 minute)

Open your browser or use curl:

#### View all routes
```bash
curl http://localhost:7070/api/routes
```

#### Register a user
```bash
curl -X POST http://localhost:7070/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username": "test", "password": "test123"}'
```

#### Login
```bash
curl -X POST http://localhost:7070/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "test", "password": "test123"}'
```

Save the token from the response!

#### Create a book (use your token)
```bash
curl -X POST http://localhost:7070/api/books \
  -H "Authorization: Bearer YOUR_TOKEN_HERE" \
  -H "Content-Type: application/json" \
  -d '{"title": "My Book", "author": "Me"}'
```

#### Get all books
```bash
curl -X GET http://localhost:7070/api/books \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

## Using test.http File

Better yet, use the included `test.http` file with IntelliJ IDEA or VS Code REST Client:

1. Open `test.http`
2. Click "Run" on each request
3. Follow the test flow from top to bottom

## Pre-populated Test Data

The app automatically creates test users and books:

**Test Users:**
- `alice` / `password123`
- `bob` / `securepass`
- `admin` / `admin123`

Just login with these credentials to see sample books!

## Running Tests

```bash
# Run all tests
mvn test

# Run only endpoint tests
mvn test -Dtest=EndpointTest

# Run only repository tests
mvn test -Dtest=*RepositoryTest
```

## Common Issues

### Port 5432 already in use
```bash
# Stop existing PostgreSQL
docker stop bookshelf-db
docker rm bookshelf-db
```

### Port 7070 already in use
```bash
# Find and kill process
lsof -i :7070
kill -9 <PID>
```

### Cannot connect to database
```bash
# Check if container is running
docker ps

# Check logs
docker logs bookshelf-db
```

## Next Steps

- ✅ Check out `README.md` for detailed documentation
- ✅ See `TESTING.md` for testing guide
- ✅ Read `DEPLOYMENT.md` for deployment instructions
- ✅ Review `PROJECT_SUMMARY.md` for what's included

## API Endpoints Summary

| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| POST | `/api/auth/register` | No | Register user |
| POST | `/api/auth/login` | No | Login & get JWT |
| GET | `/api/books` | Yes | List all books |
| GET | `/api/books/{id}` | Yes | Get one book |
| POST | `/api/books` | Yes | Create book |
| PUT | `/api/books/{id}` | Yes | Update book |
| DELETE | `/api/books/{id}` | Yes | Delete book |
| GET | `/api/routes` | No | View all routes |

That's it! You're ready to go! 🎉
