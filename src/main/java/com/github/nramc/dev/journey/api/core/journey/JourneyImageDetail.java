package com.github.nramc.dev.journey.api.core.journey;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import org.hibernate.validator.constraints.URL;

import java.time.LocalDate;

@Builder(toBuilder = true)
public record JourneyImageDetail(
        @NotBlank @URL String url,
        @NotBlank String assetId,
        @NotBlank String publicId,
        String title,
        boolean isFavorite,
        boolean isThumbnail,
        @JsonFormat(pattern = "yyyy-MM-dd") LocalDate eventDate) {
}
