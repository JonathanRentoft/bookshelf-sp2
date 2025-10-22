package app;

import app.config.SessionConfig;
import app.config.ThymeleafConfig;
import app.controller.AuthController;
import app.controller.BookController;
import app.repository.BookRepository;
import app.repository.UserRepository;
import app.security.JwtAccessManager;
import app.service.BookService;
import app.service.UserService;
import io.javalin.Javalin;
import io.javalin.rendering.template.JavalinThymeleaf;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class Main {
    public static void main(String[] args) {
        // Initializing Javalin and Jetty webserver

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
            config.staticFiles.add("/public");
            config.jetty.modifyServletContextHandler(handler -> handler.setSessionHandler(SessionConfig.sessionConfig()));
            config.fileRenderer(new JavalinThymeleaf(ThymeleafConfig.templateEngine()));
            config.router.mount(router -> {router.setRoleAccessManager(new JwtAccessManager());
            });
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