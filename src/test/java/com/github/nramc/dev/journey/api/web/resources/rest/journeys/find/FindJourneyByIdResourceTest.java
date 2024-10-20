package com.github.nramc.dev.journey.api.web.resources.rest.journeys.find;

import com.github.nramc.dev.journey.api.config.security.WebSecurityConfig;
import com.github.nramc.dev.journey.api.config.security.WebSecurityTestConfig;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Optional;
import java.util.UUID;

import static com.github.nramc.dev.journey.api.core.domain.user.Role.Constants.GUEST_USER;
import static com.github.nramc.dev.journey.api.core.domain.user.Role.Constants.MAINTAINER;
import static com.github.nramc.dev.journey.api.web.resources.rest.journeys.JourneyData.JOURNEY_ENTITY;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FindJourneyByIdResource.class)
@Import({WebSecurityConfig.class, WebSecurityTestConfig.class})
@ActiveProfiles({"prod", "test"})
@MockBean({JourneyRepository.class})
class FindJourneyByIdResourceTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private JourneyRepository journeyRepository;

    @Test
    @WithMockUser(username = "test-user", password = "test-password", authorities = {MAINTAINER})
    void find_whenJourneyExists_ShouldReturnValidJson() throws Exception {
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
    @WithMockUser(username = "guest-user", password = "test-password", authorities = {GUEST_USER})
    void find_whenJourneyExists_butDoesNotHavePermission_ShouldThrowError() throws Exception {
        when(journeyRepository.findById(JOURNEY_ENTITY.getId())).thenReturn(Optional.of(JOURNEY_ENTITY));

        mockMvc.perform(MockMvcRequestBuilders.get(Resources.FIND_JOURNEY_BY_ID, JOURNEY_ENTITY.getId())
                        .accept(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "test-user", password = "test-password", authorities = {MAINTAINER})
    void find_whenJourneyNotExists_shouldReturnError() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(Resources.FIND_JOURNEY_BY_ID, UUID.randomUUID().toString())
                        .accept(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "test-user", password = "test-password", authorities = {MAINTAINER})
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
