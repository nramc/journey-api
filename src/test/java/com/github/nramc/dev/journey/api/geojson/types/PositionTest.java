package com.github.nramc.dev.journey.api.geojson.types;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.boot.test.json.ObjectContent;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class PositionTest {
    @Autowired
    private JacksonTester<Position> jacksonTester;


    @Test
    void testSerialization_withMandatoryValuesOnly() throws IOException {
        Position position = Position.of(10.0, 15.0);
        JsonContent<Position> jsonContent = jacksonTester.write(position);
        assertThat(jsonContent).isEqualToJson("[10.0, 15.0]");
    }

    @Test
    void testSerialization_withMandatoryAndOptionalValues() throws IOException {
        Position position = Position.of(10.0, 15.0, 25.0);
        JsonContent<Position> jsonContent = jacksonTester.write(position);
        assertThat(jsonContent).isEqualToJson("[10.0, 15.0, 25.0]");
    }

    @Test
    void testDeserialization_withAllValues() throws IOException {
        String jsonString = "[18.5, 23.9, 30.2]";
        ObjectContent<Position> objectContent = jacksonTester.parse(jsonString);
        assertThat(objectContent).isNotNull()
                .satisfies(obj -> assertThat(obj.getLongitude()).isEqualTo(18.5))
                .satisfies(obj -> assertThat(obj.getLatitude()).isEqualTo(23.9))
                .satisfies(obj -> assertThat(obj.getAltitude()).isEqualTo(30.2));
    }

    @Test
    void testDeserialization_withMandatoryValues() throws IOException {
        String jsonString = "[24.1, 56.3]";
        ObjectContent<Position> objectContent = jacksonTester.parse(jsonString);
        assertThat(objectContent).isNotNull()
                .satisfies(obj -> assertThat(obj.getLongitude()).isEqualTo(24.1))
                .satisfies(obj -> assertThat(obj.getLatitude()).isEqualTo(56.3))
                .satisfies(obj -> assertThat(obj.getAltitude()).isIn(Double.NaN));
    }

    @ParameterizedTest
    @ValueSource(strings = {"[24.1]", "[]", "[1, 2, 3, 4]", "[-190, 90]", "[100.0, -180]", "[190, 74]", "[80.0, 98.0]"})
    @NullAndEmptySource
    void testDeserialization_withInvalidValues(String jsonString) {
        Assertions.assertThrows(Exception.class, () -> jacksonTester.parseObject(jsonString));
    }

}