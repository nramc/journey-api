package com.github.nramc.dev.journey.api.web.resources.rest.users.find;

import com.github.nramc.dev.journey.api.repository.auth.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static com.github.nramc.dev.journey.api.config.security.Role.Constants.ADMINISTRATOR;
import static com.github.nramc.dev.journey.api.config.security.Role.Constants.AUTHENTICATED_USER;
import static com.github.nramc.dev.journey.api.config.security.Role.Constants.GUEST_USER;
import static com.github.nramc.dev.journey.api.config.security.Role.Constants.MAINTAINER;
import static com.github.nramc.dev.journey.api.web.resources.Resources.FIND_MY_ACCOUNT;
import static com.github.nramc.dev.journey.api.web.resources.Resources.FIND_USERS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles({"test"})
@AutoConfigureMockMvc
class FindUsersResourceTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    UserRepository userRepository;
    @Container
    @ServiceConnection
    static MongoDBContainer mongoDBContainer = new MongoDBContainer(DockerImageName.parse("mongo:latest"))
            .withExposedPorts(27017);

    @Test
    void context() {
        assertDoesNotThrow(() -> {
            assertThat(mockMvc).isNotNull();
        });
    }

    @Test
    void find_whenUserNotAuthenticated_shouldThrowError() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post(FIND_USERS))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "test-user", authorities = {GUEST_USER, AUTHENTICATED_USER, MAINTAINER})
    void find_whenUserDoesNotHavePermission_shouldThrowError() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post(FIND_USERS))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "test-user", authorities = {ADMINISTRATOR})
    void find_whenUserHasPermission_shouldReturnUsersDetails() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(FIND_USERS))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].username").hasJsonPath())
                .andExpect(jsonPath("$[0].name").hasJsonPath())
                .andExpect(jsonPath("$[0].createdDate").hasJsonPath())
                .andExpect(jsonPath("$[0].passwordChangedAt").hasJsonPath())
                .andExpect(jsonPath("$[0].enabled").hasJsonPath())
                .andExpect(jsonPath("$[0].roles").hasJsonPath())
                .andExpect(jsonPath("$[0].password").doesNotHaveJsonPath())
                .andExpect(jsonPath("$[0].secret").doesNotHaveJsonPath())
                .andExpect(jsonPath("$[0].mfaEnabled").hasJsonPath());
    }

    @Test
    void findMyAccount_whenUserNotAuthenticated_shouldThrowError() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post(FIND_MY_ACCOUNT))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "test-user", authorities = {GUEST_USER})
    void findMyAccount_whenUserDoesNotHavePermission_shouldThrowError() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post(FIND_MY_ACCOUNT))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "test-user", authorities = {AUTHENTICATED_USER})
    void findMyAccount_whenUserHasPermission_shouldReturnUsersDetails() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(FIND_MY_ACCOUNT))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.username").hasJsonPath())
                .andExpect(jsonPath("$.name").hasJsonPath())
                .andExpect(jsonPath("$.createdDate").hasJsonPath())
                .andExpect(jsonPath("$.passwordChangedAt").hasJsonPath())
                .andExpect(jsonPath("$.enabled").hasJsonPath())
                .andExpect(jsonPath("$.roles").hasJsonPath())
                .andExpect(jsonPath("$.password").doesNotHaveJsonPath())
                .andExpect(jsonPath("$.secret").doesNotHaveJsonPath())
                .andExpect(jsonPath("$.mfaEnabled").hasJsonPath());
    }

}