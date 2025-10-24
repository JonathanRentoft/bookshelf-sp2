package app;

import app.config.DatabaseConfig;
import app.controller.AuthController;
import app.controller.BookController;
import app.repository.BookRepository;
import app.repository.UserRepository;
import app.service.BookService;
import app.service.UserService;
import app.utils.DatabasePopulator;
import io.javalin.Javalin;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

public class Main {
    public static void main(String[] args) {

        // Initialize JPA EntityManagerFactory with environment variable support
        EntityManagerFactory emf = DatabaseConfig.createEntityManagerFactory();
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

        // Populate database with test data (optional - comment out if not needed)
        // Only populate in development mode
        String environment = System.getenv("ENVIRONMENT");
        if (environment == null || environment.equals("development")) {
            DatabasePopulator populator = new DatabasePopulator(emf);
            populator.populate();
        }

        // Get port from environment or use default
        int port = System.getenv("PORT") != null ? 
                   Integer.parseInt(System.getenv("PORT")) : 7070;

        // Initializing Javalin and Jetty webserver
        Javalin app = Javalin.create(config -> {
            // Enable route overview plugin (publicly accessible)
            config.plugins.enableRouteOverview("/api/routes");
        }).start(port);

        System.out.println("🚀 Bookshelf API started on port " + port);
        System.out.println("📚 Route overview available at: http://localhost:" + port + "/api/routes");

        // Register API routes
        authController.registerRoutes(app);
        bookController.registerRoutes(app);

        // shutdown
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutting down Bookshelf API...");
            em.close();
            emf.close();
            app.stop();
        }));
    }
}