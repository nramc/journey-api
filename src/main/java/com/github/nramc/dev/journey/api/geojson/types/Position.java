package com.github.nramc.dev.journey.api.geojson.types;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;
import java.util.Objects;

@ToString
@EqualsAndHashCode
public final class Position implements Serializable {

    @JsonValue
    private final double[] values;

    @JsonCreator
    public Position(double[] coordinates) {
        Objects.requireNonNull(coordinates);
        if (coordinates.length == 2 || coordinates.length == 3) {
            assertLongitude(coordinates[0]);
            assertLatitude(coordinates[1]);
            this.values = coordinates;
        } else {
            throw new IllegalArgumentException("Position can have minimum 2 and maximum 3 values only");
        }
    }

    public static Position of(double[] coordinates) {
        return new Position(coordinates);
    }

    public static Position of(double longitude, double latitude) {
        return new Position(new double[]{longitude, latitude});
    }

    public static Position of(double longitude, double latitude, double altitude) {
        return new Position(new double[]{longitude, latitude, altitude});
    }

    public double[] getCoordinates() {
        return this.values;
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

    private static void assertLongitude(double longitude) {
        if (longitude < -180 || longitude > 180) {
            throw new IllegalArgumentException("Valid longitude values are between -180 and 180, both inclusive.");
        }
    }

    private static void assertLatitude(double latitude) {
        if (latitude < -90 || latitude > 90) {
            throw new IllegalArgumentException("Valid latitude values are between -90 and 90, both inclusive.");
        }
    }

}
