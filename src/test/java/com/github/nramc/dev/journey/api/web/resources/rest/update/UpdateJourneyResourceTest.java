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
import org.springframework.test.web.servlet.ResultMatcher;
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
            .thumbnail("https://example.com/thumbnail.png")
            .location(Point.of(Position.of(48.183160038296585, 11.53090747669896)))
            .createdDate(LocalDate.of(2024, 3, 27))
            .journeyDate(LocalDate.of(2024, 3, 27))
            .build();
    private static final ResultMatcher[] STATUS_AND_CONTENT_TYPE_MATCH = new ResultMatcher[]{
            status().isOk(),
            content().contentType(MediaType.APPLICATION_JSON)
    };
    private static final ResultMatcher[] JOURNEY_BASE_DETAILS_MATCH = new ResultMatcher[]{
            jsonPath("$.name").value("First Flight Experience"),
            jsonPath("$.title").value("One of the most beautiful experience ever in my life"),
            jsonPath("$.description").value("Travelled first time for work deputation to Germany, Munich city"),
            jsonPath("$.category").value("Travel"),
            jsonPath("$.city").value("Munich"),
            jsonPath("$.country").value("Germany"),
            jsonPath("$.tags").isArray(),
            jsonPath("$.tags").value(hasSize(3)),
            jsonPath("$.tags").value(hasItems("Travel", "Germany", "Munich")),
            jsonPath("$.thumbnail").value("https://example.com/thumbnail.png"),
            jsonPath("$.journeyDate").value("2024-03-27"),
            jsonPath("$.createdDate").value("2024-03-27"),
            jsonPath("$.location.type").value("Point"),
            jsonPath("$.location.coordinates").isArray(),
            jsonPath("$.location.coordinates").value(hasSize(2)),
            jsonPath("$.location.coordinates").value(hasItems(48.183160038296585, 11.53090747669896))
    };
    private static final String JOURNEY_BASIC_DETAILS = """
            {
              "name" : "First Flight Experience",
              "title" : "One of the most beautiful experience ever in my life",
              "description" : "Travelled first time for work deputation to Germany, Munich city",
              "category" : "Travel",
              "city" : "Munich",
              "country" : "Germany",
              "tags" : ["Travel", "Germany", "Munich"],
              "thumbnail" : "%s",
              "location" : {
                "type": "Point",
                "coordinates": [48.183160038296585, 11.53090747669896]
              },
              "journeyDate": "2024-03-27",
              "isPublished": %s
            }
            """;
    @Autowired
    private MockMvc mockMvc;
    @Container
    @ServiceConnection
    static MongoDBContainer mongoDBContainer = new MongoDBContainer(DockerImageName.parse("mongo:latest"))
            .withExposedPorts(27017);

    @Autowired
    private JourneyRepository journeyRepository;

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
                .andExpectAll(STATUS_AND_CONTENT_TYPE_MATCH)
                .andExpectAll(JOURNEY_BASE_DETAILS_MATCH)
                .andExpect(jsonPath("$.extendedDetails.geoDetails.geoJson.type").value("GeometryCollection"))
                .andExpect(jsonPath("$.extendedDetails.imagesDetails").isEmpty())
                .andExpect(jsonPath("$.extendedDetails.videosDetails").isEmpty());
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
                .andExpectAll(STATUS_AND_CONTENT_TYPE_MATCH)
                .andExpectAll(JOURNEY_BASE_DETAILS_MATCH)
                .andExpect(jsonPath("$.extendedDetails.imagesDetails.images").value(hasSize(3)))
                .andExpect(jsonPath("$.extendedDetails.imagesDetails.images[*].url").value(hasItems("image1.jpg", "image2.png", "image3.gif")))
                .andExpect(jsonPath("$.extendedDetails.imagesDetails.images[*].assetId").value(hasItems("first-image", "second-image", "third-image")));
    }

    @Test
    void updateVideoDetails() throws Exception {
        // setup data
        JourneyEntity journeyEntity = journeyRepository.save(VALID_JOURNEY);
        assertThat(journeyEntity).isNotNull()
                .satisfies(entity -> assertThat(entity.getId()).isNotNull());
        String journeyId = journeyEntity.getId();

        String jsonRequestTemplate = """
                { "videos": [
                 {"videoId":"VIDEO_ID001"},
                 {"videoId":"VIDEO_ID002"},
                 {"videoId":"VIDEO_ID003"}
                ]
                }
                """;
        mockMvc.perform(put(Resources.UPDATE_JOURNEY, journeyId)
                        .header(HttpHeaders.CONTENT_TYPE, Resources.MediaType.UPDATE_JOURNEY_VIDEOS_DETAILS)
                        .content(jsonRequestTemplate)
                )
                .andDo(print())
                .andExpectAll(STATUS_AND_CONTENT_TYPE_MATCH)
                .andExpectAll(JOURNEY_BASE_DETAILS_MATCH)
                .andExpect(jsonPath("$.extendedDetails.videosDetails.videos").value(hasSize(3)))
                .andExpect(jsonPath("$.extendedDetails.videosDetails.videos[*].videoId").value(hasItems("VIDEO_ID001", "VIDEO_ID002", "VIDEO_ID003")));
    }

    @Test
    void publishJourney_saveAsDraft() throws Exception {
        JourneyEntity journeyEntity = journeyRepository.save(VALID_JOURNEY);
        assertThat(journeyEntity).isNotNull()
                .satisfies(entity -> assertThat(entity.getId()).isNotNull());
        String journeyId = journeyEntity.getId();

        mockMvc.perform(put(Resources.UPDATE_JOURNEY, journeyId)
                        .header(HttpHeaders.CONTENT_TYPE, Resources.MediaType.UPDATE_JOURNEY_BASIC_DETAILS)
                        .content(JOURNEY_BASIC_DETAILS.formatted("https://example.com/thumbnail.png", false))
                )
                .andDo(print())
                .andExpectAll(STATUS_AND_CONTENT_TYPE_MATCH)
                .andExpectAll(JOURNEY_BASE_DETAILS_MATCH)
                .andExpect(jsonPath("$.isPublished").value(false));
    }

    @Test
    void publishJourney() throws Exception {
        JourneyEntity journeyEntity = journeyRepository.save(VALID_JOURNEY);
        assertThat(journeyEntity).isNotNull()
                .satisfies(entity -> assertThat(entity.getId()).isNotNull());
        String journeyId = journeyEntity.getId();

        mockMvc.perform(put(Resources.UPDATE_JOURNEY, journeyId)
                        .header(HttpHeaders.CONTENT_TYPE, Resources.MediaType.PUBLISH_JOURNEY_DETAILS)
                        .content(JOURNEY_BASIC_DETAILS.formatted("https://example.com/thumbnail.png", true))
                )
                .andDo(print())
                .andExpectAll(STATUS_AND_CONTENT_TYPE_MATCH)
                .andExpectAll(JOURNEY_BASE_DETAILS_MATCH)
                .andExpect(jsonPath("$.isPublished").value(true));
    }

    @Test
    void publishJourney_whenValidationFailsDueToInsufficientData_throwsError() throws Exception {
        JourneyEntity journeyEntity = journeyRepository.save(VALID_JOURNEY);
        assertThat(journeyEntity).isNotNull()
                .satisfies(entity -> assertThat(entity.getId()).isNotNull());
        String journeyId = journeyEntity.getId();

        mockMvc.perform(put(Resources.UPDATE_JOURNEY, journeyId)
                        .header(HttpHeaders.CONTENT_TYPE, Resources.MediaType.PUBLISH_JOURNEY_DETAILS)
                        .content(JOURNEY_BASIC_DETAILS.formatted(null, true))
                )
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

}