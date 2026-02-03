package com.david.readme.services;

import com.david.readme.dtos.BookRequest;
import com.david.readme.exceptions.ResourceNotFoundException;
import com.david.readme.models.Book;
import com.david.readme.models.Category;
import com.david.readme.repositories.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("BookService Unit Tests")
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookService bookService;

    private Book testBook1;
    private Book testBook2;
    private Category testCategory1;
    private Category testCategory2;

    @BeforeEach
    void setUp() {
        // Create test categories
        testCategory1 = new Category();
        testCategory1.setId(1L);
        testCategory1.setName("Programming");
        testCategory1.setCreatedAt(LocalDateTime.now());

        testCategory2 = new Category();
        testCategory2.setId(2L);
        testCategory2.setName("Design");
        testCategory2.setCreatedAt(LocalDateTime.now());

        // Create test book 1
        testBook1 = new Book();
        testBook1.setId(1L);
        testBook1.setTitle("Clean Code");
        testBook1.setAuthor("Robert C. Martin");
        testBook1.setDescription("A Handbook of Agile Software Craftsmanship");
        testBook1.setSlug("clean-code");
        testBook1.setPrice(new BigDecimal("45.99"));
        testBook1.setStock(10);
        testBook1.setPublishedAt(LocalDateTime.of(2008, 8, 1, 0, 0));
        testBook1.setCreatedAt(LocalDateTime.now());
        testBook1.setCategories(new HashSet<>(Collections.singletonList(testCategory1)));

        // Create test book 2
        testBook2 = new Book();
        testBook2.setId(2L);
        testBook2.setTitle("The Design of Everyday Things");
        testBook2.setAuthor("Don Norman");
        testBook2.setDescription("Revised and Expanded Edition");
        testBook2.setSlug("design-everyday-things");
        testBook2.setPrice(new BigDecimal("32.50"));
        testBook2.setStock(5);
        testBook2.setPublishedAt(LocalDateTime.of(2013, 11, 5, 0, 0));
        testBook2.setCreatedAt(LocalDateTime.now());
        testBook2.setCategories(new HashSet<>(Collections.singletonList(testCategory2)));
    }

    @Test
    @DisplayName("Should return all books when getAllBooks is called")
    void getAllBooks_ShouldReturnAllBooks() {
        // Arrange
        List<Book> books = Arrays.asList(testBook1, testBook2);
        when(bookRepository.findAll()).thenReturn(books);

        // Act
        List<BookRequest> result = bookService.getAllBooks();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Clean Code", result.get(0).title());
        assertEquals("The Design of Everyday Things", result.get(1).title());

        verify(bookRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return empty list when no books exist")
    void getAllBooks_WhenNoBooksExist_ShouldReturnEmptyList() {
        // Arrange
        when(bookRepository.findAll()).thenReturn(Collections.emptyList());

        // Act
        List<BookRequest> result = bookService.getAllBooks();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(bookRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return book when valid ID is provided")
    void getBookById_WithValidId_ShouldReturnBook() {
        // Arrange
        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook1));

        // Act
        BookRequest result = bookService.getBookById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("Clean Code", result.title());
        assertEquals("Robert C. Martin", result.author());
        assertEquals("A Handbook of Agile Software Craftsmanship", result.description());
        assertEquals("clean-code", result.slug());
        assertEquals(new BigDecimal("45.99"), result.price());
        assertEquals(10, result.stock());
        assertNotNull(result.publishedAt());
        assertNotNull(result.categories());
        assertTrue(result.categories().contains("Programming"));

        verify(bookRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should throw exception when book ID does not exist")
    void getBookById_WithInvalidId_ShouldThrowException() {
        // Arrange
        Long invalidId = 999L;
        when(bookRepository.findById(invalidId)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> bookService.getBookById(invalidId)
        );

        assertTrue(exception.getMessage().contains("Unable to find book with id: " + invalidId));

        verify(bookRepository, times(1)).findById(invalidId);
    }

    @Test
    @DisplayName("Should correctly map book entity to DTO")
    void convertToDTO_ShouldMapAllFields() {
        // Arrange
        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook1));

        // Act
        BookRequest result = bookService.getBookById(1L);

        // Assert
        assertAll("BookDTO fields",
                () -> assertEquals(testBook1.getId(), result.id()),
                () -> assertEquals(testBook1.getTitle(), result.title()),
                () -> assertEquals(testBook1.getAuthor(), result.author()),
                () -> assertEquals(testBook1.getDescription(), result.description()),
                () -> assertEquals(testBook1.getSlug(), result.slug()),
                () -> assertEquals(testBook1.getPrice(), result.price()),
                () -> assertEquals(testBook1.getStock(), result.stock()),
                () -> assertEquals(testBook1.getPublishedAt(), result.publishedAt())
        );
    }

    @Test
    @DisplayName("Should map categories correctly in DTO")
    void convertToDTO_ShouldMapCategories() {
        // Arrange
        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook1));

        // Act
        BookRequest result = bookService.getBookById(1L);

        // Assert
        assertNotNull(result.categories());
        assertEquals(1, result.categories().size());
        assertTrue(result.categories().contains("Programming"));
    }

    @Test
    @DisplayName("Should handle book with multiple categories")
    void convertToDTO_WithMultipleCategories_ShouldMapAll() {
        // Arrange
        testBook1.setCategories(new HashSet<>(Arrays.asList(testCategory1, testCategory2)));
        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook1));

        // Act
        BookRequest result = bookService.getBookById(1L);

        // Assert
        assertNotNull(result.categories());
        assertEquals(2, result.categories().size());
        assertTrue(result.categories().contains("Programming"));
        assertTrue(result.categories().contains("Design"));
    }

    @Test
    @DisplayName("Should handle book with no categories")
    void convertToDTO_WithNoCategories_ShouldReturnEmptySet() {
        // Arrange
        testBook1.setCategories(new HashSet<>());
        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook1));

        // Act
        BookRequest result = bookService.getBookById(1L);

        // Assert
        assertNotNull(result.categories());
        assertTrue(result.categories().isEmpty());
    }

    @Test
    @DisplayName("Should handle book with null published date")
    void convertToDTO_WithNullPublishedDate_ShouldHandleGracefully() {
        // Arrange
        testBook1.setPublishedAt(null);
        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook1));

        // Act
        BookRequest result = bookService.getBookById(1L);

        // Assert
        assertNull(result.publishedAt());
    }

    @Test
    @DisplayName("Should handle book with zero stock")
    void convertToDTO_WithZeroStock_ShouldReturnZero() {
        // Arrange
        testBook1.setStock(0);
        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook1));

        // Act
        BookRequest result = bookService.getBookById(1L);

        // Assert
        assertEquals(0, result.stock());
    }

    @Test
    @DisplayName("Should verify repository is called exactly once for getAllBooks")
    void getAllBooks_ShouldCallRepositoryOnce() {
        // Arrange
        when(bookRepository.findAll()).thenReturn(Arrays.asList(testBook1));

        // Act
        bookService.getAllBooks();

        // Assert
        verify(bookRepository, times(1)).findAll();
        verifyNoMoreInteractions(bookRepository);
    }

    @Test
    @DisplayName("Should verify repository is called exactly once for getBookById")
    void getBookById_ShouldCallRepositoryOnce() {
        // Arrange
        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook1));

        // Act
        bookService.getBookById(1L);

        // Assert
        verify(bookRepository, times(1)).findById(1L);
        verifyNoMoreInteractions(bookRepository);
    }

    @Test
    @DisplayName("Should handle multiple getAllBooks calls independently")
    void getAllBooks_MultipleCalls_ShouldWorkIndependently() {
        // Arrange
        when(bookRepository.findAll()).thenReturn(Arrays.asList(testBook1, testBook2));

        // Act
        List<BookRequest> result1 = bookService.getAllBooks();
        List<BookRequest> result2 = bookService.getAllBooks();

        // Assert
        assertEquals(result1.size(), result2.size());
        verify(bookRepository, times(2)).findAll();
    }

    @Test
    @DisplayName("Should preserve decimal precision in price")
    void convertToDTO_ShouldPreserveDecimalPrecision() {
        // Arrange
        testBook1.setPrice(new BigDecimal("99.99"));
        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook1));

        // Act
        BookRequest result = bookService.getBookById(1L);

        // Assert
        assertEquals(new BigDecimal("99.99"), result.price());
        assertEquals(0, new BigDecimal("99.99").compareTo(result.price()));
    }
}