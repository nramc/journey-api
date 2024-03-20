package com.github.nramc.dev.journey.api.geojson;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.github.nramc.dev.journey.api.geojson.types.GeoJsonType;
import com.github.nramc.dev.journey.api.geojson.types.Position;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

@Getter
@ToString
@EqualsAndHashCode(callSuper = false)
public final class Polygon extends Geometry {
    private final PolygonCoordinates coordinates;

    public Polygon(final PolygonCoordinates coordinates) {
        super(GeoJsonType.POLYGON);
        this.coordinates = coordinates;
    }

    public static Polygon of(final List<Position> exterior, final List<List<Position>> holes) {
        return new Polygon(new PolygonCoordinates(exterior, holes));
    }

    @SafeVarargs
    public final Polygon of(final List<Position> exterior, final List<Position>... holes) {
        return new Polygon(new PolygonCoordinates(exterior, List.of(holes)));
    }

    public static Polygon of(PolygonCoordinates coordinates) {
        return new Polygon(coordinates);
    }

    @JsonCreator
    public static Polygon of(GeoJsonType type, List<List<Position>> coordinates) {
        if (type != GeoJsonType.POLYGON) {
            throw new IllegalArgumentException("Invalid type. 'Polygon' expected");
        }
        if (CollectionUtils.isEmpty(coordinates)) {
            throw new IllegalArgumentException("Invalid Coordinates. Mandatory one position required.");
        }
        return new Polygon(PolygonCoordinates.of(coordinates));
    }

}
