package com.david.readme.dtos;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record GetBooksByCategory(
    String title,
    String author,
    String description,
    BigDecimal price,
    int stock,
    String slug,
    LocalDateTime publishedAt
) {}
