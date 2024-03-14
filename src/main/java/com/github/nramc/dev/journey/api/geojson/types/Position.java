package com.github.nramc.dev.journey.api.geojson.types;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.io.Serializable;

public final class Position implements Serializable {

    @JsonValue
    private final double[] values;

    @JsonCreator
    public Position(double[] values) {
        if (values.length < 2 || values.length > 3) {
            throw new IllegalArgumentException("Position can have minimum 2 and maximum 3 values only");
        }
        this.values = values;
    }

    public Position(double longitude, double latitude) {
        this.values = new double[]{longitude, latitude};
    }

    public Position(double longitude, double latitude, double altitude) {
        this.values = new double[]{longitude, latitude, altitude};
    }

    public double getLongitude() {
        return values.length > 0 ? values[0] : Double.NaN;
    }

    public double getLatitude() {
        return values.length > 1 ? values[1] : Double.NaN;
    }

    public double getAltitude() {
        return values.length > 2 ? values[2] : Double.NaN;
    }


}
