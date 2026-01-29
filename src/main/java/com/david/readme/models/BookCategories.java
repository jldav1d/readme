package com.david.readme.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Embeddable
@Data
class BookCategoryId implements Serializable {
    private Long categoryId;
    private Long bookId;
}

@Entity
@Table(name = "book_categories")
@Getter
@Setter
public class BookCategories {
    @EmbeddedId
    private BookCategoryId id = new BookCategoryId();

    @ManyToOne
    @MapsId("bookId")
    @JoinColumn(name = "book_id")
    private Book book;

    @ManyToOne
    @MapsId("categoryId")
    @JoinColumn(name = "category_id")
    private Category category;
}
