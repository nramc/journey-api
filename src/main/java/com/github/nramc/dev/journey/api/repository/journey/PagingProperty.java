package com.github.nramc.dev.journey.api.repository.journey;

import lombok.Builder;

@Builder(toBuilder = true)
public record PagingProperty(
        int pageIndex,
        int pageSize,
        String sortColumn,
        String sortDirection
) {

}
