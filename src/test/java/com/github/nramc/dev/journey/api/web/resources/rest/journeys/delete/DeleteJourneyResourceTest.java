package com.github.nramc.dev.journey.api.web.resources.rest.journeys.delete;

import com.github.nramc.dev.journey.api.config.security.InMemoryUserDetailsConfig;
import com.github.nramc.dev.journey.api.config.security.WebSecurityConfig;
import com.github.nramc.dev.journey.api.config.security.WithMockAdministratorUser;
import com.github.nramc.dev.journey.api.config.security.WithMockAuthenticatedUser;
import com.github.nramc.dev.journey.api.config.security.WithMockGuestUser;
import com.github.nramc.dev.journey.api.config.security.WithMockMaintainerUser;
import com.github.nramc.dev.journey.api.core.journey.security.Visibility;
import com.github.nramc.dev.journey.api.gateway.cloudinary.CloudinaryGateway;
import com.github.nramc.dev.journey.api.repository.journey.JourneyEntity;
import com.github.nramc.dev.journey.api.repository.journey.JourneyRepository;
import com.github.nramc.dev.journey.api.web.resources.Resources;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Optional;
import java.util.Set;

import static com.github.nramc.dev.journey.api.web.resources.rest.journeys.JourneyData.JOURNEY_ENTITY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DeleteJourneyResource.class)
@Import({WebSecurityConfig.class, InMemoryUserDetailsConfig.class})
@ActiveProfiles({"prod", "test"})
class DeleteJourneyResourceTest {
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    JourneyRepository journeyRepository;
    @MockitoBean
    CloudinaryGateway cloudinaryGateway;

    @Test
    void context() {
        assertThat(mockMvc).isNotNull();
    }

    @Test
    @WithMockAuthenticatedUser
    void delete_whenAuthenticatedUserOwnedJourney_shouldAllowDeletion() throws Exception {
        when(journeyRepository.findById(any())).thenReturn(Optional.of(JOURNEY_ENTITY));

        mockMvc.perform(MockMvcRequestBuilders.delete(Resources.DELETE_JOURNEY, JOURNEY_ENTITY.getId()))
                .andDo(print())
                .andExpect(status().isOk());

        verify(journeyRepository).deleteById(anyString());
        verify(cloudinaryGateway).deleteJourney(any());
    }

    @Test
    @WithMockAdministratorUser
    void delete_whenLoggedInUserDoesNotAccessToJourney_shouldGracefullyIgnoreRequest() throws Exception {
        when(journeyRepository.findById(any())).thenReturn(Optional.of(JOURNEY_ENTITY));

        mockMvc.perform(MockMvcRequestBuilders.delete(Resources.DELETE_JOURNEY, JOURNEY_ENTITY.getId()))
                .andDo(print())
                .andExpect(status().isOk());
        verify(journeyRepository, never()).deleteById(anyString());
        verify(cloudinaryGateway, never()).deleteJourney(any());
    }

    @Test
    @WithMockMaintainerUser
    void delete_whenJourneyAccessibleWithSharedRole_shouldAllowDeletion() throws Exception {
        JourneyEntity journey = JOURNEY_ENTITY.toBuilder()
                .visibilities(Set.of(Visibility.MYSELF, Visibility.MAINTAINER))
                .build();
        when(journeyRepository.findById(any())).thenReturn(Optional.of(journey));

        mockMvc.perform(MockMvcRequestBuilders.delete(Resources.DELETE_JOURNEY, journey.getId()))
                .andDo(print())
                .andExpect(status().isOk());
        verify(journeyRepository).deleteById(anyString());
        verify(cloudinaryGateway).deleteJourney(any());
    }

    @Test
    @WithMockGuestUser
    void delete_whenLoggedInUserDoesNotHaveAuthority_shouldThrowError() throws Exception {
        when(journeyRepository.findById(any())).thenReturn(Optional.of(JOURNEY_ENTITY));

        mockMvc.perform(MockMvcRequestBuilders.delete(Resources.DELETE_JOURNEY, JOURNEY_ENTITY.getId()))
                .andDo(print())
                .andExpect(status().isForbidden());
        verify(journeyRepository, never()).deleteById(anyString());
        verify(cloudinaryGateway, never()).deleteJourney(any());
    }

    @Test
    @WithAnonymousUser
    void delete_whenUserNotAuthenticated_shouldThrowError() throws Exception {
        when(journeyRepository.findById(any())).thenReturn(Optional.of(JOURNEY_ENTITY));

        mockMvc.perform(MockMvcRequestBuilders.delete(Resources.DELETE_JOURNEY, JOURNEY_ENTITY.getId()))
                .andDo(print())
                .andExpect(status().isUnauthorized());
        verify(journeyRepository, never()).deleteById(anyString());
        verify(cloudinaryGateway, never()).deleteJourney(any());
    }

}
