package com.david.readme.controllers;

import com.david.readme.dtos.OrderDetailRequest;
import com.david.readme.dtos.OrderRequest;
import com.david.readme.services.OrderService;
import com.david.readme.utils.AuthUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
@CrossOrigin
public class OrderController {
    private final OrderService orderService;
    private final AuthUtil authUtil;

    public OrderController(OrderService orderService, AuthUtil authUtil) {
        this.orderService = orderService;
        this.authUtil = authUtil;
    }

    @GetMapping
    public ResponseEntity<List<OrderRequest>> getOrders() {
        Long userId = authUtil.getCurrentUser().getId();
        List<OrderRequest> orders = orderService.getOrdersByUserId(userId);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDetailRequest> getOrderById(
            @PathVariable Long orderId) {
        Long userId = authUtil.getCurrentUser().getId();
        OrderDetailRequest order = orderService.getOrderById(orderId, userId);
        return ResponseEntity.ok(order);
    }
}
