package com.github.nramc.dev.journey.api.web.resources.rest.journeys.find;

import com.github.nramc.commons.geojson.domain.Point;
import com.github.nramc.commons.geojson.domain.Position;
import com.github.nramc.dev.journey.api.config.ApplicationProperties;
import com.github.nramc.dev.journey.api.config.security.Visibility;
import com.github.nramc.dev.journey.api.config.security.WebSecurityConfig;
import com.github.nramc.dev.journey.api.config.security.WebSecurityTestConfig;
import com.github.nramc.dev.journey.api.repository.journey.JourneyEntity;
import com.github.nramc.dev.journey.api.repository.journey.JourneyRepository;
import com.github.nramc.dev.journey.api.web.resources.Resources;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Example;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static com.github.nramc.dev.journey.api.config.security.Role.Constants.GUEST_USER;
import static com.github.nramc.dev.journey.api.config.security.Role.Constants.MAINTAINER;
import static com.github.nramc.dev.journey.api.web.resources.Resources.MediaType.JOURNEYS_GEO_JSON;
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
@EnableConfigurationProperties({ApplicationProperties.class})
@MockBean({JourneyRepository.class})
@SuppressWarnings("unchecked")
class FindPublishedJourneyResourceTest {
    private static final String VALID_UUID = "ecc76991-0137-4152-b3b2-efce70a37ed0";
    private static final JourneyEntity VALID_JOURNEY = JourneyEntity.builder()
            .id(VALID_UUID)
            .name("First Flight Experience")
            .title("One of the most beautiful experience ever in my life")
            .description("Travelled first time for work deputation to Germany, Munich city")
            .category("Travel")
            .city("Munich")
            .country("Germany")
            .tags(List.of("travel", "germany", "munich"))
            .thumbnail("valid image id")
            .icon("home")
            .location(Point.of(Position.of(48.183160038296585, 11.53090747669896)))
            .createdDate(LocalDate.of(2024, 3, 27))
            .journeyDate(LocalDate.of(2024, 3, 27))
            .visibilities(Set.of(Visibility.MAINTAINER))
            .build();
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
    @WithMockUser(username = "test-user", authorities = {GUEST_USER})
    void find_whenPublishedJourneyExists_butDoesNNotHavePermission_ShouldReturnEmptyCollection() throws Exception {
        List<JourneyEntity> journeyEntities = List.of(VALID_JOURNEY.toBuilder()
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
                VALID_JOURNEY.toBuilder().isPublished(true).build()
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
                .andExpect(jsonPath("$.features[0].id").value(VALID_JOURNEY.getId()))
                .andExpect(jsonPath("$.features[0].geometry").exists())
                .andExpect(jsonPath("$.features[0].properties").exists())
                .andExpect(jsonPath("$.features[0].properties.name").value(VALID_JOURNEY.getName()))
                .andExpect(jsonPath("$.features[0].properties.category").value(VALID_JOURNEY.getCategory()))
                .andExpect(jsonPath("$.features[0].properties.thumbnail").value(VALID_JOURNEY.getThumbnail()))
                .andExpect(jsonPath("$.features[0].properties.icon").value(VALID_JOURNEY.getIcon()))
                .andExpect(jsonPath("$.features[0].properties.description").value(VALID_JOURNEY.getDescription()))
                .andExpect(jsonPath("$.features[0].properties.tags").value(equalTo(VALID_JOURNEY.getTags())));
    }
}