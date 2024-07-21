package com.github.nramc.dev.journey.api.web.resources.rest.journeys.create;

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

import static com.github.nramc.dev.journey.api.config.security.Role.Constants.GUEST_USER;
import static com.github.nramc.dev.journey.api.config.security.Role.Constants.MAINTAINER;
import static com.github.nramc.dev.journey.api.web.resources.Resources.NEW_JOURNEY;
import static com.github.nramc.dev.journey.api.web.resources.rest.journeys.JourneyData.NEW_JOURNEY_JSON;
import static org.assertj.core.api.Assertions.assertThat;
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
    @Autowired
    private MockMvc mockMvc;
    @Container
    @ServiceConnection
    static MongoDBContainer mongoDBContainer = new MongoDBContainer(DockerImageName.parse("mongo:latest"))
            .withExposedPorts(27017);

    @Test
    void context() {
        assertThat(mockMvc).isNotNull();
    }

    @Test
    @WithMockUser(username = "admin", authorities = {MAINTAINER})
    void create_whenJourneyCreatedSuccessfully_shouldReturnCreatedResourceUrl() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post(NEW_JOURNEY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(NEW_JOURNEY_JSON))
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
                .andExpect(jsonPath("$.tags").value(hasItems("travel", "germany", "munich")))
                .andExpect(jsonPath("$.thumbnail").value("valid image id"))
                .andExpect(jsonPath("$.icon").value("home"))
                .andExpect(jsonPath("$.location.type").value("Point"))
                .andExpect(jsonPath("$.location.coordinates").value(hasItems(48.183160038296585, 11.53090747669896)))
                .andExpect(jsonPath("$.journeyDate").value("2024-03-27"))
                .andExpect(jsonPath("$.createdDate").value(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)));
    }

    @Test
    @WithAnonymousUser
    void create_whenAuthenticationMissing_shouldThrowUnAuthenticatedError() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post(NEW_JOURNEY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(NEW_JOURNEY_JSON))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "test-user", password = "test-password", authorities = {GUEST_USER})
    void create_whenAuthenticationExistsButDoesNotHaveAuthority_shouldThrowError() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post(NEW_JOURNEY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(NEW_JOURNEY_JSON))
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
              "tags" : ["travel", "germany", "munich"],
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
              "tags" : ["travel", "germany", "munich"],
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
        mockMvc.perform(MockMvcRequestBuilders.post(NEW_JOURNEY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

}
