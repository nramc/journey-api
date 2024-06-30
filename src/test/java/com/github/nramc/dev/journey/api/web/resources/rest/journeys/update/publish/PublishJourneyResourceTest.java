package com.github.nramc.dev.journey.api.web.resources.rest.journeys.update.publish;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.nramc.commons.geojson.domain.Point;
import com.github.nramc.commons.geojson.domain.Position;
import com.github.nramc.dev.journey.api.repository.journey.JourneyEntity;
import com.github.nramc.dev.journey.api.repository.journey.JourneyRepository;
import com.github.nramc.dev.journey.api.security.Visibility;
import com.github.nramc.dev.journey.api.web.resources.Resources;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJson;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static com.github.nramc.dev.journey.api.security.Role.Constants.GUEST_USER;
import static com.github.nramc.dev.journey.api.security.Role.Constants.MAINTAINER;
import static com.github.nramc.dev.journey.api.security.Visibility.ADMINISTRATOR;
import static com.github.nramc.dev.journey.api.security.Visibility.MYSELF;
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
@AutoConfigureJson
class PublishJourneyResourceTest {
    private static final JourneyEntity VALID_JOURNEY = JourneyEntity.builder()
            .name("First Flight Experience")
            .title("One of the most beautiful experience ever in my life")
            .description("Travelled first time for work deputation to Germany, Munich city")
            .category("Travel")
            .city("Munich")
            .country("Germany")
            .tags(List.of("travel", "germany", "munich"))
            .thumbnail("https://example.com/thumbnail.png")
            .icon("home")
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
            jsonPath("$.icon").value("home"),
            jsonPath("$.journeyDate").value("2024-03-27"),
            jsonPath("$.createdDate").value("2024-03-27"),
            jsonPath("$.location.type").value("Point"),
            jsonPath("$.location.coordinates").isArray(),
            jsonPath("$.location.coordinates").value(hasSize(2)),
            jsonPath("$.location.coordinates").value(hasItems(48.183160038296585, 11.53090747669896))
    };
    private static final Set<Visibility> DEFAULT_VISIBILITY = Set.of(MYSELF, ADMINISTRATOR);
    @Autowired
    private MockMvc mockMvc;
    @Container
    @ServiceConnection
    static MongoDBContainer mongoDBContainer = new MongoDBContainer(DockerImageName.parse("mongo:latest"))
            .withExposedPorts(27017);

    @Autowired
    private JourneyRepository journeyRepository;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(username = "test-user", password = "test-password", authorities = {MAINTAINER})
    void publishJourney_saveAsDraft() throws Exception {
        JourneyEntity journeyEntity = journeyRepository.save(VALID_JOURNEY);
        assertThat(journeyEntity).isNotNull()
                .satisfies(entity -> assertThat(entity.getId()).isNotNull());
        String journeyId = journeyEntity.getId();

        PublishJourneyRequest request = PublishJourneyRequest.builder()
                .visibilities(DEFAULT_VISIBILITY)
                .thumbnail("https://example.com/thumbnail.png")
                .isPublished(false)
                .build();
        mockMvc.perform(put(Resources.UPDATE_JOURNEY, journeyId)
                        .header(HttpHeaders.CONTENT_TYPE, Resources.MediaType.PUBLISH_JOURNEY_DETAILS)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andDo(print())
                .andExpectAll(STATUS_AND_CONTENT_TYPE_MATCH)
                .andExpectAll(JOURNEY_BASE_DETAILS_MATCH)
                .andExpect(jsonPath("$.isPublished").value(false));
    }

    @Test
    @WithMockUser(username = "test-user", password = "test-password", authorities = {MAINTAINER})
    void publishJourney_whenAllExists_thenShouldPublishJourney() throws Exception {
        JourneyEntity journeyEntity = journeyRepository.save(VALID_JOURNEY);
        assertThat(journeyEntity).isNotNull()
                .satisfies(entity -> assertThat(entity.getId()).isNotNull());
        String journeyId = journeyEntity.getId();

        PublishJourneyRequest request = PublishJourneyRequest.builder()
                .visibilities(DEFAULT_VISIBILITY)
                .thumbnail("https://example.com/thumbnail.png")
                .isPublished(true)
                .build();
        mockMvc.perform(put(Resources.UPDATE_JOURNEY, journeyId)
                        .header(HttpHeaders.CONTENT_TYPE, Resources.MediaType.PUBLISH_JOURNEY_DETAILS)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andDo(print())
                .andExpectAll(STATUS_AND_CONTENT_TYPE_MATCH)
                .andExpectAll(JOURNEY_BASE_DETAILS_MATCH)
                .andExpect(jsonPath("$.isPublished").value(true));
    }

    @Test
    @WithMockUser(username = "test-user", password = "test-password", authorities = {MAINTAINER})
    void publishJourney_whenVisibilityBusinessRuleValidationFails_throwsError() throws Exception {
        JourneyEntity journeyEntity = journeyRepository.save(VALID_JOURNEY);
        assertThat(journeyEntity).isNotNull()
                .satisfies(entity -> assertThat(entity.getId()).isNotNull());
        String journeyId = journeyEntity.getId();

        PublishJourneyRequest request = PublishJourneyRequest.builder()
                .visibilities(Set.of(MYSELF))
                .thumbnail(null)
                .isPublished(true)
                .build();
        mockMvc.perform(put(Resources.UPDATE_JOURNEY, journeyId)
                        .header(HttpHeaders.CONTENT_TYPE, Resources.MediaType.PUBLISH_JOURNEY_DETAILS)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "test-user", password = "test-password", authorities = {MAINTAINER})
    void publishJourney_whenValidationFailsDueToInsufficientData_throwsError() throws Exception {
        JourneyEntity journeyEntity = journeyRepository.save(VALID_JOURNEY);
        assertThat(journeyEntity).isNotNull()
                .satisfies(entity -> assertThat(entity.getId()).isNotNull());
        String journeyId = journeyEntity.getId();

        PublishJourneyRequest request = PublishJourneyRequest.builder()
                .visibilities(DEFAULT_VISIBILITY)
                .thumbnail(null)
                .isPublished(true)
                .build();
        mockMvc.perform(put(Resources.UPDATE_JOURNEY, journeyId)
                        .header(HttpHeaders.CONTENT_TYPE, Resources.MediaType.PUBLISH_JOURNEY_DETAILS)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithAnonymousUser
    void publishJourney_whenNotAuthenticated_thenShouldThrowError() throws Exception {
        JourneyEntity journeyEntity = journeyRepository.save(VALID_JOURNEY);
        assertThat(journeyEntity).isNotNull()
                .satisfies(entity -> assertThat(entity.getId()).isNotNull());
        String journeyId = journeyEntity.getId();

        PublishJourneyRequest request = PublishJourneyRequest.builder()
                .visibilities(DEFAULT_VISIBILITY)
                .thumbnail("https://example.com/thumbnail.png")
                .isPublished(true)
                .build();
        mockMvc.perform(put(Resources.UPDATE_JOURNEY, journeyId)
                        .header(HttpHeaders.CONTENT_TYPE, Resources.MediaType.PUBLISH_JOURNEY_DETAILS)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "test-user", password = "test-password", authorities = {GUEST_USER})
    void publishJourney_whenNotAuthorized_thenThrowError() throws Exception {
        JourneyEntity journeyEntity = journeyRepository.save(VALID_JOURNEY);
        assertThat(journeyEntity).isNotNull()
                .satisfies(entity -> assertThat(entity.getId()).isNotNull());
        String journeyId = journeyEntity.getId();

        PublishJourneyRequest request = PublishJourneyRequest.builder()
                .visibilities(DEFAULT_VISIBILITY)
                .thumbnail("https://example.com/thumbnail.png")
                .isPublished(true)
                .build();
        mockMvc.perform(put(Resources.UPDATE_JOURNEY, journeyId)
                        .header(HttpHeaders.CONTENT_TYPE, Resources.MediaType.PUBLISH_JOURNEY_DETAILS)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andDo(print())
                .andExpect(status().isForbidden());
    }

}