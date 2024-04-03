package com.github.nramc.dev.journey.api.web.resources.rest.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.List;

@Builder(toBuilder = true)
record JourneyImagesDetails(
        @NotNull @NotEmpty List<JourneyImageDetail> images) {
}
