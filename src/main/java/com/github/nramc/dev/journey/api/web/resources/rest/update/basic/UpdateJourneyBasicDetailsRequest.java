package com.github.nramc.dev.journey.api.web.resources.rest.update.basic;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.github.nramc.commons.geojson.domain.Geometry;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.LocalDate;
import java.util.List;

@Builder(toBuilder = true)
public record UpdateJourneyBasicDetailsRequest(
        @NotBlank String name,
        @NotBlank String title,
        @NotBlank String description,
        @NotBlank String category,
        @NotBlank String city,
        @NotBlank String country,
        @NotEmpty List<String> tags,
        @NotBlank String thumbnail,
        @NotNull Geometry location,
        @NotNull @JsonFormat(pattern = "yyyy-MM-dd") LocalDate journeyDate) {
}