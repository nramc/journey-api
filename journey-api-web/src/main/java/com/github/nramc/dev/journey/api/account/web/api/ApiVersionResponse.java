package com.github.nramc.dev.journey.api.account.web.api;

import lombok.Builder;

@Builder(toBuilder = true)
record ApiVersionResponse(
        String name,
        String version
) {
}
