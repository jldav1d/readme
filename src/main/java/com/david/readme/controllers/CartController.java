package com.david.readme.controllers;

import com.david.readme.dtos.CartRequest;
import com.david.readme.services.CartService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/cart")
@CrossOrigin
public class CartController {
    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping
    public ResponseEntity<Optional<CartRequest>> getCart(Authentication authentication) {
        // assuming the user ID is stored in the authentication principal
        // i'll be using a placeholder userId for now
        Long userId = getUserIdFromAuthentication(authentication);
        Optional<CartRequest> cart = cartService.getCartByUserId(userId);

        if (cart.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(cart);
    }

    private Long getUserIdFromAuthentication(Authentication authentication) {
        // for now, this is a placeholder

        // TODO: Implement based on your authentication mechanism
        // ex: return ((UserDetails) authentication.getPrincipal()).getId();
        return 1L;
    }
}
