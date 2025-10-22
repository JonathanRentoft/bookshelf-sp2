package app.entities;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    @DisplayName("User should be created with username and password")
    void testUserCreation() {
        // Act
        User user = new User("testuser", "hashedpassword");

        // Assert
        assertNotNull(user);
        assertEquals("testuser", user.getUsername());
        assertEquals("hashedpassword", user.getPassword());
        assertEquals("USER", user.getRole()); // Default role
        assertNotNull(user.getBooks());
        assertTrue(user.getBooks().isEmpty());
    }

    @Test
    @DisplayName("User should have default role USER")
    void testDefaultRole() {
        // Act
        User user = new User("testuser", "password");

        // Assert
        assertEquals("USER", user.getRole());
    }

    @Test
    @DisplayName("User books list should be modifiable")
    void testBooksListModifiable() {
        // Arrange
        User user = new User("testuser", "password");
        Book book = new Book("Test Book", "Test Author", user);

        // Act
        user.getBooks().add(book);

        // Assert
        assertEquals(1, user.getBooks().size());
        assertEquals("Test Book", user.getBooks().get(0).getTitle());
    }

    @Test
    @DisplayName("User setters should work correctly")
    void testSetters() {
        // Arrange
        User user = new User();

        // Act
        user.setId(1L);
        user.setUsername("newuser");
        user.setPassword("newpassword");
        user.setRole("ADMIN");

        // Assert
        assertEquals(1L, user.getId());
        assertEquals("newuser", user.getUsername());
        assertEquals("newpassword", user.getPassword());
        assertEquals("ADMIN", user.getRole());
    }
}