package com.github.nramc.dev.journey.api.web.resources.rest.journeys.update.publish;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.nramc.dev.journey.api.config.security.WebSecurityConfig;
import com.github.nramc.dev.journey.api.config.security.InMemoryUserDetailsConfig;
import com.github.nramc.dev.journey.api.config.security.WithMockAuthenticatedUser;
import com.github.nramc.dev.journey.api.config.security.WithMockGuestUser;
import com.github.nramc.dev.journey.api.core.journey.security.Visibility;
import com.github.nramc.dev.journey.api.repository.journey.JourneyRepository;
import com.github.nramc.dev.journey.api.web.resources.Resources;
import com.github.nramc.dev.journey.api.web.resources.rest.journeys.update.validator.JourneyValidator;
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
import java.util.Set;

import static com.github.nramc.dev.journey.api.core.journey.security.Visibility.ADMINISTRATOR;
import static com.github.nramc.dev.journey.api.core.journey.security.Visibility.MYSELF;
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

@WebMvcTest(PublishJourneyResource.class)
@Import({WebSecurityConfig.class, InMemoryUserDetailsConfig.class})
@ActiveProfiles({"prod", "test"})
@MockBean({JourneyRepository.class, JourneyValidator.class})
class PublishJourneyResourceTest {
    private static final ResultMatcher[] STATUS_AND_CONTENT_TYPE_MATCH = new ResultMatcher[]{
            status().isOk(),
            content().contentType(MediaType.APPLICATION_JSON)
    };
    private static final ResultMatcher[] JOURNEY_BASE_DETAILS_MATCH = new ResultMatcher[]{
            jsonPath("$.name").value("First Flight Experience"),
            jsonPath("$.description").value("Travelled first time for work deputation to Germany, Munich city"),
            jsonPath("$.tags").isArray(),
            jsonPath("$.tags").value(hasSize(3)),
            jsonPath("$.tags").value(hasItems("travel", "germany", "munich")),
            jsonPath("$.thumbnail").value("https://example.com/thumbnail.png"),
            jsonPath("$.journeyDate").value("2024-03-27"),
            jsonPath("$.createdDate").value("2024-03-27"),
    };
    private static final Set<Visibility> DEFAULT_VISIBILITY = Set.of(MYSELF, ADMINISTRATOR);
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private JourneyRepository journeyRepository;
    @Autowired
    private JourneyValidator validator;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockAuthenticatedUser
    void publishJourney_saveAsDraft() throws Exception {
        when(journeyRepository.findById(JOURNEY_ENTITY.getId())).thenReturn(Optional.of(JOURNEY_ENTITY));
        when(journeyRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(validator.canPublish(any())).thenReturn(true);

        PublishJourneyRequest request = PublishJourneyRequest.builder()
                .visibilities(DEFAULT_VISIBILITY)
                .thumbnail("https://example.com/thumbnail.png")
                .isPublished(false)
                .build();
        mockMvc.perform(put(Resources.UPDATE_JOURNEY, JOURNEY_ENTITY.getId())
                        .header(HttpHeaders.CONTENT_TYPE, Resources.MediaType.PUBLISH_JOURNEY_DETAILS)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andDo(print())
                .andExpectAll(STATUS_AND_CONTENT_TYPE_MATCH)
                .andExpectAll(JOURNEY_BASE_DETAILS_MATCH)
                .andExpect(jsonPath("$.isPublished").value(false));
    }

    @Test
    @WithMockAuthenticatedUser
    void publishJourney_whenAllExists_thenShouldPublishJourney() throws Exception {
        when(journeyRepository.findById(JOURNEY_ENTITY.getId())).thenReturn(Optional.of(JOURNEY_ENTITY));
        when(journeyRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(validator.canPublish(any())).thenReturn(true);

        PublishJourneyRequest request = PublishJourneyRequest.builder()
                .visibilities(DEFAULT_VISIBILITY)
                .thumbnail("https://example.com/thumbnail.png")
                .isPublished(true)
                .build();
        mockMvc.perform(put(Resources.UPDATE_JOURNEY, JOURNEY_ENTITY.getId())
                        .header(HttpHeaders.CONTENT_TYPE, Resources.MediaType.PUBLISH_JOURNEY_DETAILS)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andDo(print())
                .andExpectAll(STATUS_AND_CONTENT_TYPE_MATCH)
                .andExpectAll(JOURNEY_BASE_DETAILS_MATCH)
                .andExpect(jsonPath("$.isPublished").value(true));
    }

    @Test
    @WithMockAuthenticatedUser
    void publishJourney_whenValidationFailsDueToInsufficientData_throwsError() throws Exception {
        when(journeyRepository.findById(JOURNEY_ENTITY.getId())).thenReturn(Optional.of(JOURNEY_ENTITY));
        when(journeyRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(validator.canPublish(any())).thenReturn(true);

        PublishJourneyRequest request = PublishJourneyRequest.builder()
                .visibilities(DEFAULT_VISIBILITY)
                .thumbnail(null)
                .isPublished(true)
                .build();
        mockMvc.perform(put(Resources.UPDATE_JOURNEY, JOURNEY_ENTITY.getId())
                        .header(HttpHeaders.CONTENT_TYPE, Resources.MediaType.PUBLISH_JOURNEY_DETAILS)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithAnonymousUser
    void publishJourney_whenNotAuthenticated_thenShouldThrowError() throws Exception {

        PublishJourneyRequest request = PublishJourneyRequest.builder()
                .visibilities(DEFAULT_VISIBILITY)
                .thumbnail("https://example.com/thumbnail.png")
                .isPublished(true)
                .build();
        mockMvc.perform(put(Resources.UPDATE_JOURNEY, JOURNEY_ENTITY.getId())
                        .header(HttpHeaders.CONTENT_TYPE, Resources.MediaType.PUBLISH_JOURNEY_DETAILS)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockGuestUser
    void publishJourney_whenNotAuthorized_thenThrowError() throws Exception {

        PublishJourneyRequest request = PublishJourneyRequest.builder()
                .visibilities(DEFAULT_VISIBILITY)
                .thumbnail("https://example.com/thumbnail.png")
                .isPublished(true)
                .build();
        mockMvc.perform(put(Resources.UPDATE_JOURNEY, JOURNEY_ENTITY.getId())
                        .header(HttpHeaders.CONTENT_TYPE, Resources.MediaType.PUBLISH_JOURNEY_DETAILS)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andDo(print())
                .andExpect(status().isForbidden());
    }

}
