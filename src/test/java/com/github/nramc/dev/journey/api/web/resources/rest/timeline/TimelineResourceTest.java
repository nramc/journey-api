package com.github.nramc.dev.journey.api.web.resources.rest.timeline;

import com.github.nramc.dev.journey.api.repository.journey.JourneyRepository;
import com.github.nramc.dev.journey.api.security.Visibility;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.LocalDate;
import java.util.Set;
import java.util.stream.IntStream;

import static com.github.nramc.dev.journey.api.security.Role.Constants.MAINTAINER;
import static com.github.nramc.dev.journey.api.security.Visibility.MYSELF;
import static com.github.nramc.dev.journey.api.web.resources.Resources.GET_TIMELINE_DATA;
import static com.github.nramc.dev.journey.api.web.resources.rest.journeys.JourneyData.JOURNEY_ENTITY;
import static com.github.nramc.dev.journey.api.web.resources.rest.journeys.JourneyData.JOURNEY_EXTENDED_ENTITY;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles({"test"})
@AutoConfigureMockMvc
class TimelineResourceTest {
    @Autowired
    private MockMvc mockMvc;
    @Container
    @ServiceConnection
    static MongoDBContainer mongoDBContainer = new MongoDBContainer(DockerImageName.parse("mongo:latest"))
            .withExposedPorts(27017);

    @Autowired
    private JourneyRepository journeyRepository;

    @BeforeEach
    void setup() {
        journeyRepository.deleteAll();
    }

    @Test
    @WithAnonymousUser
    void getTimelineData_whenNotAuthenticated_shouldThrowError() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(GET_TIMELINE_DATA)
                        .accept(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "new-user", password = "test-password", authorities = {MAINTAINER})
    void getTimelineData_whenJourneyExists_butLoggedInUserDoesNotHavePermissions_thenShouldReturnEmptyResponse() throws Exception {
        // setup data
        IntStream.range(0, 10).forEach(index -> journeyRepository.save(
                        JOURNEY_EXTENDED_ENTITY.toBuilder()
                                .id("ID_" + index)
                                .createdDate(LocalDate.now().plusDays(index))
                                .visibilities(Set.of(MYSELF))
                                .isPublished(true)
                                .journeyDate(LocalDate.of(2024, 1, 25).plusYears(index % 2))
                                .category("Category_" + (index % 2 == 0 ? "even" : "odd"))
                                .city("City_" + (index % 2 == 0 ? "even" : "odd"))
                                .country("Country_" + (index % 2 == 0 ? "even" : "odd"))
                                .build()
                )
        );

        mockMvc.perform(MockMvcRequestBuilders.get(GET_TIMELINE_DATA)
                        .accept(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.heading").value("timeline-heading"))
                .andExpect(jsonPath("$.title").value("timeline-title"))
                .andExpect(jsonPath("$.images").isEmpty());
    }

    @Test
    @WithMockUser(username = "new-user", password = "test-password", authorities = {MAINTAINER})
    void getTimelineData_whenJourneyExistsWithAnyOfVisibility_shouldReturnResult() throws Exception {
        // setup data
        IntStream.range(0, 2).forEach(index -> journeyRepository.save(
                JOURNEY_EXTENDED_ENTITY.toBuilder()
                                .id("ID_" + index)
                                .createdDate(LocalDate.now().plusDays(index))
                                .visibilities(Set.of(MYSELF, Visibility.MAINTAINER))
                                .isPublished(true)
                                .journeyDate(LocalDate.of(2024, 1, 25).plusYears(index % 2))
                                .category("Category_" + (index % 2 == 0 ? "even" : "odd"))
                                .city("City_" + (index % 2 == 0 ? "even" : "odd"))
                                .country("Country_" + (index % 2 == 0 ? "even" : "odd"))
                                .build()
                )
        );

        mockMvc.perform(MockMvcRequestBuilders.get(GET_TIMELINE_DATA)
                        .accept(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.heading").value("timeline-heading"))
                .andExpect(jsonPath("$.title").value("timeline-title"))
                .andExpect(jsonPath("$.images").exists())
                .andExpect(jsonPath("$.images[*].src").value(CoreMatchers.hasItems("src_1", "src_1")))
                .andExpect(jsonPath("$.images[*].caption").value(CoreMatchers.hasItems("title 1", "title 1")))
                .andExpect(jsonPath("$.images[0].args").isMap())
                .andExpect(jsonPath("$.images[1].args").isMap());
    }

}