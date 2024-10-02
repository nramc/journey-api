package com.github.nramc.dev.journey.api.web.resources.rest.journeys.fetch;

import com.github.nramc.dev.journey.api.config.security.WebSecurityConfig;
import com.github.nramc.dev.journey.api.config.security.WebSecurityTestConfig;
import com.github.nramc.dev.journey.api.repository.journey.JourneyRepository;
import com.github.nramc.dev.journey.api.repository.journey.projection.CategoryOnly;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Limit;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;
import java.util.stream.IntStream;

import static com.github.nramc.dev.journey.api.core.user.security.Role.Constants.MAINTAINER;
import static com.github.nramc.dev.journey.api.web.resources.Resources.FETCH_ALL_CATEGORIES;
import static org.hamcrest.Matchers.containsInRelativeOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FetchAllCategoriesResource.class)
@Import({WebSecurityConfig.class, WebSecurityTestConfig.class})
@ActiveProfiles({"prod", "test"})
@MockBean({JourneyRepository.class})
class FetchAllCategoriesResourceTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private JourneyRepository journeyRepository;

    @Test
    @WithMockUser(username = "test-user", password = "test-password", authorities = {MAINTAINER})
    void find_whenMoreCategoriesExists_shouldLimitResultByDefault() throws Exception {
        List<CategoryOnly> categories = IntStream.range(0, 20).mapToObj(index -> (CategoryOnly) () -> "category_" + index).toList();
        when(journeyRepository.findDistinctByCategoryContainingIgnoreCaseOrderByCategory(anyString(), any(Limit.class)))
                .thenReturn(categories);


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
        List<CategoryOnly> categories = IntStream.of(2, 20).mapToObj(index -> (CategoryOnly) () -> "category_" + index).toList();
        when(journeyRepository.findDistinctByCategoryContainingIgnoreCaseOrderByCategory(anyString(), any(Limit.class)))
                .thenReturn(categories);


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
        List<CategoryOnly> categories = IntStream.range(0, 5).mapToObj(index -> (CategoryOnly) () -> "category_" + index).toList();
        when(journeyRepository.findDistinctByCategoryContainingIgnoreCaseOrderByCategory(anyString(), any(Limit.class)))
                .thenReturn(categories);

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

}
