package com.github.nramc.dev.journey.api.geojson.types;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum GeoJsonType {
    POINT("Point"),
    MULTI_POINT("MultiPoint"),
    LINE_STRING("LineString"),
    MULTI_LINE_STRING("MultiLineString"),
    POLYGON("Polygon"),
    MULTI_POLYGON("MultiPolygon"),
    GEOMETRY_COLLECTION("GeometryCollection"),
    FEATURE("Feature"),
    FEATURE_COLLECTION("FeatureCollection");

    @JsonValue
    private final String type;

    GeoJsonType(String type) {
        this.type = type;
    }

}
