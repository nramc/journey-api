package com.github.nramc.dev.journey.api.core.journey;

import com.github.nramc.commons.geojson.domain.GeoJson;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder(toBuilder = true)
public record JourneyGeoDetails(
        @NotNull GeoJson geoJson) {
}
