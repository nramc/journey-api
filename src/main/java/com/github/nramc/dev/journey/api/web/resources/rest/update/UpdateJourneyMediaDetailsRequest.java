package com.github.nramc.dev.journey.api.web.resources.rest.update;

import lombok.Builder;

import java.util.List;

@Builder(toBuilder = true)
public record UpdateJourneyMediaDetailsRequest(
        List<String> images,
        List<String> Videos) {
}
