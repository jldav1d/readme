package com.david.readme.models;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "orders")
@Getter @Setter
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "total_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalPrice;

    @CreationTimestamp
    @Column(name = "date_ordered", nullable = false, updatable = false)
    private LocalDateTime dateOrdered;


    // points to the member "order" in the OrderItems table
    @OneToMany(mappedBy = "order")
    private Set<OrderItems> orderItems = new HashSet<>();

    public Set<Book> getOrderItems() {
        return orderItems.stream()
                .map(OrderItems::getBook)
                .collect(Collectors.toSet());
    }

}
