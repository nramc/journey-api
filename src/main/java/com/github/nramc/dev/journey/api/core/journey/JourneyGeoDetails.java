package com.github.nramc.dev.journey.api.core.journey;

import com.github.nramc.commons.geojson.domain.GeoJson;
import com.github.nramc.commons.geojson.domain.Geometry;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder(toBuilder = true)
public record JourneyGeoDetails(
        @NotBlank String title,
        @NotBlank String city,
        @NotBlank String country,
        @NotNull Geometry location,
        @NotNull GeoJson geoJson
) {
}
