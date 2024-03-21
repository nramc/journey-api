package com.github.nramc.dev.journey.api.geojson;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.github.nramc.dev.journey.api.geojson.types.GeoJsonType;


@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, visible = true, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = Point.class, name = GeoJsonType.Constants.POINT_VALUE),
        @JsonSubTypes.Type(value = MultiPoint.class, name = GeoJsonType.Constants.MULTI_POINT_VALUE),
        @JsonSubTypes.Type(value = LineString.class, name = GeoJsonType.Constants.LINE_STRING_VALUE),
        @JsonSubTypes.Type(value = MultiLineString.class, name = GeoJsonType.Constants.MULTI_LINE_STRING_VALUE),
        @JsonSubTypes.Type(value = Polygon.class, name = GeoJsonType.Constants.POLYGON_VALUE),
        @JsonSubTypes.Type(value = MultiPolygon.class, name = GeoJsonType.Constants.MULTI_POLYGON_VALUE),
        @JsonSubTypes.Type(value = GeometryCollection.class, name = GeoJsonType.Constants.GEOMETRY_COLLECTION_VALUE)
})
public abstract sealed class Geometry extends GeoJson permits
        Point, MultiPoint, LineString, MultiLineString, Polygon, MultiPolygon, GeometryCollection {

    protected Geometry(GeoJsonType type) {
        super(type);
    }
}
