package com.david.readme.services;

import com.david.readme.dtos.BookRequest;
import com.david.readme.dtos.GetBooksByCategory;
import com.david.readme.dtos.PaginatedResponse;
import com.david.readme.exceptions.ResourceNotFoundException;
import com.david.readme.models.Book;
import com.david.readme.models.Category;
import com.david.readme.repositories.BookRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class BookService {
    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {this.bookRepository = bookRepository;}


//    @PreAuthorize("hasRole('ADMIN')")
    public void addNewBook(Book book){
        this.bookRepository.save(book);
    }

    public boolean isSlugADuplicate(String slug){
        return this.bookRepository.findBySlug(slug).orElse(null) != null;
    }

    public PaginatedResponse<BookRequest> getAllBooks(int currentPage, int pageSize) {
        Page<Book> bookPaginated = this.bookRepository.findAll(PageRequest.of(currentPage, pageSize, Sort.by("createdAt").descending()));
        List<BookRequest> bookRequests = bookPaginated.getContent().stream()
                .map(this::convertToBookRequest)
                .toList();
        return new PaginatedResponse<>(
            bookRequests,
            bookPaginated.getNumber(),
            bookPaginated.getTotalPages(),
            bookPaginated.getTotalElements(),
            pageSize,
            bookPaginated.hasNext(),
            bookPaginated.hasPrevious()
        );
    }

    public BookRequest getBookById(Long id) {
        Book book = this.bookRepository.findById(id).
                orElseThrow(() -> new ResourceNotFoundException("Unable to find book with id: " + id));
        return convertToBookRequest(book);
    }

    public Optional<Book> getBookByTitle(String bookTitle) {
        return this.bookRepository.findByTitle(bookTitle);
    }

    public Optional<Book> getBookBySlug(String bookSlug) {
        return this.bookRepository.findBySlug(bookSlug);
    }

    private BookRequest convertToBookRequest(Book book) {
        return new BookRequest(
                book.getId(),
                book.getTitle(),
                book.getAuthor(),
                book.getDescription(),
                book.getSlug(),
                book.getPrice(),
                book.getStock(),
                book.getPublishedAt(),
                book.getCategories().stream()
                        .map(Category::getName)
                        .collect(Collectors.toSet())
        );
    }

    public List<GetBooksByCategory> getBooksByCategoryName(String categoryName) {
        Set<Book> books = this.bookRepository.findBookByCategoriesName(categoryName);

        return books.stream()
                .map(b -> new GetBooksByCategory(
                        b.getTitle(),
                        b.getAuthor(),
                        b.getDescription(),
                        b.getPrice(),
                        b.getStock(),
                        b.getSlug(),
                        b.getPublishedAt()
                ))
                .collect(Collectors.toList());
    }

}
