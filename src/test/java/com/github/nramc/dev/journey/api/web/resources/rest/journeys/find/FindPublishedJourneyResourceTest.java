package com.github.nramc.dev.journey.api.web.resources.rest.journeys.find;

import com.github.nramc.dev.journey.api.config.security.WebSecurityConfig;
import com.github.nramc.dev.journey.api.config.security.WebSecurityTestConfig;
import com.github.nramc.dev.journey.api.repository.journey.JourneyEntity;
import com.github.nramc.dev.journey.api.repository.journey.JourneyRepository;
import com.github.nramc.dev.journey.api.web.resources.Resources;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Example;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static com.github.nramc.dev.journey.api.core.domain.user.Role.Constants.GUEST_USER;
import static com.github.nramc.dev.journey.api.core.domain.user.Role.Constants.MAINTAINER;
import static com.github.nramc.dev.journey.api.web.resources.Resources.MediaType.JOURNEYS_GEO_JSON;
import static com.github.nramc.dev.journey.api.web.resources.rest.journeys.JourneyData.JOURNEY_ENTITY;
import static com.github.nramc.dev.journey.api.web.resources.rest.journeys.JourneyData.JOURNEY_EXTENDED_ENTITY;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FindPublishedJourneyResource.class)
@Import({WebSecurityConfig.class, WebSecurityTestConfig.class})
@ActiveProfiles({"prod", "test"})
@MockBean({JourneyRepository.class})
@SuppressWarnings("unchecked")
class FindPublishedJourneyResourceTest {
    private static final String VALID_UUID = "ecc76991-0137-4152-b3b2-efce70a37ed0";
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private JourneyRepository journeyRepository;

    @Test
    @WithMockUser(username = "test-user", authorities = {MAINTAINER})
    void find_whenNoPublishedJourneyExists_ShouldReturnEmptyCollection() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(Resources.FIND_PUBLISHED_JOURNEYS)
                        .accept(JOURNEYS_GEO_JSON)
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(JOURNEYS_GEO_JSON))
                .andExpect(jsonPath("$.type").value("FeatureCollection"))
                .andExpect(jsonPath("$.features").isEmpty());
    }

    @Test
    @WithMockUser(username = "guest-user", authorities = {GUEST_USER})
    void find_whenPublishedJourneyExists_butDoesNNotHavePermission_ShouldReturnEmptyCollection() throws Exception {
        List<JourneyEntity> journeyEntities = List.of(JOURNEY_ENTITY.toBuilder()
                .isPublished(true)
                .build());
        when(journeyRepository.findAll(any(Example.class))).thenReturn(journeyEntities);

        mockMvc.perform(MockMvcRequestBuilders.get(Resources.FIND_PUBLISHED_JOURNEYS)
                        .accept(JOURNEYS_GEO_JSON)
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(JOURNEYS_GEO_JSON))
                .andExpect(jsonPath("$.type").value("FeatureCollection"))
                .andExpect(jsonPath("$.features").isEmpty());
    }

    @Test
    @WithMockUser(username = "test-user", authorities = {MAINTAINER})
    void find_whenPublishedJourneyExists_ShouldReturnValidGeoJson() throws Exception {
        List<JourneyEntity> journeyEntities = List.of(
                JOURNEY_EXTENDED_ENTITY.toBuilder().isPublished(true).build()
        );
        when(journeyRepository.findAll(any(Example.class))).thenReturn(journeyEntities);

        mockMvc.perform(MockMvcRequestBuilders.get(Resources.FIND_PUBLISHED_JOURNEYS, VALID_UUID)
                        .accept(JOURNEYS_GEO_JSON)
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(JOURNEYS_GEO_JSON))
                .andExpect(jsonPath("$.type").value("FeatureCollection"))
                .andExpect(jsonPath("$.features").isNotEmpty())
                .andExpect(jsonPath("$.features[0].type").value("Feature"))
                .andExpect(jsonPath("$.features[0].id").value(JOURNEY_EXTENDED_ENTITY.getId()))
                .andExpect(jsonPath("$.features[0].geometry").exists())
                .andExpect(jsonPath("$.features[0].properties").exists())
                .andExpect(jsonPath("$.features[0].properties.name").value(JOURNEY_EXTENDED_ENTITY.getName()))
                .andExpect(jsonPath("$.features[0].properties.category").value(JOURNEY_EXTENDED_ENTITY.getExtended().getGeoDetails().getCategory()))
                .andExpect(jsonPath("$.features[0].properties.thumbnail").value(JOURNEY_EXTENDED_ENTITY.getThumbnail()))
                .andExpect(jsonPath("$.features[0].properties.description").value(JOURNEY_EXTENDED_ENTITY.getDescription()))
                .andExpect(jsonPath("$.features[0].properties.tags").value(equalTo(JOURNEY_EXTENDED_ENTITY.getTags())));
    }
}
