package app.config;

import org.eclipse.jetty.server.session.SessionHandler;

public class SessionConfig {
    public static SessionHandler sessionConfig() {
        SessionHandler sessionHandler = new SessionHandler();
        sessionHandler.setHttpOnly(true);
        sessionHandler.setSecureRequestOnly(false); // Set to true in production with HTTPS
        sessionHandler.setMaxInactiveInterval(3600); // 1 hour
        return sessionHandler;
    }
}
