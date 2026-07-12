package com.github.nramc.dev.journey.api.account.web.users.security.attributes.totp;

import com.github.nramc.dev.journey.api.account.codes.totp.QRImageDetails;
import com.github.nramc.dev.journey.api.account.codes.totp.TotpUseCase;
import com.github.nramc.dev.journey.api.account.repository.AuthUser;
import com.github.nramc.dev.journey.api.infrastructure.security.InMemoryUserDetailsConfig;
import com.github.nramc.dev.journey.api.infrastructure.security.RateLimitConfig;
import com.github.nramc.dev.journey.api.infrastructure.security.WebSecurityConfig;
import com.github.nramc.dev.journey.api.infrastructure.security.WithMockAuthenticatedUser;
import com.github.nramc.dev.journey.api.infrastructure.security.WithMockGuestUser;
import com.github.nramc.dev.journey.api.shared.domain.user.security.TotpCode;
import com.github.nramc.dev.journey.api.shared.domain.user.security.UserSecurityAttribute;
import com.github.nramc.dev.journey.api.shared.exceptions.BusinessException;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static com.github.nramc.dev.journey.api.shared.web.Resources.MY_SECURITY_ATTRIBUTE_TOTP;
import static com.github.nramc.dev.journey.api.shared.web.Resources.MY_SECURITY_ATTRIBUTE_TOTP_STATUS;
import static com.github.nramc.dev.journey.api.shared.web.Resources.MY_SECURITY_ATTRIBUTE_TOTP_VERIFY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TotpResource.class)
@Import({WebSecurityConfig.class, InMemoryUserDetailsConfig.class, RateLimitConfig.class})
@ActiveProfiles({"prod", "test"})
class TotpResourceTest {
    private static final String SECRET_KEY = "E6DCVTM46CLPRXOE6NNNXCPWAIR3L5QZss";
    private static final String TOTP_CODE = "123456";
    private static final String ACTIVATION_REQUEST_PAYLOAD = """
            {
            "secretKey": "%s",
            "code": "%s"
            }
            """;
    private static final String VERIFY_REQUEST_PAYLOAD = """
            { "code": "%s" }
            """;
    @Autowired
    MockMvc mockMvc;
    @MockitoBean
    TotpUseCase totpUseCase;

    @Test
    void context() {
        assertThat(mockMvc).isNotNull();
    }

    @Test
    void generateSecret_whenUserNotAuthenticated_thenShouldThrowError() throws Exception {
        mockMvc.perform(get(MY_SECURITY_ATTRIBUTE_TOTP))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockGuestUser
    void generateSecret_whenUserNotAuthorized_thenShouldThrowError() throws Exception {
        mockMvc.perform(get(MY_SECURITY_ATTRIBUTE_TOTP))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockAuthenticatedUser
    void generateSecret_whenUserAuthenticated_shouldBeSuccessful() throws Exception {
        when(totpUseCase.newQRCodeData(any())).thenReturn(
                QRImageDetails.builder().data("image".getBytes()).secretKey(SECRET_KEY).build());
        mockMvc.perform(get(MY_SECURITY_ATTRIBUTE_TOTP))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.secretKey").exists())
                .andExpect(jsonPath("$.data").exists());
    }

    @Test
    @WithMockAuthenticatedUser
    void activate_whenUserAuthenticated_shouldBeSuccessful() throws Exception {
        mockMvc.perform(post(MY_SECURITY_ATTRIBUTE_TOTP)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ACTIVATION_REQUEST_PAYLOAD.formatted(SECRET_KEY, TOTP_CODE))
                ).andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @WithMockAuthenticatedUser
    void status_whenUserAuthenticated_shouldBeSuccessful() throws Exception {
        when(totpUseCase.getTotpAttributeIfExists(any(AuthUser.class))).thenReturn(
                Optional.of(UserSecurityAttribute.builder().build())
        );
        mockMvc.perform(get(MY_SECURITY_ATTRIBUTE_TOTP_STATUS)).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.active").value(Matchers.is(true)));
    }

    @Test
    @WithMockAuthenticatedUser
    void status_whenUserAuthenticatedDoesNotHaveActiveTotp_shouldReturnFalse() throws Exception {
        mockMvc.perform(get(MY_SECURITY_ATTRIBUTE_TOTP_STATUS)).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.active").value(Matchers.is(false)));
    }

    @Test
    @WithMockGuestUser
    void status_whenUserGuest_shouldBePermitted() throws Exception {
        mockMvc.perform(get(MY_SECURITY_ATTRIBUTE_TOTP_STATUS)).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.active").value(Matchers.is(false)));
    }

    @Test
    @WithMockAuthenticatedUser
    void verify_whenCodeValid_shouldReturnSuccess() throws Exception {
        when(totpUseCase.verify(any(AuthUser.class), any(TotpCode.class))).thenReturn(true);
        mockMvc.perform(post(MY_SECURITY_ATTRIBUTE_TOTP_VERIFY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VERIFY_REQUEST_PAYLOAD.formatted("123456"))
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(Matchers.is(true)));
    }

    @Test
    @WithMockAuthenticatedUser
    void verify_whenCodeInvalid_shouldReturnFailure() throws Exception {
        when(totpUseCase.verify(any(AuthUser.class), any(TotpCode.class))).thenReturn(false);
        mockMvc.perform(post(MY_SECURITY_ATTRIBUTE_TOTP_VERIFY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VERIFY_REQUEST_PAYLOAD.formatted("123456"))
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(Matchers.is(false)));
    }

    @Test
    @WithMockAuthenticatedUser
    void deactivate_whenCodeValid_shouldDeactivateTotp() throws Exception {
        mockMvc.perform(delete(MY_SECURITY_ATTRIBUTE_TOTP)).andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @WithMockAuthenticatedUser
    void deactivate_whenCodeInvalid_shouldThrowError() throws Exception {
        doThrow(new BusinessException("mocked", "totp.code.invalid")).when(totpUseCase).deactivateTotp(any(AuthUser.class));
        mockMvc.perform(delete(MY_SECURITY_ATTRIBUTE_TOTP))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

}
