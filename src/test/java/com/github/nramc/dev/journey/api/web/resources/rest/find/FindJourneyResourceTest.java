package com.github.nramc.dev.journey.api.web.resources.rest.find;

import com.github.nramc.commons.geojson.domain.Point;
import com.github.nramc.commons.geojson.domain.Position;
import com.github.nramc.dev.journey.api.repository.journey.JourneyEntity;
import com.github.nramc.dev.journey.api.repository.journey.JourneyRepository;
import com.github.nramc.dev.journey.api.web.resources.Resources;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
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
import java.util.stream.IntStream;

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
class FindJourneyResourceTest {
    private static final String VALID_UUID = "ecc76991-0137-4152-b3b2-efce70a37ed0";
    private static final String VALID_JSON_RESPONSE = """
            {
               "id": "ecc76991-0137-4152-b3b2-efce70a37ed0",
              "name" : "First Flight Experience",
              "title" : "One of the most beautiful experience ever in my life",
              "description" : "Travelled first time for work deputation to Germany, Munich city",
              "category" : "Travel",
              "city" : "Munich",
              "country" : "Germany",
              "tags" : ["Travel", "Germany", "Munich"],
              "thumbnail" : "valid image id",
              "location" : {
                "type": "Point",
                "coordinates": [48.183160038296585, 11.53090747669896]
              },
              "createdDate": "2024-03-27",
              "journeyDate": "2024-03-27"
            }
            """;
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
    void findAndReturnJson_whenJourneyExists_ShouldReturnValidJson() throws Exception {
        journeyRepository.save(VALID_JOURNEY);

        mockMvc.perform(MockMvcRequestBuilders.get(Resources.FIND_JOURNEY, VALID_UUID)
                        .accept(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(VALID_JSON_RESPONSE));
    }

    @Test
    void findAndReturnJson_whenJourneyNotExists_shouldReturnError() throws Exception {
        //Mockito.when(journeyRepository.findById(VALID_UUID)).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders.get(Resources.FIND_JOURNEY, VALID_UUID)
                        .accept(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isNotFound());

    }

    @Test
    void findAndReturnJson_whenIdNotValid_thenShouldThrowError() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(Resources.FIND_JOURNEY, " ")
                        .accept(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isBadRequest());

    }

    @Test
    void findAllAndReturnJson_whenPagingAndSortingFieldGiven_shouldReturnCorrespondingPageWithRequestedSorting_withSecondPageAndAscendingSort() throws Exception {
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
                .andExpect(jsonPath("$.content[0].id").value("ID_5"))
                .andExpect(jsonPath("$.content[1].id").value("ID_6"))
                .andExpect(jsonPath("$.content[2].id").value("ID_7"))
                .andExpect(jsonPath("$.content[3].id").value("ID_8"))
                .andExpect(jsonPath("$.content[4].id").value("ID_9"))
                .andExpect(jsonPath("$.content[5]").doesNotExist());
    }

    @Test
    void findAllAndReturnJson_whenPagingAndSortingFieldGiven_shouldReturnCorrespondingPageWithRequestedSorting_withSecondPageAndDescendingSort() throws Exception {
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
                .andExpect(jsonPath("$.content[0].id").value("ID_4"))
                .andExpect(jsonPath("$.content[1].id").value("ID_3"))
                .andExpect(jsonPath("$.content[2].id").value("ID_2"))
                .andExpect(jsonPath("$.content[3].id").value("ID_1"))
                .andExpect(jsonPath("$.content[4].id").value("ID_0"))
                .andExpect(jsonPath("$.content[5]").doesNotExist());
    }

    @Test
    void findAllAndReturnJson_whenPagingAndSortingParamsNotGiven_thenShouldConsiderDefaultValues() throws Exception {
        // setup data
        IntStream.range(0, 10).forEach(index -> journeyRepository.save(
                VALID_JOURNEY.toBuilder().id("ID_" + index).createdDate(LocalDate.now().plusDays(index)).build()));

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
                .andExpect(jsonPath("$.content[0].createdDate").value(LocalDate.now().plusDays(9).format(DateTimeFormatter.ISO_LOCAL_DATE)))
                .andExpect(jsonPath("$.content[1].createdDate").value(LocalDate.now().plusDays(8).format(DateTimeFormatter.ISO_LOCAL_DATE)))
                .andExpect(jsonPath("$.content[2].createdDate").value(LocalDate.now().plusDays(7).format(DateTimeFormatter.ISO_LOCAL_DATE)))
                .andExpect(jsonPath("$.content[3].createdDate").value(LocalDate.now().plusDays(6).format(DateTimeFormatter.ISO_LOCAL_DATE)))
                .andExpect(jsonPath("$.content[4].createdDate").value(LocalDate.now().plusDays(5).format(DateTimeFormatter.ISO_LOCAL_DATE)))
                .andExpect(jsonPath("$.content[5].createdDate").value(LocalDate.now().plusDays(4).format(DateTimeFormatter.ISO_LOCAL_DATE)))
                .andExpect(jsonPath("$.content[6].createdDate").value(LocalDate.now().plusDays(3).format(DateTimeFormatter.ISO_LOCAL_DATE)))
                .andExpect(jsonPath("$.content[7].createdDate").value(LocalDate.now().plusDays(2).format(DateTimeFormatter.ISO_LOCAL_DATE)))
                .andExpect(jsonPath("$.content[8].createdDate").value(LocalDate.now().plusDays(1).format(DateTimeFormatter.ISO_LOCAL_DATE)))
                .andExpect(jsonPath("$.content[9].createdDate").value(LocalDate.now().plusDays(0).format(DateTimeFormatter.ISO_LOCAL_DATE)))
                .andExpect(jsonPath("$.content[10]").doesNotExist());
    }

    @Test
    void findAllAndReturnGeoJson_whenNoPublishedJourneyExists_ShouldReturnEmptyCollection() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(Resources.FIND_JOURNEYS, VALID_UUID)
                        .accept(JOURNEYS_GEO_JSON)
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(JOURNEYS_GEO_JSON))
                .andExpect(jsonPath("$.type").value("FeatureCollection"))
                .andExpect(jsonPath("$.features").isEmpty());
    }

    @Test
    void findAllAndReturnGeoJson_whenPublishedJourneyExists_ShouldReturnValidGeoJson() throws Exception {
        journeyRepository.save(VALID_JOURNEY.toBuilder()
                .isPublished(true)
                .build());

        mockMvc.perform(MockMvcRequestBuilders.get(Resources.FIND_JOURNEYS, VALID_UUID)
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