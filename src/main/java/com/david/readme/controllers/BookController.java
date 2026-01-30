package com.david.readme.controllers;

import com.david.readme.models.Book;
import com.david.readme.dtos.AllBooksRequest;
import com.david.readme.repositories.BookRepository;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@RestController
@RequestMapping("/api/v1/books")
public class BookController {
    public BookRepository bookRepository;

    public BookController(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @GetMapping
    public ResponseEntity<List<AllBooksRequest>> getAllBooks() {
        return ResponseEntity.status(HttpStatus.OK)
                .header(HttpHeaders.LOCATION, "/api/v1/books")
                .body(this.bookRepository.findAll().stream().map(b -> new AllBooksRequest(
                        b.getId(),
                        b.getTitle(),
                        b.getAuthor(),
                        b.getSlug(),
                        b.getPrice(),
                        b.getStock(),
                        b.getPublishedAt()
                    )
                ).toList());
    }

    @GetMapping("/{bookId}")
    public Book getBookById(@PathVariable Long bookId) {
        return this.bookRepository.findById(bookId).orElse(null);
    }
}

