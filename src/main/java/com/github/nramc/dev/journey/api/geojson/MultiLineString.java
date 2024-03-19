package com.github.nramc.dev.journey.api.geojson;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.github.nramc.dev.journey.api.geojson.types.GeoJsonType;
import com.github.nramc.dev.journey.api.geojson.types.Position;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Getter
@ToString
@EqualsAndHashCode(callSuper = false)
public final class MultiLineString extends Geometry {
    private final List<List<Position>> coordinates;

    public MultiLineString(List<List<Position>> coordinates) {
        super(GeoJsonType.MULTI_LINE_STRING);
        coordinates.forEach(MultiLineString::validateAndThrowError);
        this.coordinates = Collections.unmodifiableList(coordinates);
    }

    @JsonCreator
    public static MultiLineString of(GeoJsonType type, List<List<Position>> coordinates) {
        if (type != GeoJsonType.MULTI_LINE_STRING) {
            throw new IllegalArgumentException("Invalid type. 'MultiLineString' expected");
        }
        return new MultiLineString(coordinates);
    }

    @SafeVarargs
    public static MultiLineString of(List<Position>... coordinates) {
        return new MultiLineString(Arrays.stream(coordinates).toList());
    }

    private static void validateAndThrowError(List<Position> coordinate) {
        if (coordinate.size() < 2) {
            throw new IllegalArgumentException("Invalid coordinates. Minimum 2 positions required");
        }
    }

}
