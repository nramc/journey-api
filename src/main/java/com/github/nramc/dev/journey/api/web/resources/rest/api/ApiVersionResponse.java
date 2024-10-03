package com.github.nramc.dev.journey.api.web.resources.rest.api;

import lombok.Builder;

@Builder(toBuilder = true)
record ApiVersionResponse(
        String name,
        String version
) {
}
