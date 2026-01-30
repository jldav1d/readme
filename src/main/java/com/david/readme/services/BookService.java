package com.david.readme.services;

import com.david.readme.dtos.GetBooksByCategory;
import com.david.readme.models.Book;
import com.david.readme.repositories.BookRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BookService {
    public BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    public Optional<Book> getBookByTitle(String bookTitle) {
        return this.bookRepository.findByTitle(bookTitle);
    }

    public Optional<Book> getBookBySlug(String bookSlug) {
        return this.bookRepository.findBySlug(bookSlug);
    }

    public List<GetBooksByCategory> getBooksByCategory(String categoryName) {
        return this.bookRepository.findBooksByCategory(categoryName);
    }

}
