package com.github.nramc.dev.journey.api.web.resources.rest.journeys.update.geo;

import com.github.nramc.dev.journey.api.config.security.WebSecurityConfig;
import com.github.nramc.dev.journey.api.config.security.WebSecurityTestConfig;
import com.github.nramc.dev.journey.api.repository.journey.JourneyRepository;
import com.github.nramc.dev.journey.api.web.resources.Resources;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import static com.github.nramc.dev.journey.api.core.domain.user.Role.Constants.GUEST_USER;
import static com.github.nramc.dev.journey.api.core.domain.user.Role.Constants.MAINTAINER;
import static com.github.nramc.dev.journey.api.web.resources.rest.journeys.JourneyData.GEO_LOCATION_JSON;
import static com.github.nramc.dev.journey.api.web.resources.rest.journeys.JourneyData.JOURNEY_ENTITY;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UpdateJourneyGeoDetailsResource.class)
@Import({WebSecurityConfig.class, WebSecurityTestConfig.class})
@ActiveProfiles({"prod", "test"})
@MockBean({JourneyRepository.class})
class UpdateJourneyGeoDetailsResourceTest {
    private static final ResultMatcher[] STATUS_AND_CONTENT_TYPE_MATCH = new ResultMatcher[]{
            status().isOk(),
            content().contentType(MediaType.APPLICATION_JSON)
    };
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private JourneyRepository journeyRepository;

    @Test
    @WithMockUser(username = "test-user", password = "test-password", authorities = {MAINTAINER})
    void updateGeoDetails() throws Exception {
        when(journeyRepository.findById(JOURNEY_ENTITY.getId())).thenReturn(Optional.of(JOURNEY_ENTITY));
        when(journeyRepository.save(Mockito.any())).thenAnswer(invocation -> invocation.getArgument(0));

        String jsonRequestTemplate = """
                {
                 "title":"Airport, Munich, Germany",
                 "city": "Munich",
                 "country": "Germany",
                 "location": %s,
                 "geoJson": %s
                  }
                """;
        String geoJson = Files.readString(Path.of("src/test/resources/data/geojson/geometry-collection.json"));
        mockMvc.perform(put(Resources.UPDATE_JOURNEY, JOURNEY_ENTITY.getId())
                        .header(HttpHeaders.CONTENT_TYPE, Resources.MediaType.UPDATE_JOURNEY_GEO_DETAILS)
                        .content(jsonRequestTemplate.formatted(GEO_LOCATION_JSON, geoJson))
                )
                .andDo(print())
                .andExpectAll(STATUS_AND_CONTENT_TYPE_MATCH)
                .andExpectAll(jsonPath("$.name").value("First Flight Experience"),
                        jsonPath("$.description").value("Travelled first time for work deputation to Germany, Munich city"),
                        jsonPath("$.category").value("Travel"),
                        jsonPath("$.tags").isArray(),
                        jsonPath("$.tags").value(hasSize(3)),
                        jsonPath("$.tags").value(hasItems("travel", "germany", "munich")),
                        jsonPath("$.thumbnail").value("https://example.com/thumbnail.png"),
                        jsonPath("$.journeyDate").value("2024-03-27"),
                        jsonPath("$.createdDate").value("2024-03-27")
                )
                .andExpectAll(
                        jsonPath("$.extendedDetails.geoDetails.title").value("Airport, Munich, Germany"),
                        jsonPath("$.extendedDetails.geoDetails.city").value("Munich"),
                        jsonPath("$.extendedDetails.geoDetails.country").value("Germany"),
                        jsonPath("$.extendedDetails.geoDetails.geoJson.type").value("GeometryCollection"),
                        jsonPath("$.extendedDetails.geoDetails.location.type").value("Point"),
                        jsonPath("$.extendedDetails.geoDetails.location.coordinates").isArray(),
                        jsonPath("$.extendedDetails.geoDetails.location.coordinates").value(hasSize(2)),
                        jsonPath("$.extendedDetails.geoDetails.location.coordinates").value(hasItems(48.183160038296585, 11.53090747669896))
                )
                .andExpect(jsonPath("$.extendedDetails.imagesDetails").isEmpty())
                .andExpect(jsonPath("$.extendedDetails.videosDetails").isEmpty());
    }

    @Test
    @WithAnonymousUser
    void updateGeoDetails_whenNotAuthenticated_shouldThrowError() throws Exception {
        String jsonRequestTemplate = """
                { "geoJson": %s }
                """;
        String geoJson = Files.readString(Path.of("src/test/resources/data/geojson/geometry-collection.json"));
        mockMvc.perform(put(Resources.UPDATE_JOURNEY, JOURNEY_ENTITY.getId())
                        .header(HttpHeaders.CONTENT_TYPE, Resources.MediaType.UPDATE_JOURNEY_GEO_DETAILS)
                        .content(jsonRequestTemplate.formatted(geoJson))
                )
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "test-user", password = "test-password", authorities = {GUEST_USER})
    void updateGeoDetails_whenNotAuthorized_shouldThrowError() throws Exception {
        String jsonRequestTemplate = """
                { "geoJson": %s }
                """;
        String geoJson = Files.readString(Path.of("src/test/resources/data/geojson/geometry-collection.json"));
        mockMvc.perform(put(Resources.UPDATE_JOURNEY, JOURNEY_ENTITY.getId())
                        .header(HttpHeaders.CONTENT_TYPE, Resources.MediaType.UPDATE_JOURNEY_GEO_DETAILS)
                        .content(jsonRequestTemplate.formatted(geoJson))
                )
                .andDo(print())
                .andExpect(status().isForbidden());
    }

}
