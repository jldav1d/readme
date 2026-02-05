package com.david.readme.controllers;

import com.david.readme.dtos.GetBooksByCategory;
import com.david.readme.dtos.PaginatedResponse;
import com.david.readme.models.Book;
import com.david.readme.dtos.BookRequest;
import com.david.readme.services.BookService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/books")
@CrossOrigin
public class BookController {
    public BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping
    public ResponseEntity<PaginatedResponse<BookRequest>> getAllBooks(@RequestParam Integer page, @RequestParam Integer pageSize) {
        PaginatedResponse<BookRequest> books = this.bookService.getAllBooks(page, pageSize);
        return ResponseEntity.status(HttpStatus.OK)
                .header(HttpHeaders.LOCATION, "/api/v1/books")
                .body(books);
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
        List<GetBooksByCategory> books = this.bookService.getBooksByCategoryName(categoryName);
        if(books.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(books);
    }

    @PostMapping("/add")
    public ResponseEntity<?> addBook(@RequestBody Book bookRequest) {

        System.out.println(bookRequest);

        if(this.bookService.isSlugADuplicate(bookRequest.getSlug())){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Duplicate slug is not allowed");
        }

        if(bookRequest.getPrice().compareTo(BigDecimal.ZERO) <= 0){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Negative price number is not allowed");
        }

        if(bookRequest.getStock() < 0){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Negative stock number is not allowed");
        }

        this.bookService.addNewBook(bookRequest);

        return ResponseEntity.ok(new SimpleMessage("Book added successfully"));
    }
}

record SimpleMessage(String message){
}