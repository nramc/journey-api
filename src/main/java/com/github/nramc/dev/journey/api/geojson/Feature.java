package com.github.nramc.dev.journey.api.geojson;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.github.nramc.dev.journey.api.geojson.types.GeoJsonType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.Map;

@Getter
@ToString
@EqualsAndHashCode(callSuper = false)
public non-sealed class Feature extends GeoJson {
    private final String id;
    private final Geometry geometry;
    private final Map<String, Serializable> properties;

    protected Feature(String id, Geometry geometry, Map<String, Serializable> properties) {
        super(GeoJsonType.FEATURE);
        this.id = id;
        this.geometry = geometry;
        this.properties = Map.copyOf(properties);
    }

    @JsonCreator
    public static Feature of(String id, GeoJsonType type, Geometry geometry, Map<String, Serializable> properties) {
        if (type != GeoJsonType.FEATURE) {
            throw new IllegalArgumentException("Invalid type. expected 'Feature', but got " + type);
        }
        if (StringUtils.isBlank(id)) {
            throw new IllegalArgumentException("Mandatory field 'id' should not be null/blank");
        }
        return new Feature(id, geometry, MapUtils.emptyIfNull(properties));
    }

    public static Feature of(String id, Geometry geometry, Map<String, Serializable> properties) {
        return of(id, GeoJsonType.FEATURE, geometry, MapUtils.emptyIfNull(properties));
    }

}
