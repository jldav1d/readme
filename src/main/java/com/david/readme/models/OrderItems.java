package com.david.readme.models;


import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;

@Embeddable
@Data
class OrderItemId implements Serializable {
    private Long orderId;
    private Long bookId;
}

@Entity
@Table(name = "order_items")
@Getter
@Setter
public class OrderItems {
    @EmbeddedId
    private OrderItemId id = new OrderItemId();

    @ManyToOne
    @MapsId("bookId")
    @JoinColumn(name = "book_id")
    private Book book;

    @ManyToOne
    @MapsId("orderId")
    @JoinColumn(name = "order_id")
    private Order order;

    @Column(name = "purchased_price", nullable = false)
    private BigDecimal purchasedPrice;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;


}
