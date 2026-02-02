package com.david.readme.controllers;

import com.david.readme.dtos.CartRequest;
import com.david.readme.services.CartService;
import com.david.readme.utils.AuthUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/api/v1/cart")
@CrossOrigin
public class CartController {
    private final CartService cartService;
    private final AuthUtil authUtil;

    public CartController(CartService cartService,  AuthUtil authUtil) {
        this.cartService = cartService;
        this.authUtil = authUtil;
    }

    @GetMapping
    public ResponseEntity<CartRequest> getCart() {
        Long userId = authUtil.getCurrentUser().getId();
        CartRequest cart = cartService.getCartByUserId(userId);

        return ResponseEntity.ok(cart);
    }
}
