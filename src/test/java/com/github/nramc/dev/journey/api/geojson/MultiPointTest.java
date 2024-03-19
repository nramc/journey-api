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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@JsonTest
class MultiPointTest {
    @Autowired
    private JacksonTester<MultiPoint> jacksonTester;

    @Test
    void deserialization_withLongitudeAndLatitude() throws IOException {
        String json = """
                {
                         "type": "MultiPoint",
                         "coordinates": [
                             [100.0, 0.0],
                             [101.0, 1.0]
                         ]
                     }
                """;
        assertThat(jacksonTester.parseObject(json)).isNotNull()
                .satisfies(obj -> assertThat(obj.getType()).isEqualTo(GeoJsonType.MULTI_POINT.getType()))
                .satisfies(obj -> assertThat(obj.getCoordinates()).asList().hasSize(2)
                        .containsExactly(Position.of(100, 0), Position.of(101, 1)));

    }

    @Test
    void deserialization_withLongitudeAndLatitudeAndAltitude() throws IOException {
        String json = """
                {
                         "type": "MultiPoint",
                         "coordinates": [
                             [100.0, 0.0, 5.0],
                             [101.0, 1.0, 5.0]
                         ]
                     }
                """;
        assertThat(jacksonTester.parseObject(json)).isNotNull()
                .satisfies(obj -> assertThat(obj.getType()).isEqualTo(GeoJsonType.MULTI_POINT.getType()))
                .satisfies(obj -> assertThat(obj.getCoordinates()).asList().hasSize(2)
                        .containsExactly(Position.of(100, 0, 5), Position.of(101, 1, 5)));

    }

    @Test
    void serialization_withLongitudeAndLatitudeAndAltitude() throws IOException {
        MultiPoint multiPoint = MultiPoint.of(
                Position.of(100, 50, 5),
                Position.of(110, 60, 5),
                Position.of(120, 70, 5),
                Position.of(130, 80, 5),
                Position.of(140, 85, 5),
                Position.of(150, 90, 5)
        );
        JsonContent<MultiPoint> jsonContent = jacksonTester.write(multiPoint);
        assertThat(jsonContent).isEqualToJson("""
                {
                         "type": "MultiPoint",
                         "coordinates": [
                             [100.0, 50.0, 5.0],
                             [110.0, 60.0, 5.0],
                             [120.0, 70.0, 5.0],
                             [130.0, 80.0, 5.0],
                             [140.0, 85.0, 5.0],
                             [150.0, 90.0, 5.0]
                         ]
                     }
                """);
    }

    @Test
    void serialization_withLongitudeAndLatitude() throws IOException {
        MultiPoint multiPoint = MultiPoint.of(
                Position.of(100, 50),
                Position.of(110, 60),
                Position.of(120, 70),
                Position.of(130, 80),
                Position.of(140, 85),
                Position.of(150, 90)
        );
        JsonContent<MultiPoint> jsonContent = jacksonTester.write(multiPoint);
        assertThat(jsonContent).isEqualToJson("""
                {
                         "type": "MultiPoint",
                         "coordinates": [
                             [100.0, 50.0],
                             [110.0, 60.0],
                             [120.0, 70.0],
                             [130.0, 80.0],
                             [140.0, 85.0],
                             [150.0, 90.0]
                         ]
                     }
                """);
    }

    @ParameterizedTest
    @ValueSource(strings = {"""
            {
                     "type": "MultiPoint",
                     "coordinates": [
                         [200.0, 0.0, 5.0],
                         [101.0, 1.0, 5.0]
                     ]
                 }
            """, """
            {
                     "type": "MultiPoint",
                     "coordinates": [
                         [100.0, 180.0, 5.0],
                         [101.0, 1.0, 5.0]
                     ]
                 }
            """, """
            {
                     "type": "MultiPoint",
                     "coordinates": [
                         [-200.0, 0.0, 5.0],
                         [101.0, 1.0, 5.0]
                     ]
                 }
            """, """
            {
                     "type": "MultiPoint",
                     "coordinates": [
                         [100.0, 0.0, 5.0],
                         [101.0, -100.0, 5.0]
                     ]
                 }
            """, """
            {
                     "type": "MultiPoint",
                     "coordinates": [
                         [100.0, 0.0, 5.0]
                     ]
                 }
            """, """
            {
                     "type": "LineString",
                     "coordinates": [
                         [100.0, 0.0, 5.0],
                         [101.0, 1.0, 5.0]
                     ]
                 }
            """, """
            {
                     "type": "MultiLineString",
                     "coordinates": [
                         [100.0, 0.0, 5.0],
                         [101.0, 1.0, 5.0]
                     ]
                 }
            """
    })
    void deserialization_withInvalidJson(String json) {
        assertThrows(Exception.class, () -> jacksonTester.parseObject(json));
    }

}