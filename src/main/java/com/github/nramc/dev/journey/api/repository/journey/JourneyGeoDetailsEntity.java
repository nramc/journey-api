package com.github.nramc.dev.journey.api.repository.journey;

import com.github.nramc.commons.geojson.domain.Geometry;
import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class JourneyGeoDetailsEntity {
    Geometry geoJson;
}
