package com.github.nramc.dev.journey.api.journey.domain;

import com.github.nramc.dev.journey.api.shared.domain.user.security.Visibility;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import org.hibernate.validator.constraints.URL;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Builder(toBuilder = true)
public record Journey(
        @NotBlank String id,
        @NotBlank String name,
        @NotBlank String description,
        @NotEmpty List<String> tags,
        @NotBlank @URL String thumbnail,
        @NotNull LocalDate journeyDate,
        @NotNull LocalDate createdDate,
        @NotNull @Valid JourneyGeoDetails geoDetails,
        @Valid JourneyImagesDetails imagesDetails,
        @Valid JourneyVideosDetails videosDetails,
        @NotEmpty Set<Visibility> visibilities,
        boolean isPublished) {
}
