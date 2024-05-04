package com.github.nramc.dev.journey.api.web.resources.rest.journeys.find;

import com.github.nramc.commons.geojson.domain.Point;
import com.github.nramc.commons.geojson.domain.Position;
import com.github.nramc.dev.journey.api.repository.journey.JourneyEntity;
import com.github.nramc.dev.journey.api.repository.journey.JourneyRepository;
import com.github.nramc.dev.journey.api.security.Visibility;
import com.github.nramc.dev.journey.api.web.resources.Resources;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
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

import static com.github.nramc.dev.journey.api.security.Role.Constants.GUEST_USER;
import static com.github.nramc.dev.journey.api.security.Role.Constants.MAINTAINER;
import static com.github.nramc.dev.journey.api.web.resources.Resources.MediaType.JOURNEYS_GEO_JSON;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles({"test"})
@AutoConfigureMockMvc
class FindPublishedJourneyResourceTest {
    private static final String VALID_UUID = "ecc76991-0137-4152-b3b2-efce70a37ed0";
    private static final JourneyEntity VALID_JOURNEY = JourneyEntity.builder()
            .id(VALID_UUID)
            .name("First Flight Experience")
            .title("One of the most beautiful experience ever in my life")
            .description("Travelled first time for work deputation to Germany, Munich city")
            .category("Travel")
            .city("Munich")
            .country("Germany")
            .tags(List.of("travel", "germany", "munich"))
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
    @WithMockUser(username = "test-user", authorities = {MAINTAINER})
    void find_whenNoPublishedJourneyExists_ShouldReturnEmptyCollection() throws Exception {
        journeyRepository.deleteAll();
        mockMvc.perform(MockMvcRequestBuilders.get(Resources.FIND_PUBLISHED_JOURNEYS)
                        .accept(JOURNEYS_GEO_JSON)
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(JOURNEYS_GEO_JSON))
                .andExpect(jsonPath("$.type").value("FeatureCollection"))
                .andExpect(jsonPath("$.features").isEmpty());
    }

    @Test
    @WithMockUser(username = "test-user", authorities = {GUEST_USER})
    void find_whenPublishedJourneyExists_butDoesNNotHavePermission_ShouldReturnEmptyCollection() throws Exception {
        journeyRepository.save(VALID_JOURNEY.toBuilder()
                .isPublished(true)
                .build());
        mockMvc.perform(MockMvcRequestBuilders.get(Resources.FIND_PUBLISHED_JOURNEYS)
                        .accept(JOURNEYS_GEO_JSON)
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(JOURNEYS_GEO_JSON))
                .andExpect(jsonPath("$.type").value("FeatureCollection"))
                .andExpect(jsonPath("$.features").isEmpty());
    }

    @Test
    @WithMockUser(username = "test-user", authorities = {MAINTAINER})
    void find_whenPublishedJourneyExists_ShouldReturnValidGeoJson() throws Exception {
        journeyRepository.save(VALID_JOURNEY.toBuilder()
                .isPublished(true)
                .build());

        mockMvc.perform(MockMvcRequestBuilders.get(Resources.FIND_PUBLISHED_JOURNEYS, VALID_UUID)
                        .accept(JOURNEYS_GEO_JSON)
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(JOURNEYS_GEO_JSON))
                .andExpect(jsonPath("$.type").value("FeatureCollection"))
                .andExpect(jsonPath("$.features").isNotEmpty())
                .andExpect(jsonPath("$.features[0].type").value("Feature"))
                .andExpect(jsonPath("$.features[0].id").value(VALID_JOURNEY.getId()))
                .andExpect(jsonPath("$.features[0].geometry").exists())
                .andExpect(jsonPath("$.features[0].properties").exists())
                .andExpect(jsonPath("$.features[0].properties.name").value(VALID_JOURNEY.getName()))
                .andExpect(jsonPath("$.features[0].properties.category").value(VALID_JOURNEY.getCategory()))
                .andExpect(jsonPath("$.features[0].properties.description").value(VALID_JOURNEY.getDescription()))
                .andExpect(jsonPath("$.features[0].properties.tags").value(equalTo(VALID_JOURNEY.getTags())));
    }
}