package com.github.nramc.dev.journey.api.web.resources.rest.users.create;

import com.github.nramc.dev.journey.api.config.TestConfig;
import com.github.nramc.dev.journey.api.config.security.WebSecurityConfig;
import com.github.nramc.dev.journey.api.core.model.AppUser;
import com.github.nramc.dev.journey.api.core.usecase.registration.RegistrationUseCase;
import com.github.nramc.dev.journey.api.web.exceptions.BusinessException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.stream.Stream;

import static com.github.nramc.dev.journey.api.config.security.Role.Constants.ADMINISTRATOR;
import static com.github.nramc.dev.journey.api.config.security.Role.Constants.AUTHENTICATED_USER;
import static com.github.nramc.dev.journey.api.config.security.Role.Constants.GUEST_USER;
import static com.github.nramc.dev.journey.api.config.security.Role.Constants.MAINTAINER;
import static com.github.nramc.dev.journey.api.web.resources.Resources.NEW_USER;
import static com.github.nramc.dev.journey.api.web.resources.Resources.SIGNUP;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {CreateUserResource.class})
@Import({TestConfig.class, WebSecurityConfig.class})
@ActiveProfiles({"test"})
class CreateUserResourceTest {
    private static final String CREATE_USER_REQUEST_TEMPLATE = """
            {
             "username": "%s",
             "password": "%s",
             "name": "%s",
             "roles": ["AUTHENTICATED_USER"]
            }""";
    private static final String SIGNUP_REQUEST_TEMPLATE = """
            {
             "username": "%s",
             "password": "%s",
             "name": "%s",
             "roles": ["AUTHENTICATED_USER"]
            }""";
    private static final String VALID_PASSWORD = "Validpasssword@001";
    private static final String VALID_NAME = "Valid Name";
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private RegistrationUseCase registrationUseCase;

    @Test
    void context() {
        assertDoesNotThrow(() -> {
            assertThat(mockMvc).isNotNull();
        });
    }

    @Test
    void create_whenUserNotAuthenticated_shouldReturnError() throws Exception {
        String userName = "wissam_banach3dx1@communities.hd";
        mockMvc.perform(MockMvcRequestBuilders.post(NEW_USER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(CREATE_USER_REQUEST_TEMPLATE.formatted(userName, VALID_PASSWORD, VALID_NAME))
                ).andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "test-user", authorities = {GUEST_USER, AUTHENTICATED_USER, MAINTAINER})
    void create_whenUserNotAuthorized_shouldReturnError() throws Exception {
        String userName = "marion_plowmanq@simultaneously.xa";
        mockMvc.perform(MockMvcRequestBuilders.post(NEW_USER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(CREATE_USER_REQUEST_TEMPLATE.formatted(userName, VALID_PASSWORD, VALID_NAME))
                ).andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "test-user", authorities = {ADMINISTRATOR})
    void create_whenUserDetailsValidAndRequesterHasValidRole_shouldCreateUser() throws Exception {
        String userName = "marya_flinnyt@pierce.ddx";
        mockMvc.perform(MockMvcRequestBuilders.post(NEW_USER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(CREATE_USER_REQUEST_TEMPLATE.formatted(userName, VALID_PASSWORD, VALID_NAME))
                ).andDo(print())
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(username = "test-user", authorities = {ADMINISTRATOR})
    void create_whenBusinessValidationFailed_shouldThrowError() throws Exception {
        doThrow(new BusinessException("mocked error", "user.exists")).when(registrationUseCase).create(any(AppUser.class));
        String userName = "marya_flinnyt@pierce.ddx";
        mockMvc.perform(MockMvcRequestBuilders.post(NEW_USER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(CREATE_USER_REQUEST_TEMPLATE.formatted(userName, VALID_PASSWORD, VALID_NAME))
                ).andDo(print())
                .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @MethodSource("invalidUserDetails")
    @WithMockUser(username = "test-user", authorities = {ADMINISTRATOR})
    void create_whenUserDetailsNotValid_shouldThrowError(String userName, String password, String name) throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post(NEW_USER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(CREATE_USER_REQUEST_TEMPLATE.formatted(userName, password, name))
                ).andDo(print())
                .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @MethodSource("invalidUserDetails")
    void signup_whenUserDetailsNotValid_shouldThrowError(String userName, String password, String name) throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post(SIGNUP)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(SIGNUP_REQUEST_TEMPLATE.formatted(userName, password, name))
                ).andDo(print())
                .andExpect(status().isBadRequest());
    }

    static Stream<Arguments> invalidUserDetails() {
        // Arguments.of("<username>", "<password>", "<name>")
        return Stream.of(
                Arguments.of("", "valid@Password7", "Valid Name"),
                Arguments.of("invalid-email-address", "valid@Password7", "Valid Name"),

                Arguments.of("valid-email-address@example.com", "", "Valid Name"),
                Arguments.of("valid-email-address@example.com", "invalid@Password", "Valid Name"),

                Arguments.of("valid-email-address@example.com", "valid@Password7", "?"),
                Arguments.of("valid-email-address@example.com", "valid@Password7", ""),

                Arguments.of("", "", ""),
                Arguments.of("invalid-email", "invalid-password", "i")
        );
    }

    @Test
    void signup_whenUserWantToSignupWithValidDetails_thenShouldCreateUser() throws Exception {
        String userName = "lyndale_theisenoya@marble.rm";
        mockMvc.perform(MockMvcRequestBuilders.post(SIGNUP)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(SIGNUP_REQUEST_TEMPLATE.formatted(userName, VALID_PASSWORD, VALID_NAME))
                ).andDo(print())
                .andExpect(status().isCreated());
    }

}