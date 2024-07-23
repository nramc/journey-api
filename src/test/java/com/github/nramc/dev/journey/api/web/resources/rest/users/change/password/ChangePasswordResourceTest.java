package com.github.nramc.dev.journey.api.web.resources.rest.users.change.password;

import com.github.nramc.dev.journey.api.repository.auth.AuthUser;
import com.github.nramc.dev.journey.api.repository.auth.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.temporal.ChronoUnit;

import static com.github.nramc.dev.journey.api.config.security.Role.Constants.ADMINISTRATOR;
import static com.github.nramc.dev.journey.api.config.security.Role.Constants.AUTHENTICATED_USER;
import static com.github.nramc.dev.journey.api.config.security.Role.Constants.GUEST_USER;
import static com.github.nramc.dev.journey.api.config.security.Role.Constants.MAINTAINER;
import static com.github.nramc.dev.journey.api.web.resources.Resources.CHANGE_MY_PASSWORD;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles({"test"})
@AutoConfigureMockMvc
class ChangePasswordResourceTest {
    private static final String REQUEST_TEMPLATE = """
            {
             "newPassword": "%s"
            }""";
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    UserRepository userRepository;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Container
    @ServiceConnection
    static MongoDBContainer mongoDBContainer = new MongoDBContainer(DockerImageName.parse("mongo:latest"))
            .withExposedPorts(27017);

    @Test
    void context() {
        assertThat(mockMvc).isNotNull();
    }

    @Test
    void find_whenUserNotAuthenticated_shouldThrowError() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post(CHANGE_MY_PASSWORD)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(REQUEST_TEMPLATE.formatted("valid-new-password"))
                ).andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "test-user", authorities = {GUEST_USER})
    void find_whenUserNotAuthorized_shouldThrowError() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post(CHANGE_MY_PASSWORD)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(REQUEST_TEMPLATE.formatted("valid-new-password"))
                ).andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", authorities = {MAINTAINER})
    void find_whenUserMaintainer_thenCanChangePassword() throws Exception {
        changePassword();
    }

    @Test
    @WithMockUser(username = "admin", authorities = {AUTHENTICATED_USER})
    void find_whenUserAuthenticatedUser_thenCanChangePassword() throws Exception {
        changePassword();
    }

    @Test
    @WithMockUser(username = "admin", authorities = {ADMINISTRATOR})
    void find_whenUserAdministrator_thenCanChangePassword() throws Exception {
        changePassword();
    }

    private void changePassword() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post(CHANGE_MY_PASSWORD)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(REQUEST_TEMPLATE.formatted("valid-new-password"))
                ).andDo(print())
                .andExpect(status().isOk());
        AuthUser user = userRepository.findUserByUsername("admin");
        assertThat(passwordEncoder.matches("valid-new-password", user.getPassword())).isTrue();
        assertThat(user.getPasswordChangedAt()).isNotNull().isCloseToUtcNow(within(1, ChronoUnit.MINUTES));
    }

}