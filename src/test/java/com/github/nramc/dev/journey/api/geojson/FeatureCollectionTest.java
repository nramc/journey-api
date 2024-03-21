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

@JsonTest
class FeatureCollectionTest {
    @Autowired
    private JacksonTester<FeatureCollection> jacksonTester;

    @Test
    void deserialization() throws IOException {
        FeatureCollection object = jacksonTester.parseObject(Files.readString(Path.of("src/test/resources/data/feature-collection.json")));
        assertThat(object).isNotNull()
                .satisfies(featureCollection -> assertThat(featureCollection.getType()).isEqualTo(GeoJsonType.Constants.FEATURE_COLLECTION_VALUE))
                .extracting(FeatureCollection::getFeatures)
                .asList().isNotEmpty()
                .hasSize(3);
    }

}