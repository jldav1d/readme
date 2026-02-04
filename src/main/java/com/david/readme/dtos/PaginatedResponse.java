package com.david.readme.dtos;

import java.util.List;

public record PaginatedResponse<T>(
   List<T> content,
   int currentPage,
   int totalPages,
   long totalItems,
   int pageSize,
   boolean hasNext,
   boolean hasPrevious
) {}
