package app.controller;

import app.Main;
import app.dto.AuthResponseDTO;
import app.dto.BookDTO;
import app.dto.UserDTO;
import app.entities.Book;
import app.entities.User;
import app.security.PasswordUtil;
import io.javalin.Javalin;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

@Testcontainers
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class EndpointTest {

    @Container
    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    private static Javalin app;
    private static EntityManagerFactory emf;
    private static EntityManager em;
    private static final int TEST_PORT = 7071;
    
    private static String aliceToken;
    private static String bobToken;
    private static Long aliceBookId;

    @BeforeAll
    static void setupAll() {
        // Set up TestContainers database
        Map<String, String> properties = new HashMap<>();
        properties.put("jakarta.persistence.jdbc.url", postgres.getJdbcUrl());
        properties.put("jakarta.persistence.jdbc.user", postgres.getUsername());
        properties.put("jakarta.persistence.jdbc.password", postgres.getPassword());
        properties.put("hibernate.hbm2ddl.auto", "create-drop");
        
        emf = Persistence.createEntityManagerFactory("bookshelfPU", properties);
        em = emf.createEntityManager();

        // Start Javalin application
        app = app.controller.TestHelper.startTestApp(emf, TEST_PORT);

        // Configure RestAssured
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = TEST_PORT;
        RestAssured.basePath = "/api";
    }

    @AfterAll
    static void teardownAll() {
        if (app != null) {
            app.stop();
        }
        if (em != null && em.isOpen()) {
            em.close();
        }
        if (emf != null) {
            emf.close();
        }
    }

    @BeforeEach
    void setup() {
        // Clean database before each test
        em.getTransaction().begin();
        em.createQuery("DELETE FROM Book").executeUpdate();
        em.createQuery("DELETE FROM User").executeUpdate();
        em.getTransaction().commit();
    }

    // ==================== AUTH ENDPOINTS ====================

    @Test
    @Order(1)
    @DisplayName("POST /api/auth/register - Should register a new user successfully")
    void testRegisterSuccess() {
        UserDTO userDTO = new UserDTO("alice", "password123");

        given()
            .contentType(ContentType.JSON)
            .body(userDTO)
        .when()
            .post("/auth/register")
        .then()
            .statusCode(201)
            .body("username", equalTo("alice"))
            .body("message", equalTo("User created"));
    }

    @Test
    @Order(2)
    @DisplayName("POST /api/auth/register - Should fail with duplicate username")
    void testRegisterDuplicateUsername() {
        // Create first user
        UserDTO userDTO = new UserDTO("alice", "password123");
        given().contentType(ContentType.JSON).body(userDTO).post("/auth/register");

        // Try to create duplicate
        given()
            .contentType(ContentType.JSON)
            .body(userDTO)
        .when()
            .post("/auth/register")
        .then()
            .statusCode(400)
            .body("message", equalTo("Username already exists"));
    }

    @Test
    @Order(3)
    @DisplayName("POST /api/auth/register - Should fail with missing username")
    void testRegisterMissingUsername() {
        UserDTO userDTO = new UserDTO(null, "password123");

        given()
            .contentType(ContentType.JSON)
            .body(userDTO)
        .when()
            .post("/auth/register")
        .then()
            .statusCode(400)
            .body("message", equalTo("Username is required"));
    }

    @Test
    @Order(4)
    @DisplayName("POST /api/auth/register - Should fail with missing password")
    void testRegisterMissingPassword() {
        UserDTO userDTO = new UserDTO("alice", null);

        given()
            .contentType(ContentType.JSON)
            .body(userDTO)
        .when()
            .post("/auth/register")
        .then()
            .statusCode(400)
            .body("message", equalTo("Password is required"));
    }

    @Test
    @Order(5)
    @DisplayName("POST /api/auth/login - Should login successfully and return JWT token")
    void testLoginSuccess() {
        // Register user first
        UserDTO userDTO = new UserDTO("alice", "password123");
        given().contentType(ContentType.JSON).body(userDTO).post("/auth/register");

        // Login
        given()
            .contentType(ContentType.JSON)
            .body(userDTO)
        .when()
            .post("/auth/login")
        .then()
            .statusCode(200)
            .body("username", equalTo("alice"))
            .body("token", notNullValue());
    }

    @Test
    @Order(6)
    @DisplayName("POST /api/auth/login - Should fail with wrong password")
    void testLoginWrongPassword() {
        // Register user first
        UserDTO registerDTO = new UserDTO("alice", "password123");
        given().contentType(ContentType.JSON).body(registerDTO).post("/auth/register");

        // Try to login with wrong password
        UserDTO loginDTO = new UserDTO("alice", "wrongpassword");
        given()
            .contentType(ContentType.JSON)
            .body(loginDTO)
        .when()
            .post("/auth/login")
        .then()
            .statusCode(401)
            .body("message", equalTo("Invalid username or password"));
    }

    @Test
    @Order(7)
    @DisplayName("POST /api/auth/login - Should fail with non-existent user")
    void testLoginNonExistentUser() {
        UserDTO userDTO = new UserDTO("nonexistent", "password123");

        given()
            .contentType(ContentType.JSON)
            .body(userDTO)
        .when()
            .post("/auth/login")
        .then()
            .statusCode(401)
            .body("message", equalTo("Invalid username or password"));
    }

    // ==================== BOOK ENDPOINTS ====================

    @Test
    @Order(10)
    @DisplayName("POST /api/books - Should create a book with valid token")
    void testCreateBookSuccess() {
        String token = registerAndLogin("alice", "password123");
        
        BookDTO bookDTO = new BookDTO(null, "The Hobbit", "J.R.R. Tolkien");

        given()
            .header("Authorization", "Bearer " + token)
            .contentType(ContentType.JSON)
            .body(bookDTO)
        .when()
            .post("/books")
        .then()
            .statusCode(201)
            .body("id", notNullValue())
            .body("title", equalTo("The Hobbit"))
            .body("author", equalTo("J.R.R. Tolkien"));
    }

    @Test
    @Order(11)
    @DisplayName("POST /api/books - Should fail without token")
    void testCreateBookNoToken() {
        BookDTO bookDTO = new BookDTO(null, "The Hobbit", "J.R.R. Tolkien");

        given()
            .contentType(ContentType.JSON)
            .body(bookDTO)
        .when()
            .post("/books")
        .then()
            .statusCode(401);
    }

    @Test
    @Order(12)
    @DisplayName("POST /api/books - Should fail with invalid token")
    void testCreateBookInvalidToken() {
        BookDTO bookDTO = new BookDTO(null, "The Hobbit", "J.R.R. Tolkien");

        given()
            .header("Authorization", "Bearer invalid-token")
            .contentType(ContentType.JSON)
            .body(bookDTO)
        .when()
            .post("/books")
        .then()
            .statusCode(401);
    }

    @Test
    @Order(13)
    @DisplayName("POST /api/books - Should fail with missing title")
    void testCreateBookMissingTitle() {
        String token = registerAndLogin("alice", "password123");
        
        BookDTO bookDTO = new BookDTO(null, null, "J.R.R. Tolkien");

        given()
            .header("Authorization", "Bearer " + token)
            .contentType(ContentType.JSON)
            .body(bookDTO)
        .when()
            .post("/books")
        .then()
            .statusCode(400)
            .body("message", equalTo("Title is required"));
    }

    @Test
    @Order(14)
    @DisplayName("POST /api/books - Should fail with missing author")
    void testCreateBookMissingAuthor() {
        String token = registerAndLogin("alice", "password123");
        
        BookDTO bookDTO = new BookDTO(null, "The Hobbit", null);

        given()
            .header("Authorization", "Bearer " + token)
            .contentType(ContentType.JSON)
            .body(bookDTO)
        .when()
            .post("/books")
        .then()
            .statusCode(400)
            .body("message", equalTo("Author is required"));
    }

    @Test
    @Order(20)
    @DisplayName("GET /api/books - Should return all books for authenticated user")
    void testGetAllBooksSuccess() {
        String token = registerAndLogin("alice", "password123");
        
        // Create two books
        createBook(token, "Book 1", "Author 1");
        createBook(token, "Book 2", "Author 2");

        given()
            .header("Authorization", "Bearer " + token)
        .when()
            .get("/books")
        .then()
            .statusCode(200)
            .body("size()", equalTo(2));
    }

    @Test
    @Order(21)
    @DisplayName("GET /api/books - Should return only user's own books")
    void testGetAllBooksOnlyOwnBooks() {
        String aliceToken = registerAndLogin("alice", "password123");
        String bobToken = registerAndLogin("bob", "securepass");
        
        // Alice creates 2 books
        createBook(aliceToken, "Alice Book 1", "Author 1");
        createBook(aliceToken, "Alice Book 2", "Author 2");
        
        // Bob creates 1 book
        createBook(bobToken, "Bob Book", "Author 3");

        // Alice should see only her 2 books
        given()
            .header("Authorization", "Bearer " + aliceToken)
        .when()
            .get("/books")
        .then()
            .statusCode(200)
            .body("size()", equalTo(2));

        // Bob should see only his 1 book
        given()
            .header("Authorization", "Bearer " + bobToken)
        .when()
            .get("/books")
        .then()
            .statusCode(200)
            .body("size()", equalTo(1));
    }

    @Test
    @Order(22)
    @DisplayName("GET /api/books - Should fail without token")
    void testGetAllBooksNoToken() {
        given()
        .when()
            .get("/books")
        .then()
            .statusCode(401);
    }

    @Test
    @Order(30)
    @DisplayName("GET /api/books/{id} - Should return book by id")
    void testGetBookByIdSuccess() {
        String token = registerAndLogin("alice", "password123");
        Long bookId = createBook(token, "Test Book", "Test Author");

        given()
            .header("Authorization", "Bearer " + token)
        .when()
            .get("/books/" + bookId)
        .then()
            .statusCode(200)
            .body("id", equalTo(bookId.intValue()))
            .body("title", equalTo("Test Book"))
            .body("author", equalTo("Test Author"));
    }

    @Test
    @Order(31)
    @DisplayName("GET /api/books/{id} - Should fail with non-existent book")
    void testGetBookByIdNotFound() {
        String token = registerAndLogin("alice", "password123");

        given()
            .header("Authorization", "Bearer " + token)
        .when()
            .get("/books/99999")
        .then()
            .statusCode(404)
            .body("message", equalTo("Book not found or does not belong to user"));
    }

    @Test
    @Order(32)
    @DisplayName("GET /api/books/{id} - Should fail accessing another user's book")
    void testGetBookByIdWrongUser() {
        String aliceToken = registerAndLogin("alice", "password123");
        String bobToken = registerAndLogin("bob", "securepass");
        
        Long aliceBookId = createBook(aliceToken, "Alice's Book", "Author");

        // Bob tries to access Alice's book
        given()
            .header("Authorization", "Bearer " + bobToken)
        .when()
            .get("/books/" + aliceBookId)
        .then()
            .statusCode(404)
            .body("message", equalTo("Book not found or does not belong to user"));
    }

    @Test
    @Order(33)
    @DisplayName("GET /api/books/{id} - Should fail with invalid book id")
    void testGetBookByIdInvalidId() {
        String token = registerAndLogin("alice", "password123");

        given()
            .header("Authorization", "Bearer " + token)
        .when()
            .get("/books/abc")
        .then()
            .statusCode(400)
            .body("message", equalTo("Invalid book ID"));
    }

    @Test
    @Order(40)
    @DisplayName("PUT /api/books/{id} - Should update book successfully")
    void testUpdateBookSuccess() {
        String token = registerAndLogin("alice", "password123");
        Long bookId = createBook(token, "Original Title", "Original Author");

        BookDTO updateDTO = new BookDTO(null, "Updated Title", "Updated Author");

        given()
            .header("Authorization", "Bearer " + token)
            .contentType(ContentType.JSON)
            .body(updateDTO)
        .when()
            .put("/books/" + bookId)
        .then()
            .statusCode(200)
            .body("id", equalTo(bookId.intValue()))
            .body("title", equalTo("Updated Title"))
            .body("author", equalTo("Updated Author"));
    }

    @Test
    @Order(41)
    @DisplayName("PUT /api/books/{id} - Should fail updating another user's book")
    void testUpdateBookWrongUser() {
        String aliceToken = registerAndLogin("alice", "password123");
        String bobToken = registerAndLogin("bob", "securepass");
        
        Long aliceBookId = createBook(aliceToken, "Alice's Book", "Author");

        BookDTO updateDTO = new BookDTO(null, "Hacked Title", "Hacked Author");

        // Bob tries to update Alice's book
        given()
            .header("Authorization", "Bearer " + bobToken)
            .contentType(ContentType.JSON)
            .body(updateDTO)
        .when()
            .put("/books/" + aliceBookId)
        .then()
            .statusCode(404)
            .body("message", equalTo("Book not found or does not belong to user"));
    }

    @Test
    @Order(42)
    @DisplayName("PUT /api/books/{id} - Should fail with missing title")
    void testUpdateBookMissingTitle() {
        String token = registerAndLogin("alice", "password123");
        Long bookId = createBook(token, "Original Title", "Original Author");

        BookDTO updateDTO = new BookDTO(null, null, "Updated Author");

        given()
            .header("Authorization", "Bearer " + token)
            .contentType(ContentType.JSON)
            .body(updateDTO)
        .when()
            .put("/books/" + bookId)
        .then()
            .statusCode(400)
            .body("message", equalTo("Title is required"));
    }

    @Test
    @Order(50)
    @DisplayName("DELETE /api/books/{id} - Should delete book successfully")
    void testDeleteBookSuccess() {
        String token = registerAndLogin("alice", "password123");
        Long bookId = createBook(token, "Test Book", "Test Author");

        given()
            .header("Authorization", "Bearer " + token)
        .when()
            .delete("/books/" + bookId)
        .then()
            .statusCode(204);

        // Verify book is deleted
        given()
            .header("Authorization", "Bearer " + token)
        .when()
            .get("/books/" + bookId)
        .then()
            .statusCode(404);
    }

    @Test
    @Order(51)
    @DisplayName("DELETE /api/books/{id} - Should fail deleting another user's book")
    void testDeleteBookWrongUser() {
        String aliceToken = registerAndLogin("alice", "password123");
        String bobToken = registerAndLogin("bob", "securepass");
        
        Long aliceBookId = createBook(aliceToken, "Alice's Book", "Author");

        // Bob tries to delete Alice's book
        given()
            .header("Authorization", "Bearer " + bobToken)
        .when()
            .delete("/books/" + aliceBookId)
        .then()
            .statusCode(404)
            .body("message", equalTo("Book not found or does not belong to user"));
    }

    @Test
    @Order(52)
    @DisplayName("DELETE /api/books/{id} - Should fail with non-existent book")
    void testDeleteBookNotFound() {
        String token = registerAndLogin("alice", "password123");

        given()
            .header("Authorization", "Bearer " + token)
        .when()
            .delete("/books/99999")
        .then()
            .statusCode(404)
            .body("message", equalTo("Book not found or does not belong to user"));
    }

    // ==================== HELPER METHODS ====================

    private String registerAndLogin(String username, String password) {
        UserDTO userDTO = new UserDTO(username, password);
        
        // Register
        given().contentType(ContentType.JSON).body(userDTO).post("/auth/register");
        
        // Login and get token
        AuthResponseDTO response = given()
            .contentType(ContentType.JSON)
            .body(userDTO)
            .post("/auth/login")
            .then()
            .statusCode(200)
            .extract()
            .as(AuthResponseDTO.class);
        
        return response.getToken();
    }

    private Long createBook(String token, String title, String author) {
        BookDTO bookDTO = new BookDTO(null, title, author);
        
        BookDTO response = given()
            .header("Authorization", "Bearer " + token)
            .contentType(ContentType.JSON)
            .body(bookDTO)
            .post("/books")
            .then()
            .statusCode(201)
            .extract()
            .as(BookDTO.class);
        
        return response.getId();
    }
}
