package app.service;

import app.dto.BookDTO;
import app.entities.Book;
import app.entities.User;
import app.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BookServiceTest {

    private BookRepository bookRepository;
    private BookService bookService;
    private User testUser;

    @BeforeEach
    void setUp() {
        bookRepository = mock(BookRepository.class);
        bookService = new BookService(bookRepository);
        testUser = new User("testuser", "hashedpassword");
        testUser.setId(1L);
    }

    @Test
    @DisplayName("Get all books should return user's books")
    void testGetAllBooksByUser() {
        // Arrange
        Book book1 = new Book("The Hobbit", "J.R.R. Tolkien", testUser);
        book1.setId(1L);
        Book book2 = new Book("1984", "George Orwell", testUser);
        book2.setId(2L);

        when(bookRepository.findByUserId(1L)).thenReturn(Arrays.asList(book1, book2));

        // Act
        List<BookDTO> result = bookService.getAllBooksByUser(1L);

        // Assert
        assertEquals(2, result.size());
        assertEquals("The Hobbit", result.get(0).getTitle());
        assertEquals("J.R.R. Tolkien", result.get(0).getAuthor());
        assertEquals("1984", result.get(1).getTitle());
        verify(bookRepository, times(1)).findByUserId(1L);
    }

    @Test
    @DisplayName("Get book by ID should return book when it belongs to user")
    void testGetBookByIdSuccess() throws Exception {
        // Arrange
        Book book = new Book("The Hobbit", "J.R.R. Tolkien", testUser);
        book.setId(1L);
        when(bookRepository.findByIdAndUserId(1L, 1L)).thenReturn(book);

        // Act
        BookDTO result = bookService.getBookById(1L, 1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("The Hobbit", result.getTitle());
        assertEquals("J.R.R. Tolkien", result.getAuthor());
    }

    @Test
    @DisplayName("Get book by ID should throw exception when book not found")
    void testGetBookByIdNotFound() {
        // Arrange
        when(bookRepository.findByIdAndUserId(999L, 1L)).thenReturn(null);

        // Act & Assert
        Exception exception = assertThrows(Exception.class, () -> {
            bookService.getBookById(999L, 1L);
        });

        assertEquals("Book not found or does not belong to user", exception.getMessage());
    }

    @Test
    @DisplayName("Create book should save and return book")
    void testCreateBook() {
        // Arrange
        BookDTO bookDTO = new BookDTO("New Book", "New Author");
        Book savedBook = new Book("New Book", "New Author", testUser);
        savedBook.setId(1L);

        when(bookRepository.save(any(Book.class))).thenReturn(savedBook);

        // Act
        BookDTO result = bookService.createBook(bookDTO, testUser);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("New Book", result.getTitle());
        assertEquals("New Author", result.getAuthor());
        verify(bookRepository, times(1)).save(any(Book.class));
    }

    @Test
    @DisplayName("Update book should update and return book")
    void testUpdateBookSuccess() throws Exception {
        // Arrange
        Book existingBook = new Book("Old Title", "Old Author", testUser);
        existingBook.setId(1L);

        BookDTO updateDTO = new BookDTO("Updated Title", "Updated Author");

        when(bookRepository.findByIdAndUserId(1L, 1L)).thenReturn(existingBook);
        when(bookRepository.update(any(Book.class))).thenReturn(existingBook);

        // Act
        BookDTO result = bookService.updateBook(1L, updateDTO, 1L);

        // Assert
        assertNotNull(result);
        assertEquals("Updated Title", result.getTitle());
        assertEquals("Updated Author", result.getAuthor());
        verify(bookRepository, times(1)).update(any(Book.class));
    }

    @Test
    @DisplayName("Update book should throw exception when book not found")
    void testUpdateBookNotFound() {
        // Arrange
        BookDTO updateDTO = new BookDTO("Updated Title", "Updated Author");
        when(bookRepository.findByIdAndUserId(999L, 1L)).thenReturn(null);

        // Act & Assert
        Exception exception = assertThrows(Exception.class, () -> {
            bookService.updateBook(999L, updateDTO, 1L);
        });

        assertEquals("Book not found or does not belong to user", exception.getMessage());
    }

    @Test
    @DisplayName("Delete book should remove book")
    void testDeleteBookSuccess() throws Exception {
        // Arrange
        Book book = new Book("The Hobbit", "J.R.R. Tolkien", testUser);
        book.setId(1L);
        when(bookRepository.findByIdAndUserId(1L, 1L)).thenReturn(book);

        // Act
        assertDoesNotThrow(() -> bookService.deleteBook(1L, 1L));

        // Assert
        verify(bookRepository, times(1)).delete(book);
    }

    @Test
    @DisplayName("Delete book should throw exception when book not found")
    void testDeleteBookNotFound() {
        // Arrange
        when(bookRepository.findByIdAndUserId(999L, 1L)).thenReturn(null);

        // Act & Assert
        Exception exception = assertThrows(Exception.class, () -> {
            bookService.deleteBook(999L, 1L);
        });

        assertEquals("Book not found or does not belong to user", exception.getMessage());
    }
}