package com.github.nramc.dev.journey.api.web.resources.rest.journeys.fetch;

import com.github.nramc.dev.journey.api.repository.journey.JourneyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.stream.IntStream;

import static com.github.nramc.dev.journey.api.security.Role.Constants.MAINTAINER;
import static com.github.nramc.dev.journey.api.web.resources.Resources.FETCH_ALL_CATEGORIES;
import static com.github.nramc.dev.journey.api.web.resources.rest.journeys.JourneyData.JOURNEY_ENTITY;
import static org.hamcrest.Matchers.containsInRelativeOrder;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles({"test"})
@AutoConfigureMockMvc
class FetchAllCategoriesResourceTest {
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
    @WithMockUser(username = "test-user", password = "test-password", authorities = {MAINTAINER})
    void find_whenMoreCategoriesExists_shouldLimitResultByDefault() throws Exception {
        IntStream.range(0, 20).forEach(index -> journeyRepository.save(JOURNEY_ENTITY.toBuilder().category("category_" + index).build()));


        mockMvc.perform(MockMvcRequestBuilders.get(FETCH_ALL_CATEGORIES)
                        .accept(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.*").isArray())
                .andExpect(jsonPath("$.*").value(hasSize(10)))
                .andExpect(jsonPath("$.*").value(containsInRelativeOrder(
                        "category_0", "category_1", "category_10", "category_11", "category_12",
                        "category_13", "category_14", "category_15", "category_16", "category_17"
                )));
    }

    @Test
    @WithMockUser(username = "test-user", password = "test-password", authorities = {MAINTAINER})
    void find_whenTextGiven_shouldProvideResultForText() throws Exception {
        IntStream.range(0, 21).forEach(index -> journeyRepository.save(JOURNEY_ENTITY.toBuilder().category("category_" + index).build()));


        mockMvc.perform(MockMvcRequestBuilders.get(FETCH_ALL_CATEGORIES)
                        .queryParam("text", "category_2")
                        .accept(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.*").isArray())
                .andExpect(jsonPath("$.*").value(hasSize(2)))
                .andExpect(jsonPath("$.*").value(containsInRelativeOrder("category_2", "category_20")));
    }

    @Test
    @WithMockUser(username = "test-user", password = "test-password", authorities = {MAINTAINER})
    void find_whenMoreCategoriesExists_shouldLimitResult() throws Exception {
        IntStream.range(0, 10).forEach(index -> journeyRepository.save(JOURNEY_ENTITY.toBuilder().category("category_" + index).build()));

        mockMvc.perform(MockMvcRequestBuilders.get(FETCH_ALL_CATEGORIES)
                        .queryParam("limit", "5")
                        .accept(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.*").isArray())
                .andExpect(jsonPath("$.*").value(hasSize(5)))
                .andExpect(jsonPath("$.*").value(containsInRelativeOrder("category_0", "category_1", "category_2", "category_3", "category_4")));
    }

    @Test
    @WithMockUser(username = "test-user", password = "test-password", authorities = {MAINTAINER})
    void find_whenDuplicatesCategoriesExists_shouldReturnUniqueCategories() throws Exception {
        IntStream.range(0, 10).forEach(index -> journeyRepository.save(
                JOURNEY_ENTITY.toBuilder().category("category_" + index % 2).build()));


        mockMvc.perform(MockMvcRequestBuilders.get(FETCH_ALL_CATEGORIES)
                        .accept(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.*").isArray())
                .andExpect(jsonPath("$.*").value(hasSize(2)))
                .andExpect(jsonPath("$.*").value(hasItems("category_0", "category_1")));
    }

}