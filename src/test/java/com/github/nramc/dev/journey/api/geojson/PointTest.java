package com.github.nramc.dev.journey.api.geojson;

import com.github.nramc.dev.journey.api.geojson.types.Position;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class PointTest {
    @Autowired
    private JacksonTester<Point> jacksonTester;


    @Test
    void testJsonSerialisation_withLongitudeAndLatitude() throws IOException {
        assertThat(jacksonTester.write(Point.of(Position.of(60.8, 20.5))))
                .isEqualToJson("""
                        { "type": "Point", "coordinates": [60.8, 20.5] }""");

    }

    @Test
    void testJsonSerialisation_withLongitudeAndLatitudeAndAltitude() throws IOException {
        assertThat(jacksonTester.write(Point.of(Position.of(60.8, 20.5, 43.2))))
                .isEqualToJson("""
                        { "type": "Point", "coordinates": [60.8, 20.5, 43.2] }""");

    }

    @Test
    void testDeserialization_withLongitudeAndLatitudeAndWithoutAptitude() throws IOException {
        String jsonString = """
                { "type": "Point", "coordinates": [60.8, 20.5] }""";
        Point point = Point.of(Position.of(60.8, 20.5));
        assertThat(jacksonTester.parseObject(jsonString))
                .satisfies(p -> assertThat(p.getType()).isEqualTo(point.getType()))
                .satisfies(p -> assertThat(p.getCoordinates().getCoordinates()).isEqualTo(point.getCoordinates().getCoordinates()));
    }

    @Test
    void testDeserialization_withLongitudeAndLatitudeAndAltitude() throws IOException {
        String jsonString = """
                { "type": "Point", "coordinates": [60.8, 20.5, 54.7] }""";
        Point point = Point.of(Position.of(60.8, 20.5, 54.7));
        assertThat(jacksonTester.parseObject(jsonString))
                .satisfies(p -> assertThat(p.getType()).isEqualTo(point.getType()))
                .satisfies(p -> assertThat(p.getCoordinates().getCoordinates()).isEqualTo(point.getCoordinates().getCoordinates()));
    }

    @ParameterizedTest
    @ValueSource(strings = {"""
            { "type": "Point", "coordinates": [60.8, 20.5, 54.7, 11.2] }""", """
            { "type": "Point", "coordinates": [] }""", """
            { "type": "Point", "coordinates": [60.8, 20.5, 54.7, 10.4, 15.7] }""", """
            { "type": "Point" }"""

    })
    void testDeserialization_withInvalidCoordinates(String jsonString) {
        Assertions.assertThrows(Exception.class, () -> jacksonTester.parseObject(jsonString));
    }

}