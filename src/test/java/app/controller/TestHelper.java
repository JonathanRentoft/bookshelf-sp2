package app.controller;

import app.repository.BookRepository;
import app.repository.UserRepository;
import app.service.BookService;
import app.service.UserService;
import io.javalin.Javalin;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

/**
 * Helper class to set up test application
 */
public class TestHelper {

    public static Javalin startTestApp(EntityManagerFactory emf, int port) {
        EntityManager em = emf.createEntityManager();

        // Initialize repositories
        UserRepository userRepository = new UserRepository(em);
        BookRepository bookRepository = new BookRepository(em);

        // Initialize services
        UserService userService = new UserService(userRepository);
        BookService bookService = new BookService(bookRepository);

        // Initialize controllers
        AuthController authController = new AuthController(userService);
        BookController bookController = new BookController(bookService, userService);

        // Create Javalin app (without route overview for tests)
        Javalin app = Javalin.create().start(port);

        // Register routes
        authController.registerRoutes(app);
        bookController.registerRoutes(app);

        return app;
    }
}
