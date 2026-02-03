package com.david.readme.services;

import com.david.readme.dtos.BookRequest;
import com.david.readme.dtos.GetBooksByCategory;
import com.david.readme.exceptions.ResourceNotFoundException;
import com.david.readme.models.Book;
import com.david.readme.models.Category;
import com.david.readme.repositories.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class BookService {
    private BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public List<BookRequest> getAllBooks() {
        return bookRepository.findAll().stream()
                .map(this::convertToBookRequest)
                .collect(Collectors.toList());
    }

    public BookRequest getBookById(Long id) {
        Book book = this.bookRepository.findById(id).
                orElseThrow(() -> new ResourceNotFoundException("Unable to find book with id: "+ id));
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
