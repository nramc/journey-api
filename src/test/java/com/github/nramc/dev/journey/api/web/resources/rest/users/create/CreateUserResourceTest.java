package com.github.nramc.dev.journey.api.web.resources.rest.users.create;

import com.github.nramc.dev.journey.api.repository.auth.AuthUser;
import com.github.nramc.dev.journey.api.repository.auth.UserRepository;
import com.github.nramc.dev.journey.api.security.Roles;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
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

import java.time.LocalDateTime;
import java.util.Set;

import static com.github.nramc.dev.journey.api.security.Roles.Constants.ADMINISTRATOR;
import static com.github.nramc.dev.journey.api.security.Roles.Constants.AUTHENTICATED_USER;
import static com.github.nramc.dev.journey.api.security.Roles.Constants.GUEST;
import static com.github.nramc.dev.journey.api.security.Roles.Constants.MAINTAINER;
import static com.github.nramc.dev.journey.api.web.resources.Resources.NEW_USER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles({"test"})
@AutoConfigureMockMvc
class CreateUserResourceTest {
    private static final String CREATE_USER_REQUEST_TEMPLATE = """
            {
             "username": "%s",
             "password": "%s",
             "name": "%s",
             "emailAddress": "%s"
            }""";
    private static final String USER_NAME = "valid-user-name";
    private static final String VALID_PASSWORD = "valid-passsword@001";
    private static final String VALID_NAME = "Valid Name";
    private static final String EMAIL_ADDRESS = "valid-email-address@gmail.com";
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
        Assertions.assertNotNull(mockMvc);
    }

    @Test
    void create_whenUserNotAuthenticated_shouldReturnError() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post(NEW_USER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(CREATE_USER_REQUEST_TEMPLATE.formatted(USER_NAME, VALID_PASSWORD, VALID_NAME, EMAIL_ADDRESS))
                ).andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "test-user", authorities = {GUEST, AUTHENTICATED_USER, MAINTAINER})
    void create_whenUserNotAuthorized_shouldReturnError() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post(NEW_USER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(CREATE_USER_REQUEST_TEMPLATE.formatted(USER_NAME, VALID_PASSWORD, VALID_NAME, EMAIL_ADDRESS))
                ).andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "test-user", authorities = {ADMINISTRATOR})
    void create_whenUserDetailsValidAndRequesterHasValidRole_shouldCreateUser() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post(NEW_USER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(CREATE_USER_REQUEST_TEMPLATE.formatted(USER_NAME, VALID_PASSWORD, VALID_NAME, EMAIL_ADDRESS))
                ).andDo(print())
                .andExpect(status().isCreated());
        AuthUser user = userRepository.findUserByUsername(USER_NAME);
        assertThat(user).isNotNull()
                .satisfies(u -> assertThat(u.getId()).isNotNull())
                .satisfies(u -> assertThat(u.getName()).isEqualTo(VALID_NAME))
                .satisfies(u -> assertThat(u.getPassword()).isEqualTo(VALID_PASSWORD))
                .satisfies(u -> assertThat(u.getCreatedDate()).isBeforeOrEqualTo(LocalDateTime.now()))
                .satisfies(u -> assertThat(u.getSecret()).isBlank())
                .satisfies(u -> assertThat(u.getEmailAddress()).isEqualTo(EMAIL_ADDRESS))
                .satisfies(u -> assertThat(u.getRoles()).isEqualTo(Set.of(Roles.AUTHENTICATED_USER.name())))
                .satisfies(u -> assertThat(u.isEnabled()).isFalse());
    }

    @ParameterizedTest
    @CsvSource({
            "invalid, Valid@Password, Valid Name, valid-email-address@gmail.com",
            "validUserName, invalid, Valid Name, valid-email-address@gmail.com",
            "validUserName, Valid@Password, e, valid-email-address@gmail.com",
            "validUserName, invalid, Valid Name, invalid",
    })
    @WithMockUser(username = "test-user", authorities = {ADMINISTRATOR})
    void create_whenUserDetailsNotValid_shouldThrowError(String userName, String password, String name, String emailAddress) throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post(NEW_USER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(CREATE_USER_REQUEST_TEMPLATE.formatted(userName, password, name, emailAddress))
                ).andDo(print())
                .andExpect(status().isBadRequest());
    }

}