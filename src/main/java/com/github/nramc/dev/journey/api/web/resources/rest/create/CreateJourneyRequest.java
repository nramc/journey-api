package com.github.nramc.dev.journey.api.web.resources.rest.create;

import com.github.nramc.commons.geojson.domain.Geometry;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import org.hibernate.validator.constraints.UUID;

import java.util.List;

@Builder(toBuilder = true)
public record CreateJourneyRequest(
        @NotBlank @UUID String id,
        @NotBlank String name,
        @NotBlank String title,
        @NotBlank String description,
        @NotBlank String category,
        @NotBlank String city,
        @NotBlank String country,
        @NotEmpty List<String> tags,
        @NotBlank String thumbnail,
        @NotNull Geometry location) {
}
