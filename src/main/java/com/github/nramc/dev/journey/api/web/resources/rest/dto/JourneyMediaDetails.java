package com.github.nramc.dev.journey.api.web.resources.rest.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record JourneyMediaDetails(
        @NotNull @NotEmpty List<String> images,
        @NotNull @NotEmpty List<String> videos) {
}
