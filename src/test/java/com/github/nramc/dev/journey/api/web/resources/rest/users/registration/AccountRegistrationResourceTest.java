package com.github.nramc.dev.journey.api.web.resources.rest.users.registration;

import com.github.nramc.dev.journey.api.config.security.WebSecurityConfig;
import com.github.nramc.dev.journey.api.config.security.InMemoryUserDetailsConfig;
import com.github.nramc.dev.journey.api.core.usecase.registration.AccountActivationUseCase;
import com.github.nramc.dev.journey.api.core.usecase.registration.RegistrationUseCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.stream.Stream;

import static com.github.nramc.dev.journey.api.web.resources.Resources.ACTIVATE_ACCOUNT;
import static com.github.nramc.dev.journey.api.web.resources.Resources.SIGNUP;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {AccountRegistrationResource.class})
@Import({InMemoryUserDetailsConfig.class, WebSecurityConfig.class})
@ActiveProfiles({"test"})
@MockBean({RegistrationUseCase.class, AccountActivationUseCase.class})
class AccountRegistrationResourceTest {
    private static final String SIGNUP_REQUEST_TEMPLATE = """
            {
             "username": "%s",
             "password": "%s",
             "name": "%s",
             "roles": ["AUTHENTICATED_USER"]
            }""";
    private static final String ACTIVATION_REQUEST_TEMPLATE = """
            {
             "username": "%s",
             "emailToken": "%s"
            }""";
    private static final String VALID_PASSWORD = "Validpasssword@001";
    private static final String VALID_NAME = "Valid Name";
    @Autowired
    private MockMvc mockMvc;

    @Test
    void context() {
        assertDoesNotThrow(() -> {
            assertThat(mockMvc).isNotNull();
        });
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

    @Test
    void activate_whenGivenDataValid_shouldActivateAccount() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post(ACTIVATE_ACCOUNT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ACTIVATION_REQUEST_TEMPLATE.formatted("marcel_plasenciazo9@clerk.dxa", "fd205bfc-cb66-4a4b-91ee-106756d1362a"))
                ).andDo(print())
                .andExpect(status().isOk());
    }

    @ParameterizedTest
    @MethodSource
    void activate_whenGivenDataNotValid_shouldThrowError(String username, String emailToken) throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post(ACTIVATE_ACCOUNT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ACTIVATION_REQUEST_TEMPLATE.formatted(username, emailToken))
                ).andDo(print())
                .andExpect(status().isBadRequest());
    }

    static Stream<Arguments> activate_whenGivenDataNotValid_shouldThrowError() {
        return Stream.of(
                Arguments.of("", ""),
                Arguments.of("ronesha_dilorenzo27i2@company.jy", "invalid"),
                Arguments.of("invalid", "a765d21b-afed-4aee-bc32-d5c3038ffe10")
        );
    }

}
