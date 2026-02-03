package com.david.readme.dtos;

public record AddToCartRequest(
    Long bookId,
    Integer quantity
) {}
