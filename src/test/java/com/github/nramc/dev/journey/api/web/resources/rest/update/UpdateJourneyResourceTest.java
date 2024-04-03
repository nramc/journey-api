package com.github.nramc.dev.journey.api.web.resources.rest.update;

import com.github.nramc.commons.geojson.domain.Point;
import com.github.nramc.commons.geojson.domain.Position;
import com.github.nramc.dev.journey.api.repository.journey.JourneyEntity;
import com.github.nramc.dev.journey.api.repository.journey.JourneyRepository;
import com.github.nramc.dev.journey.api.web.resources.Resources;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles({"test"})
@AutoConfigureMockMvc
class UpdateJourneyResourceTest {
    private static final JourneyEntity VALID_JOURNEY = JourneyEntity.builder()
            .name("First Flight Experience")
            .title("One of the most beautiful experience ever in my life")
            .description("Travelled first time for work deputation to Germany, Munich city")
            .category("Travel")
            .city("Munich")
            .country("Germany")
            .tags(List.of("Travel", "Germany", "Munich"))
            .thumbnail("valid image id")
            .location(Point.of(Position.of(48.183160038296585, 11.53090747669896)))
            .createdDate(LocalDate.of(2024, 3, 27))
            .journeyDate(LocalDate.of(2024, 3, 27))
            .build();
    @Autowired
    private MockMvc mockMvc;
    @Container
    @ServiceConnection
    static MongoDBContainer mongoDBContainer = new MongoDBContainer(DockerImageName.parse("mongo:latest"))
            .withExposedPorts(27017);

    @Autowired
    private JourneyRepository journeyRepository;

    @Test
    void updateBasicDetails() throws Exception {
        // setup data
        JourneyEntity journeyEntity = journeyRepository.save(VALID_JOURNEY);
        assertThat(journeyEntity).isNotNull()
                .satisfies(entity -> assertThat(entity.getId()).isNotNull());
        String journeyId = journeyEntity.getId();

        mockMvc.perform(put(Resources.UPDATE_JOURNEY, journeyId)
                        .header(HttpHeaders.CONTENT_TYPE, Resources.MediaType.UPDATE_JOURNEY_BASIC_DETAILS)
                        .content("""
                                {
                                  "name" : "First Internation Flight Experience",
                                  "title" : "One of the most beautiful experience ever happened in my life",
                                  "description" : "Travelled first time for work deputation from India to Germany, Munich city",
                                  "category" : "Work",
                                  "city" : "Chennai",
                                  "country" : "India",
                                  "tags" : ["Travel", "Germany", "Munich", "Updated"],
                                  "thumbnail" : "valid image id Updated",
                                  "location" : {
                                    "type": "Point",
                                    "coordinates": [48.183160038296585, 11.53090747669896]
                                  },
                                  "journeyDate": "2050-01-31"
                                }
                                """)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("First Internation Flight Experience"))
                .andExpect(jsonPath("$.title").value("One of the most beautiful experience ever happened in my life"))
                .andExpect(jsonPath("$.description").value("Travelled first time for work deputation from India to Germany, Munich city"))
                .andExpect(jsonPath("$.category").value("Work"))
                .andExpect(jsonPath("$.city").value("Chennai"))
                .andExpect(jsonPath("$.country").value("India"))
                .andExpect(jsonPath("$.tags").isArray())
                .andExpect(jsonPath("$.tags").value(hasSize(4)))
                .andExpect(jsonPath("$.tags").value(hasItems("Travel", "Germany", "Munich", "Updated")))
                .andExpect(jsonPath("$.thumbnail").value("valid image id Updated"))
                .andExpect(jsonPath("$.journeyDate").value("2050-01-31"))
                .andExpect(jsonPath("$.location.type").value("Point"));
    }

    @Test
    void updateGeoDetails() throws Exception {
        // setup data
        JourneyEntity journeyEntity = journeyRepository.save(VALID_JOURNEY);
        assertThat(journeyEntity).isNotNull()
                .satisfies(entity -> assertThat(entity.getId()).isNotNull());
        String journeyId = journeyEntity.getId();

        String jsonRequestTemplate = """
                { "geoJson": %s }
                """;
        String geoJson = Files.readString(Path.of("src/test/resources/data/geojson/geometry-collection.json"));
        mockMvc.perform(put(Resources.UPDATE_JOURNEY, journeyId)
                        .header(HttpHeaders.CONTENT_TYPE, Resources.MediaType.UPDATE_JOURNEY_GEO_DETAILS)
                        .content(jsonRequestTemplate.formatted(geoJson))
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("First Flight Experience"))
                .andExpect(jsonPath("$.title").value("One of the most beautiful experience ever in my life"))
                .andExpect(jsonPath("$.description").value("Travelled first time for work deputation to Germany, Munich city"))
                .andExpect(jsonPath("$.category").value("Travel"))
                .andExpect(jsonPath("$.city").value("Munich"))
                .andExpect(jsonPath("$.country").value("Germany"))
                .andExpect(jsonPath("$.tags").isArray())
                .andExpect(jsonPath("$.tags").value(hasSize(3)))
                .andExpect(jsonPath("$.tags").value(hasItems("Travel", "Germany", "Munich")))
                .andExpect(jsonPath("$.thumbnail").value("valid image id"))
                .andExpect(jsonPath("$.journeyDate").value("2024-03-27"))
                .andExpect(jsonPath("$.createdDate").value("2024-03-27"))
                .andExpect(jsonPath("$.location.type").value("Point"))
                .andExpect(jsonPath("$.location.coordinates").isArray())
                .andExpect(jsonPath("$.location.coordinates").value(hasSize(2)))
                .andExpect(jsonPath("$.location.coordinates").value(hasItems(48.183160038296585, 11.53090747669896)))
                .andExpect(jsonPath("$.extendedDetails.geoDetails.geoJson.type").value("GeometryCollection"))
                .andExpect(jsonPath("$.extendedDetails.imagesDetails").isEmpty());
    }

    @Test
    void updateImagesDetails() throws Exception {
        // setup data
        JourneyEntity journeyEntity = journeyRepository.save(VALID_JOURNEY);
        assertThat(journeyEntity).isNotNull()
                .satisfies(entity -> assertThat(entity.getId()).isNotNull());
        String journeyId = journeyEntity.getId();

        String jsonRequestTemplate = """
                { "images": [
                 {"url":"image1.jpg", "assetId": "first-image"},
                 {"url":"image2.png", "assetId": "second-image"},
                 {"url":"image3.gif", "assetId": "third-image"}
                ]
                }
                """;
        mockMvc.perform(put(Resources.UPDATE_JOURNEY, journeyId)
                        .header(HttpHeaders.CONTENT_TYPE, Resources.MediaType.UPDATE_JOURNEY_IMAGES_DETAILS)
                        .content(jsonRequestTemplate)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("First Flight Experience"))
                .andExpect(jsonPath("$.title").value("One of the most beautiful experience ever in my life"))
                .andExpect(jsonPath("$.description").value("Travelled first time for work deputation to Germany, Munich city"))
                .andExpect(jsonPath("$.category").value("Travel"))
                .andExpect(jsonPath("$.city").value("Munich"))
                .andExpect(jsonPath("$.country").value("Germany"))
                .andExpect(jsonPath("$.tags").isArray())
                .andExpect(jsonPath("$.tags").value(hasSize(3)))
                .andExpect(jsonPath("$.tags").value(hasItems("Travel", "Germany", "Munich")))
                .andExpect(jsonPath("$.thumbnail").value("valid image id"))
                .andExpect(jsonPath("$.journeyDate").value("2024-03-27"))
                .andExpect(jsonPath("$.createdDate").value("2024-03-27"))
                .andExpect(jsonPath("$.location.type").value("Point"))
                .andExpect(jsonPath("$.location.coordinates").isArray())
                .andExpect(jsonPath("$.location.coordinates").value(hasSize(2)))
                .andExpect(jsonPath("$.location.coordinates").value(hasItems(48.183160038296585, 11.53090747669896)))
                .andExpect(jsonPath("$.extendedDetails.imagesDetails.images").value(hasSize(3)))
                .andExpect(jsonPath("$.extendedDetails.imagesDetails.images[*].url").value(hasItems("image1.jpg", "image2.png", "image3.gif")))
                .andExpect(jsonPath("$.extendedDetails.imagesDetails.images[*].assetId").value(hasItems("first-image", "second-image", "third-image")));
    }

}