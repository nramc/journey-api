package com.github.nramc.dev.journey.api.geojson;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.github.nramc.dev.journey.api.geojson.types.GeoJsonType;
import com.github.nramc.dev.journey.api.geojson.types.Position;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public final class Point extends Geometry {
    private final Position coordinates;

    public Point(Position coordinates) {
        super(GeoJsonType.POINT);
        this.coordinates = coordinates;
    }

    @JsonCreator
    public static Point of(GeoJsonType type, Position coordinates) {
        if (type != GeoJsonType.POINT) {
            throw new IllegalArgumentException("Invalid type. 'Point' expected");
        }
        if (coordinates == null) {
            throw new IllegalArgumentException("Invalid coordinates. 'coordinates' can not be empty or null");
        }
        return new Point(coordinates);
    }

    public static Point of(Position coordinates) {
        return of(GeoJsonType.POINT, coordinates);
    }

}
