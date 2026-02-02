package com.david.readme.services;

import com.david.readme.dtos.CartItemRequest;
import com.david.readme.dtos.CartRequest;
import com.david.readme.exceptions.ResourceNotFoundException;
import com.david.readme.models.Cart;
import com.david.readme.models.CartItems;
import com.david.readme.repositories.CartRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class CartService {
    public CartRepository cartRepository;

    public CartService(CartRepository cartRepository) {
        this.cartRepository = cartRepository;
    }

    public CartRequest getCartByUserId(Long userId) {
        Cart cart = this.cartRepository.findByUserId(userId).orElseThrow(() -> new ResourceNotFoundException("Cart not found with userId " + userId));
        return convertToCartRequest(cart);
    }

    private CartRequest convertToCartRequest(Cart cart) {

        List<CartItemRequest> items = cart.getCartItems()
                .stream()
                .map(this::convertToCartItemRequest)
                .toList();

        BigDecimal totalPrice = items
                .stream()
                .map(CartItemRequest::subtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Integer totalItems = items
                .stream()
                .map(CartItemRequest::quantity)
                .reduce(0, Integer::sum);


        return new CartRequest(
            cart.getId(),
            cart.getUser().getId(),
            items,
            totalPrice,
            totalItems
        );
    }

    private CartItemRequest convertToCartItemRequest(CartItems item) {
        BigDecimal subtotal = item.getBook().getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));

        return new CartItemRequest(
            item.getId(),
            item.getBook().getId(),
            item.getBook().getTitle(),
            item.getBook().getAuthor(),
            item.getBook().getPrice(),
            item.getQuantity(),
            subtotal,
            item.getDateAdded()
        );

    }
}
