package com.david.readme.dtos;

import java.math.BigDecimal;

public record OrderItemRequest(
    Long id,
    Long bookId,
    String bookTitle,
    String bookAuthor,
    Integer quantity,
    BigDecimal purchasedPrice,
    BigDecimal subtotal
) {}
