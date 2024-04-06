package com.github.nramc.dev.journey.api.web.resources.rest.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.List;

@Builder(toBuilder = true)
public record JourneyVideosDetails(
        @NotNull @NotEmpty List<JourneyVideoDetail> videos) {
}
