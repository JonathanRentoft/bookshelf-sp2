package app.security;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    @Test
    @DisplayName("Generate token should create valid JWT")
    void testGenerateToken() {
        // Act
        String token = JwtUtil.generateToken("testuser", "USER");

        // Assert
        assertNotNull(token);
        assertTrue(token.length() > 0);
        assertTrue(token.startsWith("eyJ")); // JWT tokens start with eyJ
    }

    @Test
    @DisplayName("Validate token should return claims for valid token")
    void testValidateToken() {
        // Arrange
        String token = JwtUtil.generateToken("testuser", "USER");

        // Act
        Claims claims = JwtUtil.validateToken(token);

        // Assert
        assertNotNull(claims);
        assertEquals("testuser", claims.getSubject());
        assertEquals("USER", claims.get("role", String.class));
    }

    @Test
    @DisplayName("Validate token should return null for invalid token")
    void testValidateInvalidToken() {
        // Act
        Claims claims = JwtUtil.validateToken("invalid.token.here");

        // Assert
        assertNull(claims);
    }

    @Test
    @DisplayName("Get username from token should return correct username")
    void testGetUsernameFromToken() {
        // Arrange
        String token = JwtUtil.generateToken("john_doe", "USER");

        // Act
        String username = JwtUtil.getUsernameFromToken(token);

        // Assert
        assertEquals("john_doe", username);
    }

    @Test
    @DisplayName("Get role from token should return correct role")
    void testGetRoleFromToken() {
        // Arrange
        String token = JwtUtil.generateToken("admin", "ADMIN");

        // Act
        String role = JwtUtil.getRoleFromToken(token);

        // Assert
        assertEquals("ADMIN", role);
    }

    @Test
    @DisplayName("Is token valid should return true for valid token")
    void testIsTokenValid() {
        // Arrange
        String token = JwtUtil.generateToken("testuser", "USER");

        // Act
        boolean isValid = JwtUtil.isTokenValid(token);

        // Assert
        assertTrue(isValid);
    }

    @Test
    @DisplayName("Is token valid should return false for invalid token")
    void testIsTokenInvalid() {
        // Act
        boolean isValid = JwtUtil.isTokenValid("invalid.token.here");

        // Assert
        assertFalse(isValid);
    }

    @Test
    @DisplayName("Get username from invalid token should return null")
    void testGetUsernameFromInvalidToken() {
        // Act
        String username = JwtUtil.getUsernameFromToken("invalid.token");

        // Assert
        assertNull(username);
    }

    @Test
    @DisplayName("Get role from invalid token should return null")
    void testGetRoleFromInvalidToken() {
        // Act
        String role = JwtUtil.getRoleFromToken("invalid.token");

        // Assert
        assertNull(role);
    }
}