package com.github.nramc.dev.journey.api.geojson;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.github.nramc.dev.journey.api.geojson.types.Position;
import org.apache.commons.collections4.CollectionUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


public record PolygonCoordinates(List<Position> exterior, List<List<Position>> holes) implements Serializable {
    public PolygonCoordinates(final List<Position> exterior, final List<List<Position>> holes) {
        validateAndThrowError(exterior);
        this.exterior = Collections.unmodifiableList(exterior);

        CollectionUtils.emptyIfNull(holes).forEach(PolygonCoordinates::validateAndThrowError);
        this.holes = CollectionUtils.isNotEmpty(holes) ? Collections.unmodifiableList(holes) : null;
    }

    @SafeVarargs
    public static PolygonCoordinates of(List<Position>... positions) {
        return of(Arrays.asList(positions));

    }

    @JsonCreator
    public static PolygonCoordinates of(List<List<Position>> linearRings) {
        if (linearRings.size() < 2) {
            return new PolygonCoordinates(linearRings.getFirst(), List.of());
        } else {
            return new PolygonCoordinates(linearRings.getFirst(), linearRings.subList(1, linearRings.size()));
        }
    }

    @JsonValue
    public List<List<Position>> getCoordinates() {
        List<List<Position>> coordinates = new ArrayList<>();
        coordinates.add(this.exterior);
        if (CollectionUtils.isNotEmpty(this.holes)) {
            coordinates.addAll(this.holes);
        }
        return coordinates;
    }


    private static void validateAndThrowError(List<Position> linearRing) {
        if (CollectionUtils.isEmpty(linearRing) || linearRing.size() < 4) {
            throw new IllegalArgumentException("ring must contain at least four positions");
        }
        if (!linearRing.getFirst().equals(linearRing.getLast())) {
            throw new IllegalArgumentException("first and last position must be the same");
        }
    }


}
