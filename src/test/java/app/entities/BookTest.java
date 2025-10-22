package app.entities;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

class BookTest {

    @Test
    @DisplayName("Book should be created with title, author, and user")
    void testBookCreation() {
        // Arrange
        User user = new User("testuser", "password");

        // Act
        Book book = new Book("The Hobbit", "J.R.R. Tolkien", user);

        // Assert
        assertNotNull(book);
        assertEquals("The Hobbit", book.getTitle());
        assertEquals("J.R.R. Tolkien", book.getAuthor());
        assertEquals(user, book.getUser());
    }

    @Test
    @DisplayName("Book setters should work correctly")
    void testSetters() {
        // Arrange
        User user = new User("testuser", "password");
        Book book = new Book();

        // Act
        book.setId(1L);
        book.setTitle("1984");
        book.setAuthor("George Orwell");
        book.setUser(user);

        // Assert
        assertEquals(1L, book.getId());
        assertEquals("1984", book.getTitle());
        assertEquals("George Orwell", book.getAuthor());
        assertEquals(user, book.getUser());
    }

    @Test
    @DisplayName("Book should maintain relationship with user")
    void testUserRelationship() {
        // Arrange
        User user = new User("testuser", "password");
        user.setId(1L);

        // Act
        Book book = new Book("Test Book", "Test Author", user);

        // Assert
        assertNotNull(book.getUser());
        assertEquals("testuser", book.getUser().getUsername());
        assertEquals(1L, book.getUser().getId());
    }

    @Test
    @DisplayName("Book can be created with no-arg constructor")
    void testNoArgConstructor() {
        // Act
        Book book = new Book();

        // Assert
        assertNotNull(book);
        assertNull(book.getTitle());
        assertNull(book.getAuthor());
        assertNull(book.getUser());
    }
}
