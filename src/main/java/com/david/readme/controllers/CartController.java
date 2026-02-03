package com.david.readme.controllers;

import com.david.readme.dtos.AddToCartRequest;
import com.david.readme.dtos.CartRequest;
import com.david.readme.dtos.UpdateQuantityRequest;
import com.david.readme.services.CartService;
import com.david.readme.utils.AuthUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/add")
    public ResponseEntity<CartRequest> addToCart(@RequestBody AddToCartRequest request) {
        Long userId = authUtil.getCurrentUser().getId();
        CartRequest cart = cartService.addToCart(userId, request);
        return ResponseEntity.ok(cart);
    }

    @PutMapping("/items/{cartItemId}")
    public ResponseEntity<CartRequest> updateCartItem(
            @PathVariable Long cartItemId,
            @RequestBody UpdateQuantityRequest request) {
        Long userId = authUtil.getCurrentUser().getId();
        CartRequest cart = cartService.updateCartItem(userId, cartItemId, request);
        return ResponseEntity.ok(cart);
    }

    @DeleteMapping("/items/{cartItemId}")
    public ResponseEntity<CartRequest> removeFromCart(@PathVariable Long cartItemId) {
        Long userId = authUtil.getCurrentUser().getId();
        CartRequest cart = cartService.removeFromCart(userId, cartItemId);
        return ResponseEntity.ok(cart);
    }

    @DeleteMapping("/clear")
    public ResponseEntity<Void> clearCart() {
        Long userId = authUtil.getCurrentUser().getId();
        cartService.clearCart(userId);
        return ResponseEntity.noContent().build();
    }
}
