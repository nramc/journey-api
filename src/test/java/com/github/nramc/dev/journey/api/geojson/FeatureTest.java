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
import static org.assertj.core.api.Assertions.entry;

@JsonTest
class FeatureTest {
    @Autowired
    private JacksonTester<Feature> jacksonTester;

    @Test
    void deserialization() throws IOException {
        Feature object = jacksonTester.parseObject(Files.readString(Path.of("src/test/resources/data/feature.json")));
        assertThat(object).isNotNull()
                .satisfies(feature -> assertThat(feature.type).isEqualTo(GeoJsonType.FEATURE))
                .satisfies(feature -> assertThat(feature.getId()).isEqualTo("ID_001"))
                .satisfies(feature -> assertThat(feature.getGeometry()).extracting(GeoJson::getType).isEqualTo(GeoJsonType.Constants.POLYGON_VALUE))
                .satisfies(feature -> assertThat(feature.getProperties()).contains(entry("name", "Olympic Park")))
                .satisfies(feature -> assertThat(feature.getProperties()).contains(entry("size", 85)));
    }

}