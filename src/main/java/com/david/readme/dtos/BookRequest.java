package com.david.readme.dtos;


import java.math.BigDecimal;
import java.time.LocalDateTime;

public record GetAllBooksRequest(
        Long id,
        String title,
        String author,
        String slug ,
        BigDecimal price,
        int stock,
        LocalDateTime publishedAt
) {}
