package com.github.nramc.dev.journey.api.web.resources.rest.journeys.create;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.LocalDate;
import java.util.List;

@Builder(toBuilder = true)
public record CreateJourneyResponse(
        @NotBlank String id,
        @NotBlank String name,
        @NotBlank String description,
        @NotEmpty List<String> tags,
        @NotBlank String thumbnail,
        @NotNull LocalDate createdDate,
        @NotNull @JsonFormat(pattern = "yyyy-MM-dd") LocalDate journeyDate) {
}
