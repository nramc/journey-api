package com.github.nramc.dev.journey.api.web.resources.rest.timeline;

import com.github.nramc.dev.journey.api.config.TestContainersConfiguration;
import com.github.nramc.dev.journey.api.core.journey.security.Visibility;
import com.github.nramc.dev.journey.api.repository.journey.JourneyExtendedEntity;
import com.github.nramc.dev.journey.api.repository.journey.JourneyImageDetailEntity;
import com.github.nramc.dev.journey.api.repository.journey.JourneyImagesDetailsEntity;
import com.github.nramc.dev.journey.api.repository.journey.JourneyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

import static com.github.nramc.dev.journey.api.core.domain.user.Role.Constants.MAINTAINER;
import static com.github.nramc.dev.journey.api.core.journey.security.Visibility.MYSELF;
import static com.github.nramc.dev.journey.api.web.resources.Resources.GET_TIMELINE_DATA;
import static com.github.nramc.dev.journey.api.web.resources.rest.journeys.JourneyData.JOURNEY_EXTENDED_ENTITY;
import static com.github.nramc.dev.journey.api.web.resources.rest.journeys.JourneyData.newImageDetailEntityWith;
import static com.github.nramc.dev.journey.api.web.resources.rest.timeline.tranformer.TimelineDataTransformer.DEFAULT_HEADING;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestContainersConfiguration.class)
@ActiveProfiles({"test"})
@AutoConfigureMockMvc
class TimelineResourceTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private JourneyRepository journeyRepository;

    @BeforeEach
    void setup() {
        journeyRepository.deleteAll();
    }

    @Test
    @WithAnonymousUser
    void getTimelineData_whenNotAuthenticated_shouldThrowError() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(GET_TIMELINE_DATA)
                        .accept(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "new-user", password = "test-password", authorities = {MAINTAINER})
    void getTimelineData_whenJourneyExists_butLoggedInUserDoesNotHavePermissions_thenShouldReturnEmptyResponse() throws Exception {
        // setup data
        IntStream.range(0, 10).forEach(index -> journeyRepository.save(
                        JOURNEY_EXTENDED_ENTITY.toBuilder()
                                .id("ID_" + index)
                                .visibilities(Set.of(MYSELF))
                                .isPublished(true)
                                .build()
                )
        );

        mockMvc.perform(MockMvcRequestBuilders.get(GET_TIMELINE_DATA)
                        .accept(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.heading").value(DEFAULT_HEADING))
                .andExpect(jsonPath("$.images").isEmpty());
    }

    @Test
    @WithMockUser(username = "new-user", password = "test-password", authorities = {MAINTAINER})
    void getTimelineData_whenJourneyExistsWithAnyOfVisibility_shouldReturnResult() throws Exception {
        // setup data
        IntStream.range(0, 2).forEach(index -> journeyRepository.save(
                        JOURNEY_EXTENDED_ENTITY.toBuilder()
                                .id("ID_" + index)
                                .visibilities(Set.of(MYSELF, Visibility.MAINTAINER))
                                .isPublished(true)
                                .build()
                )
        );

        mockMvc.perform(MockMvcRequestBuilders.get(GET_TIMELINE_DATA)
                        .accept(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.heading").value(DEFAULT_HEADING))
                .andExpect(jsonPath("$.images").exists())
                .andExpect(jsonPath("$.images").value(hasSize(2)))
                .andExpect(jsonPath("$.images[*].src").value(hasItems("src_1")))
                .andExpect(jsonPath("$.images[*].caption").value(hasItems("title 1")))
                .andExpect(jsonPath("$.images[0].args").isMap())
                .andExpect(jsonPath("$.images[1].args").isMap());
    }

    @Test
    @WithMockUser(username = "new-user", password = "test-password", authorities = {MAINTAINER})
    void getTimelineData_whenNotFavoriteImageExists_shouldTakeFirstNImages() throws Exception {
        // setup data
        List<JourneyImageDetailEntity> images = List.of(
                newImageDetailEntityWith("src_1", "asset 1", "title 1"),
                newImageDetailEntityWith("src_2", "asset 2", "title 2"),
                newImageDetailEntityWith("src_3", "asset 3", "title 3"),
                newImageDetailEntityWith("src_4", "asset 4", "title 4"),
                newImageDetailEntityWith("src_5", "asset 5", "title 5")
        );
        journeyRepository.save(
                JOURNEY_EXTENDED_ENTITY.toBuilder()
                        .id("ID_12345")
                        .visibilities(Set.of(MYSELF, Visibility.MAINTAINER))
                        .isPublished(true)
                        .extended(JourneyExtendedEntity.builder().imagesDetails(JourneyImagesDetailsEntity.builder().images(images).build()).build())
                        .build()
        );

        mockMvc.perform(MockMvcRequestBuilders.get(GET_TIMELINE_DATA)
                        .accept(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.heading").value(DEFAULT_HEADING))
                .andExpect(jsonPath("$.images").exists())
                .andExpect(jsonPath("$.images").value(hasSize(3)))
                .andExpect(jsonPath("$.images[*].src").value(hasItems("src_1", "src_2", "src_3")))
                .andExpect(jsonPath("$.images[*].caption").value(hasItems("title 1", "title 2", "title 3")))
                .andExpect(jsonPath("$.images[0].args").isMap())
                .andExpect(jsonPath("$.images[1].args").isMap());
    }

    @Test
    @WithMockUser(username = "test-user", password = "test-password", authorities = {MAINTAINER})
    void getTimelineData_withJourneyIDs_whenJourneyExistsWithAnyOfVisibility_shouldReturnResult() throws Exception {
        // setup data
        IntStream.range(0, 5).forEach(index -> journeyRepository.save(
                        JOURNEY_EXTENDED_ENTITY.toBuilder()
                                .id("ID_" + index)
                                .visibilities(Set.of(MYSELF))
                                .isPublished(true)
                                .build()
                )
        );

        mockMvc.perform(MockMvcRequestBuilders.get(GET_TIMELINE_DATA)
                        .queryParam("IDs", "ID_1", "ID_2")
                        .accept(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.heading").value("Journeys"))
                .andExpect(jsonPath("$.images").exists())
                .andExpect(jsonPath("$.images").value(hasSize(2)))
                .andExpect(jsonPath("$.images[*].src").value(hasItems("src_1")))
                .andExpect(jsonPath("$.images[*].caption").value(hasItems("title 1")))
                .andExpect(jsonPath("$.images[0].title").isEmpty())
                .andExpect(jsonPath("$.images[1].title").isEmpty())
                .andExpect(jsonPath("$.images[0].args").isMap())
                .andExpect(jsonPath("$.images[1].args").isMap());
    }

    @Test
    @WithMockUser(username = "test-user", password = "test-password", authorities = {MAINTAINER})
    void getTimelineData_withSingleJourneyID_whenJourneyExistsWithAnyOfVisibility_shouldReturnResult() throws Exception {
        // setup data
        IntStream.range(0, 5).forEach(index -> journeyRepository.save(
                        JOURNEY_EXTENDED_ENTITY.toBuilder()
                                .id("ID_" + index)
                                .visibilities(Set.of(MYSELF))
                                .isPublished(true)
                                .build()
                )
        );

        mockMvc.perform(MockMvcRequestBuilders.get(GET_TIMELINE_DATA)
                        .queryParam("IDs", "ID_1")
                        .accept(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.heading").value("First Flight Experience"))
                .andExpect(jsonPath("$.images").exists())
                .andExpect(jsonPath("$.images").value(hasSize(2)))
                .andExpect(jsonPath("$.images[*].src").value(hasItems("src_1", "src_2")))
                .andExpect(jsonPath("$.images[*].caption").value(hasItems("title 1", "title 2")))
                .andExpect(jsonPath("$.images[0].title").isEmpty())
                .andExpect(jsonPath("$.images[0].args").isMap());
    }

    @Test
    @WithMockUser(username = "test-user", password = "test-password", authorities = {MAINTAINER})
    @Disabled("refactor separately")
    void getTimelineData_withCities_whenJourneyExistsWithAnyOfVisibility_shouldReturnResult() throws Exception {
        // setup data
        IntStream.range(0, 5).forEach(index -> journeyRepository.save(
                        JOURNEY_EXTENDED_ENTITY.toBuilder()
                                .id("ID_" + index)
                                .visibilities(Set.of(MYSELF))
                                .isPublished(true)
                                .extended(JOURNEY_EXTENDED_ENTITY.getExtended().toBuilder()
                                        .geoDetails(JOURNEY_EXTENDED_ENTITY.getExtended().getGeoDetails().toBuilder()
                                                .city("City_" + index)
                                                .build())
                                        .build()
                                )
                                .build()
                )
        );

        mockMvc.perform(MockMvcRequestBuilders.get(GET_TIMELINE_DATA)
                        .queryParam("city", "City_1, City_2")
                        .accept(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.heading").value("City"))
                .andExpect(jsonPath("$.images").exists())
                .andExpect(jsonPath("$.images").value(hasSize(2)))
                .andExpect(jsonPath("$.images[*].src").value(hasItems("src_1")))
                .andExpect(jsonPath("$.images[*].caption").value(hasItems("title 1")))
                .andExpect(jsonPath("$.images[*].title").value(hasItems("City_1", "City_2")))
                .andExpect(jsonPath("$.images[0].args").isMap())
                .andExpect(jsonPath("$.images[1].args").isMap());
    }

    @Test
    @WithMockUser(username = "test-user", password = "test-password", authorities = {MAINTAINER})
    @Disabled("refactor separately")
    void getTimelineData_withSingleCity_whenJourneyExistsWithAnyOfVisibility_shouldReturnResult() throws Exception {
        // setup data
        IntStream.range(0, 5).forEach(index -> journeyRepository.save(
                        JOURNEY_EXTENDED_ENTITY.toBuilder()
                                .id("ID_" + index)
                                .visibilities(Set.of(MYSELF))
                                .isPublished(true)
                                .extended(JOURNEY_EXTENDED_ENTITY.getExtended().toBuilder()
                                        .geoDetails(JOURNEY_EXTENDED_ENTITY.getExtended().getGeoDetails().toBuilder()
                                                .city("City_" + index)
                                                .build())
                                        .build()
                                )
                                .build()
                )
        );

        mockMvc.perform(MockMvcRequestBuilders.get(GET_TIMELINE_DATA)
                        .queryParam("city", "City_1")
                        .accept(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.heading").value("City"))
                .andExpect(jsonPath("$.images").exists())
                .andExpect(jsonPath("$.images").value(hasSize(1)))
                .andExpect(jsonPath("$.images[*].src").value(hasItems("src_1")))
                .andExpect(jsonPath("$.images[*].caption").value(hasItems("title 1")))
                .andExpect(jsonPath("$.images[*].title").value(hasItems("City_1")))
                .andExpect(jsonPath("$.images[0].args").isMap());
    }

    @Test
    @WithMockUser(username = "test-user", password = "test-password", authorities = {MAINTAINER})
    @Disabled("refactor separately")
    void getTimelineData_withCountries_whenJourneyExistsWithAnyOfVisibility_shouldReturnResult() throws Exception {
        // setup data
        IntStream.range(0, 5).forEach(index -> journeyRepository.save(
                        JOURNEY_EXTENDED_ENTITY.toBuilder()
                                .id("ID_" + index)
                                .visibilities(Set.of(MYSELF))
                                .isPublished(true)

                                .extended(JOURNEY_EXTENDED_ENTITY.getExtended().toBuilder()
                                        .geoDetails(JOURNEY_EXTENDED_ENTITY.getExtended().getGeoDetails().toBuilder()
                                                .country("Country_" + index)
                                                .build())
                                        .build()
                                )
                                .build()
                )
        );

        mockMvc.perform(MockMvcRequestBuilders.get(GET_TIMELINE_DATA)
                        .queryParam("country", "Country_1, Country_2")
                        .accept(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.heading").value("Country"))
                .andExpect(jsonPath("$.images").exists())
                .andExpect(jsonPath("$.images").value(hasSize(2)))
                .andExpect(jsonPath("$.images[*].src").value(hasItems("src_1")))
                .andExpect(jsonPath("$.images[*].caption").value(hasItems("title 1")))
                .andExpect(jsonPath("$.images[*].title").value(hasItems("Country_1", "Country_1")))
                .andExpect(jsonPath("$.images[0].args").isMap())
                .andExpect(jsonPath("$.images[1].args").isMap());
    }

    @Test
    @WithMockUser(username = "test-user", password = "test-password", authorities = {MAINTAINER})
    @Disabled("refactor separately")
    void getTimelineData_withSingleCountry_whenJourneyExistsWithAnyOfVisibility_shouldReturnResult() throws Exception {
        // setup data
        IntStream.range(0, 5).forEach(index -> journeyRepository.save(
                        JOURNEY_EXTENDED_ENTITY.toBuilder()
                                .id("ID_" + index)
                                .visibilities(Set.of(MYSELF))
                                .isPublished(true)
                                .extended(JOURNEY_EXTENDED_ENTITY.getExtended().toBuilder()
                                        .geoDetails(JOURNEY_EXTENDED_ENTITY.getExtended().getGeoDetails().toBuilder()
                                                .country("Country_" + index)
                                                .build())
                                        .build()
                                )
                                .build()
                )
        );

        mockMvc.perform(MockMvcRequestBuilders.get(GET_TIMELINE_DATA)
                        .queryParam("country", "Country_1")
                        .accept(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.heading").value("Country"))
                .andExpect(jsonPath("$.images").exists())
                .andExpect(jsonPath("$.images").value(hasSize(1)))
                .andExpect(jsonPath("$.images[*].src").value(hasItems("src_1")))
                .andExpect(jsonPath("$.images[*].caption").value(hasItems("title 1")))
                .andExpect(jsonPath("$.images[*].title").value(hasItems("Country_1")))
                .andExpect(jsonPath("$.images[0].args").isMap());
    }

    @Test
    @WithMockUser(username = "test-user", password = "test-password", authorities = {MAINTAINER})
    @Disabled("refactor separately")
    void getTimelineData_withCategories_whenJourneyExistsWithAnyOfVisibility_shouldReturnResult() throws Exception {
        // setup data
        IntStream.range(0, 5).forEach(index -> journeyRepository.save(
                        JOURNEY_EXTENDED_ENTITY.toBuilder()
                                .id("ID_" + index)
                                .visibilities(Set.of(MYSELF))
                                .isPublished(true)
                                .extended(JOURNEY_EXTENDED_ENTITY.getExtended().toBuilder()
                                        .geoDetails(JOURNEY_EXTENDED_ENTITY.getExtended().getGeoDetails().toBuilder()
                                                .category("Category_" + index)
                                                .build())
                                        .build()
                                )
                                .build()
                )
        );

        mockMvc.perform(MockMvcRequestBuilders.get(GET_TIMELINE_DATA)
                        .queryParam("category", "Category_1, Category_2")
                        .accept(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.heading").value("Category"))
                .andExpect(jsonPath("$.images").exists())
                .andExpect(jsonPath("$.images").value(hasSize(2)))
                .andExpect(jsonPath("$.images[*].src").value(hasItems("src_1")))
                .andExpect(jsonPath("$.images[*].caption").value(hasItems("title 1")))
                .andExpect(jsonPath("$.images[*].title").value(hasItems("Category_1", "Category_2")))
                .andExpect(jsonPath("$.images[0].args").isMap())
                .andExpect(jsonPath("$.images[1].args").isMap());
    }

    @Test
    @WithMockUser(username = "test-user", password = "test-password", authorities = {MAINTAINER})
    void getTimelineData_withYears_whenJourneyExistsWithAnyOfVisibility_shouldReturnResult() throws Exception {
        // setup data
        IntStream.range(0, 5).forEach(index -> journeyRepository.save(
                        JOURNEY_EXTENDED_ENTITY.toBuilder()
                                .id("ID_" + index)
                                .visibilities(Set.of(MYSELF))
                                .isPublished(true)
                                .journeyDate(LocalDate.of(2024, 1, 25).plusYears(index))
                                .build()
                )
        );

        mockMvc.perform(MockMvcRequestBuilders.get(GET_TIMELINE_DATA)
                        .queryParam("year", "2024, 2025, 2026")
                        .accept(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.heading").value("2024 - 2026"))
                .andExpect(jsonPath("$.images").exists())
                .andExpect(jsonPath("$.images").value(hasSize(3)))
                .andExpect(jsonPath("$.images[*].src").value(hasItems("src_1")))
                .andExpect(jsonPath("$.images[*].caption").value(hasItems("title 1")))
                .andExpect(jsonPath("$.images[*].title").value(hasItems("2024", "2025", "2026")))
                .andExpect(jsonPath("$.images[0].args").isMap())
                .andExpect(jsonPath("$.images[1].args").isMap());
    }

    @Test
    @WithMockUser(username = "test-user", password = "test-password", authorities = {MAINTAINER})
    void getTimelineData_forToday_whenJourneyExistsWithAnyOfVisibility_shouldReturnResult() throws Exception {
        // setup data
        IntStream.range(0, 5).forEach(index -> journeyRepository.save(
                        JOURNEY_EXTENDED_ENTITY.toBuilder()
                                .id("ID_" + index)
                                .visibilities(Set.of(MYSELF))
                                .isPublished(true)
                                .journeyDate(LocalDate.now().minusYears(index))
                                .build()
                )
        );

        journeyRepository.findAll().forEach(System.out::println);

        mockMvc.perform(MockMvcRequestBuilders.get(GET_TIMELINE_DATA)
                        .queryParam("today", "true")
                        .accept(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.heading").value("Today in History"))
                .andExpect(jsonPath("$.images").exists())
                .andExpect(jsonPath("$.images").value(hasSize(5)))
                .andExpect(jsonPath("$.images[*].src").value(hasItems("src_1")))
                .andExpect(jsonPath("$.images[*].caption").value(hasItems("title 1")))
                .andExpect(jsonPath("$.images[0].args").isMap());
    }

    @ParameterizedTest
    @ValueSource(strings = {"3", "5", "10", "15", "25", "31"})
    @WithMockUser(username = "test-user", password = "test-password", authorities = {MAINTAINER})
    void getTimelineData_forUpcomingDays_whenJourneyExistsWithAnyOfVisibility_shouldReturnResult(String numberOfDays) throws Exception {
        // setup data
        IntStream.range(0, Integer.parseInt(numberOfDays + 1)).forEach(index -> journeyRepository.save(
                        JOURNEY_EXTENDED_ENTITY.toBuilder()
                                .id("ID_" + index)
                                .visibilities(Set.of(MYSELF))
                                .isPublished(true)
                                .journeyDate(LocalDate.now().plusDays(index).minusYears(index))
                                .build()
                )
        );

        mockMvc.perform(MockMvcRequestBuilders.get(GET_TIMELINE_DATA)
                        .queryParam("upcoming", numberOfDays)
                        .accept(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.heading").value("Upcoming Journiversaries"))
                .andExpect(jsonPath("$.images").exists())
                .andExpect(jsonPath("$.images").value(hasSize(Integer.parseInt(numberOfDays))))
                .andExpect(jsonPath("$.images[*].src").value(hasItems("src_1")))
                .andExpect(jsonPath("$.images[*].caption").value(hasItems("title 1")))
                .andExpect(jsonPath("$.images[0].args").isMap());
    }

}
