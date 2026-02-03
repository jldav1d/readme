package com.david.readme.services;

import com.david.readme.dtos.AddToCartRequest;
import com.david.readme.dtos.CartItemRequest;
import com.david.readme.dtos.CartRequest;
import com.david.readme.dtos.UpdateQuantityRequest;
import com.david.readme.exceptions.ResourceNotFoundException;
import com.david.readme.models.Book;
import com.david.readme.models.Cart;
import com.david.readme.models.CartItems;
import com.david.readme.repositories.BookRepository;
import com.david.readme.repositories.CartItemRepository;
import com.david.readme.repositories.CartRepository;
import com.david.readme.utils.AuthUtil;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class CartService {
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final BookRepository bookRepository;


    public CartService(CartRepository cartRepository, CartItemRepository cartItemRepository, BookRepository bookRepository) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.bookRepository = bookRepository;
    }

    @Transactional
    public CartRequest addToCart(Long userId, AddToCartRequest request) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found for user id: " + userId));

        Book book = bookRepository.findById(request.bookId())
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + request.bookId()));

        Integer requestedQuantity = request.quantity() != null ? request.quantity() : 1;
        if (book.getStock() < requestedQuantity) {
            throw new RuntimeException("Insufficient stock. Only " + book.getStock() + " available.");
        }

        // check if book already in cart
        Optional<CartItems> existingItem = cartItemRepository.findByCartIdAndBookId(cart.getId(), book.getId());

        if (existingItem.isPresent()) {
            // update quantity of existing item
            CartItems cartItem = existingItem.get();
            int newQuantity = cartItem.getQuantity() + requestedQuantity;

            // check if new quantity exceeds stock
            if (newQuantity > book.getStock()) {
                throw new RuntimeException("Cannot add more. Only " + book.getStock() + " available.");
            }

            cartItem.setQuantity(newQuantity);
            cartItemRepository.save(cartItem);
        } else {
            CartItems cartItem = new CartItems();
            cartItem.setCart(cart);
            cartItem.setBook(book);
            cartItem.setQuantity(requestedQuantity);
            cartItemRepository.save(cartItem);

            cart.getCartItems().add(cartItem);
        }

        return convertToCartRequest(cart);
    }

    @Transactional
    public CartRequest updateCartItem(Long userId, Long cartItemId, UpdateQuantityRequest request) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found for user id: " + userId));

        CartItems cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found with id: " + cartItemId));

        if (!cartItem.getCart().getId().equals(cart.getId())) {
            throw new RuntimeException("Unauthorized: Cart item does not belong to user");
        }

        if (request.quantity() > cartItem.getBook().getStock()) {
            throw new RuntimeException("Insufficient stock. Only " + cartItem.getBook().getStock() + " available.");
        }

        // update quantity
        cartItem.setQuantity(request.quantity());
        cartItemRepository.save(cartItem);

        return convertToCartRequest(cart);
    }

    @Transactional
    public CartRequest removeFromCart(Long userId, Long cartItemId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found for user id: " + userId));

        CartItems cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found with id: " + cartItemId));

        if (!cartItem.getCart().getId().equals(cart.getId())) {
            throw new RuntimeException("Unauthorized: Cart item does not belong to user");
        }

        cart.getCartItems().remove(cartItem);
        cartItemRepository.delete(cartItem);

        return convertToCartRequest(cart);
    }

    @Transactional
    public void clearCart(Long userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found for user id: " + userId));

        cart.getCartItems().clear();
        cartItemRepository.deleteAll(cart.getCartItems());
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
