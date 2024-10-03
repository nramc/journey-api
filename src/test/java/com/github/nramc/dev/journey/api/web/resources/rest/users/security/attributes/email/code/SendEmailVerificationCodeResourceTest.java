package com.github.nramc.dev.journey.api.web.resources.rest.users.security.attributes.email.code;

import com.github.nramc.dev.journey.api.config.security.WebSecurityConfig;
import com.github.nramc.dev.journey.api.config.security.WebSecurityTestConfig;
import com.github.nramc.dev.journey.api.core.usecase.codes.emailcode.EmailCodeUseCase;
import com.github.nramc.dev.journey.api.repository.user.AuthUser;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static com.github.nramc.dev.journey.api.core.domain.user.Role.Constants.GUEST_USER;
import static com.github.nramc.dev.journey.api.core.domain.user.Role.Constants.MAINTAINER;
import static com.github.nramc.dev.journey.api.web.resources.Resources.SEND_EMAIL_CODE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SendEmailVerificationCodeResource.class)
@Import({WebSecurityConfig.class, WebSecurityTestConfig.class})
@ActiveProfiles({"prod", "test"})
class SendEmailVerificationCodeResourceTest {
    @Autowired
    private MockMvc mvc;
    @MockBean
    private EmailCodeUseCase emailCodeUseCase;

    @Test
    @WithMockUser(username = "auth-user", authorities = {MAINTAINER})
    void sendEmailCode_whenUserHasAccess_shouldSendEmailCode() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post(SEND_EMAIL_CODE)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
        verify(emailCodeUseCase).send(any(AuthUser.class));
    }

    @Test
    @WithMockUser(username = "test-user", authorities = {GUEST_USER})
    void sendEmailCode_whenUserDoesNotHaveAccess_shouldThrowError() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post(SEND_EMAIL_CODE)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isForbidden());
        verifyNoInteractions(emailCodeUseCase);
    }

    @Test
    void sendEmailCode_whenUserNotAuthenticated_shouldThrowError() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post(SEND_EMAIL_CODE))
                .andDo(print())
                .andExpect(status().isUnauthorized());
        verifyNoInteractions(emailCodeUseCase);
    }


}
