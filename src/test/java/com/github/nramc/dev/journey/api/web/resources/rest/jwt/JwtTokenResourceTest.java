package com.github.nramc.dev.journey.api.web.resources.rest.jwt;

import com.github.nramc.dev.journey.api.repository.auth.AuthUser;
import com.github.nramc.dev.journey.api.repository.auth.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
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

import static org.hamcrest.Matchers.blankOrNullString;
import static org.hamcrest.Matchers.not;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles({"test"})
@AutoConfigureMockMvc
class JwtTokenResourceTest {
    @Autowired
    private MockMvc mockMvc;
    @Container
    @ServiceConnection
    static MongoDBContainer mongoDBContainer = new MongoDBContainer(DockerImageName.parse("mongo:latest"))
            .withExposedPorts(27017);
    @Autowired
    private UserRepository userRepository;

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
        mockMvc.perform(MockMvcRequestBuilders.post("/token")
                        .with(httpBasic("admin", "password"))
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN))
                .andExpect(content().string(not(blankOrNullString())));
    }

    @Test
    void token_whenJwtValid_thenShouldAuthenticateSuccessfully() throws Exception {
        String jwtToken = mockMvc.perform(MockMvcRequestBuilders.post("/token")
                        .with(httpBasic("admin", "password"))
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN))
                .andExpect(content().string(not(blankOrNullString())))
                .andReturn().getResponse().getContentAsString();

        mockMvc.perform(
                        MockMvcRequestBuilders.get("/rest/journeys")
                                .accept(MediaType.APPLICATION_JSON)
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken)
                ).andDo(print())
                .andExpect(status().isOk());
    }

}