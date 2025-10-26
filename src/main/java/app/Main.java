package app;

import app.controller.AuthController;
import app.controller.BookController;
import app.repository.BookRepository;
import app.repository.UserRepository;
import app.service.BookService;
import app.service.UserService;
import app.utils.DataSeeder;
import io.javalin.Javalin;
import io.javalin.openapi.plugin.OpenApiPlugin;
import io.javalin.openapi.plugin.swagger.SwaggerPlugin;
import io.javalin.rendering.template.JavalinThymeleaf;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class Main {
    public static void main(String[] args) {

        // Initialize JPA EntityManagerFactory
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("bookshelfPU");
        EntityManager em = emf.createEntityManager();
        
        // Seed database with test data
        DataSeeder.seedDatabase(em);

        // Initialize repositories
        UserRepository userRepository = new UserRepository(em);
        BookRepository bookRepository = new BookRepository(em);

        // Initialize services
        UserService userService = new UserService(userRepository);
        BookService bookService = new BookService(bookRepository);

        // Initialize controllers
        AuthController authController = new AuthController(userService);
        BookController bookController = new BookController(bookService, userService);

        // Initializing Javalin and Jetty webserver with OpenAPI and Swagger
        Javalin app = Javalin.create(config -> {
            config.plugins.register(new OpenApiPlugin(pluginConfig -> {
                pluginConfig.withDefinitionConfiguration((version, definition) -> {
                    definition.withInfo(info -> {
                        info.setTitle("Bookshelf API");
                        info.setVersion("1.0.0");
                        info.setDescription("API til at håndtere din personlige boghylde");
                    });
                });
            }));
            config.plugins.register(new SwaggerPlugin());
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