package com.github.nramc.dev.journey.api.web.resources.rest.journeys.update.basic;

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

import static com.github.nramc.dev.journey.api.security.Role.Constants.AUTHENTICATED_USER;
import static com.github.nramc.dev.journey.api.security.Role.Constants.MAINTAINER;
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
class UpdateJourneyBasicDetailsResourceTest {
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
    private static final String VALID_REQUEST = """
            {
              "name" : "First Internation Flight Experience",
              "title" : "One of the most beautiful experience ever happened in my life",
              "description" : "Travelled first time for work deputation from India to Germany, Munich city",
              "category" : "Work",
              "city" : "Chennai",
              "country" : "India",
              "tags" : ["travel", "germany", "munich", "updated"],
              "thumbnail" : "https://example.com/thumbnail.png",
              "icon": "home",
              "location" : {
                "type": "Point",
                "coordinates": [48.183160038296585, 11.53090747669896]
              },
              "journeyDate": "2050-01-31"
            }
            """;
    private static final ResultMatcher[] STATUS_AND_CONTENT_TYPE_MATCH = new ResultMatcher[]{
            status().isOk(),
            content().contentType(MediaType.APPLICATION_JSON)
    };
    @Autowired
    private MockMvc mockMvc;
    @Container
    @ServiceConnection
    static MongoDBContainer mongoDBContainer = new MongoDBContainer(DockerImageName.parse("mongo:latest"))
            .withExposedPorts(27017);

    @Autowired
    private JourneyRepository journeyRepository;

    @Test
    @WithMockUser(username = "test-user", password = "test-password", authorities = {MAINTAINER})
    void updateBasicDetails() throws Exception {
        // setup data
        JourneyEntity journeyEntity = journeyRepository.save(VALID_JOURNEY);
        assertThat(journeyEntity).isNotNull()
                .satisfies(entity -> assertThat(entity.getId()).isNotNull());
        String journeyId = journeyEntity.getId();

        mockMvc.perform(put(Resources.UPDATE_JOURNEY, journeyId)
                        .header(HttpHeaders.CONTENT_TYPE, Resources.MediaType.UPDATE_JOURNEY_BASIC_DETAILS)
                        .content(VALID_REQUEST)
                )
                .andDo(print())
                .andExpectAll(STATUS_AND_CONTENT_TYPE_MATCH)
                .andExpect(jsonPath("$.name").value("First Internation Flight Experience"))
                .andExpect(jsonPath("$.title").value("One of the most beautiful experience ever happened in my life"))
                .andExpect(jsonPath("$.description").value("Travelled first time for work deputation from India to Germany, Munich city"))
                .andExpect(jsonPath("$.category").value("Work"))
                .andExpect(jsonPath("$.city").value("Chennai"))
                .andExpect(jsonPath("$.country").value("India"))
                .andExpect(jsonPath("$.tags").isArray())
                .andExpect(jsonPath("$.tags").value(hasSize(4)))
                .andExpect(jsonPath("$.tags").value(hasItems("travel", "germany", "munich", "updated")))
                .andExpect(jsonPath("$.thumbnail").value("https://example.com/thumbnail.png"))
                .andExpect(jsonPath("$.icon").value("home"))
                .andExpect(jsonPath("$.journeyDate").value("2050-01-31"))
                .andExpect(jsonPath("$.location.type").value("Point"));
    }

    @Test
    @WithAnonymousUser
    void updateBasicDetails_whenNotAuthenticated_throwError() throws Exception {
        JourneyEntity journeyEntity = journeyRepository.save(VALID_JOURNEY);
        assertThat(journeyEntity).isNotNull()
                .satisfies(entity -> assertThat(entity.getId()).isNotNull());
        String journeyId = journeyEntity.getId();

        mockMvc.perform(put(Resources.UPDATE_JOURNEY, journeyId)
                        .header(HttpHeaders.CONTENT_TYPE, Resources.MediaType.UPDATE_JOURNEY_BASIC_DETAILS)
                        .content(VALID_REQUEST)
                )
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "test-user", password = "test-password", authorities = {AUTHENTICATED_USER})
    void updateBasicDetails_whenNotAuthorized_throwError() throws Exception {
        JourneyEntity journeyEntity = journeyRepository.save(VALID_JOURNEY);
        assertThat(journeyEntity).isNotNull()
                .satisfies(entity -> assertThat(entity.getId()).isNotNull());
        String journeyId = journeyEntity.getId();

        mockMvc.perform(put(Resources.UPDATE_JOURNEY, journeyId)
                        .header(HttpHeaders.CONTENT_TYPE, Resources.MediaType.UPDATE_JOURNEY_BASIC_DETAILS)
                        .content(VALID_REQUEST)
                )
                .andDo(print())
                .andExpect(status().isForbidden());
    }

}