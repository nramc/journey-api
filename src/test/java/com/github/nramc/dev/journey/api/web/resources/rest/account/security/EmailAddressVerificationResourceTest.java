package com.github.nramc.dev.journey.api.web.resources.rest.account.security;

import com.github.nramc.dev.journey.api.config.ApplicationProperties;
import com.github.nramc.dev.journey.api.config.security.WebSecurityConfig;
import com.github.nramc.dev.journey.api.config.security.WebSecurityTestConfig;
import com.github.nramc.dev.journey.api.repository.auth.AuthUser;
import com.github.nramc.dev.journey.api.services.confirmationcode.ConfirmationCode;
import com.github.nramc.dev.journey.api.services.confirmationcode.ConfirmationUseCase;
import com.github.nramc.dev.journey.api.services.email.EmailConfirmationCodeService;
import com.mongodb.assertions.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static com.github.nramc.dev.journey.api.security.Role.Constants.GUEST_USER;
import static com.github.nramc.dev.journey.api.security.Role.Constants.MAINTAINER;
import static com.github.nramc.dev.journey.api.services.confirmationcode.ConfirmationUseCase.VERIFY_EMAIL_ADDRESS;
import static com.github.nramc.dev.journey.api.web.resources.Resources.SEND_EMAIL_CODE;
import static com.github.nramc.dev.journey.api.web.resources.Resources.VERIFY_EMAIL_CODE;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EmailAddressVerificationResource.class)
@Import({WebSecurityConfig.class, WebSecurityTestConfig.class})
@ActiveProfiles({"prod", "test"})
@EnableConfigurationProperties({ApplicationProperties.class})
class EmailAddressVerificationResourceTest {
    private static final String VERIFICATION_REQUEST_PAYLOAD = """
            { "code": "123456" }
            """;
    @Autowired
    private MockMvc mvc;
    @MockBean
    private EmailConfirmationCodeService emailConfirmationCodeService;
    @SpyBean
    private UserDetailsManager userDetailsManager;

    @Test
    @WithMockUser(username = "auth-user", authorities = {MAINTAINER})
    void sendEmailCode_whenUserHasAccess_shouldSendEmailCode() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post(SEND_EMAIL_CODE)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
        verify(emailConfirmationCodeService).send(any(AuthUser.class), eq(VERIFY_EMAIL_ADDRESS));
    }

    @Test
    @WithMockUser(username = "test-user", authorities = {MAINTAINER})
    void sendEmailCode_whenEmailNotExists_shouldThrowError() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post(SEND_EMAIL_CODE)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());
        verifyNoInteractions(emailConfirmationCodeService);
    }

    @Test
    @WithMockUser(username = "test-user", authorities = {GUEST_USER})
    void sendEmailCode_whenUserDoesNotHaveAccess_shouldThrowError() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post(SEND_EMAIL_CODE)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isForbidden());
        verifyNoInteractions(emailConfirmationCodeService);
    }

    @Test
    void sendEmailCode_whenUserNotAuthenticated_shouldThrowError() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post(SEND_EMAIL_CODE))
                .andDo(print())
                .andExpect(status().isUnauthorized());
        verifyNoInteractions(emailConfirmationCodeService);
    }

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
        verify(userDetailsManager).updateUser(argThat((AuthUser user) -> Assertions.assertTrue(user.isEmailAddressVerified())));
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
        verify(userDetailsManager, never()).updateUser(argThat((AuthUser user) -> Assertions.assertTrue(user.isEmailAddressVerified())));
    }

    @Test
    @WithMockUser(username = "test-user", authorities = {MAINTAINER})
    void verifyEmailCode_whenEmailNotExists_shouldThrowError() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post(VERIFY_EMAIL_CODE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VERIFICATION_REQUEST_PAYLOAD)
                )
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());
        verifyNoInteractions(emailConfirmationCodeService);
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