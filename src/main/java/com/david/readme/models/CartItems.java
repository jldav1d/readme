package com.david.readme.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Embeddable
@Data
class CartItemId implements Serializable {
    private Long cartId;
    private Long bookId;
}

@Entity
@Table(name = "cart_items")
@Getter
@Setter
public class CartItems {
    @EmbeddedId
    private CartItemId id = new CartItemId();

    @ManyToOne
    // maps the cartId field in the composite key
    @MapsId("cartId")
    @JoinColumn(name = "cart_id")
    private Cart cart;

    @ManyToOne
    // maps the bookId field in the composite key
    @MapsId("bookId")
    @JoinColumn(name = "book_id")
    private Book book;

    @Column(name = "quantity", nullable = false)
    private Integer quantity = 1;
}
