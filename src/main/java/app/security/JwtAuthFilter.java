package app.security;

import app.dto.ErrorDTO;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus; // <-- VIGTIG IMPORT

public class JwtAuthFilter {

    public static void authenticate(Context ctx) {
        String authHeader = ctx.header("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            ctx.status(401).json(new ErrorDTO("missing or invalid auth header"));
            return;
        }

        String token = authHeader.substring(7);

        if (!JwtUtil.isTokenValid(token)) {
            ctx.status(401).json(new ErrorDTO("invalid token"));
            return;
        }
        String username = JwtUtil.getUsernameFromToken(token);
        String role = JwtUtil.getRoleFromToken(token);

        ctx.attribute("username", username);
        ctx.attribute("role", role);
    }

    public static void authenticateWithRole(Context ctx, String requiredRole) {

        authenticate(ctx);

        if (ctx.status() == HttpStatus.UNAUTHORIZED) {
            return;
        }

        String userRole = ctx.attribute("role");

        if (userRole == null || !requiredRole.equals(userRole)) {
            ctx.status(403).json(new ErrorDTO("Access Denied"));
        }
    }
}