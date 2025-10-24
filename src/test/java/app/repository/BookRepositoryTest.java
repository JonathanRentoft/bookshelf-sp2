package app.repository;

import app.entities.Book;
import app.entities.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Testcontainers
class BookRepositoryTest {

    @Container
    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    private static EntityManagerFactory emf;
    private EntityManager em;
    private BookRepository bookRepository;
    private UserRepository userRepository;

    @BeforeAll
    static void setupAll() {
        Map<String, String> properties = new HashMap<>();
        properties.put("jakarta.persistence.jdbc.url", postgres.getJdbcUrl());
        properties.put("jakarta.persistence.jdbc.user", postgres.getUsername());
        properties.put("jakarta.persistence.jdbc.password", postgres.getPassword());
        properties.put("hibernate.hbm2ddl.auto", "create-drop");
        
        emf = Persistence.createEntityManagerFactory("bookshelfPU", properties);
    }

    @BeforeEach
    void setup() {
        em = emf.createEntityManager();
        bookRepository = new BookRepository(em);
        userRepository = new UserRepository(em);
    }

    @AfterEach
    void teardown() {
        if (em != null && em.isOpen()) {
            // Clean up data
            em.getTransaction().begin();
            em.createQuery("DELETE FROM Book").executeUpdate();
            em.createQuery("DELETE FROM User").executeUpdate();
            em.getTransaction().commit();
            em.close();
        }
    }

    @AfterAll
    static void teardownAll() {
        if (emf != null) {
            emf.close();
        }
    }

    @Test
    @DisplayName("Should save a book")
    void testSaveBook() {
        // Given
        User user = new User("testuser", "password");
        userRepository.save(user);
        
        Book book = new Book("Test Book", "Test Author", user);

        // When
        Book savedBook = bookRepository.save(book);

        // Then
        assertThat(savedBook.getId(), is(notNullValue()));
        assertThat(savedBook.getTitle(), is("Test Book"));
        assertThat(savedBook.getAuthor(), is("Test Author"));
    }

    @Test
    @DisplayName("Should find book by id")
    void testFindById() {
        // Given
        User user = new User("testuser", "password");
        userRepository.save(user);
        
        Book book = new Book("Test Book", "Test Author", user);
        Book savedBook = bookRepository.save(book);

        // When
        Book foundBook = bookRepository.findById(savedBook.getId());

        // Then
        assertThat(foundBook, is(notNullValue()));
        assertThat(foundBook.getTitle(), is("Test Book"));
        assertThat(foundBook.getAuthor(), is("Test Author"));
    }

    @Test
    @DisplayName("Should find books by user id")
    void testFindByUserId() {
        // Given
        User user1 = new User("user1", "password");
        User user2 = new User("user2", "password");
        userRepository.save(user1);
        userRepository.save(user2);
        
        bookRepository.save(new Book("Book 1", "Author 1", user1));
        bookRepository.save(new Book("Book 2", "Author 2", user1));
        bookRepository.save(new Book("Book 3", "Author 3", user2));

        // When
        List<Book> user1Books = bookRepository.findByUserId(user1.getId());
        List<Book> user2Books = bookRepository.findByUserId(user2.getId());

        // Then
        assertThat(user1Books, hasSize(2));
        assertThat(user2Books, hasSize(1));
    }

    @Test
    @DisplayName("Should find book by id and user id")
    void testFindByIdAndUserId() {
        // Given
        User user = new User("testuser", "password");
        userRepository.save(user);
        
        Book book = new Book("Test Book", "Test Author", user);
        Book savedBook = bookRepository.save(book);

        // When
        Book foundBook = bookRepository.findByIdAndUserId(savedBook.getId(), user.getId());

        // Then
        assertThat(foundBook, is(notNullValue()));
        assertThat(foundBook.getTitle(), is("Test Book"));
    }

    @Test
    @DisplayName("Should not find book with wrong user id")
    void testFindByIdAndWrongUserId() {
        // Given
        User user1 = new User("user1", "password");
        User user2 = new User("user2", "password");
        userRepository.save(user1);
        userRepository.save(user2);
        
        Book book = new Book("Test Book", "Test Author", user1);
        Book savedBook = bookRepository.save(book);

        // When
        Book foundBook = bookRepository.findByIdAndUserId(savedBook.getId(), user2.getId());

        // Then
        assertThat(foundBook, is(nullValue()));
    }

    @Test
    @DisplayName("Should update a book")
    void testUpdateBook() {
        // Given
        User user = new User("testuser", "password");
        userRepository.save(user);
        
        Book book = new Book("Original Title", "Original Author", user);
        Book savedBook = bookRepository.save(book);

        // When
        savedBook.setTitle("Updated Title");
        savedBook.setAuthor("Updated Author");
        Book updatedBook = bookRepository.update(savedBook);

        // Then
        assertThat(updatedBook.getTitle(), is("Updated Title"));
        assertThat(updatedBook.getAuthor(), is("Updated Author"));
    }

    @Test
    @DisplayName("Should delete a book")
    void testDeleteBook() {
        // Given
        User user = new User("testuser", "password");
        userRepository.save(user);
        
        Book book = new Book("Test Book", "Test Author", user);
        Book savedBook = bookRepository.save(book);
        Long bookId = savedBook.getId();

        // When
        bookRepository.delete(savedBook);

        // Then
        Book deletedBook = bookRepository.findById(bookId);
        assertThat(deletedBook, is(nullValue()));
    }
}
