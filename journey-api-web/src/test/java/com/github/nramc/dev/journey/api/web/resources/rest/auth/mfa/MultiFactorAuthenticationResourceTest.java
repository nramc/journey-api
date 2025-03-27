package com.github.nramc.dev.journey.api.web.resources.rest.auth.mfa;

import com.github.nramc.dev.journey.api.config.security.InMemoryUserDetailsConfig;
import com.github.nramc.dev.journey.api.config.security.WebSecurityConfig;
import com.github.nramc.dev.journey.api.core.jwt.JwtGenerator;
import com.github.nramc.dev.journey.api.core.jwt.JwtProperties;
import com.github.nramc.dev.journey.api.core.usecase.codes.ConfirmationCodeUseCase;
import com.github.nramc.dev.journey.api.core.usecase.codes.EmailCode;
import com.github.nramc.dev.journey.api.repository.user.AuthUser;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJson;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static com.github.nramc.dev.journey.api.core.domain.user.UserSecurityAttributeType.EMAIL_ADDRESS;
import static com.github.nramc.dev.journey.api.web.resources.Resources.LOGIN_MFA;
import static com.github.nramc.dev.journey.api.web.resources.rest.users.UsersData.AUTHENTICATED_USER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.blankOrNullString;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {MultiFactorAuthenticationResource.class})
@Import({InMemoryUserDetailsConfig.class, JwtGenerator.class, WebSecurityConfig.class})
@EnableConfigurationProperties({JwtProperties.class})
@ActiveProfiles({"test"})
@AutoConfigureJson
class MultiFactorAuthenticationResourceTest {
    private static final EmailCode EMAIL_CODE = EmailCode.valueOf(123456);
    private static final String REQUEST_PAYLOAD = """
            {
             "type": "%s",
             "value": "%s"
            }""";
    @Autowired
    MockMvc mockMvc;
    @MockitoBean
    ConfirmationCodeUseCase confirmationCodeUseCase;

    @Test
    void test() {
        assertDoesNotThrow(() -> {
            assertThat(mockMvc).isNotNull();
        });
    }

    @Test
    void mfa_whenConfirmationCodeValid_shouldProvideToken() throws Exception {
        when(confirmationCodeUseCase.verify(eq(EMAIL_CODE), any(AuthUser.class))).thenReturn(true);

        mockMvc.perform(MockMvcRequestBuilders.post(LOGIN_MFA)
                        .with(httpBasic(AUTHENTICATED_USER.getUsername(), AUTHENTICATED_USER.getPassword()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(REQUEST_PAYLOAD.formatted(EMAIL_ADDRESS, "123456"))
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.token").value(not(blankOrNullString())))
                .andExpect(jsonPath("$.expiredAt").value(not(blankOrNullString())))
                .andExpect(jsonPath("$.name").value(AUTHENTICATED_USER.getName()))
                .andExpect(jsonPath("$.authorities").value(hasItems("AUTHENTICATED_USER")))
                .andExpect(jsonPath("$.additionalFactorRequired").value(false))
                .andExpect(jsonPath("$.securityAttributes").doesNotExist());
    }

    @Test
    void mfa_whenConfirmationCodeInvalid_shouldThrowError() throws Exception {
        when(confirmationCodeUseCase.verify(eq(EMAIL_CODE), any(AuthUser.class))).thenReturn(false);

        mockMvc.perform(MockMvcRequestBuilders.post(LOGIN_MFA)
                        .with(httpBasic(AUTHENTICATED_USER.getUsername(), AUTHENTICATED_USER.getPassword()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(REQUEST_PAYLOAD.formatted(EMAIL_ADDRESS, "123456"))
                ).andDo(print())
                .andExpect(status().isUnprocessableEntity());
    }

}
