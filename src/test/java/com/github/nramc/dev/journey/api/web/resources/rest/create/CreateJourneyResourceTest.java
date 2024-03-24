package com.github.nramc.dev.journey.api.web.resources.rest.create;

import com.github.nramc.dev.journey.api.repository.journey.JourneyEntity;
import com.github.nramc.dev.journey.api.repository.journey.JourneyRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.junit.jupiter.Testcontainers;

import static com.github.nramc.dev.journey.api.web.resources.Resources.CREATE_JOURNEY;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {CreateJourneyResource.class})
@ActiveProfiles({"prod", "test"})
@Testcontainers
class CreateJourneyResourceTest {
    private static final String VALID_UUID = "ecc76991-0137-4152-b3b2-efce70a37ed0";
    private static final String VALID_JSON_REQUEST = """
            {
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
                "coordinates": [100.0, 0.0]
              }
            }
            """;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private JourneyRepository journeyRepository;

    @BeforeEach
    void setup() {
        Mockito.when(journeyRepository.save(Mockito.any(JourneyEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0, JourneyEntity.class).toBuilder().id(VALID_UUID).build());
    }

    @Test
    void testContext() {
        Assertions.assertNotNull(mockMvc);
    }

    @Test
    void create_whenJourneyCreatedSuccessfully_shouldReturnCreatedResourceUrl() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post(CREATE_JOURNEY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_JSON_REQUEST))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().string(""));
    }

    @ParameterizedTest
    @ValueSource(strings = {/* Mandatory name field is blank */"""
            {
              "name" : "",
              "title" : "One of the most beautiful experience ever in my life",
              "description" : "Travelled first time for work deputation to Germany, Munich city",
              "category" : "Travel",
              "city" : "Munich",
              "country" : "Germany",
              "tags" : ["Travel", "Germany", "Munich"],
              "thumbnail" : "valid image id",
              "location" : {
                "type": "Point",
                "coordinates": [100.0, 0.0]
              }
             }
            """
    })
    void create_whenJourneyDataNotValid_thenShouldThrowError(String jsonContent) throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post(CREATE_JOURNEY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

}
