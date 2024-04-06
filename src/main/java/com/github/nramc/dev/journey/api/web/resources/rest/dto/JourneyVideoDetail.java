package com.github.nramc.dev.journey.api.web.resources.rest.dto;

import lombok.Builder;

@Builder(toBuilder = true)
public record JourneyVideoDetail(String videoId) {
}
