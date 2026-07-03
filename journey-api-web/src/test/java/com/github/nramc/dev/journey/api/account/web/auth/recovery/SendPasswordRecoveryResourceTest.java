package com.github.nramc.dev.journey.api.account.web.auth.recovery;

import com.github.nramc.dev.journey.api.account.usecase.PasswordRecoveryUseCase;
import com.github.nramc.dev.journey.api.infrastructure.security.WebSecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

import static com.github.nramc.dev.journey.api.shared.web.Resources.SEND_ACCOUNT_RECOVERY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@WebMvcTest(SendPasswordRecoveryResource.class)
@Import({WebSecurityConfig.class})
@ActiveProfiles({"prod", "test"})
class SendPasswordRecoveryResourceTest {
    private static final String REQUEST_TEMPLATE = """
            {
             "username": "%s"
            }""";

    @MockitoBean
    private PasswordRecoveryUseCase passwordRecoveryUseCase;
    @Autowired
    MockMvcTester mockMvcTester;

    @Test
    void recover_whenUnauthenticated_shouldStillProcessRequest() {
        var requestBuilder = post(SEND_ACCOUNT_RECOVERY)
                .contentType(MediaType.APPLICATION_JSON)
                .content(REQUEST_TEMPLATE.formatted("john@example.com"));
        assertThat(mockMvcTester.perform(requestBuilder))
                .hasStatusOk();
        verify(passwordRecoveryUseCase).sendRecoveryEmail("john@example.com");
    }

    @Test
    void recover_whenUsernameBlank_shouldReturnBadRequest() {
        var requestBuilder = post(SEND_ACCOUNT_RECOVERY)
                .contentType(MediaType.APPLICATION_JSON)
                .content(REQUEST_TEMPLATE.formatted(""));
        assertThat(mockMvcTester.perform(requestBuilder))
                .hasStatus(HttpStatus.BAD_REQUEST);
    }

}
