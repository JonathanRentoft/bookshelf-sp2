package app.security;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

class PasswordUtilTest {

    @Test
    @DisplayName("Hash password should create BCrypt hash")
    void testHashPassword() {
        // Arrange
        String password = "password123";

        // Act
        String hashedPassword = PasswordUtil.hashPassword(password);

        // Assert
        assertNotNull(hashedPassword);
        assertTrue(hashedPassword.startsWith("$2a$")); // BCrypt hashes start with $2a$
        assertNotEquals(password, hashedPassword);
    }

    @Test
    @DisplayName("Hash password should create different hashes for same password")
    void testHashPasswordUniqueSalt() {
        // Arrange
        String password = "password123";

        // Act
        String hash1 = PasswordUtil.hashPassword(password);
        String hash2 = PasswordUtil.hashPassword(password);

        // Assert
        assertNotEquals(hash1, hash2); // Each hash should be unique due to salt
    }

    @Test
    @DisplayName("Verify password should return true for correct password")
    void testVerifyPasswordCorrect() {
        // Arrange
        String password = "password123";
        String hashedPassword = PasswordUtil.hashPassword(password);

        // Act
        boolean isValid = PasswordUtil.verifyPassword(password, hashedPassword);

        // Assert
        assertTrue(isValid);
    }

    @Test
    @DisplayName("Verify password should return false for incorrect password")
    void testVerifyPasswordIncorrect() {
        // Arrange
        String correctPassword = "password123";
        String wrongPassword = "wrongpassword";
        String hashedPassword = PasswordUtil.hashPassword(correctPassword);

        // Act
        boolean isValid = PasswordUtil.verifyPassword(wrongPassword, hashedPassword);

        // Assert
        assertFalse(isValid);
    }


    @Test
    @DisplayName("Hash password should handle empty string")
    void testHashPasswordEmpty() {
        // Arrange
        String password = "";

        // Act
        String hashedPassword = PasswordUtil.hashPassword(password);

        // Assert
        assertNotNull(hashedPassword);
        assertTrue(hashedPassword.startsWith("$2a$"));
    }

    @Test
    @DisplayName("Hash password should handle special characters")
    void testHashPasswordSpecialCharacters() {
        // Arrange
        String password = "p@ssw0rd!#$%^&*()";

        // Act
        String hashedPassword = PasswordUtil.hashPassword(password);
        boolean isValid = PasswordUtil.verifyPassword(password, hashedPassword);

        // Assert
        assertNotNull(hashedPassword);
        assertTrue(isValid);
    }
}
