package com.github.nramc.dev.journey.api.web.resources.rest.create;

import com.github.nramc.dev.journey.api.config.security.WebSecurityConfig;
import com.github.nramc.dev.journey.api.config.security.WebSecurityTestConfig;
import com.github.nramc.dev.journey.api.repository.journey.JourneyEntity;
import com.github.nramc.dev.journey.api.repository.journey.JourneyRepository;
import com.github.nramc.dev.journey.api.web.exceptions.NonTechnicalException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;

import static com.github.nramc.dev.journey.api.web.resources.Resources.CREATE_JOURNEY;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {CreateJourneyResource.class})
@Import({WebSecurityConfig.class, WebSecurityTestConfig.class})
@ActiveProfiles({"prod", "test"})
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
                "coordinates": [48.183160038296585, 11.53090747669896]
              },
              "journeyDate": "2024-03-27"
            }
            """;
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
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private JourneyRepository journeyRepository;

    @BeforeEach
    void setup() {
        when(journeyRepository.save(any(JourneyEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0, JourneyEntity.class).toBuilder()
                        .createdDate(LocalDate.of(2024, 3, 27))
                        .id(VALID_UUID)
                        .build());
    }

    @Test
    void testContext() {
        Assertions.assertNotNull(mockMvc);
    }

    @Test
    @WithMockUser(username = "test-user", password = "test-password", authorities = {"CREATE_JOURNEY"})
    void create_whenJourneyCreatedSuccessfully_shouldReturnCreatedResourceUrl() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post(CREATE_JOURNEY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_JSON_REQUEST))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().json(VALID_JSON_RESPONSE));
    }

    @Test
    void create_whenAuthenticationMissing_shouldThrowUnAuthenticatedError() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post(CREATE_JOURNEY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_JSON_REQUEST))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "test-user", password = "test-password", authorities = {"USER"})
    void create_whenAuthenticationExistsButDoesBNotHaveAuthority_shouldThrowError() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post(CREATE_JOURNEY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_JSON_REQUEST))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @ParameterizedTest
    @ValueSource(strings = {/* Field Validation failure for Mandatory field is blank */"""
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
            """,/* Deserialization error */"""
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
                "type": "Invalid Type",
                "coordinates": [100.0, 0.0]
              }
             }
            """
    })
    @WithMockUser(username = "test-user", password = "test-password", authorities = {"CREATE_JOURNEY"})
    void create_whenJourneyDataNotValid_thenShouldThrowError(String jsonContent) throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post(CREATE_JOURNEY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "test-user", password = "test-password", authorities = {"CREATE_JOURNEY"})
    void create_whenAnyNonTechnicalErrorOccurred_shouldThrowError() throws Exception {
        when(journeyRepository.save(any(JourneyEntity.class))).thenThrow(new NonTechnicalException("mocked"));
        mockMvc.perform(MockMvcRequestBuilders.post(CREATE_JOURNEY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_JSON_REQUEST))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());
    }

}
