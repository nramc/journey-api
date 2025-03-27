package com.github.nramc.dev.journey.api.web.resources.rest.journeys.update.images;

import com.fasterxml.jackson.annotation.JsonFormat;
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
            @JsonFormat(pattern = "yyyy-MM-dd") LocalDate eventDate) {

    }
}
