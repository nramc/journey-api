package com.github.nramc.dev.journey.api.web.resources.rest.find;

import com.github.nramc.commons.geojson.domain.Point;
import com.github.nramc.commons.geojson.domain.Position;
import com.github.nramc.dev.journey.api.repository.journey.JourneyEntity;
import com.github.nramc.dev.journey.api.repository.journey.JourneyRepository;
import com.github.nramc.dev.journey.api.security.Visibility;
import com.github.nramc.dev.journey.api.web.resources.Resources;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static com.github.nramc.dev.journey.api.config.security.Authority.GUEST;
import static com.github.nramc.dev.journey.api.config.security.Authority.MAINTAINER;
import static com.github.nramc.dev.journey.api.config.security.Authority.USER;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles({"test"})
@AutoConfigureMockMvc
class FindJourneyByIdResourceTest {
    private static final String VALID_UUID = "ecc76991-0137-4152-b3b2-efce70a37ed0";
    private static final JourneyEntity VALID_JOURNEY = JourneyEntity.builder()
            .id(VALID_UUID)
            .name("First Flight Experience")
            .title("One of the most beautiful experience ever in my life")
            .description("Travelled first time for work deputation to Germany, Munich city")
            .category("Travel")
            .city("Munich")
            .country("Germany")
            .tags(List.of("Travel", "Germany", "Munich"))
            .thumbnail("valid image id")
            .location(Point.of(Position.of(48.183160038296585, 11.53090747669896)))
            .createdDate(LocalDate.of(2024, 3, 27))
            .journeyDate(LocalDate.of(2024, 3, 27))
            .visibilities(Set.of(Visibility.MAINTAINER))
            .build();
    @Autowired
    private MockMvc mockMvc;
    @Container
    @ServiceConnection
    static MongoDBContainer mongoDBContainer = new MongoDBContainer(DockerImageName.parse("mongo:latest"))
            .withExposedPorts(27017);

    @Autowired
    private JourneyRepository journeyRepository;

    @Test
    @WithMockUser(username = "test-user", password = "test-password", authorities = {MAINTAINER})
    void find_whenJourneyExists_ShouldReturnValidJson() throws Exception {
        journeyRepository.save(VALID_JOURNEY);

        mockMvc.perform(MockMvcRequestBuilders.get(Resources.FIND_JOURNEY, VALID_UUID)
                        .accept(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(VALID_UUID))
                .andExpect(jsonPath("$.name").value(VALID_JOURNEY.getName()))
                .andExpect(jsonPath("$.title").value(VALID_JOURNEY.getTitle()))
                .andExpect(jsonPath("$.description").value(VALID_JOURNEY.getDescription()))
                .andExpect(jsonPath("$.category").value(VALID_JOURNEY.getCategory()))
                .andExpect(jsonPath("$.city").value(VALID_JOURNEY.getCity()))
                .andExpect(jsonPath("$.country").value(VALID_JOURNEY.getCountry()))
                .andExpect(jsonPath("$.tags").value(Matchers.hasItems("Travel", "Germany", "Munich")))
                .andExpect(jsonPath("$.thumbnail").value(VALID_JOURNEY.getThumbnail()))
                .andExpect(jsonPath("$.location.type").value("Point"))
                .andExpect(jsonPath("$.location.coordinates").value(Matchers.hasItems(48.183160038296585, 11.53090747669896)))
                .andExpect(jsonPath("$.journeyDate").value("2024-03-27"))
                .andExpect(jsonPath("$.createdDate").value("2024-03-27"))
                .andExpect(jsonPath("$.isPublished").value(false))
                .andExpect(jsonPath("$.extendedDetails").value(VALID_JOURNEY.getExtended()))
        ;
    }

    @Test
    @WithMockUser(username = "test-user", password = "test-password", authorities = {GUEST})
    void find_whenJourneyExists_butDoesNotHavePermission_ShouldThrowError() throws Exception {
        journeyRepository.save(VALID_JOURNEY);

        mockMvc.perform(MockMvcRequestBuilders.get(Resources.FIND_JOURNEY, VALID_UUID)
                        .accept(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "test-user", password = "test-password", authorities = {MAINTAINER})
    void find_whenJourneyNotExists_shouldReturnError() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(Resources.FIND_JOURNEY, UUID.randomUUID().toString())
                        .accept(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "test-user", password = "test-password", authorities = {MAINTAINER})
    void find_whenIdNotValid_thenShouldThrowError() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(Resources.FIND_JOURNEY, " ")
                        .accept(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithAnonymousUser
    void find_whenNotAuthenticated_thenShouldThrowError() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(Resources.FIND_JOURNEY, " ")
                        .accept(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "test-user", password = "test-password", authorities = {USER})
    void find_whenNotAuthorized_thenShouldThrowError() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(Resources.FIND_JOURNEY, " ")
                        .accept(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isForbidden());
    }

}