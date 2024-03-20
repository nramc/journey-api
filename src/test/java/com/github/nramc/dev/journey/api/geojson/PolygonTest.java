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
class PolygonTest {
    private static final String POLYGON_WITHOUT_HOLES_JSON = """
            {
                     "type": "Polygon",
                     "coordinates": [
                         [
                             [100.0, 0.0],
                             [101.0, 0.0],
                             [101.0, 1.0],
                             [100.0, 1.0],
                             [100.0, 0.0]
                         ]
                     ]
            }
            """;
    private static final String POLYGON_WITH_HOLES_JSON = """
            {
                     "type": "Polygon",
                     "coordinates": [
                         [
                             [100.0, 0.0],
                             [101.0, 0.0],
                             [101.0, 1.0],
                             [100.0, 1.0],
                             [100.0, 0.0]
                         ],
                         [
                            [100.8, 0.8],
                            [100.8, 0.2],
                            [100.2, 0.2],
                            [100.2, 0.8],
                            [100.8, 0.8]
                         ]
                     ]
            }
            """;
    private static final List<Position> EXTERIOR_RING = List.of(
            Position.of(100, 0),
            Position.of(101, 0),
            Position.of(101, 1),
            Position.of(100, 1),
            Position.of(100, 0)
    );
    private static final List<Position> INTERIOR_RING = List.of(
            Position.of(100.8, 0.8),
            Position.of(100.8, 0.2),
            Position.of(100.2, 0.2),
            Position.of(100.2, 0.8),
            Position.of(100.8, 0.8)
    );
    @Autowired
    private JacksonTester<Polygon> jacksonTester;

    @Test
    void deserialization_withoutHoles() throws IOException {

        Polygon obj = jacksonTester.parseObject(POLYGON_WITHOUT_HOLES_JSON);

        assertThat(obj).isNotNull()
                .satisfies(polygon -> assertThat(polygon.getType()).isEqualTo(GeoJsonType.POLYGON.getType()))
                .extracting(Polygon::getCoordinates)
                .isNotNull()
                .satisfies(coordinates -> assertThat(coordinates.getCoordinates()).asList().isNotEmpty().containsExactly(EXTERIOR_RING))
                .satisfies(coordinates -> assertThat(coordinates.exterior()).asList().isNotEmpty().containsAll(EXTERIOR_RING))
                .satisfies(coordinates -> assertThat(coordinates.holes()).isNullOrEmpty());
    }

    @Test
    void deserialization_withHoles() throws IOException {

        Polygon obj = jacksonTester.parseObject(POLYGON_WITH_HOLES_JSON);

        assertThat(obj).isNotNull()
                .satisfies(polygon -> assertThat(polygon.getType()).isEqualTo(GeoJsonType.POLYGON.getType()))
                .extracting(Polygon::getCoordinates)
                .isNotNull()
                .satisfies(coordinates -> assertThat(coordinates.getCoordinates()).asList().isNotEmpty().containsExactly(EXTERIOR_RING, INTERIOR_RING))
                .satisfies(coordinates -> assertThat(coordinates.exterior()).asList().isNotEmpty().containsAll(EXTERIOR_RING))
                .satisfies(coordinates -> assertThat(coordinates.holes()).asList().isNotEmpty().containsExactly(INTERIOR_RING));
    }

    @Test
    void serialization_withHoles() throws IOException {
        Polygon polygon = Polygon.of(PolygonCoordinates.of(EXTERIOR_RING, INTERIOR_RING));
        JsonContent<Polygon> jsonContent = jacksonTester.write(polygon);
        assertThat(jsonContent).isEqualToJson(POLYGON_WITH_HOLES_JSON);
    }

    @Test
    void serialization_withoutHoles() throws IOException {
        Polygon polygon = Polygon.of(PolygonCoordinates.of(EXTERIOR_RING));
        JsonContent<Polygon> jsonContent = jacksonTester.write(polygon);
        assertThat(jsonContent).isEqualToJson(POLYGON_WITHOUT_HOLES_JSON);
    }


    @ParameterizedTest
    @ValueSource(strings = {/* Invalid type */"""
            {
                     "type": "Invalid type",
                     "coordinates": [
                         [
                             [100.0, 0.0],
                             [101.0, 0.0],
                             [101.0, 1.0],
                             [100.0, 1.0],
                             [100.0, 0.0]
                         ]
                     ]
            }
            """,/* Invalid Coordinate longitude > 180 */"""
            {
                     "type": "Polygon",
                     "coordinates": [
                         [
                             [100.0, 0.0],
                             [190.0, 0.0],
                             [101.0, 1.0],
                             [100.0, 1.0],
                             [100.0, 0.0]
                         ]
                     ]
            }
            """,/* Invalid coordinate latitude > 90 */"""
            {
                     "type": "Polygon",
                     "coordinates": [
                         [
                             [100.0, 0.0],
                             [101.0, 95.0],
                             [101.0, 1.0],
                             [100.0, 1.0],
                             [100.0, 0.0]
                         ]
                     ]
            }
            """,/* Invalid Coordinate first and last position are not same in exterior ring */"""
            {
                     "type": "Polygon",
                     "coordinates": [
                         [
                             [100.0, 0.0],
                             [101.0, 0.0],
                             [101.0, 1.0],
                             [100.0, 1.0],
                             [120.0, 0.0]
                         ]
                     ]
            }
            """,/* Invalid Coordinate, minimum 4 position required in exterior ring*/"""
            {
                     "type": "Polygon",
                     "coordinates": [
                         [
                             [100.0, 0.0],
                             [101.0, 0.0],
                             [100.0, 0.0]
                         ],
                         [
                            [100.8, 0.8],
                            [100.8, 0.2],
                            [100.2, 0.2],
                            [100.2, 0.8],
                            [100.8, 0.8]
                         ]
                     ]
            }
            """
    })
    void deserialization_withInvalidJson(String json) {
        assertThrows(Exception.class, () -> jacksonTester.parseObject(json));
    }

}
