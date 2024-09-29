package com.github.nramc.dev.journey.api.web.resources.rest.journeys.update.basic;

import com.github.nramc.dev.journey.api.config.security.WebSecurityConfig;
import com.github.nramc.dev.journey.api.config.security.WebSecurityTestConfig;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;

import java.util.Optional;

import static com.github.nramc.dev.journey.api.config.security.Role.Constants.GUEST_USER;
import static com.github.nramc.dev.journey.api.config.security.Role.Constants.MAINTAINER;
import static com.github.nramc.dev.journey.api.web.resources.Resources.UPDATE_JOURNEY;
import static com.github.nramc.dev.journey.api.web.resources.rest.journeys.JourneyData.JOURNEY_ENTITY;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UpdateJourneyBasicDetailsResource.class)
@Import({WebSecurityConfig.class, WebSecurityTestConfig.class})
@ActiveProfiles({"prod", "test"})
@MockBean({JourneyRepository.class})
class UpdateJourneyBasicDetailsResourceTest {
    private static final String VALID_REQUEST = """
            {
              "name" : "First Internation Flight Experience",
              "title" : "One of the most beautiful experience ever happened in my life",
              "description" : "Travelled first time for work deputation from India to Germany, Munich city",
              "category" : "Work",
              "city" : "Chennai",
              "country" : "India",
              "tags" : ["travel", "germany", "munich", "updated"],
              "thumbnail" : "https://example.com/thumbnail.png",
              "icon": "home",
              "location" : {
                "type": "Point",
                "coordinates": [48.183160038296585, 11.53090747669896]
              },
              "journeyDate": "2050-01-31"
            }
            """;
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
    void updateBasicDetails() throws Exception {
        when(journeyRepository.findById(anyString())).thenReturn(Optional.of(JOURNEY_ENTITY));
        when(journeyRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        mockMvc.perform(put(UPDATE_JOURNEY, JOURNEY_ENTITY.getId())
                        .header(HttpHeaders.CONTENT_TYPE, Resources.MediaType.UPDATE_JOURNEY_BASIC_DETAILS)
                        .content(VALID_REQUEST)
                )
                .andDo(print())
                .andExpectAll(STATUS_AND_CONTENT_TYPE_MATCH)
                .andExpect(jsonPath("$.name").value("First Internation Flight Experience"))
                .andExpect(jsonPath("$.title").value("One of the most beautiful experience ever happened in my life"))
                .andExpect(jsonPath("$.description").value("Travelled first time for work deputation from India to Germany, Munich city"))
                .andExpect(jsonPath("$.category").value("Work"))
                .andExpect(jsonPath("$.city").value("Chennai"))
                .andExpect(jsonPath("$.country").value("India"))
                .andExpect(jsonPath("$.tags").isArray())
                .andExpect(jsonPath("$.tags").value(hasSize(4)))
                .andExpect(jsonPath("$.tags").value(hasItems("travel", "germany", "munich", "updated")))
                .andExpect(jsonPath("$.thumbnail").value("https://example.com/thumbnail.png"))
                .andExpect(jsonPath("$.icon").value("home"))
                .andExpect(jsonPath("$.journeyDate").value("2050-01-31"))
                .andExpect(jsonPath("$.location.type").value("Point"));
    }

    @Test
    @WithAnonymousUser
    void updateBasicDetails_whenNotAuthenticated_throwError() throws Exception {
        when(journeyRepository.findById(JOURNEY_ENTITY.getId())).thenReturn(Optional.of(JOURNEY_ENTITY));

        mockMvc.perform(put(UPDATE_JOURNEY, JOURNEY_ENTITY.getId())
                        .header(HttpHeaders.CONTENT_TYPE, Resources.MediaType.UPDATE_JOURNEY_BASIC_DETAILS)
                        .content(VALID_REQUEST)
                )
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "guest-user", password = "test-password", authorities = {GUEST_USER})
    void updateBasicDetails_whenNotAuthorized_throwError() throws Exception {
        when(journeyRepository.findById(JOURNEY_ENTITY.getId())).thenReturn(Optional.of(JOURNEY_ENTITY));

        mockMvc.perform(put(UPDATE_JOURNEY, JOURNEY_ENTITY.getId())
                        .header(HttpHeaders.CONTENT_TYPE, Resources.MediaType.UPDATE_JOURNEY_BASIC_DETAILS)
                        .content(VALID_REQUEST)
                )
                .andDo(print())
                .andExpect(status().isForbidden());
    }

}
