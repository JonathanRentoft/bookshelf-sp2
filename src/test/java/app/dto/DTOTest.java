package app.dto;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

class DTOTest {

    @Test
    @DisplayName("UserDTO should be created correctly")
    void testUserDTO() {
        // Act
        UserDTO dto = new UserDTO("testuser", "password123");
        dto.setMessage("Test message");

        // Assert
        assertEquals("testuser", dto.getUsername());
        assertEquals("password123", dto.getPassword());
        assertEquals("Test message", dto.getMessage());
    }

    @Test
    @DisplayName("BookDTO should be created with all fields")
    void testBookDTOWithId() {
        // Act
        BookDTO dto = new BookDTO(1L, "The Hobbit", "J.R.R. Tolkien");

        // Assert
        assertEquals(1L, dto.getId());
        assertEquals("The Hobbit", dto.getTitle());
        assertEquals("J.R.R. Tolkien", dto.getAuthor());
    }

    @Test
    @DisplayName("BookDTO should be created without ID")
    void testBookDTOWithoutId() {
        // Act
        BookDTO dto = new BookDTO("1984", "George Orwell");

        // Assert
        assertNull(dto.getId());
        assertEquals("1984", dto.getTitle());
        assertEquals("George Orwell", dto.getAuthor());
    }

    @Test
    @DisplayName("AuthResponseDTO should contain username and token")
    void testAuthResponseDTO() {
        // Act
        AuthResponseDTO dto = new AuthResponseDTO("testuser", "jwt.token.here");

        // Assert
        assertEquals("testuser", dto.getUsername());
        assertEquals("jwt.token.here", dto.getToken());
    }

    @Test
    @DisplayName("ErrorDTO should contain error message")
    void testErrorDTO() {
        // Act
        ErrorDTO dto = new ErrorDTO("Something went wrong");

        // Assert
        assertEquals("Something went wrong", dto.getError());
    }

    @Test
    @DisplayName("DTOs should support setters")
    void testDTOSetters() {
        // Arrange
        BookDTO bookDTO = new BookDTO();

        // Act
        bookDTO.setId(5L);
        bookDTO.setTitle("New Title");
        bookDTO.setAuthor("New Author");

        // Assert
        assertEquals(5L, bookDTO.getId());
        assertEquals("New Title", bookDTO.getTitle());
        assertEquals("New Author", bookDTO.getAuthor());
    }
}