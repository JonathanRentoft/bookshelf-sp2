package app.repository;

import app.entities.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Testcontainers
class UserRepositoryTest {

    @Container
    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    private static EntityManagerFactory emf;
    private EntityManager em;
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
    @DisplayName("Should save a user")
    void testSaveUser() {
        // Given
        User user = new User("testuser", "password123");

        // When
        User savedUser = userRepository.save(user);

        // Then
        assertThat(savedUser.getId(), is(notNullValue()));
        assertThat(savedUser.getUsername(), is("testuser"));
        assertThat(savedUser.getPassword(), is("password123"));
        assertThat(savedUser.getRole(), is("USER"));
    }

    @Test
    @DisplayName("Should find user by username")
    void testFindByUsername() {
        // Given
        User user = new User("testuser", "password123");
        userRepository.save(user);

        // When
        User foundUser = userRepository.findByUsername("testuser");

        // Then
        assertThat(foundUser, is(notNullValue()));
        assertThat(foundUser.getUsername(), is("testuser"));
    }

    @Test
    @DisplayName("Should return null when user not found by username")
    void testFindByUsernameNotFound() {
        // When
        User foundUser = userRepository.findByUsername("nonexistent");

        // Then
        assertThat(foundUser, is(nullValue()));
    }

    @Test
    @DisplayName("Should find user by id")
    void testFindById() {
        // Given
        User user = new User("testuser", "password123");
        User savedUser = userRepository.save(user);

        // When
        User foundUser = userRepository.findById(savedUser.getId());

        // Then
        assertThat(foundUser, is(notNullValue()));
        assertThat(foundUser.getUsername(), is("testuser"));
    }

    @Test
    @DisplayName("Should check if username exists")
    void testExistsByUsername() {
        // Given
        User user = new User("testuser", "password123");
        userRepository.save(user);

        // When
        boolean exists = userRepository.existsByUsername("testuser");
        boolean notExists = userRepository.existsByUsername("nonexistent");

        // Then
        assertThat(exists, is(true));
        assertThat(notExists, is(false));
    }

    @Test
    @DisplayName("Should save user with custom role")
    void testSaveUserWithCustomRole() {
        // Given
        User user = new User("admin", "adminpass");
        user.setRole("ADMIN");

        // When
        User savedUser = userRepository.save(user);

        // Then
        assertThat(savedUser.getRole(), is("ADMIN"));
    }
}
