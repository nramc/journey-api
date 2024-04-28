package com.github.nramc.dev.journey.api.web.resources.rest.journeys.find;

import com.github.nramc.commons.geojson.domain.Point;
import com.github.nramc.commons.geojson.domain.Position;
import com.github.nramc.dev.journey.api.config.security.Authority;
import com.github.nramc.dev.journey.api.repository.journey.JourneyEntity;
import com.github.nramc.dev.journey.api.repository.journey.JourneyRepository;
import com.github.nramc.dev.journey.api.web.resources.Resources;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.data.domain.Example;
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
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

import static com.github.nramc.dev.journey.api.security.Visibility.MAINTAINER;
import static com.github.nramc.dev.journey.api.security.Visibility.MYSELF;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasItems;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles({"test"})
@AutoConfigureMockMvc
class FindJourneyByQueryResourceTest {
    private static final String VALID_UUID = "ecc76991-0137-4152-b3b2-efce70a37ed0";
    private static final JourneyEntity VALID_JOURNEY = JourneyEntity.builder()
            .id(VALID_UUID)
            .name("First Flight Experience")
            .title("One of the most beautiful experience ever in my life")
            .description("First travel for work deputation to Germany, Munich city")
            .category("Travel")
            .city("Munich")
            .country("Germany")
            .tags(List.of("Travel", "Germany", "Munich"))
            .thumbnail("valid image id")
            .location(Point.of(Position.of(48.183160038296585, 11.53090747669896)))
            .createdDate(LocalDate.of(2024, 3, 27))
            .journeyDate(LocalDate.of(2024, 3, 27))
            .createdBy("test-user")
            .visibilities(Set.of(MYSELF))
            .isPublished(false)
            .build();
    @Autowired
    private MockMvc mockMvc;
    @Container
    @ServiceConnection
    static MongoDBContainer mongoDBContainer = new MongoDBContainer(DockerImageName.parse("mongo:latest"))
            .withExposedPorts(27017);

    @Autowired
    private JourneyRepository journeyRepository;

    @BeforeEach
    void setup() {
        journeyRepository.deleteAll();
    }

    @Test
    @WithMockUser(username = "test-user", password = "test-password", authorities = {Authority.MAINTAINER})
    void find_whenJourneyExists_butLoggedInUserDoesNotHavePermission_thenShouldReturnEmptyResponse() throws Exception {
        // setup data
        IntStream.range(0, 10).forEach(index -> journeyRepository.save(
                        VALID_JOURNEY.toBuilder()
                                .id("ID_" + index)
                                .createdDate(LocalDate.now().plusDays(index))
                                .createdBy("admin-user")
                                .visibilities(Set.of(MYSELF))
                                .build()
                )
        );

        mockMvc.perform(MockMvcRequestBuilders.get(Resources.FIND_JOURNEYS)
                        .accept(MediaType.APPLICATION_JSON)
                        .param("sort", "id")
                        .param("order", "ASC")
                        .param("pageIndex", "0")
                        .param("pageSize", "5")
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                // assert paging
                .andExpect(jsonPath("$.pageable.pageNumber").value("0"))
                .andExpect(jsonPath("$.pageable.pageSize").value("5"))
                .andExpect(jsonPath("$.totalPages").value("0"))
                .andExpect(jsonPath("$.totalElements").value("0"));
    }

    @Test
    @WithMockUser(username = "test-user", password = "test-password", authorities = {Authority.MAINTAINER})
    void find_whenJourneyExists_butLoggedInUserDoesHavePermissionDueToVisibility_thenShouldReturnEmptyResponse() throws Exception {
        // setup data
        IntStream.range(0, 10).forEach(index -> journeyRepository.save(
                        VALID_JOURNEY.toBuilder()
                                .id("ID_" + index)
                                .createdDate(LocalDate.now().plusDays(index))
                                .createdBy("admin-user")
                                .visibilities(Set.of(MAINTAINER))
                                .build()
                )
        );

        mockMvc.perform(MockMvcRequestBuilders.get(Resources.FIND_JOURNEYS)
                        .accept(MediaType.APPLICATION_JSON)
                        .param("sort", "id")
                        .param("order", "ASC")
                        .param("pageIndex", "0")
                        .param("pageSize", "5")
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                // assert paging
                .andExpect(jsonPath("$.pageable.pageNumber").value("0"))
                .andExpect(jsonPath("$.pageable.pageSize").value("5"))
                .andExpect(jsonPath("$.totalPages").value("2"))
                .andExpect(jsonPath("$.totalElements").value("10"));
    }

    @Test
    @WithMockUser(username = "test-user", password = "test-password", authorities = {Authority.MAINTAINER})
    void find_whenPagingAndSortingFieldGiven_shouldReturnCorrespondingPageWithRequestedSorting_withSecondPageAndAscendingSort() throws Exception {
        // setup data
        IntStream.range(0, 10).forEach(index -> journeyRepository.save(
                VALID_JOURNEY.toBuilder().id("ID_" + index).createdDate(LocalDate.now().plusDays(index)).build()));

        // Request result with page number 1 (second page) and order by id ascending
        mockMvc.perform(MockMvcRequestBuilders.get(Resources.FIND_JOURNEYS)
                        .accept(MediaType.APPLICATION_JSON)
                        .param("sort", "id")
                        .param("order", "ASC")
                        .param("pageIndex", "1")
                        .param("pageSize", "5")
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                // assert paging
                .andExpect(jsonPath("$.pageable.pageNumber").value("1"))
                .andExpect(jsonPath("$.pageable.pageSize").value("5"))
                .andExpect(jsonPath("$.totalPages").value("2"))
                .andExpect(jsonPath("$.totalElements").value("10"))
                // assert sorting order
                .andExpect(jsonPath("$.sort.sorted").value("true"))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[*].id").value(hasItems("ID_5", "ID_6", "ID_7", "ID_8", "ID_9")))
                .andExpect(jsonPath("$.content[5]").doesNotExist());
    }

    @Test
    @WithMockUser(username = "test-user", authorities = {Authority.MAINTAINER})
    void find_whenSearchQueryProvided_shouldReturnSearchQuerySatisfiedJourneys() throws Exception {
        // setup data
        journeyRepository.save(VALID_JOURNEY.toBuilder().id("ID_00").build());
        journeyRepository.save(VALID_JOURNEY.toBuilder().id("ID_01").name("Name have search query 'first time' journeys").build());
        journeyRepository.save(VALID_JOURNEY.toBuilder().id("ID_02").title("Title have search query 'first time' journeys").build());
        journeyRepository.save(VALID_JOURNEY.toBuilder().id("ID_03").description("Description have search query 'first time' journeys").build());
        journeyRepository.save(VALID_JOURNEY.toBuilder().id("ID_04").build());

        JourneyEntity entity = new JourneyEntity();
        entity.setIsPublished(true);
        System.out.println(journeyRepository.findAll(Example.of(entity)));

        mockMvc.perform(MockMvcRequestBuilders.get(Resources.FIND_JOURNEYS)
                        .accept(MediaType.APPLICATION_JSON)
                        .param("sort", "id")
                        .param("order", "ASC")
                        .param("pageIndex", "0")
                        .param("pageSize", "5")
                        .param("q", "first time")
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                // assert paging
                .andExpect(jsonPath("$.pageable.pageNumber").value("0"))
                .andExpect(jsonPath("$.pageable.pageSize").value("5"))
                .andExpect(jsonPath("$.totalPages").value("1"))
                .andExpect(jsonPath("$.totalElements").value("3"))
                // assert sorting order
                .andExpect(jsonPath("$.sort.sorted").value("true"))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[*].id").value(hasItems("ID_01", "ID_02", "ID_03")))
                .andExpect(jsonPath("$.content[5]").doesNotExist());
    }

    @Test
    @WithMockUser(username = "test-user", authorities = {Authority.MAINTAINER})
    void find_whenPublishedOnlyRequested_shouldReturnOnlyPublishedJourneys() throws Exception {
        // setup data
        IntStream.range(0, 10).forEach(index -> journeyRepository.save(
                        VALID_JOURNEY.toBuilder()
                                .id("ID_" + index)
                                .createdDate(LocalDate.now().plusDays(index))
                                .isPublished(index % 2 == 0) // only even indexed journeys published
                                .build()
                )
        );
        JourneyEntity entity = new JourneyEntity();
        entity.setIsPublished(true);
        System.out.println(journeyRepository.findAll(Example.of(entity)));

        mockMvc.perform(MockMvcRequestBuilders.get(Resources.FIND_JOURNEYS)
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
                .andExpect(jsonPath("$.pageable.pageNumber").value("0"))
                .andExpect(jsonPath("$.pageable.pageSize").value("5"))
                .andExpect(jsonPath("$.totalPages").value("1"))
                .andExpect(jsonPath("$.totalElements").value("5"))
                // assert sorting order
                .andExpect(jsonPath("$.sort.sorted").value("true"))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[*].id").value(hasItems("ID_0", "ID_2", "ID_4", "ID_6", "ID_8")))
                .andExpect(jsonPath("$.content[5]").doesNotExist());
    }

    @Test
    @WithMockUser(username = "test-user", password = "test-password", authorities = {Authority.MAINTAINER})
    void find_whenPagingAndSortingFieldGiven_shouldReturnCorrespondingPageWithRequestedSorting_withSecondPageAndDescendingSort() throws Exception {
        // setup data
        IntStream.range(0, 10).forEach(index -> journeyRepository.save(
                VALID_JOURNEY.toBuilder().id("ID_" + index).createdDate(LocalDate.now().plusDays(index)).build()));

        // Request result with page number 1 (second page) and order by id ascending
        mockMvc.perform(MockMvcRequestBuilders.get(Resources.FIND_JOURNEYS)
                        .accept(MediaType.APPLICATION_JSON)
                        .param("sort", "id")
                        .param("order", "DESC")
                        .param("pageIndex", "1")
                        .param("pageSize", "5")
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                // assert paging
                .andExpect(jsonPath("$.pageable.pageNumber").value("1"))
                .andExpect(jsonPath("$.pageable.pageSize").value("5"))
                .andExpect(jsonPath("$.totalPages").value("2"))
                .andExpect(jsonPath("$.totalElements").value("10"))
                // assert sorting order
                .andExpect(jsonPath("$.sort.sorted").value("true"))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[*].id").value(hasItems("ID_4", "ID_3", "ID_2", "ID_1", "ID_0")))
                .andExpect(jsonPath("$.content[5]").doesNotExist());
    }

    @Test
    @WithMockUser(username = "test-user", password = "test-password", authorities = {Authority.MAINTAINER})
    void findAllAndReturnJson_whenPagingAndSortingParamsNotGiven_thenShouldConsiderDefaultValues() throws Exception {
        // setup data
        IntStream.range(0, 10).forEach(index -> journeyRepository.save(
                VALID_JOURNEY.toBuilder().id("ID_" + index).createdDate(LocalDate.now().plusDays(index)).build()));

        String[] expectedDates = IntStream.iterate(9, i -> i - 1).limit(10)
                .mapToObj(index -> LocalDate.now().plusDays(index).format(DateTimeFormatter.ISO_LOCAL_DATE))
                .toArray(String[]::new);

        // Request result with page number 1 (second page) and order by id ascending
        mockMvc.perform(MockMvcRequestBuilders.get(Resources.FIND_JOURNEYS)
                        .accept(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                // assert paging
                .andExpect(jsonPath("$.pageable.pageNumber").value("0"))
                .andExpect(jsonPath("$.pageable.pageSize").value("10"))
                .andExpect(jsonPath("$.totalPages").value("1"))
                .andExpect(jsonPath("$.totalElements").value("10"))
                // assert sorting order
                .andExpect(jsonPath("$.sort.sorted").value("true"))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[*].createdDate").value(contains(expectedDates)))
                .andExpect(jsonPath("$.content[10]").doesNotExist());
    }

    @Test
    @WithAnonymousUser
    void find_whenNotAuthenticated_shouldThrowError() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(Resources.FIND_JOURNEYS)
                        .accept(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "test-user", password = "test-password", authorities = {Authority.USER})
    void find_whenNotAuthorized_shouldThrowError() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(Resources.FIND_JOURNEYS)
                        .accept(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isForbidden());
    }

}