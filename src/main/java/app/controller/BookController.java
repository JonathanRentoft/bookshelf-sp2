package app.controller;

import app.dto.BookDTO;
import app.dto.ErrorDTO;
import app.entity.User;
import app.security.Role;
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
        app.get("/api/books", this::getAllBooks, Role.USER);
        app.get("/api/books/{id}", this::getBookById, Role.USER);
        app.post("/api/books", this::createBook, Role.USER);
        app.put("/api/books/{id}", this::updateBook, Role.USER);
        app.delete("/api/books/{id}", this::deleteBook, Role.USER);
    }

    private void getAllBooks(Context ctx) {
        try {
            String username = ctx.attribute("username");
            User user = userService.findByUsername(username);

            if (user == null) {
                ctx.status(401).json(new ErrorDTO("User not found"));
                return;
            }

            List<BookDTO> books = bookService.getAllBooksByUser(user.getId());
            ctx.status(200).json(books);
        } catch (Exception e) {
            ctx.status(500).json(new ErrorDTO("Internal server error"));
        }
    }

    private void getBookById(Context ctx) {
        try {
            Long bookId = Long.parseLong(ctx.pathParam("id"));
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
        try {
            Long bookId = Long.parseLong(ctx.pathParam("id"));
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