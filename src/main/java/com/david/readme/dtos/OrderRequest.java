package com.david.readme.dtos;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OrderRequest(
    Long id,
    Long userId,
    BigDecimal totalPrice,
    LocalDateTime dateOrdered,
    Integer totalItems
) {}
