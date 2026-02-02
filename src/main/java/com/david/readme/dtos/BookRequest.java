package com.david.readme.dtos;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

public record BookRequest(
    Long id,
    String title,
    String author,
    String description,
    String slug,
    BigDecimal price,
    Integer stock,
    LocalDateTime publishedAt,
    Set<String> categories
) {}
