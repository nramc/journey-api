package com.github.nramc.dev.journey.api.core.journey;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder(toBuilder = true)
public record JourneyExtendedDetails(
        @NotNull @Valid JourneyGeoDetails geoDetails,
        @Valid JourneyImagesDetails imagesDetails,
        @Valid JourneyVideosDetails videosDetails) {
}
