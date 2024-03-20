package com.github.nramc.dev.journey.api.geojson;

import com.github.nramc.dev.journey.api.geojson.types.GeoJsonType;
import com.github.nramc.dev.journey.api.geojson.types.Position;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class MultiPolygonTest {
    public static final PolygonCoordinates POLYGON_COORDINATES_ONE = PolygonCoordinates.of(List.of(
            Position.of(180, 40),
            Position.of(180, 50),
            Position.of(170, 50),
            Position.of(170, 40),
            Position.of(180, 40)
    ));
    public static final PolygonCoordinates POLYGON_COORDINATES_TWO = PolygonCoordinates.of(List.of(
            Position.of(-170, 40),
            Position.of(-170, 50),
            Position.of(-180, 50),
            Position.of(-180, 40),
            Position.of(-170, 40)
    ));
    @Autowired
    private JacksonTester<MultiPolygon> jacksonTester;

    @Test
    void deserialization() throws IOException {
        String json = """
                 {
                        "type": "MultiPolygon",
                        "coordinates": [
                            [
                                [
                                    [180.0, 40.0], [180.0, 50.0], [170.0, 50.0],
                                    [170.0, 40.0], [180.0, 40.0]
                                ]
                            ],
                            [
                                [
                                    [-170.0, 40.0], [-170.0, 50.0], [-180.0, 50.0],
                                    [-180.0, 40.0], [-170.0, 40.0]
                                ]
                            ]
                        ]
                }
                 """;

        MultiPolygon obj = jacksonTester.parseObject(json);
        assertThat(obj).isNotNull()
                .satisfies(multiPolygon -> assertThat(multiPolygon.getType()).isEqualTo(GeoJsonType.MULTI_POLYGON.getType()))
                .extracting(MultiPolygon::getCoordinates).asList().isNotEmpty()
                .hasSize(2)
                .containsExactly(POLYGON_COORDINATES_ONE, POLYGON_COORDINATES_TWO);
    }

}
