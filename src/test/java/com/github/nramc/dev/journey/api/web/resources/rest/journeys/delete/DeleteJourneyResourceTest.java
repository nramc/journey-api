package com.github.nramc.dev.journey.api.web.resources.rest.journeys.delete;

import com.github.nramc.dev.journey.api.core.journey.security.Visibility;
import com.github.nramc.dev.journey.api.config.security.WebSecurityConfig;
import com.github.nramc.dev.journey.api.config.security.WebSecurityTestConfig;
import com.github.nramc.dev.journey.api.gateway.cloudinary.CloudinaryService;
import com.github.nramc.dev.journey.api.repository.journey.JourneyEntity;
import com.github.nramc.dev.journey.api.repository.journey.JourneyRepository;
import com.github.nramc.dev.journey.api.web.resources.Resources;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Optional;
import java.util.Set;

import static com.github.nramc.dev.journey.api.core.user.security.Role.Constants.AUTHENTICATED_USER;
import static com.github.nramc.dev.journey.api.core.user.security.Role.Constants.GUEST_USER;
import static com.github.nramc.dev.journey.api.core.user.security.Role.Constants.MAINTAINER;
import static com.github.nramc.dev.journey.api.web.resources.rest.journeys.JourneyData.JOURNEY_EXTENDED_ENTITY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DeleteJourneyResource.class)
@Import({WebSecurityConfig.class, WebSecurityTestConfig.class})
@ActiveProfiles({"prod", "test"})
@MockBean({JourneyRepository.class, CloudinaryService.class})
class DeleteJourneyResourceTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private JourneyRepository journeyRepository;
    @Autowired
    private CloudinaryService cloudinaryService;

    @Test
    void context() {
        assertThat(mockMvc).isNotNull();
    }

    @Test
    @WithMockUser(username = "test-user", password = "test-password", authorities = {AUTHENTICATED_USER})
    void delete_whenAuthenticatedUserOwnedJourney_shouldAllowDeletion() throws Exception {
        when(journeyRepository.findById(any())).thenReturn(Optional.of(JOURNEY_EXTENDED_ENTITY));

        mockMvc.perform(MockMvcRequestBuilders.delete(Resources.DELETE_JOURNEY, JOURNEY_EXTENDED_ENTITY.getId()))
                .andDo(print())
                .andExpect(status().isOk());

        verify(journeyRepository).deleteById(anyString());
        verify(cloudinaryService).deleteJourney(any());
    }

    @Test
    @WithMockUser(username = "test-admin", password = "test-password", authorities = {MAINTAINER})
    void delete_whenLoggedInUserDoesNotAccessToJourney_shouldGracefullyIgnoreRequest() throws Exception {
        when(journeyRepository.findById(any())).thenReturn(Optional.of(JOURNEY_EXTENDED_ENTITY));

        mockMvc.perform(MockMvcRequestBuilders.delete(Resources.DELETE_JOURNEY, JOURNEY_EXTENDED_ENTITY.getId()))
                .andDo(print())
                .andExpect(status().isOk());
        verify(journeyRepository, never()).deleteById(anyString());
        verify(cloudinaryService, never()).deleteJourney(any());
    }

    @Test
    @WithMockUser(username = "test-admin", password = "test-password", authorities = {MAINTAINER})
    void delete_whenJourneyAccessibleWithSharedRole_shouldAllowDeletion() throws Exception {
        JourneyEntity journey = JOURNEY_EXTENDED_ENTITY.toBuilder()
                .visibilities(Set.of(Visibility.MYSELF, Visibility.MAINTAINER))
                .build();
        when(journeyRepository.findById(any())).thenReturn(Optional.of(journey));

        mockMvc.perform(MockMvcRequestBuilders.delete(Resources.DELETE_JOURNEY, journey.getId()))
                .andDo(print())
                .andExpect(status().isOk());
        verify(journeyRepository).deleteById(anyString());
        verify(cloudinaryService).deleteJourney(any());
    }

    @Test
    @WithMockUser(username = "test-user", password = "test-password", authorities = {GUEST_USER})
    void delete_whenLoggedInUserDoesNotAuthority_shouldThrowError() throws Exception {
        when(journeyRepository.findById(any())).thenReturn(Optional.of(JOURNEY_EXTENDED_ENTITY));

        mockMvc.perform(MockMvcRequestBuilders.delete(Resources.DELETE_JOURNEY, JOURNEY_EXTENDED_ENTITY.getId()))
                .andDo(print())
                .andExpect(status().isForbidden());
        verify(journeyRepository, never()).deleteById(anyString());
        verify(cloudinaryService, never()).deleteJourney(any());
    }

    @Test
    void delete_whenUserNotAuthenticated_shouldThrowError() throws Exception {
        when(journeyRepository.findById(any())).thenReturn(Optional.of(JOURNEY_EXTENDED_ENTITY));

        mockMvc.perform(MockMvcRequestBuilders.delete(Resources.DELETE_JOURNEY, JOURNEY_EXTENDED_ENTITY.getId()))
                .andDo(print())
                .andExpect(status().isUnauthorized());
        verify(journeyRepository, never()).deleteById(anyString());
        verify(cloudinaryService, never()).deleteJourney(any());
    }

}
