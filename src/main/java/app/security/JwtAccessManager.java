package app.security;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import io.javalin.security.RouteRole;

public class JwtAccessManager implements io.javalin.security.RouteRolesAccessManager {

    @Override
    public void manage(Handler handler, Context ctx,
                       java.util.Set<? extends RouteRole> routeRoles)
            throws Exception {
        if (routeRoles.isEmpty() || routeRoles.contains(Role.ANYONE)) {
            handler.handle(ctx);
            return;
        }

        String authHeader = ctx.header("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            ctx.status(401).json(java.util.Map.of("error", "Unauthorized - No token provided"));
            return;
        }

        String token = authHeader.substring(7);

        if (!JwtUtil.isTokenValid(token)) {
            ctx.status(401).json(java.util.Map.of("error", "Unauthorized - Invalid token"));
            return;
        }

        String username = JwtUtil.getUsernameFromToken(token);
        String role = JwtUtil.getRoleFromToken(token);

        // Set user info in context for later use
        ctx.attribute("username", username);
        ctx.attribute("role", role);

        if (routeRoles.contains(Role.USER) && "USER".equals(role)) {
            handler.handle(ctx);
        } else if (routeRoles.contains(Role.ADMIN) && "ADMIN".equals(role)) {
            handler.handle(ctx);
        } else {
            ctx.status(403).json(java.util.Map.of("error", "Forbidden - Insufficient permissions"));
        }
    }
}