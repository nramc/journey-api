package com.github.nramc.dev.journey.api.web.resources.rest.journeys.find;

import com.github.nramc.dev.journey.api.config.security.WebSecurityConfig;
import com.github.nramc.dev.journey.api.config.security.InMemoryUserDetailsConfig;
import com.github.nramc.dev.journey.api.config.security.WithMockAuthenticatedUser;
import com.github.nramc.dev.journey.api.config.security.WithMockGuestUser;
import com.github.nramc.dev.journey.api.repository.journey.JourneyRepository;
import com.github.nramc.dev.journey.api.web.resources.Resources;
import org.hamcrest.Matchers;
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

import java.util.Optional;
import java.util.UUID;

import static com.github.nramc.dev.journey.api.web.resources.rest.journeys.JourneyData.JOURNEY_ENTITY;
import static com.github.nramc.dev.journey.api.web.resources.rest.journeys.JourneyData.JOURNEY_EXTENDED_ENTITY;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FindJourneyByIdResource.class)
@Import({WebSecurityConfig.class, InMemoryUserDetailsConfig.class})
@ActiveProfiles({"prod", "test"})
@MockBean({JourneyRepository.class})
class FindJourneyByIdResourceTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private JourneyRepository journeyRepository;

    @Test
    @WithMockAuthenticatedUser
    void find_whenJourneyExistsWithIncompleteData_ShouldReturnValidJson() throws Exception {
        when(journeyRepository.findById(JOURNEY_ENTITY.getId())).thenReturn(Optional.of(JOURNEY_ENTITY));

        mockMvc.perform(MockMvcRequestBuilders.get(Resources.FIND_JOURNEY_BY_ID, JOURNEY_ENTITY.getId())
                        .accept(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(JOURNEY_ENTITY.getId()))
                .andExpect(jsonPath("$.name").value(JOURNEY_ENTITY.getName()))
                .andExpect(jsonPath("$.description").value(JOURNEY_ENTITY.getDescription()))
                .andExpect(jsonPath("$.tags").value(Matchers.hasItems("travel", "germany", "munich")))
                .andExpect(jsonPath("$.thumbnail").value(JOURNEY_ENTITY.getThumbnail()))
                .andExpect(jsonPath("$.journeyDate").value("2024-03-27"))
                .andExpect(jsonPath("$.createdDate").value("2024-03-27"))
                .andExpect(jsonPath("$.isPublished").value(false))
                .andExpect(jsonPath("$.extendedDetails").value(JOURNEY_ENTITY.getExtended()))
        ;
    }

    @Test
    @WithMockAuthenticatedUser
    void find_whenJourneyExistsWithCompleteData_ShouldReturnValidJson() throws Exception {
        when(journeyRepository.findById(JOURNEY_EXTENDED_ENTITY.getId())).thenReturn(Optional.of(JOURNEY_EXTENDED_ENTITY));

        mockMvc.perform(MockMvcRequestBuilders.get(Resources.FIND_JOURNEY_BY_ID, JOURNEY_EXTENDED_ENTITY.getId())
                        .accept(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        jsonPath("$.id").value(JOURNEY_EXTENDED_ENTITY.getId()),
                        jsonPath("$.name").value(JOURNEY_EXTENDED_ENTITY.getName()),
                        jsonPath("$.description").value(JOURNEY_EXTENDED_ENTITY.getDescription()),
                        jsonPath("$.tags").value(Matchers.hasItems("travel", "germany", "munich")),
                        jsonPath("$.thumbnail").value(JOURNEY_EXTENDED_ENTITY.getThumbnail()),
                        jsonPath("$.journeyDate").value("2024-03-27"),
                        jsonPath("$.createdDate").value("2024-03-27"),
                        jsonPath("$.isPublished").value(false),
                        jsonPath("$.extendedDetails").exists()
                )
                .andExpectAll(
                        jsonPath("$.extendedDetails.geoDetails.title").value("Airport, Munich, Germany"),
                        jsonPath("$.extendedDetails.geoDetails.city").value("Munich"),
                        jsonPath("$.extendedDetails.geoDetails.country").value("Germany"),
                        jsonPath("$.extendedDetails.geoDetails.category").value("default"),
                        jsonPath("$.extendedDetails.geoDetails.location.type").value("Point"),
                        jsonPath("$.extendedDetails.geoDetails.location.coordinates").isArray(),
                        jsonPath("$.extendedDetails.geoDetails.location.coordinates").value(hasSize(2)),
                        jsonPath("$.extendedDetails.geoDetails.location.coordinates").value(hasItems(48.183160038296585, 11.53090747669896)),
                        jsonPath("$.extendedDetails.geoDetails.geoJson.type").value("FeatureCollection"),
                        jsonPath("$.extendedDetails.geoDetails.location.coordinates").value(hasItems(48.183160038296585, 11.53090747669896))
                )
                .andExpectAll(
                        jsonPath("$.extendedDetails.imagesDetails.images").value(hasSize(2)),
                        jsonPath("$.extendedDetails.imagesDetails.images[*].url").value(hasItems("image1.jpg", "image2.jpg")),
                        jsonPath("$.extendedDetails.imagesDetails.images[*].assetId").value(hasItems("asset 1", "asset 2")),
                        jsonPath("$.extendedDetails.imagesDetails.images[*].title").value(hasItems("Image 1 Title", "Image 2 Title")),
                        jsonPath("$.extendedDetails.imagesDetails.images[*].isFavorite").value(hasItems(true, true)),
                        jsonPath("$.extendedDetails.imagesDetails.images[*].isThumbnail").value(hasItems(false, false))
                )
                .andExpectAll(
                        jsonPath("$.extendedDetails.videosDetails.videos").value(hasSize(2)),
                        jsonPath("$.extendedDetails.videosDetails.videos[*].videoId").value(hasItems("VIDEO_ID_1", "https://example.com/example.mp4"))
                );
    }

    @Test
    @WithMockGuestUser
    void find_whenJourneyExists_butDoesNotHavePermission_ShouldThrowError() throws Exception {
        when(journeyRepository.findById(JOURNEY_ENTITY.getId())).thenReturn(Optional.of(JOURNEY_ENTITY));

        mockMvc.perform(MockMvcRequestBuilders.get(Resources.FIND_JOURNEY_BY_ID, JOURNEY_ENTITY.getId())
                        .accept(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockAuthenticatedUser
    void find_whenJourneyNotExists_shouldReturnError() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(Resources.FIND_JOURNEY_BY_ID, UUID.randomUUID().toString())
                        .accept(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockAuthenticatedUser
    void find_whenIdNotValid_thenShouldThrowError() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(Resources.FIND_JOURNEY_BY_ID, " ")
                        .accept(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithAnonymousUser
    void find_whenNotAuthenticated_thenShouldThrowError() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(Resources.FIND_JOURNEY_BY_ID, " ")
                        .accept(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isUnauthorized());
    }

}
