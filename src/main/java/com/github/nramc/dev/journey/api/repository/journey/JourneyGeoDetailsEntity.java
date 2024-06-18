package com.github.nramc.dev.journey.api.repository.journey;

import com.github.nramc.commons.geojson.domain.GeoJson;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class JourneyGeoDetailsEntity {
    GeoJson geoJson;
}
