package com.github.nramc.dev.journey.api.web.resources.rest.users.delete;

import com.github.nramc.dev.journey.api.repository.auth.AuthUser;
import com.github.nramc.dev.journey.api.repository.auth.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static com.github.nramc.dev.journey.api.security.Role.Constants.ADMINISTRATOR;
import static com.github.nramc.dev.journey.api.security.Role.Constants.AUTHENTICATED_USER;
import static com.github.nramc.dev.journey.api.security.Role.Constants.GUEST_USER;
import static com.github.nramc.dev.journey.api.security.Role.Constants.MAINTAINER;
import static com.github.nramc.dev.journey.api.web.resources.Resources.DELETE_MY_ACCOUNT;
import static com.github.nramc.dev.journey.api.web.resources.Resources.DELETE_USER_BY_USERNAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles({"test"})
@AutoConfigureMockMvc
class DeleteUserResourceTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    UserRepository userRepository;
    @Container
    @ServiceConnection
    static MongoDBContainer mongoDBContainer = new MongoDBContainer(DockerImageName.parse("mongo:latest"))
            .withExposedPorts(27017);

    @Test
    void testContext() {
        assertThat(mockMvc).isNotNull();
    }

    @Test
    void find_whenUserNotAuthenticated_shouldThrowError() throws Exception {
        mockMvc.perform(delete(DELETE_USER_BY_USERNAME, "test-user"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "test-user", authorities = {GUEST_USER, AUTHENTICATED_USER, MAINTAINER})
    void find_whenUserDoesNotHavePermission_shouldThrowError() throws Exception {
        mockMvc.perform(delete(DELETE_USER_BY_USERNAME, "test-user"))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin-user", authorities = {ADMINISTRATOR})
    void deleteByUsername_whenUserHasPermission_thenShouldDeleteUser() throws Exception {
        mockMvc.perform(delete(DELETE_USER_BY_USERNAME, "admin-user"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void deleteMyAccount_whenUserNotAuthenticated_shouldThrowError() throws Exception {
        mockMvc.perform(delete(DELETE_MY_ACCOUNT))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "test-user", authorities = {GUEST_USER})
    void deleteMyAccount_whenUserDoesNotHavePermission_shouldThrowError() throws Exception {
        mockMvc.perform(delete(DELETE_MY_ACCOUNT))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", authorities = {ADMINISTRATOR})
    void deleteMyAccount_whenUserAdministrator_thenShouldDeleteUserAccount() throws Exception {
        mockMvc.perform(delete(DELETE_MY_ACCOUNT))
                .andDo(print())
                .andExpect(status().isOk());
        AuthUser user = userRepository.findUserByUsername("admin");
        assertThat(user.isEnabled()).isFalse();
    }


}