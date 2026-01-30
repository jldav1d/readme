package com.david.readme.repositories;

import com.david.readme.dtos.GetBooksByCategory;
import com.david.readme.models.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {
    Optional<Book> findByTitle(String title);
    Optional<Book> findBySlug(String slug);


    @Query("SELECT new com.david.readme.dtos.GetBooksByCategory(b.title, b.author, b.description, b.price, b.stock, b.slug, b.publishedAt)" +
            "FROM Book b JOIN b.categories c " +
            "WHERE LOWER(c.name) = :categoryName"
    )
    List<GetBooksByCategory> findBooksByCategory(@Param("categoryName") String categoryName);
}
