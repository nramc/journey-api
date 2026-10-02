package com.github.nramc.dev.journey.api.journey.web.journeys.update.images;

import lombok.Builder;

import java.time.LocalDate;
import java.util.List;

@Builder(toBuilder = true)
public record UpdateJourneyImagesDetailsRequest(List<ImageDetail> images) {

    @Builder(toBuilder = true)
    public record ImageDetail(
            String url,
            String assetId,
            String publicId,
            String title,
            boolean isFavorite,
            boolean isThumbnail,
            LocalDate eventDate) {

    }
}
