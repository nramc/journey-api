package com.github.nramc.dev.journey.api.web.resources.rest.users.security.email.code;

import com.github.nramc.dev.journey.api.config.ApplicationProperties;
import com.github.nramc.dev.journey.api.config.security.WebSecurityConfig;
import com.github.nramc.dev.journey.api.config.security.WebSecurityTestConfig;
import com.github.nramc.dev.journey.api.repository.auth.AuthUser;
import com.github.nramc.dev.journey.api.web.resources.rest.users.security.attributes.email.code.EmailCodeVerificationResource;
import com.github.nramc.dev.journey.api.web.resources.rest.users.security.confirmationcode.ConfirmationCode;
import com.github.nramc.dev.journey.api.web.resources.rest.users.security.confirmationcode.ConfirmationUseCase;
import com.github.nramc.dev.journey.api.web.resources.rest.users.security.attributes.email.EmailConfirmationCodeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static com.github.nramc.dev.journey.api.config.security.Role.Constants.GUEST_USER;
import static com.github.nramc.dev.journey.api.config.security.Role.Constants.MAINTAINER;
import static com.github.nramc.dev.journey.api.web.resources.rest.users.security.confirmationcode.ConfirmationUseCase.VERIFY_EMAIL_ADDRESS;
import static com.github.nramc.dev.journey.api.web.resources.Resources.VERIFY_EMAIL_CODE;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EmailCodeVerificationResource.class)
@Import({WebSecurityConfig.class, WebSecurityTestConfig.class})
@ActiveProfiles({"prod", "test"})
@EnableConfigurationProperties({ApplicationProperties.class})
class EmailCodeVerificationResourceTest {
    private static final String VERIFICATION_REQUEST_PAYLOAD = """
            { "code": "123456" }
            """;
    @Autowired
    private MockMvc mvc;
    @MockBean
    private EmailConfirmationCodeService emailConfirmationCodeService;

    @Test
    @WithMockUser(username = "auth-user", authorities = {MAINTAINER})
    void verifyEmailCode_whenVerificationSuccess_shouldUpdateStatus() throws Exception {
        when(emailConfirmationCodeService.verify(any(ConfirmationCode.class), any(AuthUser.class), any(ConfirmationUseCase.class)))
                .thenReturn(true);
        mvc.perform(MockMvcRequestBuilders.post(VERIFY_EMAIL_CODE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VERIFICATION_REQUEST_PAYLOAD)
                )
                .andDo(print())
                .andExpect(status().isOk());
        verify(emailConfirmationCodeService).verify(any(ConfirmationCode.class), any(AuthUser.class), eq(VERIFY_EMAIL_ADDRESS));
    }

    @Test
    @WithMockUser(username = "auth-user", authorities = {MAINTAINER})
    void verifyEmailCode_whenVerificationFailed_shouldThrowError() throws Exception {
        when(emailConfirmationCodeService.verify(any(ConfirmationCode.class), any(AuthUser.class), any(ConfirmationUseCase.class)))
                .thenReturn(false);
        mvc.perform(MockMvcRequestBuilders.post(VERIFY_EMAIL_CODE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VERIFICATION_REQUEST_PAYLOAD)
                )
                .andDo(print())
                .andExpect(status().isBadRequest());
        verify(emailConfirmationCodeService).verify(any(ConfirmationCode.class), any(AuthUser.class), eq(VERIFY_EMAIL_ADDRESS));
    }

    @Test
    @WithMockUser(username = "test-user", authorities = {MAINTAINER})
    void verifyEmailCode_whenEmailNotExists_shouldThrowError() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post(VERIFY_EMAIL_CODE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VERIFICATION_REQUEST_PAYLOAD)
                )
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "test-user", authorities = {GUEST_USER})
    void verifyEmailCode_whenUserDoesNotHaveAccess_shouldThrowError() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post(VERIFY_EMAIL_CODE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VERIFICATION_REQUEST_PAYLOAD)
                )
                .andDo(print())
                .andExpect(status().isForbidden());
        verifyNoInteractions(emailConfirmationCodeService);
    }

    @Test
    void verifyEmailCode_whenUserNotAuthenticated_shouldThrowError() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post(VERIFY_EMAIL_CODE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VERIFICATION_REQUEST_PAYLOAD))
                .andDo(print())
                .andExpect(status().isUnauthorized());
        verifyNoInteractions(emailConfirmationCodeService);
    }

}