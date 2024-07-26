package com.github.nramc.dev.journey.api.web.resources.rest.journeys.fetch;

import com.github.nramc.commons.geojson.domain.Point;
import com.github.nramc.commons.geojson.domain.Position;
import com.github.nramc.dev.journey.api.config.TestContainersConfiguration;
import com.github.nramc.dev.journey.api.config.security.Visibility;
import com.github.nramc.dev.journey.api.repository.journey.JourneyEntity;
import com.github.nramc.dev.journey.api.repository.journey.JourneyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

import static com.github.nramc.dev.journey.api.config.security.Role.Constants.MAINTAINER;
import static com.github.nramc.dev.journey.api.web.resources.Resources.FETCH_ALL_CATEGORIES;
import static org.hamcrest.Matchers.containsInRelativeOrder;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestContainersConfiguration.class)
@ActiveProfiles({"test"})
@AutoConfigureMockMvc
class FetchAllCategoriesResourceTest {
    private static final JourneyEntity VALID_JOURNEY = JourneyEntity.builder()
            .name("First Flight Experience")
            .title("One of the most beautiful experience ever in my life")
            .description("Travelled first time for work deputation to Germany, Munich city")
            .category("Travel")
            .city("Munich")
            .country("Germany")
            .tags(List.of("travel", "germany", "munich"))
            .thumbnail("valid image id")
            .location(Point.of(Position.of(48.183160038296585, 11.53090747669896)))
            .createdDate(LocalDate.of(2024, 3, 27))
            .journeyDate(LocalDate.of(2024, 3, 27))
            .visibilities(Set.of(Visibility.MAINTAINER))
            .build();
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
    void find_whenMoreCategoriesExists_shouldLimitResultByDefault() throws Exception {
        IntStream.range(0, 20).forEach(index -> journeyRepository.save(VALID_JOURNEY.toBuilder().category("category_" + index).build()));


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
        IntStream.range(0, 21).forEach(index -> journeyRepository.save(VALID_JOURNEY.toBuilder().category("category_" + index).build()));


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
        IntStream.range(0, 10).forEach(index -> journeyRepository.save(VALID_JOURNEY.toBuilder().category("category_" + index).build()));

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
                VALID_JOURNEY.toBuilder().category("category_" + index % 2).build()));


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