package com.david.readme.controllers;

import com.david.readme.dtos.OrderDetailRequest;
import com.david.readme.dtos.OrderRequest;
import com.david.readme.services.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
@CrossOrigin
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public ResponseEntity<List<OrderRequest>> getOrders(Authentication authentication) {
        Long userId = getUserIdFromAuthentication(authentication);
        List<OrderRequest> orders = orderService.getOrdersByUserId(userId);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDetailRequest> getOrderById(
            @PathVariable Long orderId,
            Authentication authentication) {
        Long userId = getUserIdFromAuthentication(authentication);
        OrderDetailRequest order = orderService.getOrderById(orderId, userId);
        return ResponseEntity.ok(order);
    }

    private Long getUserIdFromAuthentication(Authentication authentication) {
        // TODO: Implement based on your authentication mechanism
        // For now, this is a placeholder
        return 1L; // Placeholder
    }
}
