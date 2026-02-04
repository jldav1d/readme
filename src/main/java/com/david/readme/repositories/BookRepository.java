package com.david.readme.repositories;

import com.david.readme.models.Book;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.Set;

public interface BookRepository extends JpaRepository<Book, Long> {
    Optional<Book> findByTitle(String title);
    Optional<Book> findBySlug(String slug);
    Set<Book> findBookByCategoriesName(String categoryName);
}
