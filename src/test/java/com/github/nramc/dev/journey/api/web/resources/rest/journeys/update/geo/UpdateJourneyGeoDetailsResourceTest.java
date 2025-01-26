package com.github.nramc.dev.journey.api.web.resources.rest.journeys.update.geo;

import com.github.nramc.dev.journey.api.config.security.InMemoryUserDetailsConfig;
import com.github.nramc.dev.journey.api.config.security.WebSecurityConfig;
import com.github.nramc.dev.journey.api.config.security.WithMockAuthenticatedUser;
import com.github.nramc.dev.journey.api.config.security.WithMockGuestUser;
import com.github.nramc.dev.journey.api.repository.journey.JourneyRepository;
import com.github.nramc.dev.journey.api.web.resources.Resources;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import static com.github.nramc.dev.journey.api.web.resources.rest.journeys.JourneyData.GEO_LOCATION_JSON;
import static com.github.nramc.dev.journey.api.web.resources.rest.journeys.JourneyData.NEW_JOURNEY_ENTITY;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UpdateJourneyGeoDetailsResource.class)
@Import({WebSecurityConfig.class, InMemoryUserDetailsConfig.class})
@ActiveProfiles({"prod", "test"})
class UpdateJourneyGeoDetailsResourceTest {
    private static final ResultMatcher[] STATUS_AND_CONTENT_TYPE_MATCH = new ResultMatcher[]{
            status().isOk(),
            content().contentType(MediaType.APPLICATION_JSON)
    };
    @Autowired
    MockMvc mockMvc;
    @MockitoBean
    JourneyRepository journeyRepository;

    @Test
    @WithMockAuthenticatedUser
    void updateGeoDetails() throws Exception {
        when(journeyRepository.findById(NEW_JOURNEY_ENTITY.getId())).thenReturn(Optional.of(NEW_JOURNEY_ENTITY));
        when(journeyRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        String jsonRequestTemplate = """
                {
                 "title":"Airport, Munich, Germany",
                 "city": "Munich",
                 "country": "Germany",
                 "category": "default",
                 "location": %s,
                 "geoJson": %s
                  }
                """;
        String geoJson = Files.readString(Path.of("src/test/resources/data/geojson/geometry-collection.json"));
        mockMvc.perform(put(Resources.UPDATE_JOURNEY, NEW_JOURNEY_ENTITY.getId())
                        .header(HttpHeaders.CONTENT_TYPE, Resources.MediaType.UPDATE_JOURNEY_GEO_DETAILS)
                        .content(jsonRequestTemplate.formatted(GEO_LOCATION_JSON, geoJson))
                )
                .andDo(print())
                .andExpectAll(STATUS_AND_CONTENT_TYPE_MATCH)
                .andExpectAll(jsonPath("$.name").value("First Flight Experience"),
                        jsonPath("$.description").value("Travelled first time for work deputation to Germany, Munich city"),
                        jsonPath("$.tags").isArray(),
                        jsonPath("$.tags").value(hasSize(3)),
                        jsonPath("$.tags").value(hasItems("travel", "germany", "munich")),
                        jsonPath("$.thumbnail").value("https://example.com/thumbnail.png"),
                        jsonPath("$.journeyDate").value("2024-03-27"),
                        jsonPath("$.createdDate").value("2024-03-27")
                )
                .andExpectAll(
                        jsonPath("$.geoDetails.title").value("Airport, Munich, Germany"),
                        jsonPath("$.geoDetails.city").value("Munich"),
                        jsonPath("$.geoDetails.country").value("Germany"),
                        jsonPath("$.geoDetails.category").value("default"),
                        jsonPath("$.geoDetails.geoJson.type").value("GeometryCollection"),
                        jsonPath("$.geoDetails.location.type").value("Point"),
                        jsonPath("$.geoDetails.location.coordinates").isArray(),
                        jsonPath("$.geoDetails.location.coordinates").value(hasSize(2)),
                        jsonPath("$.geoDetails.location.coordinates").value(hasItems(48.183160038296585, 11.53090747669896))
                )
                .andExpect(jsonPath("$.imagesDetails").isEmpty())
                .andExpect(jsonPath("$.videosDetails").isEmpty());
    }

    @Test
    @WithAnonymousUser
    void updateGeoDetails_whenNotAuthenticated_shouldThrowError() throws Exception {
        String jsonRequestTemplate = """
                { "geoJson": %s }
                """;
        String geoJson = Files.readString(Path.of("src/test/resources/data/geojson/geometry-collection.json"));
        mockMvc.perform(put(Resources.UPDATE_JOURNEY, NEW_JOURNEY_ENTITY.getId())
                        .header(HttpHeaders.CONTENT_TYPE, Resources.MediaType.UPDATE_JOURNEY_GEO_DETAILS)
                        .content(jsonRequestTemplate.formatted(geoJson))
                )
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockGuestUser
    void updateGeoDetails_whenNotAuthorized_shouldThrowError() throws Exception {
        String jsonRequestTemplate = """
                { "geoJson": %s }
                """;
        String geoJson = Files.readString(Path.of("src/test/resources/data/geojson/geometry-collection.json"));
        mockMvc.perform(put(Resources.UPDATE_JOURNEY, NEW_JOURNEY_ENTITY.getId())
                        .header(HttpHeaders.CONTENT_TYPE, Resources.MediaType.UPDATE_JOURNEY_GEO_DETAILS)
                        .content(jsonRequestTemplate.formatted(geoJson))
                )
                .andDo(print())
                .andExpect(status().isForbidden());
    }

}
