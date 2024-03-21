package com.github.nramc.dev.journey.api.geojson.types;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum GeoJsonType {
    POINT(Constants.POINT_VALUE),
    MULTI_POINT(Constants.MULTI_POINT_VALUE),
    LINE_STRING(Constants.LINE_STRING_VALUE),
    MULTI_LINE_STRING(Constants.MULTI_LINE_STRING_VALUE),
    POLYGON(Constants.POLYGON_VALUE),
    MULTI_POLYGON(Constants.MULTI_POLYGON_VALUE),
    GEOMETRY_COLLECTION(Constants.GEOMETRY_COLLECTION_VALUE),
    FEATURE(Constants.FEATURE_VALUE),
    FEATURE_COLLECTION(Constants.FEATURE_COLLECTION_VALUE);

    @JsonValue
    private final String type;

    GeoJsonType(String type) {
        this.type = type;
    }

    public static class Constants {
        private Constants() {
        }

        public static final String POINT_VALUE = "Point";
        public static final String MULTI_POINT_VALUE = "MultiPoint";
        public static final String LINE_STRING_VALUE = "LineString";
        public static final String MULTI_LINE_STRING_VALUE = "MultiLineString";
        public static final String POLYGON_VALUE = "Polygon";
        public static final String MULTI_POLYGON_VALUE = "MultiPolygon";
        public static final String GEOMETRY_COLLECTION_VALUE = "GeometryCollection";
        public static final String FEATURE_VALUE = "Feature";
        public static final String FEATURE_COLLECTION_VALUE = "FeatureCollection";

    }

}
