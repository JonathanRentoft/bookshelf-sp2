package app;

import app.controller.AuthController;
import app.controller.BookController;
import app.repository.BookRepository;
import app.repository.UserRepository;
import app.service.BookService;
import app.service.UserService;
import io.javalin.Javalin;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class Main {
    public static void main(String[] args) {
        // Initializing Javalin and Jetty webserver

        // Initialize JPA EntityManagerFactory (læser fra persistence.xml)
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("bookshelfPU");
        EntityManager em = emf.createEntityManager();

        // Initialize repositories (håndterer database-operationer)
        UserRepository userRepository = new UserRepository(em);
        BookRepository bookRepository = new BookRepository(em);

        // Initialize services (forretningslogik)
        UserService userService = new UserService(userRepository);
        BookService bookService = new BookService(bookRepository);

        // Initialize controllers (håndterer HTTP requests)
        AuthController authController = new AuthController(userService);
        BookController bookController = new BookController(bookService, userService);

        // Start Javalin webserver (simpel version uden sikkerhed)
        Javalin app = Javalin.create().start(7070);

        // Register API routes (definerer endpoints)
        authController.registerRoutes(app);
        bookController.registerRoutes(app);

        System.out.println("Server kører på http://localhost:7070");

        // Shutdown hook (lukker database-forbindelse når programmet stopper)
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            em.close();
            emf.close();
            System.out.println("Database-forbindelse lukket");
        }));
    }
}