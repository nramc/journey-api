package com.github.nramc.dev.journey.api.geojson;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.github.nramc.dev.journey.api.geojson.types.GeoJsonType;
import com.github.nramc.dev.journey.api.geojson.types.Position;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.Collections;
import java.util.List;

@Getter
@ToString
@EqualsAndHashCode(callSuper = false)
public final class LineString extends Geometry {
    private final List<Position> coordinates;

    public LineString(List<Position> coordinates) {
        super(GeoJsonType.LINE_STRING);
        if (coordinates.size() < 2) {
            throw new IllegalArgumentException("Invalid coordinates. Minimum 2 positions required");
        }
        this.coordinates = Collections.unmodifiableList(coordinates);
    }

    @JsonCreator
    public static LineString of(GeoJsonType type, List<Position> coordinates) {
        if (type != GeoJsonType.LINE_STRING) {
            throw new IllegalArgumentException(String.format("Invalid type. expected[%s] got:[%s]",
                    GeoJsonType.LINE_STRING.getType(), type.getType()));
        }
        return new LineString(coordinates);
    }

    public static LineString of(List<Position> coordinates) {
        return of(GeoJsonType.LINE_STRING, coordinates);
    }
}
