package com.github.nramc.dev.journey.api.geojson;

import com.github.nramc.dev.journey.api.geojson.types.GeoJsonType;
import com.github.nramc.dev.journey.api.geojson.types.Position;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@JsonTest
class LineStringTest {
    @Autowired
    JacksonTester<LineString> jacksonTester;

    @Test
    void deserialization_withLongitudeAndLatitude() throws IOException {
        String json = """
                {
                         "type": "LineString",
                         "coordinates": [
                             [100.0, 0.0],
                             [101.0, 1.0]
                         ]
                     }
                """;
        LineString object = jacksonTester.parseObject(json);
        assertThat(object).isNotNull()
                .satisfies(obj -> assertThat(obj.getType()).isEqualTo(GeoJsonType.LINE_STRING.getType()))
                .extracting(LineString::getCoordinates).asList().isNotEmpty()
                .hasSize(2)
                .containsExactly(Position.of(100.0, 0.0), Position.of(101.0, 1.0));
    }

    @Test
    void deserialization_withLongitudeAndLatitudeAndAltitude() throws IOException {
        String json = """
                {
                         "type": "LineString",
                         "coordinates": [
                             [100.0, 0.0, 45.0],
                             [101.0, 1.0, 45.0]
                         ]
                     }
                """;
        LineString object = jacksonTester.parseObject(json);
        assertThat(object).isNotNull()
                .satisfies(obj -> assertThat(obj.getType()).isEqualTo(GeoJsonType.LINE_STRING.getType()))
                .extracting(LineString::getCoordinates).asList().isNotEmpty()
                .hasSize(2)
                .containsExactly(Position.of(100.0, 0.0, 45.0), Position.of(101.0, 1.0, 45.0));
    }

    @Test
    void deserialization_withLongitudeAndLatitudeAndAltitude_andWithManyCoordinates() throws IOException {
        String json = """
                {
                         "type": "LineString",
                         "coordinates": [
                             [100.0, 51.0, 45.0],
                             [101.0, 52.0, 45.0],
                             [102.0, 53.0, 45.0],
                             [103.0, 54.0, 45.0],
                             [104.0, 55.0, 45.0],
                             [105.0, 56.0, 45.0],
                             [106.0, 57.0, 45.0],
                             [107.0, 58.0, 45.0],
                             [108.0, 59.0, 45.0],
                             [109.0, 60.0, 45.0],
                             [110.0, 61.0, 45.0]
                         ]
                     }
                """;
        LineString object = jacksonTester.parseObject(json);
        assertThat(object).isNotNull()
                .satisfies(obj -> assertThat(obj.getType()).isEqualTo(GeoJsonType.LINE_STRING.getType()))
                .extracting(LineString::getCoordinates).asList().isNotEmpty()
                .hasSize(11)
                .containsExactly(
                        Position.of(100.0, 51.0, 45.0),
                        Position.of(101.0, 52.0, 45.0),
                        Position.of(102.0, 53.0, 45.0),
                        Position.of(103.0, 54.0, 45.0),
                        Position.of(104.0, 55.0, 45.0),
                        Position.of(105.0, 56.0, 45.0),
                        Position.of(106.0, 57.0, 45.0),
                        Position.of(107.0, 58.0, 45.0),
                        Position.of(108.0, 59.0, 45.0),
                        Position.of(109.0, 60.0, 45.0),
                        Position.of(110.0, 61.0, 45.0)
                );
    }

    @ParameterizedTest
    @ValueSource(strings = {
            """
                            {
                                     "type": "invalidType",
                                     "coordinates": [
                                         [100.0, 0.0, 45.0],
                                         [101.0, 1.0, 45.0]
                                     ]
                                 }
                    """, """
            {
                     "type": "LineString",
                     "coordinates": [
                         [190.0, 0.0, 45.0],
                         [101.0, 1.0, 45.0]
                     ]
                 }
            """, """
            {
                     "type": "LineString",
                     "coordinates": [
                         [90.0, 91.0, 45.0],
                         [101.0, 1.0, 45.0]
                     ]
                 }
            """, """
            {
                     "type": "LineString",
                     "coordinates": [
                         [90.0, 45.0, 45.0]
                     ]
                 }
            """, """
            {
                     "type": "LineString",
                     "coordinates": [
                         [190.0, 90.0, 45.0],
                         [101.0, 1.0, 45.0]
                     ]
                 }
            """, """
            {
                     "type": "LineString",
                     "coordinates": [
                         [90.0, 90.0, 45.0],
                         [101.0, -91.0, 45.0]
                     ]
                 }
            """
    })
    void deserialization_withInvalidContent(String json) {
        assertThrows(Exception.class, () -> jacksonTester.parseObject(json));
    }

    @Test
    void serialisation_withCoordinates_shouldThrowError() {
        assertThrows(Exception.class, () -> new LineString(List.of()));
    }

    @Test
    void serialisation_withOneCoordinates_shouldThrowError() {
        assertThrows(Exception.class, () -> new LineString(List.of(Position.of(180, 90))));
    }

    @Test
    void serialisation_withValidCoordinates_shouldBeSuccess() throws IOException {
        LineString lineString = new LineString(List.of(Position.of(180, 90), Position.of(-180, -90)));
        JsonContent<LineString> jsonContent = jacksonTester.write(lineString);
        assertThat(jsonContent).isEqualToJson("""
                {
                     "type": "LineString",
                     "coordinates": [
                         [180.0, 90.0],
                         [-180.0, -90.0]
                     ]
                 }
                """);
    }

    @Test
    void serialisation_withValidCoordinates_andAltitude_shouldBeSuccess() throws IOException {
        LineString lineString = new LineString(List.of(Position.of(180, 90, 45), Position.of(-180, -90, 45)));
        JsonContent<LineString> jsonContent = jacksonTester.write(lineString);
        assertThat(jsonContent).isEqualToJson("""
                {
                     "type": "LineString",
                     "coordinates": [
                         [180.0, 90.0, 45.0],
                         [-180.0, -90.0, 45.0]
                     ]
                 }
                """);
    }

}