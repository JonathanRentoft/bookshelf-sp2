package app;

import app.controller.AuthController;
import app.controller.BookController;
import app.repository.BookRepository;
import app.repository.UserRepository;
import app.service.BookService;
import app.service.UserService;
import io.javalin.Javalin;
import io.javalin.rendering.template.JavalinThymeleaf;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import io.javalin.plugin.bundled.RouteOverviewPlugin;

public class Main {
    public static void main(String[] args) {

        // Initialize JPA EntityManagerFactory
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("bookshelfPU");
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

        // Initializing Javalin and Jetty webserver with JWT Access Manager
        Javalin app = Javalin.create(config -> {
            config.registerPlugin(new RouteOverviewPlugin(pluginConfig -> {
                pluginConfig.path = "/api/routes";
            }));


        }).start(7070);

        // Register API routes
        authController.registerRoutes(app);
        bookController.registerRoutes(app);

        // shutdown
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            em.close();
            emf.close();
        }));
    }
}