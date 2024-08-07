package com.github.nramc.dev.journey.api.web.resources.rest.journeys.update.images;

import com.github.nramc.commons.geojson.domain.Point;
import com.github.nramc.commons.geojson.domain.Position;
import com.github.nramc.dev.journey.api.config.TestContainersConfiguration;
import com.github.nramc.dev.journey.api.repository.journey.JourneyEntity;
import com.github.nramc.dev.journey.api.repository.journey.JourneyRepository;
import com.github.nramc.dev.journey.api.web.resources.Resources;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;

import java.time.LocalDate;
import java.util.List;

import static com.github.nramc.dev.journey.api.config.security.Role.Constants.GUEST_USER;
import static com.github.nramc.dev.journey.api.config.security.Role.Constants.MAINTAINER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestContainersConfiguration.class)
@ActiveProfiles({"test"})
@AutoConfigureMockMvc
class UpdateJourneyImagesDetailsResourceTest {
    private static final JourneyEntity VALID_JOURNEY = JourneyEntity.builder()
            .name("First Flight Experience")
            .title("One of the most beautiful experience ever in my life")
            .description("Travelled first time for work deputation to Germany, Munich city")
            .category("Travel")
            .city("Munich")
            .country("Germany")
            .tags(List.of("travel", "germany", "munich"))
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
            jsonPath("$.tags").value(hasItems("travel", "germany", "munich")),
            jsonPath("$.thumbnail").value("https://example.com/thumbnail.png"),
            jsonPath("$.journeyDate").value("2024-03-27"),
            jsonPath("$.createdDate").value("2024-03-27"),
            jsonPath("$.location.type").value("Point"),
            jsonPath("$.location.coordinates").isArray(),
            jsonPath("$.location.coordinates").value(hasSize(2)),
            jsonPath("$.location.coordinates").value(hasItems(48.183160038296585, 11.53090747669896))
    };
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private JourneyRepository journeyRepository;

    @BeforeEach
    void setup() {
        journeyRepository.deleteAll();
    }

    @Test
    @WithMockUser(username = "test-user", password = "test-password", authorities = {MAINTAINER})
    void updateImagesDetails_whenOptionalFieldDoesNotExists_thenShouldUpdateWithDefaultValues() throws Exception {
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
                .andExpect(jsonPath("$.extendedDetails.imagesDetails.images[*].assetId").value(hasItems("first-image", "second-image", "third-image")))
                .andExpect(jsonPath("$.extendedDetails.imagesDetails.images[*].title").value(hasItems(nullValue(), nullValue(), nullValue())))
                .andExpect(jsonPath("$.extendedDetails.imagesDetails.images[*].isFavorite").value(hasItems(false, false, false)))
                .andExpect(jsonPath("$.extendedDetails.imagesDetails.images[*].isThumbnail").value(hasItems(false, false, false)));
    }

    @Test
    @WithMockUser(username = "test-user", password = "test-password", authorities = {MAINTAINER})
    void updateImagesDetails_whenOptionalFieldHaveValues_thenShouldUpdateWithGivenValues() throws Exception {
        // setup data
        JourneyEntity journeyEntity = journeyRepository.save(VALID_JOURNEY);
        assertThat(journeyEntity).isNotNull()
                .satisfies(entity -> assertThat(entity.getId()).isNotNull());
        String journeyId = journeyEntity.getId();

        String jsonRequestTemplate = """
                { "images": [
                 {"url":"image1.jpg", "assetId": "first-image", "title": "Image1 Title", "isFavorite": true, "isThumbnail": false},
                 {"url":"image2.png", "assetId": "second-image", "title": "Image2 Title", "isFavorite": true, "isThumbnail": false},
                 {"url":"image3.gif", "assetId": "third-image", "title": "Image3 Title", "isFavorite": true, "isThumbnail": false}
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
                .andExpect(jsonPath("$.extendedDetails.imagesDetails.images[*].assetId").value(hasItems("first-image", "second-image", "third-image")))
                .andExpect(jsonPath("$.extendedDetails.imagesDetails.images[*].title").value(hasItems("Image1 Title", "Image2 Title", "Image3 Title")))
                .andExpect(jsonPath("$.extendedDetails.imagesDetails.images[*].isFavorite").value(hasItems(true, true, true)))
                .andExpect(jsonPath("$.extendedDetails.imagesDetails.images[*].isThumbnail").value(hasItems(false, false, false)));
    }

    @Test
    @WithMockUser(username = "test-user", password = "test-password", authorities = {MAINTAINER})
    void updateImagesDetails_whenIsThumbnailFlagEnabled_thenShouldUpdateThumbnailField() throws Exception {
        // setup data
        JourneyEntity journeyEntity = journeyRepository.save(VALID_JOURNEY);
        assertThat(journeyEntity).isNotNull()
                .satisfies(entity -> assertThat(entity.getId()).isNotNull());
        String journeyId = journeyEntity.getId();

        String jsonRequestTemplate = """
                { "images": [
                 {"url":"image1.jpg", "assetId": "first-image", "title": "Image1 Title", "isFavorite": true, "isThumbnail": true},
                 {"url":"image2.png", "assetId": "second-image", "title": "Image2 Title", "isFavorite": true, "isThumbnail": false},
                 {"url":"image3.gif", "assetId": "third-image", "title": "Image3 Title", "isFavorite": true, "isThumbnail": false}
                ]
                }
                """;
        mockMvc.perform(put(Resources.UPDATE_JOURNEY, journeyId)
                        .header(HttpHeaders.CONTENT_TYPE, Resources.MediaType.UPDATE_JOURNEY_IMAGES_DETAILS)
                        .content(jsonRequestTemplate)
                )
                .andDo(print())
                .andExpectAll(STATUS_AND_CONTENT_TYPE_MATCH)
                .andExpectAll(
                        jsonPath("$.name").value("First Flight Experience"),
                        jsonPath("$.title").value("One of the most beautiful experience ever in my life"),
                        jsonPath("$.description").value("Travelled first time for work deputation to Germany, Munich city"),
                        jsonPath("$.category").value("Travel"),
                        jsonPath("$.city").value("Munich"),
                        jsonPath("$.country").value("Germany"),
                        jsonPath("$.tags").isArray(),
                        jsonPath("$.tags").value(hasSize(3)),
                        jsonPath("$.tags").value(hasItems("travel", "germany", "munich")),
                        jsonPath("$.thumbnail").value("image1.jpg"),
                        jsonPath("$.journeyDate").value("2024-03-27"),
                        jsonPath("$.createdDate").value("2024-03-27"),
                        jsonPath("$.location.type").value("Point"),
                        jsonPath("$.location.coordinates").isArray(),
                        jsonPath("$.location.coordinates").value(hasSize(2)),
                        jsonPath("$.location.coordinates").value(hasItems(48.183160038296585, 11.53090747669896))
                )
                .andExpect(jsonPath("$.extendedDetails.imagesDetails.images").value(hasSize(3)))
                .andExpect(jsonPath("$.extendedDetails.imagesDetails.images[*].url").value(hasItems("image1.jpg", "image2.png", "image3.gif")))
                .andExpect(jsonPath("$.extendedDetails.imagesDetails.images[*].assetId").value(hasItems("first-image", "second-image", "third-image")))
                .andExpect(jsonPath("$.extendedDetails.imagesDetails.images[*].title").value(hasItems("Image1 Title", "Image2 Title", "Image3 Title")))
                .andExpect(jsonPath("$.extendedDetails.imagesDetails.images[*].isFavorite").value(hasItems(true, true, true)))
                .andExpect(jsonPath("$.extendedDetails.imagesDetails.images[*].isThumbnail").value(hasItems(false, false, false)));
    }

    @Test
    @WithAnonymousUser
    void updateImagesDetails_whenNotAuthenticated_shouldThrowError() throws Exception {
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
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "test-user", password = "test-password", authorities = {GUEST_USER})
    void updateImagesDetails_whenNotAuthorized_shouldThrowError() throws Exception {
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
                .andExpect(status().isForbidden());
    }

}