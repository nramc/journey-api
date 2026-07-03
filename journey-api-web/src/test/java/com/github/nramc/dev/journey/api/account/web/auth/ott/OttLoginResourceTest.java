package com.github.nramc.dev.journey.api.account.web.auth.ott;

import com.github.nramc.dev.journey.api.account.usecase.OttLoginUseCase;
import com.github.nramc.dev.journey.api.account.web.auth.dto.LoginResponse;
import com.github.nramc.dev.journey.api.infrastructure.security.WebSecurityConfig;
import com.github.nramc.dev.journey.api.shared.exceptions.BusinessException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

import static com.github.nramc.dev.journey.api.shared.web.Resources.LOGIN_OTT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@WebMvcTest(OttLoginResource.class)
@Import({WebSecurityConfig.class})
@ActiveProfiles({"prod", "test"})
class OttLoginResourceTest {
    private static final String REQUEST_TEMPLATE = """
            {
             "token": "%s"
            }""";

    @Autowired
    private MockMvcTester mockMvcTester;
    @MockitoBean
    private OttLoginUseCase ottLoginUseCase;

    @Test
    void login_whenTokenValid_shouldReturnJwt() {
        when(ottLoginUseCase.login("valid-token")).thenReturn(LoginResponse.builder().token("jwt-token").build());

        var requestBuilder = post(LOGIN_OTT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(REQUEST_TEMPLATE.formatted("valid-token"));
        assertThat(mockMvcTester.perform(requestBuilder))
                .hasStatusOk()
                .hasContentTypeCompatibleWith(MediaType.APPLICATION_JSON)
                .bodyJson()
                .convertTo(LoginResponse.class)
                .extracting(LoginResponse::token)
                .isEqualTo("jwt-token");
    }

    @Test
    void login_whenTokenInvalid_shouldReturnBadRequest() {
        when(ottLoginUseCase.login("invalid-token"))
                .thenThrow(new BusinessException("Token is invalid, expired or already used", "token.invalid.not.exists"));

        var requestBuilder = post(LOGIN_OTT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(REQUEST_TEMPLATE.formatted("invalid-token"));
        assertThat(mockMvcTester.perform(requestBuilder))
                .hasStatus(HttpStatus.BAD_REQUEST);
    }

    @Test
    void login_whenTokenBlank_shouldReturnBadRequest() {
        var requestBuilder = post(LOGIN_OTT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(REQUEST_TEMPLATE.formatted(""));
        assertThat(mockMvcTester.perform(requestBuilder))
                .hasStatus(HttpStatus.BAD_REQUEST);
    }

}

