package com.github.nramc.dev.journey.api.geojson;

import com.github.nramc.dev.journey.api.geojson.types.GeoJsonType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@JsonTest
class GeometryCollectionTest {
    @Autowired
    private JacksonTester<GeometryCollection> jacksonTester;

    @Test
    void deserialization() throws IOException {

        GeometryCollection geometryCollection = jacksonTester.parseObject(Files.readString(Path.of("src/test/resources/data/geometry-collection.json")));
        assertThat(geometryCollection).isNotNull()
                .satisfies(geometry -> assertThat(geometry.getType()).isEqualTo(GeoJsonType.Constants.GEOMETRY_COLLECTION_VALUE))
                .extracting(GeometryCollection::getGeometries).asList().isNotEmpty()
                .hasSize(7);
    }

    @Test
    void deserialization_whenGeometriesHasNestedGeometryCollection() {
        String json = """
                {
                         "type": "GeometryCollection",
                         "geometries": [{
                             "type": "Point",
                             "coordinates": [100.0, 0.0]
                         }, {
                            "type": "GeometryCollection",
                            "geometries": [
                                {
                                    "type": "LineString",
                                    "coordinates": [
                                        [101.0, 0.0],
                                        [102.0, 1.0]
                                    ]
                                }
                            ]
                         }
                         ]
                     }
                """;

        assertThrows(Exception.class, () -> jacksonTester.parseObject(json));
    }

}