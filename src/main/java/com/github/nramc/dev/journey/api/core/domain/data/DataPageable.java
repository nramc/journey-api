package com.github.nramc.dev.journey.api.core.domain.data;

import lombok.Builder;

import java.util.List;

@Builder(toBuilder = true)
public record DataPageable<T>(
        List<T> content,
        long numberOfElements,
        long totalElements,
        long totalPages,
        long pageNumber,
        long pageSize) {
}
