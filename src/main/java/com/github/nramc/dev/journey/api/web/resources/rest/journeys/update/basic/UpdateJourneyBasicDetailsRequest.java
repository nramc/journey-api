package com.github.nramc.dev.journey.api.web.resources.rest.journeys.update.basic;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.LocalDate;
import java.util.List;

@Builder(toBuilder = true)
record UpdateJourneyBasicDetailsRequest(
        @NotBlank String name,
        @NotBlank String description,
        @NotEmpty List<String> tags,
        @NotBlank String thumbnail,
        @NotNull @JsonFormat(pattern = "yyyy-MM-dd") LocalDate journeyDate) {
}
