package com.github.nramc.dev.journey.api.journey.repository;

import lombok.Builder;

@Builder(toBuilder = true)
public record PagingProperty(
        int pageIndex,
        int pageSize,
        String sortColumn,
        String sortDirection
) {

}
