package com.github.nramc.dev.journey.api.shared.domain.data;

import lombok.Builder;

@Builder(toBuilder = true)
public record PagingProperty(
        int pageIndex,
        int pageSize,
        String sortColumn,
        String sortDirection
) {

}
