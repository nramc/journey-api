package com.github.nramc.dev.journey.api.web.resources.rest.create;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
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
import java.time.format.DateTimeFormatter;

import static com.github.nramc.dev.journey.api.config.security.Authority.MAINTAINER;
import static com.github.nramc.dev.journey.api.config.security.Authority.USER;
import static com.github.nramc.dev.journey.api.web.resources.Resources.CREATE_JOURNEY;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles({"test"})
@AutoConfigureMockMvc
class CreateJourneyResourceTest {
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
    @Autowired
    private MockMvc mockMvc;
    @Container
    @ServiceConnection
    static MongoDBContainer mongoDBContainer = new MongoDBContainer(DockerImageName.parse("mongo:latest"))
            .withExposedPorts(27017);

    @Test
    void testContext() {
        Assertions.assertNotNull(mockMvc);
    }

    @Test
    @WithMockUser(username = "admin", authorities = {MAINTAINER})
    void create_whenJourneyCreatedSuccessfully_shouldReturnCreatedResourceUrl() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post(CREATE_JOURNEY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_JSON_REQUEST))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(is(notNullValue())))
                .andExpect(jsonPath("$.name").value("First Flight Experience"))
                .andExpect(jsonPath("$.title").value("One of the most beautiful experience ever in my life"))
                .andExpect(jsonPath("$.description").value("Travelled first time for work deputation to Germany, Munich city"))
                .andExpect(jsonPath("$.category").value("Travel"))
                .andExpect(jsonPath("$.city").value("Munich"))
                .andExpect(jsonPath("$.country").value("Germany"))
                .andExpect(jsonPath("$.tags").value(hasItems("Travel", "Germany", "Munich")))
                .andExpect(jsonPath("$.thumbnail").value("valid image id"))
                .andExpect(jsonPath("$.location.type").value("Point"))
                .andExpect(jsonPath("$.location.coordinates").value(hasItems(48.183160038296585, 11.53090747669896)))
                .andExpect(jsonPath("$.journeyDate").value("2024-03-27"))
                .andExpect(jsonPath("$.createdDate").value(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)));
    }

    @Test
    @WithAnonymousUser
    void create_whenAuthenticationMissing_shouldThrowUnAuthenticatedError() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post(CREATE_JOURNEY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_JSON_REQUEST))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "test-user", password = "test-password", authorities = {USER})
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
    @WithMockUser(username = "test-user", password = "test-password", authorities = {MAINTAINER})
    void create_whenJourneyDataNotValid_thenShouldThrowError(String jsonContent) throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post(CREATE_JOURNEY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

}
