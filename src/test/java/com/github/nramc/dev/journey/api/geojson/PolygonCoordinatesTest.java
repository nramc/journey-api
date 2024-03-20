package com.github.nramc.dev.journey.api.geojson;

import com.github.nramc.dev.journey.api.geojson.types.Position;
import org.junit.jupiter.api.Assertions;
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

@JsonTest
class PolygonCoordinatesTest {
    @Autowired
    private JacksonTester<PolygonCoordinates> jacksonTester;

    @Test
    void deserialization_withoutHoles() throws IOException {
        String jsonContent = """
                [
                             [
                                 [100.0, 0.0],
                                 [101.0, 0.0],
                                 [101.0, 1.0],
                                 [100.0, 1.0],
                                 [100.0, 0.0]
                             ]
                         ]
                """;
        assertThat(jacksonTester.parseObject(jsonContent)).isNotNull()
                .satisfies(obj -> assertThat(obj.exterior()).isNotNull().asList().hasSize(5)
                        .containsExactly(
                                Position.of(100, 0),
                                Position.of(101, 0),
                                Position.of(101, 1),
                                Position.of(100, 1),
                                Position.of(100, 0)
                        ))
                .satisfies(obj -> assertThat(obj.holes()).isNullOrEmpty());

    }

    @Test
    void deserialization_withHoles() throws IOException {
        String jsonContent = """
                [
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
                """;
        assertThat(jacksonTester.parseObject(jsonContent)).isNotNull()
                .satisfies(obj -> assertThat(obj.exterior()).isNotNull().asList().hasSize(5)
                        .containsExactly(
                                Position.of(100, 0),
                                Position.of(101, 0),
                                Position.of(101, 1),
                                Position.of(100, 1),
                                Position.of(100, 0)
                        ))
                .satisfies(obj -> assertThat(obj.holes()).isNotEmpty().asList().hasSize(1)
                        .containsExactly(List.of(
                                Position.of(100.8, 0.8),
                                Position.of(100.8, 0.2),
                                Position.of(100.2, 0.2),
                                Position.of(100.2, 0.8),
                                Position.of(100.8, 0.8)
                        )));

    }

    @Test
    void serialisation_withoutHoles() throws IOException {
        List<Position> exteriorRing = List.of(
                Position.of(100, 0),
                Position.of(101, 0),
                Position.of(101, 1),
                Position.of(100, 1),
                Position.of(100, 0));
        PolygonCoordinates coordinates = PolygonCoordinates.of(exteriorRing);
        JsonContent<PolygonCoordinates> jsonContent = jacksonTester.write(coordinates);
        assertThat(jsonContent).isEqualToJson("""
                [
                             [
                                 [100.0, 0.0],
                                 [101.0, 0.0],
                                 [101.0, 1.0],
                                 [100.0, 1.0],
                                 [100.0, 0.0]
                             ]
                         ]
                """);
    }

    @Test
    void serialization_withHoles() throws IOException {
        List<Position> exteriorRing = List.of(
                Position.of(100, 0),
                Position.of(101, 0),
                Position.of(101, 1),
                Position.of(100, 1),
                Position.of(100, 0)
        );
        List<Position> hole = List.of(
                Position.of(100.8, 0.8),
                Position.of(100.8, 0.2),
                Position.of(100.2, 0.2),
                Position.of(100.2, 0.8),
                Position.of(100.8, 0.8)
        );

        PolygonCoordinates polygonCoordinates = PolygonCoordinates.of(exteriorRing, hole);

        JsonContent<PolygonCoordinates> jsonContent = jacksonTester.write(polygonCoordinates);
        assertThat(jsonContent).isEqualToJson("""
                [
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
                """);
    }

    @ParameterizedTest
    @ValueSource(strings = {/* exterior ring Should have minimum 4 positions */"""
            [
                         [
                             [100.0, 0.0],
                             [101.0, 0.0],
                             [100.0, 0.0]
                         ]
                     ]
            """,/* exterior ring's first and last position should be same */"""
            [
                         [
                             [100.0, 0.0],
                             [101.0, 0.0],
                             [101.0, 1.0],
                             [100.0, 1.0],
                             [180.0, 0.0]
                         ]
                     ]
            """, /* each hole should have minimum 4 positions */"""
            [
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
                                [100.8, 0.8]
                             ]
                     ]
            """, /* each hole's first and last position should be same */"""
            [
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
                                [180.8, 0.8]
                             ]
                     ]
            """
    })
    void deserialization_withInvalidJson(String jsonContent) {
        Assertions.assertThrows(Exception.class, () -> jacksonTester.parseObject(jsonContent));
    }


}