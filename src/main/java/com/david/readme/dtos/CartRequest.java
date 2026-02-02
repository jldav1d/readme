package com.david.readme.dtos;

import java.math.BigDecimal;
import java.util.List;

public record CartRequest(
    Long id,
    Long userId,
    List<CartItemRequest> items,
    BigDecimal totalPrice,
    Integer totalItems
){}
