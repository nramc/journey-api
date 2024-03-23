package com.github.nramc.dev.journey.api.geojson;

import com.github.nramc.dev.journey.api.geojson.types.GeoJsonType;

import java.io.Serializable;


public abstract sealed class GeoJson implements Serializable permits Feature, FeatureCollection, Geometry {
    protected final GeoJsonType type;

    protected GeoJson(GeoJsonType type) {
        this.type = type;
    }


    public final String getType() {
        return type.getType();
    }

}
