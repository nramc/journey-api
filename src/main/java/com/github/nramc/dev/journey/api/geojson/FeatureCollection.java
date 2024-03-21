package com.github.nramc.dev.journey.api.geojson;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.github.nramc.dev.journey.api.geojson.types.GeoJsonType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;


@Getter
@ToString
@EqualsAndHashCode(callSuper = false)
public final class FeatureCollection extends GeoJson {
    private final List<Feature> features;

    public FeatureCollection(List<Feature> features) {
        super(GeoJsonType.FEATURE_COLLECTION);
        this.features = CollectionUtils.emptyIfNull(features).stream().toList();
    }

    @JsonCreator
    public static FeatureCollection of(GeoJsonType type, List<Feature> features) {
        if (type != GeoJsonType.FEATURE_COLLECTION) {
            throw new IllegalArgumentException("Invalid type. expected 'FeatureCollection', but got " + type);
        }
        return new FeatureCollection(features);
    }

    public static FeatureCollection of(List<Feature> features) {
        return of(GeoJsonType.FEATURE_COLLECTION, features);
    }

    public static FeatureCollection of(Feature... features) {
        return of(List.of(features));
    }

}
