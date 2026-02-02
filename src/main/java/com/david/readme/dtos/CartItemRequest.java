package com.david.readme.dtos;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CartItemRequest(
    Long id,
    Long bookId,
    String bookTitle,
    String bookAuthor,
    BigDecimal bookPrice,
    Integer quantity,
    BigDecimal subtotal,
    LocalDateTime dateAdded
) {}
