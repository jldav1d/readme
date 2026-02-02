package com.david.readme.services;

import com.david.readme.dtos.OrderDetailRequest;
import com.david.readme.dtos.OrderItemRequest;
import com.david.readme.dtos.OrderRequest;
import com.david.readme.models.Order;
import com.david.readme.models.OrderItems;
import com.david.readme.repositories.OrderRepository;
import org.springframework.boot.ssl.SslBundles;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class OrderService {
    private OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository, SslBundles sslBundles) {
        this.orderRepository = orderRepository;
    }

    public List<OrderRequest> getOrdersByUserId(Long userId) {
        return orderRepository.findByUserIdOrderByDateOrderedDesc(userId)
                .stream()
                .map(this::convertToOrderRequest)
                .toList();
    }

    public OrderDetailRequest getOrderById(Long orderId, Long userId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));

        // verify the order belongs to the user
        if (!order.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized access to order");
        }

        return convertToOrderDetailRequest(order);
    }

    private OrderDetailRequest convertToOrderDetailRequest(Order order) {

        List<OrderItemRequest> items = order.getOrderItems()
                .stream()
                .map(this::convertToOrderItemRequest)
                .toList();

        Integer totalItems = order.getOrderItems()
                .stream()
                .map(OrderItems::getQuantity)
                .reduce(0, Integer::sum);

        return new OrderDetailRequest(
            order.getId(),
            order.getUser().getId(),
            order.getTotalPrice(),
            order.getDateOrdered(),
            items,
            totalItems
        );
    }

    private OrderRequest convertToOrderRequest(Order order) {

        Integer totalItems = order.getOrderItems()
                .stream()
                .map(OrderItems::getQuantity)
                .reduce(0, Integer::sum);

        return new OrderRequest(
            order.getId(),
            order.getUser().getId(),
            order.getTotalPrice(),
            order.getDateOrdered(),
            totalItems
        );
    }

    private OrderItemRequest convertToOrderItemRequest(OrderItems item){
        BigDecimal subtotal = item.getPurchasedPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
        return new OrderItemRequest(
            item.getId(),
            item.getBook().getId(),
            item.getBook().getTitle(),
            item.getBook().getAuthor(),
            item.getQuantity(),
            item.getPurchasedPrice(),
            subtotal
        );
    }
}
