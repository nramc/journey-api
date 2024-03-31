package com.github.nramc.dev.journey.api.repository.journey;

import com.github.nramc.commons.geojson.domain.GeoJson;
import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class JourneyGeoDetailsEntity {
    GeoJson geoJson;
}
