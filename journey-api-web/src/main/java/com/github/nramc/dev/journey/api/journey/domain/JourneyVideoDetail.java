package com.github.nramc.dev.journey.api.journey.domain;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder(toBuilder = true)
public record JourneyVideoDetail(@NotBlank String videoId) {
}
