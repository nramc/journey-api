package com.github.nramc.dev.journey.api.web.dto;

import lombok.Builder;

@Builder(toBuilder = true)
public record JourneyVideoDetail(String videoId) {
}
