package com.github.nramc.dev.journey.api.web.resources.rest.journeys.update.publish;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.nramc.dev.journey.api.config.security.InMemoryUserDetailsConfig;
import com.github.nramc.dev.journey.api.config.security.WebSecurityConfig;
import com.github.nramc.dev.journey.api.config.security.WithMockAuthenticatedUser;
import com.github.nramc.dev.journey.api.config.security.WithMockGuestUser;
import com.github.nramc.dev.journey.api.core.journey.security.Visibility;
import com.github.nramc.dev.journey.api.repository.journey.JourneyEntity;
import com.github.nramc.dev.journey.api.repository.journey.JourneyRepository;
import com.github.nramc.dev.journey.api.web.resources.Resources;
import com.github.nramc.dev.journey.api.web.resources.rest.journeys.JourneyData;
import com.github.nramc.dev.journey.api.web.resources.rest.journeys.update.validator.JourneyValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration;
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
import java.util.stream.Stream;

import static com.github.nramc.dev.journey.api.core.journey.security.Visibility.MYSELF;
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

@WebMvcTest(PublishJourneyResource.class)
@Import({WebSecurityConfig.class, InMemoryUserDetailsConfig.class, ValidationAutoConfiguration.class, JourneyValidator.class})
@ActiveProfiles({"prod", "test"})
@MockBean({JourneyRepository.class})
class PublishJourneyResourceTest {
    private static final JourneyEntity JOURNEY_ENTITY = JourneyData.JOURNEY_ENTITY.toBuilder().build();
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
            jsonPath("$.createdDate").value("2024-03-27")
    };
    private static final Set<Visibility> DEFAULT_VISIBILITY = Set.of(MYSELF);
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private JourneyRepository journeyRepository;
    @Autowired
    private ObjectMapper objectMapper;

    static Stream<JourneyEntity> validJourneyDataProvider() {

        return Stream.of(
                JOURNEY_ENTITY.toBuilder().build(),
                JOURNEY_ENTITY.toBuilder().videosDetails(null).build(),
                JOURNEY_ENTITY.toBuilder().imagesDetails(null).build(),
                JOURNEY_ENTITY.toBuilder().imagesDetails(null).videosDetails(null).build()
        );
    }

    @ParameterizedTest
    @WithMockAuthenticatedUser
    @MethodSource("validJourneyDataProvider")
    void publishJourney_saveAsDraft(JourneyEntity journeyEntity) throws Exception {
        when(journeyRepository.findById(journeyEntity.getId())).thenReturn(Optional.of(journeyEntity));
        when(journeyRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        PublishJourneyRequest request = PublishJourneyRequest.builder()
                .visibilities(DEFAULT_VISIBILITY)
                .thumbnail("https://example.com/thumbnail.png")
                .isPublished(false)
                .build();
        mockMvc.perform(put(Resources.UPDATE_JOURNEY, journeyEntity.getId())
                        .header(HttpHeaders.CONTENT_TYPE, Resources.MediaType.PUBLISH_JOURNEY_DETAILS)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andDo(print())
                .andExpectAll(STATUS_AND_CONTENT_TYPE_MATCH)
                .andExpectAll(JOURNEY_BASE_DETAILS_MATCH)
                .andExpect(jsonPath("$.isPublished").value(false));
    }

    @ParameterizedTest
    @WithMockAuthenticatedUser
    @MethodSource("validJourneyDataProvider")
    void publishJourney_whenAllExists_thenShouldPublishJourney(JourneyEntity journeyEntity) throws Exception {
        when(journeyRepository.findById(journeyEntity.getId())).thenReturn(Optional.of(journeyEntity));
        when(journeyRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        PublishJourneyRequest request = PublishJourneyRequest.builder()
                .visibilities(DEFAULT_VISIBILITY)
                .thumbnail("https://example.com/thumbnail.png")
                .isPublished(true)
                .build();
        mockMvc.perform(put(Resources.UPDATE_JOURNEY, journeyEntity.getId())
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
        when(journeyRepository.findById(NEW_JOURNEY_ENTITY.getId())).thenReturn(Optional.of(NEW_JOURNEY_ENTITY));
        when(journeyRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        PublishJourneyRequest request = PublishJourneyRequest.builder()
                .visibilities(DEFAULT_VISIBILITY)
                .thumbnail(null)
                .isPublished(true)
                .build();
        mockMvc.perform(put(Resources.UPDATE_JOURNEY, NEW_JOURNEY_ENTITY.getId())
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
        mockMvc.perform(put(Resources.UPDATE_JOURNEY, NEW_JOURNEY_ENTITY.getId())
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
        mockMvc.perform(put(Resources.UPDATE_JOURNEY, NEW_JOURNEY_ENTITY.getId())
                        .header(HttpHeaders.CONTENT_TYPE, Resources.MediaType.PUBLISH_JOURNEY_DETAILS)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andDo(print())
                .andExpect(status().isForbidden());
    }

}
