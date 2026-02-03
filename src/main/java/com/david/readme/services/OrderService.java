package com.david.readme.services;

import com.david.readme.dtos.OrderDetailRequest;
import com.david.readme.dtos.OrderItemRequest;
import com.david.readme.dtos.OrderRequest;
import com.david.readme.exceptions.ResourceNotFoundException;
import com.david.readme.exceptions.UnauthorizedException;
import com.david.readme.models.*;
import com.david.readme.repositories.CartRepository;
import com.david.readme.repositories.OrderRepository;
import jakarta.transaction.Transactional;
import org.springframework.boot.ssl.SslBundles;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class OrderService {
    private OrderRepository orderRepository;
    private CartRepository cartRepository;

    public OrderService(OrderRepository orderRepository, CartRepository cartRepository) {
        this.orderRepository = orderRepository;
        this.cartRepository = cartRepository;
    }

    public List<OrderRequest> getOrdersByUserId(Long userId) {
        return this.orderRepository.findByUserIdOrderByDateOrderedDesc(userId)
                .stream()
                .map(this::convertToOrderRequest)
                .toList();
    }

    public OrderDetailRequest getOrderById(Long orderId, Long userId) {
        Order order = this.orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

        // verify the order belongs to the user
        if (!order.getUser().getId().equals(userId)) {
            throw new UnauthorizedException("Unauthorized access to order");
        }

        return convertToOrderDetailRequest(order);
    }

    @Transactional
    public OrderDetailRequest checkout(Long userId) {
        Cart cart = this.cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found for user"));

        if (cart.getCartItems().isEmpty()) {
            throw new RuntimeException("Cannot checkout with an empty cart");
        }

        // Validate stock availability for all items
        for (CartItems cartItem : cart.getCartItems()) {
            Book book = cartItem.getBook();
            if (book.getStock() < cartItem.getQuantity()) {
                throw new RuntimeException("Insufficient stock for: " + book.getTitle());
            }
        }

        Order order = new Order();
        order.setUser(cart.getUser());

        BigDecimal totalPrice = BigDecimal.ZERO;

        for (CartItems cartItem : cart.getCartItems()) {
            Book book = cartItem.getBook();

            OrderItems orderItem = new OrderItems();
            orderItem.setOrder(order);
            orderItem.setBook(book);
            orderItem.setQuantity(cartItem.getQuantity());
            // lock in the price at time of purchase
            orderItem.setPurchasedPrice(book.getPrice());

            order.getOrderItems().add(orderItem);

            // calculate subtotal
            BigDecimal subtotal = book.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity()));
            totalPrice = totalPrice.add(subtotal);

            // decrease book stock
            book.setStock(book.getStock() - cartItem.getQuantity());
        }

        order.setTotalPrice(totalPrice);

        // save order (cascade will save order items)
        Order savedOrder = orderRepository.save(order);

        // clear the cart
        cart.getCartItems().clear();
        cartRepository.save(cart);

        return convertToOrderDetailRequest(savedOrder);
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
