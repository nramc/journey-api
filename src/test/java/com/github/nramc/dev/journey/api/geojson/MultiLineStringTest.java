package com.github.nramc.dev.journey.api.geojson;

import com.github.nramc.dev.journey.api.geojson.types.GeoJsonType;
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
class MultiLineStringTest {
    @Autowired
    private JacksonTester<MultiLineString> jacksonTester;

    @Test
    void deserialization_withLongitudeAndLatitude() throws IOException {
        String json = """
                {
                         "type": "MultiLineString",
                         "coordinates": [
                             [ [100.0, 0.0], [101.0, 1.0] ],
                             [ [102.0, 2.0], [103.0, 3.0] ],
                             [ [104.0, 4.0], [105.0, 5.0] ],
                             [ [106.0, 6.0], [107.0, 7.0] ],
                             [ [108.0, 8.0], [109.0, 9.0] ]
                         ]
                     }
                """;
        assertThat(jacksonTester.parseObject(json))
                .isNotNull()
                .satisfies(obj -> assertThat(obj.getType()).isEqualTo(GeoJsonType.MULTI_LINE_STRING.getType()))
                .satisfies(obj -> assertThat(obj.getCoordinates()).asList().hasSize(5)
                        .containsExactly(
                                List.of(Position.of(100, 0), Position.of(101, 1)),
                                List.of(Position.of(102, 2), Position.of(103, 3)),
                                List.of(Position.of(104, 4), Position.of(105, 5)),
                                List.of(Position.of(106, 6), Position.of(107, 7)),
                                List.of(Position.of(108, 8), Position.of(109, 9))
                        )
                );
    }

    @Test
    void deserialization_withLongitudeAndLatitudeAndAltitude() throws IOException {
        String json = """
                {
                         "type": "MultiLineString",
                         "coordinates": [
                             [ [100.0, 0.0, 0], [101.0, 1.0, 1] ],
                             [ [102.0, 2.0, 2], [103.0, 3.0, 3] ],
                             [ [104.0, 4.0, 4], [105.0, 5.0, 5] ],
                             [ [106.0, 6.0, 6], [107.0, 7.0, 7] ],
                             [ [108.0, 8.0, 8], [109.0, 9.0, 9] ]
                         ]
                     }
                """;
        assertThat(jacksonTester.parseObject(json))
                .isNotNull()
                .satisfies(obj -> assertThat(obj.getType()).isEqualTo(GeoJsonType.MULTI_LINE_STRING.getType()))
                .satisfies(obj -> assertThat(obj.getCoordinates()).asList().hasSize(5)
                        .containsExactly(
                                List.of(Position.of(100, 0, 0), Position.of(101, 1, 1)),
                                List.of(Position.of(102, 2, 2), Position.of(103, 3, 3)),
                                List.of(Position.of(104, 4, 4), Position.of(105, 5, 5)),
                                List.of(Position.of(106, 6, 6), Position.of(107, 7, 7)),
                                List.of(Position.of(108, 8, 8), Position.of(109, 9, 9))
                        )
                );
    }

    @Test
    void serialization_withLongitudeAndLatitudeAndAltitude() throws IOException {
        MultiLineString multiLineString = MultiLineString.of(
                List.of(Position.of(100, 0, 0), Position.of(101, 1, 1)),
                List.of(Position.of(102, 2, 2), Position.of(103, 3, 3)),
                List.of(Position.of(104, 4, 4), Position.of(105, 5, 5)),
                List.of(Position.of(106, 6, 6), Position.of(107, 7, 7)),
                List.of(Position.of(108, 8, 8), Position.of(109, 9, 9))
        );

        JsonContent<MultiLineString> jsonContent = jacksonTester.write(multiLineString);
        assertThat(jsonContent).isEqualToJson("""
                {
                         "type": "MultiLineString",
                         "coordinates": [
                             [ [100.0, 0.0, 0.0], [101.0, 1.0, 1.0] ],
                             [ [102.0, 2.0, 2.0], [103.0, 3.0, 3.0] ],
                             [ [104.0, 4.0, 4.0], [105.0, 5.0, 5.0] ],
                             [ [106.0, 6.0, 6.0], [107.0, 7.0, 7.0] ],
                             [ [108.0, 8.0, 8.0], [109.0, 9.0, 9.0] ]
                         ]
                     }
                """);
    }

    @Test
    void serialization_withLongitudeAndLatitude() throws IOException {
        MultiLineString multiLineString = MultiLineString.of(
                List.of(Position.of(100, 0), Position.of(101, 1)),
                List.of(Position.of(102, 2), Position.of(103, 3)),
                List.of(Position.of(104, 4), Position.of(105, 5)),
                List.of(Position.of(106, 6), Position.of(107, 7)),
                List.of(Position.of(108, 8), Position.of(109, 9))
        );

        JsonContent<MultiLineString> jsonContent = jacksonTester.write(multiLineString);
        assertThat(jsonContent).isEqualToJson("""
                {
                         "type": "MultiLineString",
                         "coordinates": [
                             [ [100.0, 0.0], [101.0, 1.0] ],
                             [ [102.0, 2.0], [103.0, 3.0] ],
                             [ [104.0, 4.0], [105.0, 5.0] ],
                             [ [106.0, 6.0], [107.0, 7.0] ],
                             [ [108.0, 8.0], [109.0, 9.0] ]
                         ]
                     }
                """);
    }


    @ParameterizedTest
    @ValueSource(strings = {"""
            {
                 "type": "Invalid type",
                 "coordinates": [
                     [ [100.0, 0.0], [101.0, 1.0] ],
                     [ [102.0, 2.0], [103.0, 3.0] ],
                     [ [104.0, 4.0], [105.0, 5.0] ],
                     [ [106.0, 6.0], [107.0, 7.0] ],
                     [ [108.0, 8.0], [109.0, 9.0] ]
                 ]
             }
            """, """
            {
                 "type": "LineString",
                 "coordinates": [
                     [ [100.0, 0.0], [101.0, 1.0] ],
                     [ [102.0, 2.0], [103.0, 3.0] ],
                     [ [104.0, 4.0], [105.0, 5.0] ],
                     [ [106.0, 6.0], [107.0, 7.0] ],
                     [ [108.0, 8.0], [109.0, 9.0] ]
                 ]
             }
            """, """
            {
                 "type": "MultiLineString",
                 "coordinates": [
                     [ [190.0, 0.0], [101.0, 1.0] ],
                     [ [102.0, 2.0], [103.0, 3.0] ],
                     [ [104.0, 4.0], [105.0, 5.0] ],
                     [ [106.0, 6.0], [107.0, 7.0] ],
                     [ [108.0, 8.0], [109.0, 9.0] ]
                 ]
             }
            """, """
            {
                 "type": "MultiLineString",
                 "coordinates": [
                     [ [100.0, 180.0], [101.0, 1.0] ],
                     [ [102.0, 2.0], [103.0, 3.0] ],
                     [ [104.0, 4.0], [105.0, 5.0] ],
                     [ [106.0, 6.0], [107.0, 7.0] ],
                     [ [108.0, 8.0], [109.0, 9.0] ]
                 ]
             }
            """, """
            {
                 "type": "MultiLineString",
                 "coordinates": [
                     [ [100.0, 0.0] ]
                 ]
             }
            """
    })
    void serialization_withInvalidJson(String json) {
        Assertions.assertThrows(Exception.class, () -> jacksonTester.parseObject(json));
    }

}
