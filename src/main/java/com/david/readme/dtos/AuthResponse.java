package com.david.readme.dtos;

public record AuthResponse(
    Long userId,
    String username,
    String role,
    String message
){}
