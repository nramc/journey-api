package com.github.nramc.dev.journey.api.web.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.List;

@Builder(toBuilder = true)
public record JourneyImagesDetails(
        @NotNull @NotEmpty List<JourneyImageDetail> images) {
}
