package com.github.nramc.dev.journey.api.web.resources.rest.journeys.delete;

import com.github.nramc.dev.journey.api.config.TestContainersConfiguration;
import com.github.nramc.dev.journey.api.config.security.Visibility;
import com.github.nramc.dev.journey.api.repository.journey.JourneyEntity;
import com.github.nramc.dev.journey.api.repository.journey.JourneyRepository;
import com.github.nramc.dev.journey.api.core.services.cloudinary.CloudinaryService;
import com.github.nramc.dev.journey.api.web.resources.Resources;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Optional;
import java.util.Set;

import static com.github.nramc.dev.journey.api.config.security.Role.Constants.AUTHENTICATED_USER;
import static com.github.nramc.dev.journey.api.config.security.Role.Constants.GUEST_USER;
import static com.github.nramc.dev.journey.api.config.security.Role.Constants.MAINTAINER;
import static com.github.nramc.dev.journey.api.web.resources.rest.journeys.JourneyData.JOURNEY_EXTENDED_ENTITY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestContainersConfiguration.class)
@ActiveProfiles({"test"})
@AutoConfigureMockMvc
class DeleteJourneyResourceTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private JourneyRepository journeyRepository;
    @MockBean
    private CloudinaryService cloudinaryService;

    @BeforeEach
    void setup() {
        journeyRepository.deleteAll();
    }

    @Test
    void context() {
        assertThat(mockMvc).isNotNull();
    }

    @Test
    @WithMockUser(username = "test-user", password = "test-password", authorities = {AUTHENTICATED_USER})
    void delete_whenAuthenticatedUserOwnedJourney_shouldAllowDeletion() throws Exception {
        journeyRepository.save(JOURNEY_EXTENDED_ENTITY);

        mockMvc.perform(MockMvcRequestBuilders.delete(Resources.DELETE_JOURNEY, JOURNEY_EXTENDED_ENTITY.getId()))
                .andDo(print())
                .andExpect(status().isOk());
        verify(cloudinaryService).deleteJourney(any());
        Optional<JourneyEntity> optionalJourney = journeyRepository.findById(JOURNEY_EXTENDED_ENTITY.getId());
        assertThat(optionalJourney).isEmpty();
    }

    @Test
    @WithMockUser(username = "test-admin", password = "test-password", authorities = {MAINTAINER})
    void delete_whenLoggedInUserDoesNotAccessToJourney_shouldThrowError() throws Exception {
        journeyRepository.save(JOURNEY_EXTENDED_ENTITY);

        mockMvc.perform(MockMvcRequestBuilders.delete(Resources.DELETE_JOURNEY, JOURNEY_EXTENDED_ENTITY.getId()))
                .andDo(print())
                .andExpect(status().isOk());
        verify(cloudinaryService, never()).deleteJourney(any());
        Optional<JourneyEntity> optionalJourney = journeyRepository.findById(JOURNEY_EXTENDED_ENTITY.getId());
        assertThat(optionalJourney).isNotEmpty();
    }

    @Test
    @WithMockUser(username = "test-admin", password = "test-password", authorities = {MAINTAINER})
    void delete_whenJourneyAccessibleWithSharedRole_shouldAllowDeletion() throws Exception {
        JourneyEntity journey = JOURNEY_EXTENDED_ENTITY.toBuilder()
                .visibilities(Set.of(Visibility.MYSELF, Visibility.MAINTAINER))
                .build();
        journeyRepository.save(journey);

        mockMvc.perform(MockMvcRequestBuilders.delete(Resources.DELETE_JOURNEY, journey.getId()))
                .andDo(print())
                .andExpect(status().isOk());
        verify(cloudinaryService).deleteJourney(any());
        Optional<JourneyEntity> optionalJourney = journeyRepository.findById(journey.getId());
        assertThat(optionalJourney).isEmpty();
    }

    @Test
    @WithMockUser(username = "test-user", password = "test-password", authorities = {GUEST_USER})
    void delete_whenLoggedInUserDoesNotAuthority_shouldThrowError() throws Exception {
        journeyRepository.save(JOURNEY_EXTENDED_ENTITY);

        mockMvc.perform(MockMvcRequestBuilders.delete(Resources.DELETE_JOURNEY, JOURNEY_EXTENDED_ENTITY.getId()))
                .andDo(print())
                .andExpect(status().isForbidden());
        verify(cloudinaryService, never()).deleteJourney(any());
        Optional<JourneyEntity> optionalJourney = journeyRepository.findById(JOURNEY_EXTENDED_ENTITY.getId());
        assertThat(optionalJourney).isNotEmpty();
    }

    @Test
    void delete_whenUSerNotAuthenticated_shouldThrowError() throws Exception {
        journeyRepository.save(JOURNEY_EXTENDED_ENTITY);

        mockMvc.perform(MockMvcRequestBuilders.delete(Resources.DELETE_JOURNEY, JOURNEY_EXTENDED_ENTITY.getId()))
                .andDo(print())
                .andExpect(status().isUnauthorized());
        verify(cloudinaryService, never()).deleteJourney(any());
        Optional<JourneyEntity> optionalJourney = journeyRepository.findById(JOURNEY_EXTENDED_ENTITY.getId());
        assertThat(optionalJourney).isNotEmpty();
    }

}

