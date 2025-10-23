package app.controller;

import app.dto.BookDTO;
import app.dto.ErrorDTO;
import app.entities.User;
import app.security.JwtAuthFilter;
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
        app.get("/api/books", this::getAllBooks);
        app.get("/api/books/{id}", this::getBookById);
        app.post("/api/books", this::createBook);
        app.put("/api/books/{id}", this::updateBook);
        app.delete("/api/books/{id}", this::deleteBook);
    }

    private void getAllBooks(Context ctx) {
        // Authenticate user with JWT
        JwtAuthFilter.authenticate(ctx);
        if (ctx.status() == 401) return; // Stop if authentication failed

        try {
            // Get username from JWT token (stored in context by the filter)
            String username = ctx.attribute("username");
            
            User user = userService.findByUsername(username);
            if (user == null) {
                ctx.status(404).json(new ErrorDTO("User not found"));
                return;
            }

            List<BookDTO> books = bookService.getAllBooksByUser(user.getId());
            ctx.status(200).json(books);
        } catch (Exception e) {
            ctx.status(500).json(new ErrorDTO("Server error: " + e.getMessage()));
        }
    }

    private void getBookById(Context ctx) {
        // Authenticate user with JWT
        JwtAuthFilter.authenticate(ctx);
        if (ctx.status() == 401) return; // Stop if authentication failed

        try {
            Long bookId = Long.parseLong(ctx.pathParam("id"));
            
            // Get username from JWT token
            String username = ctx.attribute("username");
            User user = userService.findByUsername(username);
            if (user == null) {
                ctx.status(401).json(new ErrorDTO("User not found"));
                return;
            }

            BookDTO book = bookService.getBookById(bookId, user.getId());
            ctx.status(200).json(book);
        } catch (NumberFormatException e) {
            ctx.status(400).json(new ErrorDTO("Invalid book ID"));
        } catch (Exception e) {
            if (e.getMessage().equals("Book not found or does not belong to user")) {
                ctx.status(404).json(new ErrorDTO(e.getMessage()));
            } else {
                ctx.status(500).json(new ErrorDTO("Internal server error"));
            }
        }
    }

    private void createBook(Context ctx) {
        // Authenticate user with JWT
        JwtAuthFilter.authenticate(ctx);
        if (ctx.status() == 401) return; // Stop if authentication failed

        try {
            BookDTO bookDTO = ctx.bodyAsClass(BookDTO.class);

            if (bookDTO.getTitle() == null || bookDTO.getTitle().isEmpty()) {
                ctx.status(400).json(new ErrorDTO("Title is required"));
                return;
            }

            if (bookDTO.getAuthor() == null || bookDTO.getAuthor().isEmpty()) {
                ctx.status(400).json(new ErrorDTO("Author is required"));
                return;
            }

            // Get username from JWT token
            String username = ctx.attribute("username");
            User user = userService.findByUsername(username);

            if (user == null) {
                ctx.status(401).json(new ErrorDTO("User not found"));
                return;
            }

            BookDTO createdBook = bookService.createBook(bookDTO, user);
            ctx.status(201).json(createdBook);
        } catch (Exception e) {
            ctx.status(500).json(new ErrorDTO("Internal server error"));
        }
    }

    private void updateBook(Context ctx) {
        // Authenticate user with JWT
        JwtAuthFilter.authenticate(ctx);
        if (ctx.status() == 401) return; // Stop if authentication failed

        try {
            Long bookId = Long.parseLong(ctx.pathParam("id"));
            BookDTO bookDTO = ctx.bodyAsClass(BookDTO.class);

            if (bookDTO.getTitle() == null || bookDTO.getTitle().isEmpty()) {
                ctx.status(400).json(new ErrorDTO("Title is required"));
                return;
            }

            if (bookDTO.getAuthor() == null || bookDTO.getAuthor().isEmpty()) {
                ctx.status(400).json(new ErrorDTO("Author is required"));
                return;
            }

            // Get username from JWT token
            String username = ctx.attribute("username");
            User user = userService.findByUsername(username);

            if (user == null) {
                ctx.status(401).json(new ErrorDTO("User not found"));
                return;
            }

            BookDTO updatedBook = bookService.updateBook(bookId, bookDTO, user.getId());
            ctx.status(200).json(updatedBook);
        } catch (NumberFormatException e) {
            ctx.status(400).json(new ErrorDTO("Invalid book ID"));
        } catch (Exception e) {
            if (e.getMessage().equals("Book not found or does not belong to user")) {
                ctx.status(404).json(new ErrorDTO(e.getMessage()));
            } else {
                ctx.status(500).json(new ErrorDTO("Internal server error"));
            }
        }
    }

    private void deleteBook(Context ctx) {
        // Authenticate user with JWT
        JwtAuthFilter.authenticate(ctx);
        if (ctx.status() == 401) return; // Stop if authentication failed

        try {
            Long bookId = Long.parseLong(ctx.pathParam("id"));
            
            // Get username from JWT token
            String username = ctx.attribute("username");
            User user = userService.findByUsername(username);
            if (user == null) {
                ctx.status(401).json(new ErrorDTO("User not found"));
                return;
            }

            bookService.deleteBook(bookId, user.getId());
            ctx.status(204);
        } catch (NumberFormatException e) {
            ctx.status(400).json(new ErrorDTO("Invalid book ID"));
        } catch (Exception e) {
            if (e.getMessage().equals("Book not found or does not belong to user")) {
                ctx.status(404).json(new ErrorDTO(e.getMessage()));
            } else {
                ctx.status(500).json(new ErrorDTO("Internal server error"));
            }
        }
    }
}