package com.david.readme.dtos;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderDetailRequest(
    Long id,
    Long userId,
    BigDecimal totalPrice,
    LocalDateTime dateOrdered,
    List<OrderItemRequest> items,
    Integer totalItems
) {}
