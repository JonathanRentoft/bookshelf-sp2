package app.security;

import app.dto.ErrorDTO;
import io.javalin.http.Context;

/**
 * Simple JWT Authentication Filter
 * This is a beginner-friendly approach that checks JWT tokens before allowing access to protected endpoints.
 */
public class JwtAuthFilter {

    /**
     * Validates JWT token and extracts user information
     * Call this in endpoints that need authentication
     */
    public static void authenticate(Context ctx) {
        // Get the Authorization header
        String authHeader = ctx.header("Authorization");
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            ctx.status(401).json(new ErrorDTO("Missing or invalid Authorization header. Use: Bearer <token>"));
            return;
        }

        // Extract token (remove "Bearer " prefix)
        String token = authHeader.substring(7);

        // Validate token
        if (!JwtUtil.isTokenValid(token)) {
            ctx.status(401).json(new ErrorDTO("Invalid or expired token"));
            return;
        }

        // Extract user info from token and store in context for the endpoint to use
        String username = JwtUtil.getUsernameFromToken(token);
        String role = JwtUtil.getRoleFromToken(token);

        ctx.attribute("username", username);
        ctx.attribute("role", role);
    }

    /**
     * Validates JWT token AND checks if user has required role
     * Use this for admin-only endpoints
     */
    public static void authenticateWithRole(Context ctx, String requiredRole) {
        // First do normal authentication
        authenticate(ctx);
        
        // If authentication failed, the response is already set, so just return
        if (ctx.status() == 401) {
            return;
        }

        // Check role
        String userRole = ctx.attribute("role");
        if (!requiredRole.equals(userRole)) {
            ctx.status(403).json(new ErrorDTO("Access denied. Required role: " + requiredRole));
        }
    }
}
