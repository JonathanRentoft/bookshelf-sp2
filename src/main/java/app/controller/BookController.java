package app.controller;

import app.dto.BookDTO;
import app.dto.ErrorDTO;
import app.entities.User;
import app.service.BookService;
import app.service.UserService;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.List;

public class BookController {

    private final BookService bookService;
    private final UserService userService;

    public BookController(BookService bookService, UserService userService) {
        this.bookService = bookService;
        this.userService = userService;
    }

    public void registerRoutes(Javalin app) {
        // SIMPEL VERSION: Ingen sikkerhed endnu!
        // Du kan tilføje sikkerhed senere når du forstår det basale
        app.get("/api/books", this::getAllBooks);
        app.get("/api/books/{id}", this::getBookById);
        app.post("/api/books", this::createBook);
        app.put("/api/books/{id}", this::updateBook);
        app.delete("/api/books/{id}", this::deleteBook);
    }

    private void getAllBooks(Context ctx) {
        try {
            // SIMPEL VERSION: Hent brugernavn fra query parameter
            // Eksempel: GET /api/books?username=john
            String username = ctx.queryParam("username");
            
            if (username == null || username.isEmpty()) {
                ctx.status(400).json(new ErrorDTO("username parameter er påkrævet"));
                return;
            }

            User user = userService.findByUsername(username);
            if (user == null) {
                ctx.status(404).json(new ErrorDTO("Bruger ikke fundet"));
                return;
            }

            List<BookDTO> books = bookService.getAllBooksByUser(user.getId());
            ctx.status(200).json(books);
        } catch (Exception e) {
            ctx.status(500).json(new ErrorDTO("Server fejl: " + e.getMessage()));
        }
    }

    private void getBookById(Context ctx) {
        try {
            Long bookId = Long.parseLong(ctx.pathParam("id"));
            String username = ctx.queryParam("username");
            
            if (username == null || username.isEmpty()) {
                ctx.status(400).json(new ErrorDTO("username parameter er påkrævet"));
                return;
            }

            User user = userService.findByUsername(username);
            if (user == null) {
                ctx.status(404).json(new ErrorDTO("Bruger ikke fundet"));
                return;
            }

            BookDTO book = bookService.getBookById(bookId, user.getId());
            ctx.status(200).json(book);
        } catch (NumberFormatException e) {
            ctx.status(400).json(new ErrorDTO("Ugyldigt bog ID"));
        } catch (Exception e) {
            if (e.getMessage().equals("Book not found or does not belong to user")) {
                ctx.status(404).json(new ErrorDTO("Bog ikke fundet"));
            } else {
                ctx.status(500).json(new ErrorDTO("Server fejl: " + e.getMessage()));
            }
        }
    }

    private void createBook(Context ctx) {
        try {
            BookDTO bookDTO = ctx.bodyAsClass(BookDTO.class);

            if (bookDTO.getTitle() == null || bookDTO.getTitle().isEmpty()) {
                ctx.status(400).json(new ErrorDTO("Titel er påkrævet"));
                return;
            }

            if (bookDTO.getAuthor() == null || bookDTO.getAuthor().isEmpty()) {
                ctx.status(400).json(new ErrorDTO("Forfatter er påkrævet"));
                return;
            }

            String username = ctx.queryParam("username");
            if (username == null || username.isEmpty()) {
                ctx.status(400).json(new ErrorDTO("username parameter er påkrævet"));
                return;
            }

            User user = userService.findByUsername(username);
            if (user == null) {
                ctx.status(404).json(new ErrorDTO("Bruger ikke fundet"));
                return;
            }

            BookDTO createdBook = bookService.createBook(bookDTO, user);
            ctx.status(201).json(createdBook);
        } catch (Exception e) {
            ctx.status(500).json(new ErrorDTO("Server fejl: " + e.getMessage()));
        }
    }

    private void updateBook(Context ctx) {
        try {
            Long bookId = Long.parseLong(ctx.pathParam("id"));
            BookDTO bookDTO = ctx.bodyAsClass(BookDTO.class);

            if (bookDTO.getTitle() == null || bookDTO.getTitle().isEmpty()) {
                ctx.status(400).json(new ErrorDTO("Titel er påkrævet"));
                return;
            }

            if (bookDTO.getAuthor() == null || bookDTO.getAuthor().isEmpty()) {
                ctx.status(400).json(new ErrorDTO("Forfatter er påkrævet"));
                return;
            }

            String username = ctx.queryParam("username");
            if (username == null || username.isEmpty()) {
                ctx.status(400).json(new ErrorDTO("username parameter er påkrævet"));
                return;
            }

            User user = userService.findByUsername(username);
            if (user == null) {
                ctx.status(404).json(new ErrorDTO("Bruger ikke fundet"));
                return;
            }

            BookDTO updatedBook = bookService.updateBook(bookId, bookDTO, user.getId());
            ctx.status(200).json(updatedBook);
        } catch (NumberFormatException e) {
            ctx.status(400).json(new ErrorDTO("Ugyldigt bog ID"));
        } catch (Exception e) {
            if (e.getMessage().equals("Book not found or does not belong to user")) {
                ctx.status(404).json(new ErrorDTO("Bog ikke fundet"));
            } else {
                ctx.status(500).json(new ErrorDTO("Server fejl: " + e.getMessage()));
            }
        }
    }

    private void deleteBook(Context ctx) {
        try {
            Long bookId = Long.parseLong(ctx.pathParam("id"));
            String username = ctx.queryParam("username");
            
            if (username == null || username.isEmpty()) {
                ctx.status(400).json(new ErrorDTO("username parameter er påkrævet"));
                return;
            }

            User user = userService.findByUsername(username);
            if (user == null) {
                ctx.status(404).json(new ErrorDTO("Bruger ikke fundet"));
                return;
            }

            bookService.deleteBook(bookId, user.getId());
            ctx.status(204);
        } catch (NumberFormatException e) {
            ctx.status(400).json(new ErrorDTO("Ugyldigt bog ID"));
        } catch (Exception e) {
            if (e.getMessage().equals("Book not found or does not belong to user")) {
                ctx.status(404).json(new ErrorDTO("Bog ikke fundet"));
            } else {
                ctx.status(500).json(new ErrorDTO("Server fejl: " + e.getMessage()));
            }
        }
    }
}