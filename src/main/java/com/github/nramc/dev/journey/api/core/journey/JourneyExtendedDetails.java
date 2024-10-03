package com.github.nramc.dev.journey.api.core.journey;

import lombok.Builder;

@Builder(toBuilder = true)
public record JourneyExtendedDetails(
        JourneyGeoDetails geoDetails,
        JourneyImagesDetails imagesDetails,
        JourneyVideosDetails videosDetails) {
}
