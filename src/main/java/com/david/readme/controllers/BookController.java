package com.david.readme.controllers;

import com.david.readme.dtos.GetBooksByCategory;
import com.david.readme.models.Book;
import com.david.readme.dtos.GetAllBooksRequest;
import com.david.readme.models.Category;
import com.david.readme.services.BookService;
import org.apache.coyote.Response;
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
    public BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping
    public ResponseEntity<List<GetAllBooksRequest>> getAllBooks() {
        return ResponseEntity.status(HttpStatus.OK)
                .header(HttpHeaders.LOCATION, "/api/v1/books")
                .body(this.bookService.getAllBooks().stream().map(b -> new GetAllBooksRequest(
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

    @GetMapping("/{bookSlug}")
    public ResponseEntity<Book> getBookBySlug(@PathVariable String bookSlug) {
        Book book = this.bookService.getBookBySlug(bookSlug).orElse(null);
        return book != null
                ?  ResponseEntity.ok(book)
                : ResponseEntity.notFound().build();
    }

    @GetMapping("/categories/{categoryName}")
    public ResponseEntity<List<GetBooksByCategory>> getAllCategories(@PathVariable String categoryName) {
        List<GetBooksByCategory> books = this.bookService.getBooksByCategory(categoryName);

        if(books.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        return ResponseEntity.ok(books);
    }
}

