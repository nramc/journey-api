package com.github.nramc.dev.journey.api.journey.repository;

import lombok.Builder;

// todo: move it to shared module's data package
@Builder(toBuilder = true)
public record PagingProperty(
        int pageIndex,
        int pageSize,
        String sortColumn,
        String sortDirection
) {

}
