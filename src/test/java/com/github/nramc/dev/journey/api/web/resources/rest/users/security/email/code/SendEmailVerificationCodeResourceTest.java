package com.github.nramc.dev.journey.api.web.resources.rest.users.security.email.code;

import com.github.nramc.dev.journey.api.config.ApplicationProperties;
import com.github.nramc.dev.journey.api.config.security.WebSecurityConfig;
import com.github.nramc.dev.journey.api.config.security.WebSecurityTestConfig;
import com.github.nramc.dev.journey.api.repository.auth.AuthUser;
import com.github.nramc.dev.journey.api.services.email.EmailConfirmationCodeService;
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

import static com.github.nramc.dev.journey.api.security.Role.Constants.GUEST_USER;
import static com.github.nramc.dev.journey.api.security.Role.Constants.MAINTAINER;
import static com.github.nramc.dev.journey.api.services.confirmationcode.ConfirmationUseCase.VERIFY_EMAIL_ADDRESS;
import static com.github.nramc.dev.journey.api.web.resources.Resources.SEND_EMAIL_CODE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SendEmailVerificationCodeResource.class)
@Import({WebSecurityConfig.class, WebSecurityTestConfig.class})
@ActiveProfiles({"prod", "test"})
@EnableConfigurationProperties({ApplicationProperties.class})
class SendEmailVerificationCodeResourceTest {
    @Autowired
    private MockMvc mvc;
    @MockBean
    private EmailConfirmationCodeService emailConfirmationCodeService;

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


}