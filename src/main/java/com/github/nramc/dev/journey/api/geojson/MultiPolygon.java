package com.github.nramc.dev.journey.api.geojson;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.github.nramc.dev.journey.api.geojson.types.GeoJsonType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Collections;
import java.util.List;

@Getter
@ToString
@EqualsAndHashCode(callSuper = false)
public final class MultiPolygon extends Geometry {
    private final List<PolygonCoordinates> coordinates;

    public MultiPolygon(final List<PolygonCoordinates> coordinates) {
        super(GeoJsonType.MULTI_POLYGON);
        this.coordinates = Collections.unmodifiableList(coordinates);
    }

    public static MultiPolygon of(PolygonCoordinates... coordinates) {
        return new MultiPolygon(List.of(coordinates));
    }

    @JsonCreator
    public static MultiPolygon of(GeoJsonType type, List<PolygonCoordinates> coordinates) {
        if (type != GeoJsonType.MULTI_POLYGON) {
            throw new IllegalArgumentException("Invalid type. expected 'MultiPolygon'");
        }
        if (CollectionUtils.isEmpty(coordinates)) {
            throw new IllegalArgumentException("Invalid coordinates. Minimum one required");
        }
        return new MultiPolygon(coordinates);
    }


}
