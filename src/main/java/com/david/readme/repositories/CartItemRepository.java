package com.david.readme.repositories;

import com.david.readme.models.CartItems;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItems, Long> {

    Optional<CartItems> findByCartIdAndBookId(Long cartId, Long bookId);
}