package com.github.nramc.dev.journey.api.web.resources.rest.users.security.attributes.email.code;

import com.github.nramc.dev.journey.api.config.security.InMemoryUserDetailsConfig;
import com.github.nramc.dev.journey.api.config.security.WebSecurityConfig;
import com.github.nramc.dev.journey.api.config.security.WithMockAuthenticatedUser;
import com.github.nramc.dev.journey.api.config.security.WithMockGuestUser;
import com.github.nramc.dev.journey.api.core.usecase.codes.ConfirmationCode;
import com.github.nramc.dev.journey.api.core.usecase.codes.emailcode.EmailCodeUseCase;
import com.github.nramc.dev.journey.api.repository.user.AuthUser;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static com.github.nramc.dev.journey.api.web.resources.Resources.VERIFY_EMAIL_CODE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EmailCodeVerificationResource.class)
@Import({WebSecurityConfig.class, InMemoryUserDetailsConfig.class})
@ActiveProfiles({"prod", "test"})
class EmailCodeVerificationResourceTest {
    private static final String VERIFICATION_REQUEST_PAYLOAD = """
            { "code": "123456" }
            """;
    @Autowired
    MockMvc mvc;
    @MockitoBean
    EmailCodeUseCase emailCodeUseCase;

    @Test
    @WithMockAuthenticatedUser
    void verifyEmailCode_whenVerificationSuccess_shouldUpdateStatus() throws Exception {
        when(emailCodeUseCase.verify(any(), any()))
                .thenReturn(true);
        mvc.perform(MockMvcRequestBuilders.post(VERIFY_EMAIL_CODE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VERIFICATION_REQUEST_PAYLOAD)
                )
                .andDo(print())
                .andExpect(status().isOk());
        verify(emailCodeUseCase).verify(any(), any());
    }

    @Test
    @WithMockAuthenticatedUser
    void verifyEmailCode_whenVerificationFailed_shouldThrowError() throws Exception {
        when(emailCodeUseCase.verify(any(ConfirmationCode.class), any(AuthUser.class)))
                .thenReturn(false);
        mvc.perform(MockMvcRequestBuilders.post(VERIFY_EMAIL_CODE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VERIFICATION_REQUEST_PAYLOAD)
                )
                .andDo(print())
                .andExpect(status().isBadRequest());
        verify(emailCodeUseCase).verify(any(ConfirmationCode.class), any(AuthUser.class));
    }

    @Test
    @WithMockAuthenticatedUser
    void verifyEmailCode_whenEmailNotExists_shouldThrowError() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post(VERIFY_EMAIL_CODE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VERIFICATION_REQUEST_PAYLOAD)
                )
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockGuestUser
    void verifyEmailCode_whenUserDoesNotHaveAccess_shouldThrowError() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post(VERIFY_EMAIL_CODE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VERIFICATION_REQUEST_PAYLOAD)
                )
                .andDo(print())
                .andExpect(status().isForbidden());
        verifyNoInteractions(emailCodeUseCase);
    }

    @Test
    void verifyEmailCode_whenUserNotAuthenticated_shouldThrowError() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post(VERIFY_EMAIL_CODE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VERIFICATION_REQUEST_PAYLOAD))
                .andDo(print())
                .andExpect(status().isUnauthorized());
        verifyNoInteractions(emailCodeUseCase);
    }

}
