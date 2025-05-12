package com.github.nramc.dev.journey.api.web.resources.rest.journeys.find;

import com.github.nramc.dev.journey.api.config.TestContainersConfiguration;
import com.github.nramc.dev.journey.api.config.security.WithMockAdministratorUser;
import com.github.nramc.dev.journey.api.config.security.WithMockAuthenticatedUser;
import com.github.nramc.dev.journey.api.repository.journey.JourneyEntity;
import com.github.nramc.dev.journey.api.repository.journey.JourneyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

import static com.github.nramc.dev.journey.api.core.journey.security.Visibility.MYSELF;
import static com.github.nramc.dev.journey.api.web.resources.Resources.FIND_JOURNEYS;
import static com.github.nramc.dev.journey.api.web.resources.Resources.FIND_UPCOMING_ANNIVERSARY;
import static com.github.nramc.dev.journey.api.web.resources.rest.journeys.JourneyData.JOURNEY_ENTITY;
import static org.hamcrest.Matchers.contains;
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
class FindJourneyByQueryResourceTest {
    private static final String VALID_UUID = "ecc76991-0137-4152-b3b2-efce70a37ed0";
    private static final JourneyEntity VALID_JOURNEY = JOURNEY_ENTITY.toBuilder()
            .id(VALID_UUID)
            .visibilities(Set.of(MYSELF))
            .createdBy(WithMockAuthenticatedUser.USER_DETAILS.username())
            .createdDate(LocalDate.now())
            .build();
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private JourneyRepository journeyRepository;

    @BeforeEach
    void setup() {
        journeyRepository.deleteAll();
    }

    @Test
    @WithMockAdministratorUser
    void find_whenJourneyExists_butLoggedInUserDoesNotHavePermission_thenShouldReturnEmptyResponse() throws Exception {
        // setup data
        IntStream.range(0, 10).forEach(index -> journeyRepository.save(
                        VALID_JOURNEY.toBuilder()
                                .id("ID_" + index)
                                .createdDate(LocalDate.now().plusDays(index))
                                .createdBy(WithMockAuthenticatedUser.USER_DETAILS.username())
                                .visibilities(Set.of(MYSELF))
                                .build()
                )
        );

        mockMvc.perform(MockMvcRequestBuilders.get(FIND_JOURNEYS)
                        .accept(MediaType.APPLICATION_JSON)
                        .param("sort", "id")
                        .param("order", "ASC")
                        .param("pageIndex", "0")
                        .param("pageSize", "5")
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                // assert paging
                .andExpect(jsonPath("$.pageNumber").value("0"))
                .andExpect(jsonPath("$.pageSize").value("5"))
                .andExpect(jsonPath("$.totalPages").value("0"))
                .andExpect(jsonPath("$.totalElements").value("0"));
    }

    @Test
    @WithMockAdministratorUser
    void find_whenJourneyExists_butLoggedInUserDoesHavePermissionDueToVisibility_thenShouldReturnEmptyResponse() throws Exception {
        // setup data
        IntStream.range(0, 10).forEach(index -> journeyRepository.save(
                        VALID_JOURNEY.toBuilder()
                                .id("ID_" + index)
                                .build()
                )
        );

        mockMvc.perform(MockMvcRequestBuilders.get(FIND_JOURNEYS)
                        .accept(MediaType.APPLICATION_JSON)
                        .param("sort", "id")
                        .param("order", "ASC")
                        .param("pageIndex", "0")
                        .param("pageSize", "5")
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                // assert paging
                .andExpect(jsonPath("$.pageNumber").value("0"))
                .andExpect(jsonPath("$.pageSize").value("5"))
                .andExpect(jsonPath("$.totalPages").value("0"))
                .andExpect(jsonPath("$.totalElements").value("0"));
    }

    @Test
    @WithMockAuthenticatedUser
    void find_whenPagingAndSortingFieldGiven_shouldReturnCorrespondingPageWithRequestedSorting_withSecondPageAndAscendingSort() throws Exception {
        // setup data
        IntStream.range(0, 10).forEach(index -> journeyRepository.save(VALID_JOURNEY.toBuilder().id("ID_" + index).build()));

        // Request result with page number 1 (second page) and order by id ascending
        mockMvc.perform(MockMvcRequestBuilders.get(FIND_JOURNEYS)
                        .accept(MediaType.APPLICATION_JSON)
                        .param("sort", "id")
                        .param("order", "ASC")
                        .param("pageIndex", "1")
                        .param("pageSize", "5")
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                // assert paging
                .andExpect(jsonPath("$.pageNumber").value("1"))
                .andExpect(jsonPath("$.pageSize").value("5"))
                .andExpect(jsonPath("$.totalPages").value("2"))
                .andExpect(jsonPath("$.totalElements").value("10"))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[*].id").value(hasItems("ID_5", "ID_6", "ID_7", "ID_8", "ID_9")))
                .andExpect(jsonPath("$.content[5]").doesNotExist());
    }

    @Test
    @WithMockAuthenticatedUser
    void find_whenSearchTextProvided_shouldReturnResultForSearchText() throws Exception {
        // setup data
        journeyRepository.save(VALID_JOURNEY.toBuilder().id("ID_00").build());
        journeyRepository.save(VALID_JOURNEY.toBuilder().id("ID_01").name("Name have search query 'Fantasy and Adventures' journeys").build());
        journeyRepository.save(VALID_JOURNEY.toBuilder().id("ID_03").description("Description have search query 'Fantasy and Adventures' journeys").build());
        journeyRepository.save(VALID_JOURNEY.toBuilder().id("ID_04").build());

        mockMvc.perform(MockMvcRequestBuilders.get(FIND_JOURNEYS)
                        .accept(MediaType.APPLICATION_JSON)
                        .param("sort", "id")
                        .param("order", "ASC")
                        .param("pageIndex", "0")
                        .param("pageSize", "5")
                        .param("q", "Fantasy and Adventures")
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                // assert paging
                .andExpect(jsonPath("$.pageNumber").value("0"))
                .andExpect(jsonPath("$.pageSize").value("5"))
                .andExpect(jsonPath("$.totalPages").value("1"))
                .andExpect(jsonPath("$.totalElements").value("2"))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[*].id").value(hasItems("ID_01", "ID_03")))
                .andExpect(jsonPath("$.content[5]").doesNotExist());
    }

    @Test
    @WithMockAuthenticatedUser
    void find_whenPublishedOnlyRequested_shouldReturnOnlyPublishedJourneys() throws Exception {
        // setup data
        IntStream.range(0, 10).forEach(index -> journeyRepository.save(
                        VALID_JOURNEY.toBuilder()
                                .id("ID_" + index)
                                .createdDate(LocalDate.now().plusDays(index))
                                .isPublished(index % 2 == 0)// only even indexed journeys published
                                .build()
                )
        );
        JourneyEntity entity = new JourneyEntity();
        entity.setIsPublished(true);

        mockMvc.perform(MockMvcRequestBuilders.get(FIND_JOURNEYS)
                        .accept(MediaType.APPLICATION_JSON)
                        .param("sort", "id")
                        .param("order", "ASC")
                        .param("pageIndex", "0")
                        .param("pageSize", "5")
                        .param("publishedOnly", "true")
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                // assert paging
                .andExpect(jsonPath("$.pageNumber").value("0"))
                .andExpect(jsonPath("$.pageSize").value("5"))
                .andExpect(jsonPath("$.totalPages").value("1"))
                .andExpect(jsonPath("$.totalElements").value("5"))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[*].id").value(hasItems("ID_0", "ID_2", "ID_4", "ID_6", "ID_8")))
                .andExpect(jsonPath("$.content[5]").doesNotExist());
    }

    @Test
    @WithMockAuthenticatedUser
    void find_whenPagingAndSortingFieldGiven_shouldReturnCorrespondingPageWithRequestedSorting_withSecondPageAndDescendingSort() throws Exception {
        // setup data
        IntStream.range(0, 10).forEach(index -> journeyRepository.save(
                VALID_JOURNEY.toBuilder().id("ID_" + index).createdDate(LocalDate.now().plusDays(index)).build()));

        // Request result with page number 1 (second page) and order by id ascending
        mockMvc.perform(MockMvcRequestBuilders.get(FIND_JOURNEYS)
                        .accept(MediaType.APPLICATION_JSON)
                        .param("sort", "id")
                        .param("order", "DESC")
                        .param("pageIndex", "1")
                        .param("pageSize", "5")
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                // assert paging
                .andExpect(jsonPath("$.pageNumber").value("1"))
                .andExpect(jsonPath("$.pageSize").value("5"))
                .andExpect(jsonPath("$.totalPages").value("2"))
                .andExpect(jsonPath("$.totalElements").value("10"))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[*].id").value(hasItems("ID_4", "ID_3", "ID_2", "ID_1", "ID_0")))
                .andExpect(jsonPath("$.content[5]").doesNotExist());
    }

    @Test
    @WithMockAuthenticatedUser
    void findAllAndReturnJson_whenPagingAndSortingParamsNotGiven_thenShouldConsiderDefaultValues() throws Exception {
        // setup data
        IntStream.range(0, 10).forEach(index -> journeyRepository.save(
                VALID_JOURNEY.toBuilder().id("ID_" + index).createdDate(LocalDate.now().plusDays(index)).build()));

        String[] expectedDates = IntStream.iterate(9, i -> i - 1).limit(10)
                .mapToObj(index -> LocalDate.now().plusDays(index).format(DateTimeFormatter.ISO_LOCAL_DATE))
                .toArray(String[]::new);

        // Request result with page number 1 (second page) and order by id ascending
        mockMvc.perform(MockMvcRequestBuilders.get(FIND_JOURNEYS)
                        .accept(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                // assert paging
                .andExpect(jsonPath("$.pageNumber").value("0"))
                .andExpect(jsonPath("$.pageSize").value("10"))
                .andExpect(jsonPath("$.totalPages").value("1"))
                .andExpect(jsonPath("$.totalElements").value("10"))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[*].createdDate").value(contains(expectedDates)))
                .andExpect(jsonPath("$.content[10]").doesNotExist());
    }

    @Test
    @WithMockAuthenticatedUser
    void find_whenTagsGiven_thenShouldFilterResultByGivenTags() throws Exception {
        // setup data
        IntStream.range(0, 10).forEach(index -> journeyRepository.save(
                VALID_JOURNEY.toBuilder().id("ID_" + index).tags(List.of("tag_" + index)).build()));

        // Request result with page number 1 (second page) and order by id ascending
        mockMvc.perform(MockMvcRequestBuilders.get(FIND_JOURNEYS)
                        .queryParam("tags", "tag")
                        .accept(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.pageNumber").value("0"))
                .andExpect(jsonPath("$.pageSize").value("10"))
                .andExpect(jsonPath("$.totalPages").value("0"))
                .andExpect(jsonPath("$.totalElements").value("0"));
    }

    @Test
    @WithMockAuthenticatedUser
    void find_whenMoreThanOneTagsGiven_thenShouldFilterResultByUnion() throws Exception {
        // setup data
        IntStream.range(0, 10).forEach(index -> journeyRepository.save(
                VALID_JOURNEY.toBuilder().id("ID_" + index).tags(List.of("tag_" + index)).build()));

        // Request result with page number 1 (second page) and order by id ascending
        mockMvc.perform(MockMvcRequestBuilders.get(FIND_JOURNEYS)
                        .queryParam("tags", "tag_0", "tag_1")
                        .accept(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.pageNumber").value("0"))
                .andExpect(jsonPath("$.pageSize").value("10"))
                .andExpect(jsonPath("$.totalPages").value("1"))
                .andExpect(jsonPath("$.totalElements").value("2"));
    }

    @Test
    @WithMockAuthenticatedUser
    void find_whenMoreThanOneTagsGivenAsCSV_thenShouldFilterResultByUnion() throws Exception {
        // setup data
        IntStream.range(0, 10).forEach(index -> journeyRepository.save(
                VALID_JOURNEY.toBuilder().id("ID_" + index).tags(List.of("tag_" + index)).build()));

        // Request result with page number 1 (second page) and order by id ascending
        mockMvc.perform(MockMvcRequestBuilders.get(FIND_JOURNEYS)
                        .queryParam("tags", "tag_0, tag_1")
                        .accept(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.pageNumber").value("0"))
                .andExpect(jsonPath("$.pageSize").value("10"))
                .andExpect(jsonPath("$.totalPages").value("1"))
                .andExpect(jsonPath("$.totalElements").value("2"));
    }

    @Test
    @WithAnonymousUser
    void find_whenNotAuthenticated_shouldThrowError() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(FIND_JOURNEYS)
                        .accept(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockAuthenticatedUser
    void find_whenCityGiven_thenShouldFilterResultByGivenValue() throws Exception {
        // setup data
        IntStream.range(0, 10).forEach(index -> journeyRepository.save(
                        VALID_JOURNEY.toBuilder().id("ID_" + index)
                                .geoDetails(JOURNEY_ENTITY.getGeoDetails().toBuilder().city("City_" + index).build())
                                .build()
                )
        );
        journeyRepository.findAll().forEach(System.out::println);

        // Request result with page number 1 (second page) and order by id ascending
        mockMvc.perform(MockMvcRequestBuilders.get(FIND_JOURNEYS)
                        .queryParam("city", "City_5")
                        .accept(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.pageNumber").value("0"))
                .andExpect(jsonPath("$.pageSize").value("10"))
                .andExpect(jsonPath("$.totalPages").value("1"))
                .andExpect(jsonPath("$.totalElements").value("1"));
    }

    @Test
    @WithMockAuthenticatedUser
    void find_whenCountryGiven_thenShouldFilterResultByGivenValue() throws Exception {
        // setup data
        IntStream.range(0, 10).forEach(index -> journeyRepository.save(
                VALID_JOURNEY.toBuilder().id("ID_" + index)
                        .geoDetails(JOURNEY_ENTITY.getGeoDetails().toBuilder()
                                .country("Country_" + index)
                                .build())
                        .build()));
        journeyRepository.findAll().forEach(System.out::println);

        // Request result with page number 1 (second page) and order by id ascending
        mockMvc.perform(MockMvcRequestBuilders.get(FIND_JOURNEYS)
                        .queryParam("country", "Country_5")
                        .accept(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.pageNumber").value("0"))
                .andExpect(jsonPath("$.pageSize").value("10"))
                .andExpect(jsonPath("$.totalPages").value("1"))
                .andExpect(jsonPath("$.totalElements").value("1"));
    }

    @Test
    @WithMockAuthenticatedUser
    void find_whenCategoryGiven_thenShouldFilterResultByGivenValue() throws Exception {
        // setup data
        IntStream.range(0, 10).forEach(index -> journeyRepository.save(
                VALID_JOURNEY.toBuilder().id("ID_" + index)
                        .geoDetails(JOURNEY_ENTITY.getGeoDetails().toBuilder()
                                .category("Category_" + index)
                                .build()
                        ).build())
        );
        journeyRepository.findAll().forEach(System.out::println);

        // Request result with page number 1 (second page) and order by id ascending
        mockMvc.perform(MockMvcRequestBuilders.get(FIND_JOURNEYS)
                        .queryParam("category", "Category_5")
                        .accept(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.pageNumber").value("0"))
                .andExpect(jsonPath("$.pageSize").value("10"))
                .andExpect(jsonPath("$.totalPages").value("1"))
                .andExpect(jsonPath("$.totalElements").value("1"));
    }

    @Test
    @WithMockAuthenticatedUser
    void find_whenYearGiven_thenShouldFilterResultByGivenValue() throws Exception {
        // setup data
        IntStream.range(0, 10).forEach(index -> journeyRepository.save(
                VALID_JOURNEY.toBuilder().id("ID_" + index).journeyDate(LocalDate.now().plusYears(index)).build()));
        journeyRepository.findAll().forEach(System.out::println);

        // Request result with page number 1 (second page) and order by id ascending
        mockMvc.perform(MockMvcRequestBuilders.get(FIND_JOURNEYS)
                        .queryParam("year", String.valueOf(LocalDate.now().getYear()))
                        .accept(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.pageNumber").value("0"))
                .andExpect(jsonPath("$.pageSize").value("10"))
                .andExpect(jsonPath("$.totalPages").value("1"))
                .andExpect(jsonPath("$.totalElements").value("1"));
    }

    @ParameterizedTest
    @ValueSource(ints = {5, 7, 10, 14})
    @WithMockAuthenticatedUser
    void getUpcomingAnniversaries_whenDaysGiven_andJourneysAvailable_shouldReturnJourneys(int days) throws Exception {
        IntStream.range(1, 20).forEach(index -> journeyRepository.save(
                        VALID_JOURNEY.toBuilder()
                                .id("ID_" + index)
                                .journeyDate(LocalDate.now().plusDays(index).minusYears(index))
                                .isPublished(true)
                                .build()
                )
        );
        journeyRepository.findAll().forEach(System.out::println);

        mockMvc.perform(MockMvcRequestBuilders.get(FIND_UPCOMING_ANNIVERSARY)
                        .queryParam("days", String.valueOf(days))
                        .accept(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[*]").value(hasSize(days)));
    }

    @Test
    @WithMockAuthenticatedUser
    void getUpcomingAnniversaries_whenDaysNotGiven_shouldConsiderDefaultValue_andReturnExistingJourney() throws Exception {
        IntStream.range(1, 10).forEach(index -> journeyRepository.save(
                        VALID_JOURNEY.toBuilder()
                                .id("ID_" + index)
                                .journeyDate(LocalDate.now().plusDays(index).minusYears(index))
                                .isPublished(true)
                                .build()
                )
        );
        journeyRepository.findAll().forEach(System.out::println);

        mockMvc.perform(MockMvcRequestBuilders.get(FIND_UPCOMING_ANNIVERSARY)
                        .queryParam("days", String.valueOf(7))
                        .accept(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[*]").value(hasSize(7)));
    }

}
