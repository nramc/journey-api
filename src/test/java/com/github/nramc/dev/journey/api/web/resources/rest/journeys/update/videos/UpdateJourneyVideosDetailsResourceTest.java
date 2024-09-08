package com.github.nramc.dev.journey.api.web.resources.rest.journeys.update.videos;

import com.github.nramc.dev.journey.api.config.security.WebSecurityConfig;
import com.github.nramc.dev.journey.api.config.security.WebSecurityTestConfig;
import com.github.nramc.dev.journey.api.config.security.WithMockAuthenticatedUser;
import com.github.nramc.dev.journey.api.config.security.WithMockGuestUser;
import com.github.nramc.dev.journey.api.repository.journey.JourneyRepository;
import com.github.nramc.dev.journey.api.web.resources.Resources;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;

import java.util.Optional;

import static com.github.nramc.dev.journey.api.web.resources.rest.journeys.JourneyData.JOURNEY_ENTITY;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UpdateJourneyVideosDetailsResource.class)
@Import({WebSecurityConfig.class, WebSecurityTestConfig.class})
@ActiveProfiles({"prod", "test"})
@MockBean({JourneyRepository.class})
class UpdateJourneyVideosDetailsResourceTest {
    private static final ResultMatcher[] STATUS_AND_CONTENT_TYPE_MATCH = new ResultMatcher[]{
            status().isOk(),
            content().contentType(MediaType.APPLICATION_JSON)
    };
    private static final ResultMatcher[] JOURNEY_BASE_DETAILS_MATCH = new ResultMatcher[]{
            jsonPath("$.name").value("First Flight Experience"),
            jsonPath("$.title").value("One of the most beautiful experience ever in my life"),
            jsonPath("$.description").value("Travelled first time for work deputation to Germany, Munich city"),
            jsonPath("$.category").value("Travel"),
            jsonPath("$.city").value("Munich"),
            jsonPath("$.country").value("Germany"),
            jsonPath("$.tags").isArray(),
            jsonPath("$.tags").value(hasSize(3)),
            jsonPath("$.tags").value(hasItems("travel", "germany", "munich")),
            jsonPath("$.thumbnail").value("https://example.com/thumbnail.png"),
            jsonPath("$.journeyDate").value("2024-03-27"),
            jsonPath("$.createdDate").value("2024-03-27"),
            jsonPath("$.location.type").value("Point"),
            jsonPath("$.location.coordinates").isArray(),
            jsonPath("$.location.coordinates").value(hasSize(2)),
            jsonPath("$.location.coordinates").value(hasItems(48.183160038296585, 11.53090747669896))
    };

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private JourneyRepository journeyRepository;

    @Test
    @WithMockAuthenticatedUser
    void updateVideoDetails() throws Exception {
        when(journeyRepository.findById(JOURNEY_ENTITY.getId())).thenReturn(Optional.of(JOURNEY_ENTITY));
        when(journeyRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        String jsonRequestTemplate = """
                { "videos": [
                 {"videoId":"VIDEO_ID001"},
                 {"videoId":"VIDEO_ID002"},
                 {"videoId":"VIDEO_ID003"}
                ]
                }
                """;
        mockMvc.perform(put(Resources.UPDATE_JOURNEY, JOURNEY_ENTITY.getId())
                        .header(HttpHeaders.CONTENT_TYPE, Resources.MediaType.UPDATE_JOURNEY_VIDEOS_DETAILS)
                        .content(jsonRequestTemplate)
                )
                .andDo(print())
                .andExpectAll(STATUS_AND_CONTENT_TYPE_MATCH)
                .andExpectAll(JOURNEY_BASE_DETAILS_MATCH)
                .andExpect(jsonPath("$.extendedDetails.videosDetails.videos").value(hasSize(3)))
                .andExpect(jsonPath("$.extendedDetails.videosDetails.videos[*].videoId").value(hasItems("VIDEO_ID001", "VIDEO_ID002", "VIDEO_ID003")));
    }

    @Test
    @WithAnonymousUser
    void updateVideoDetails_whenNotAuthenticated_thenShouldThrowError() throws Exception {
        String jsonRequestTemplate = """
                { "videos": [
                 {"videoId":"VIDEO_ID001"},
                 {"videoId":"VIDEO_ID002"},
                 {"videoId":"VIDEO_ID003"}
                ]
                }
                """;
        mockMvc.perform(put(Resources.UPDATE_JOURNEY, JOURNEY_ENTITY.getId())
                        .header(HttpHeaders.CONTENT_TYPE, Resources.MediaType.UPDATE_JOURNEY_VIDEOS_DETAILS)
                        .content(jsonRequestTemplate)
                )
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockGuestUser
    void updateVideoDetails_whenNotAuthorized_thenShouldThrowError() throws Exception {
        String jsonRequestTemplate = """
                { "videos": [
                 {"videoId":"VIDEO_ID001"},
                 {"videoId":"VIDEO_ID002"},
                 {"videoId":"VIDEO_ID003"}
                ]
                }
                """;
        mockMvc.perform(put(Resources.UPDATE_JOURNEY, JOURNEY_ENTITY.getId())
                        .header(HttpHeaders.CONTENT_TYPE, Resources.MediaType.UPDATE_JOURNEY_VIDEOS_DETAILS)
                        .content(jsonRequestTemplate)
                )
                .andDo(print())
                .andExpect(status().isForbidden());
    }

}
