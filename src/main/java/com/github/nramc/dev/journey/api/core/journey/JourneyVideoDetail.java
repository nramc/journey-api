package com.github.nramc.dev.journey.api.core.journey;

import lombok.Builder;

@Builder(toBuilder = true)
public record JourneyVideoDetail(String videoId) {
}
