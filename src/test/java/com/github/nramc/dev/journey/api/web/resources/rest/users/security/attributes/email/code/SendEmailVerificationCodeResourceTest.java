package com.github.nramc.dev.journey.api.web.resources.rest.users.security.attributes.email.code;

import com.github.nramc.dev.journey.api.config.security.InMemoryUserDetailsConfig;
import com.github.nramc.dev.journey.api.config.security.WebSecurityConfig;
import com.github.nramc.dev.journey.api.config.security.WithMockAuthenticatedUser;
import com.github.nramc.dev.journey.api.config.security.WithMockGuestUser;
import com.github.nramc.dev.journey.api.core.usecase.codes.emailcode.EmailCodeUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static com.github.nramc.dev.journey.api.web.resources.Resources.SEND_EMAIL_CODE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SendEmailVerificationCodeResource.class)
@Import({WebSecurityConfig.class, InMemoryUserDetailsConfig.class})
@ActiveProfiles({"prod", "test"})
class SendEmailVerificationCodeResourceTest {
    @Autowired
    private MockMvc mvc;
    @MockBean
    private EmailCodeUseCase emailCodeUseCase;

    @Test
    @WithMockAuthenticatedUser
    void sendEmailCode_whenUserHasAccess_shouldSendEmailCode() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post(SEND_EMAIL_CODE)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
        verify(emailCodeUseCase).send(any());
    }

    @Test
    @WithMockGuestUser
    void sendEmailCode_whenUserDoesNotHaveAccess_shouldThrowError() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post(SEND_EMAIL_CODE)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isForbidden());
        verifyNoInteractions(emailCodeUseCase);
    }

    @Test
    @WithAnonymousUser
    void sendEmailCode_whenUserNotAuthenticated_shouldThrowError() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post(SEND_EMAIL_CODE))
                .andDo(print())
                .andExpect(status().isUnauthorized());
        verifyNoInteractions(emailCodeUseCase);
    }


}
