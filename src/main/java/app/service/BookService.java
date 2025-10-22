package app.service;

import app.dto.BookDTO;
import app.entities.Book;
import app.entities.User;
import app.repository.BookRepository;

import java.util.List;
import java.util.stream.Collectors;

public class BookService {

    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public List<BookDTO> getAllBooksByUser(Long userId) {
        return bookRepository.findByUserId(userId).stream()
                .map(book -> new BookDTO(book.getId(), book.getTitle(), book.getAuthor()))
                .collect(Collectors.toList());
    }

    public BookDTO getBookById(Long bookId, Long userId) throws Exception {
        Book book = bookRepository.findByIdAndUserId(bookId, userId);
        if (book == null) {
            throw new Exception("Book not found or does not belong to user");
        }
        return new BookDTO(book.getId(), book.getTitle(), book.getAuthor());
    }

    public BookDTO createBook(BookDTO bookDTO, User user) {
        Book book = new Book(bookDTO.getTitle(), bookDTO.getAuthor(), user);
        Book savedBook = bookRepository.save(book);
        return new BookDTO(savedBook.getId(), savedBook.getTitle(), savedBook.getAuthor());
    }

    public BookDTO updateBook(Long bookId, BookDTO bookDTO, Long userId) throws Exception {
        Book book = bookRepository.findByIdAndUserId(bookId, userId);
        if (book == null) {
            throw new Exception("Book not found or does not belong to user");
        }

        book.setTitle(bookDTO.getTitle());
        book.setAuthor(bookDTO.getAuthor());
        Book updatedBook = bookRepository.update(book);

        return new BookDTO(updatedBook.getId(), updatedBook.getTitle(), updatedBook.getAuthor());
    }

    public void deleteBook(Long bookId, Long userId) throws Exception {
        Book book = bookRepository.findByIdAndUserId(bookId, userId);
        if (book == null) {
            throw new Exception("Book not found or does not belong to user");
        }

        bookRepository.delete(book);
    }
}