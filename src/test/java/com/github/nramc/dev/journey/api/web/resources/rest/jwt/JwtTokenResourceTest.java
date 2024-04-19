package com.github.nramc.dev.journey.api.web.resources.rest.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.nramc.dev.journey.api.repository.auth.AuthUser;
import com.github.nramc.dev.journey.api.repository.auth.UserRepository;
import com.github.nramc.dev.journey.api.web.resources.rest.auth.jwt.LoginResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJson;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static com.github.nramc.dev.journey.api.web.resources.Resources.LOGIN;
import static org.hamcrest.Matchers.blankOrNullString;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.not;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles({"test"})
@AutoConfigureMockMvc
@AutoConfigureJson
class JwtTokenResourceTest {
    @Autowired
    private MockMvc mockMvc;
    @Container
    @ServiceConnection
    static MongoDBContainer mongoDBContainer = new MongoDBContainer(DockerImageName.parse("mongo:latest"))
            .withExposedPorts(27017);
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void test() {
        Assertions.assertNotNull(mockMvc);
    }

    @Test
    void operationalUserShouldExistsInDatabase() {
        AuthUser operationalUser = userRepository.findUserByUsername("admin");
        System.out.println(operationalUser);
        Assertions.assertNotNull(operationalUser);
    }

    @Test
    void token_whenUserAuthenticated_thenShouldGetJwtToken() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post(LOGIN)
                        .with(httpBasic("admin", "password"))
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.token").value(not(blankOrNullString())))
                .andExpect(jsonPath("$.expiredAt").value(not(blankOrNullString())))
                .andExpect(jsonPath("$.name").value("Administrator"))
                .andExpect(jsonPath("$.authorities").value(hasItems("MAINTAINER", "USER")));

    }

    @Test
    void token_whenJwtValid_thenShouldAuthenticateSuccessfully() throws Exception {
        String response = mockMvc.perform(MockMvcRequestBuilders.post(LOGIN)
                        .with(httpBasic("admin", "password"))
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().string(not(blankOrNullString())))
                .andReturn().getResponse().getContentAsString();

        LoginResponse loginResponse = objectMapper.readValue(response, LoginResponse.class);
        mockMvc.perform(
                        MockMvcRequestBuilders.get("/rest/journeys")
                                .accept(MediaType.APPLICATION_JSON)
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + loginResponse.token())
                ).andDo(print())
                .andExpect(status().isOk());
    }

}