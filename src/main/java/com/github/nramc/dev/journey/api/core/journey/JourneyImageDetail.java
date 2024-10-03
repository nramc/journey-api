package com.github.nramc.dev.journey.api.core.journey;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;

import java.time.LocalDate;

@Builder(toBuilder = true)
public record JourneyImageDetail(
        String url,
        String assetId,
        String publicId,
        String title,
        boolean isFavorite,
        boolean isThumbnail,
        @JsonFormat(pattern = "yyyy-MM-dd") LocalDate eventDate) {
}
