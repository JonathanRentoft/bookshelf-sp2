package app.service;

import app.dto.AuthResponseDTO;
import app.dto.UserDTO;
import app.entities.User;
import app.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    private UserRepository userRepository;
    private UserService userService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        userService = new UserService(userRepository);
    }

    @Test
    @DisplayName("Register should create new user successfully")
    void testRegisterSuccess() throws Exception {
        // Arrange
        UserDTO userDTO = new UserDTO("testuser", "password123");
        when(userRepository.existsByUsername("testuser")).thenReturn(false);

        // Act
        UserDTO result = userService.register(userDTO);

        // Assert
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertEquals("User created", result.getMessage());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Register should throw exception when username exists")
    void testRegisterDuplicateUsername() {
        // Arrange
        UserDTO userDTO = new UserDTO("existinguser", "password123");
        when(userRepository.existsByUsername("existinguser")).thenReturn(true);

        // Act & Assert
        Exception exception = assertThrows(Exception.class, () -> {
            userService.register(userDTO);
        });

        assertEquals("Username already exists", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Login should throw exception for invalid username")
    void testLoginInvalidUsername() {
        // Arrange
        UserDTO userDTO = new UserDTO("nonexistent", "password123");
        when(userRepository.findByUsername("nonexistent")).thenReturn(null);

        // Act & Assert
        Exception exception = assertThrows(Exception.class, () -> {
            userService.login(userDTO);
        });

        assertEquals("Invalid username or password", exception.getMessage());
    }

    @Test
    @DisplayName("Login should throw exception for invalid password")
    void testLoginInvalidPassword() {
        // Arrange
        String username = "testuser";
        String correctPassword = "password123";
        String wrongPassword = "wrongpassword";
        String hashedPassword = "$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5pXuxb7OLQBYW";

        User user = new User(username, hashedPassword);
        when(userRepository.findByUsername(username)).thenReturn(user);

        UserDTO userDTO = new UserDTO(username, wrongPassword);

        // Act & Assert
        Exception exception = assertThrows(Exception.class, () -> {
            userService.login(userDTO);
        });

        assertEquals("Invalid username or password", exception.getMessage());
    }
}