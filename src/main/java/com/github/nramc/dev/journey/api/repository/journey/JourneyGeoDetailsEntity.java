package com.github.nramc.dev.journey.api.repository.journey;

import com.github.nramc.geojson.domain.GeoJson;
import com.github.nramc.geojson.domain.Geometry;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class JourneyGeoDetailsEntity {
    String title;
    String city;
    String country;
    String category;
    Geometry location;
    GeoJson geoJson;
}
