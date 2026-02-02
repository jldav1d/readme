package com.david.readme.repositories;

import com.david.readme.models.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserIdOrderByDateOrderedDesc(Long userId);
}
