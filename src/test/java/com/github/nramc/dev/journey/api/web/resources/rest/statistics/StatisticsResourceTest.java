package com.github.nramc.dev.journey.api.web.resources.rest.statistics;

import com.github.nramc.dev.journey.api.config.security.InMemoryUserDetailsConfig;
import com.github.nramc.dev.journey.api.config.security.WebSecurityConfig;
import com.github.nramc.dev.journey.api.config.security.WithMockAuthenticatedUser;
import com.github.nramc.dev.journey.api.core.journey.Journey;
import com.github.nramc.dev.journey.api.repository.journey.JourneyService;
import com.github.nramc.dev.journey.api.repository.journey.converter.JourneyConverter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

import static com.github.nramc.dev.journey.api.core.journey.security.Visibility.MYSELF;
import static com.github.nramc.dev.journey.api.web.resources.Resources.GET_STATISTICS;
import static com.github.nramc.dev.journey.api.web.resources.rest.journeys.JourneyData.JOURNEY_ENTITY;
import static org.hamcrest.Matchers.hasItems;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(StatisticsResource.class)
@Import({WebSecurityConfig.class, InMemoryUserDetailsConfig.class})
@ActiveProfiles({"prod", "test"})
@MockBean({JourneyService.class})
class StatisticsResourceTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private JourneyService journeyService;

    @Test
    @WithAnonymousUser
    void getStatistics_whenNotAuthenticated_shouldThrowError() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(GET_STATISTICS)
                        .accept(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockAuthenticatedUser
    void find_whenJourneyExists_thenShouldReturnResponse() throws Exception {
        // setup data
        List<Journey> journeys = IntStream.range(0, 10).mapToObj(index ->
                JOURNEY_ENTITY.toBuilder()
                        .id("ID_" + index)
                        .createdDate(LocalDate.now().plusDays(index))
                        .visibilities(Set.of(MYSELF))
                        .isPublished(true)
                        .journeyDate(LocalDate.of(2024, 1, 25).plusYears(index % 2))
                        .geoDetails(JOURNEY_ENTITY.getGeoDetails().toBuilder()
                                .category("Category_" + (index % 2 == 0 ? "even" : "odd"))
                                .city("City_" + (index % 2 == 0 ? "even" : "odd"))
                                .country("Country_" + (index % 2 == 0 ? "even" : "odd"))
                                .build()
                        ).build()
        ).map(JourneyConverter::convert).toList();
        when(journeyService.findAllPublishedJourneys(any())).thenReturn(journeys);

        mockMvc.perform(MockMvcRequestBuilders.get(GET_STATISTICS)
                        .accept(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                // assert categories stats
                .andExpect(jsonPath("$.categories").exists())
                .andExpect(jsonPath("$.categories[*].name").value(hasItems("Category_even", "Category_odd")))
                .andExpect(jsonPath("$.categories[*].count").value(hasItems(5, 5)))
                // assert cities stats
                .andExpect(jsonPath("$.cities").exists())
                .andExpect(jsonPath("$.cities[*].name").value(hasItems("City_even", "City_odd")))
                .andExpect(jsonPath("$.cities[*].count").value(hasItems(5, 5)))
                // assert countries stats
                .andExpect(jsonPath("$.countries").exists())
                .andExpect(jsonPath("$.countries[*].name").value(hasItems("Country_even", "Country_odd")))
                .andExpect(jsonPath("$.countries[*].count").value(hasItems(5, 5)))
                // assert years stats
                .andExpect(jsonPath("$.years").exists())
                .andExpect(jsonPath("$.years[*].name").value(hasItems("2025", "2024")))
                .andExpect(jsonPath("$.years[*].count").value(hasItems(5, 5)))
        ;
    }


}
