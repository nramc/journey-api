package com.github.nramc.dev.journey.api.web.resources.rest.users.security.totp;

import com.github.nramc.dev.journey.api.repository.auth.AuthUser;
import com.github.nramc.dev.journey.api.security.totp.model.TotpCode;
import com.github.nramc.dev.journey.api.web.dto.user.security.UserSecurityAttribute;
import com.github.nramc.dev.journey.api.web.exceptions.BusinessException;
import org.hamcrest.core.Is;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.Optional;

import static com.github.nramc.dev.journey.api.security.Role.Constants.AUTHENTICATED_USER;
import static com.github.nramc.dev.journey.api.security.Role.Constants.GUEST_USER;
import static com.github.nramc.dev.journey.api.web.resources.Resources.MY_SECURITY_ATTRIBUTE_TOTP;
import static com.github.nramc.dev.journey.api.web.resources.Resources.MY_SECURITY_ATTRIBUTE_TOTP_DEACTIVATE;
import static com.github.nramc.dev.journey.api.web.resources.Resources.MY_SECURITY_ATTRIBUTE_TOTP_STATUS;
import static com.github.nramc.dev.journey.api.web.resources.Resources.MY_SECURITY_ATTRIBUTE_TOTP_VERIFY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles({"test"})
@AutoConfigureMockMvc
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
    private static final String DEACTIVATE_REQUEST_PAYLOAD = """
            { "code": "%s" }
            """;
    @Container
    @ServiceConnection
    static MongoDBContainer mongoDBContainer = new MongoDBContainer(DockerImageName.parse("mongo:latest"))
            .withExposedPorts(27017);
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private TotpService totpService;

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
    @WithMockUser(username = "test-user", authorities = {GUEST_USER})
    void generateSecret_whenUserNotAuthorized_thenShouldThrowError() throws Exception {
        mockMvc.perform(get(MY_SECURITY_ATTRIBUTE_TOTP))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "test-user", authorities = {AUTHENTICATED_USER})
    void generateSecret_whenUserAuthenticated_shouldBeSuccessful() throws Exception {
        when(totpService.newQRCodeData(any(AuthUser.class))).thenReturn(
                QRImageDetails.builder().data("image".getBytes()).secretKey(SECRET_KEY).build());
        mockMvc.perform(get(MY_SECURITY_ATTRIBUTE_TOTP))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.secretKey").exists())
                .andExpect(jsonPath("$.data").exists());
    }

    @Test
    @WithMockUser(username = "test-user", authorities = {AUTHENTICATED_USER})
    void activate_whenUserAuthenticated_shouldBeSuccessful() throws Exception {
        mockMvc.perform(post(MY_SECURITY_ATTRIBUTE_TOTP)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ACTIVATION_REQUEST_PAYLOAD.formatted(SECRET_KEY, TOTP_CODE))
                ).andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "test-user", authorities = {AUTHENTICATED_USER})
    void status_whenUserAuthenticated_shouldBeSuccessful() throws Exception {
        when(totpService.getTotpAttributeIfExists(any(AuthUser.class))).thenReturn(
                Optional.of(UserSecurityAttribute.builder().build())
        );
        mockMvc.perform(get(MY_SECURITY_ATTRIBUTE_TOTP_STATUS)).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.active").value(Is.is(true)));
    }

    @Test
    @WithMockUser(username = "test-user", authorities = {AUTHENTICATED_USER})
    void status_whenUserAuthenticatedDoesNotHaveActiveTotp_shouldReturnFalse() throws Exception {
        mockMvc.perform(get(MY_SECURITY_ATTRIBUTE_TOTP_STATUS)).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.active").value(Is.is(false)));
    }

    @Test
    @WithMockUser(username = "test-user", authorities = {AUTHENTICATED_USER})
    void verify_whenCodeValid_shouldReturnSuccess() throws Exception {
        when(totpService.verify(any(AuthUser.class), any(TotpCode.class))).thenReturn(true);
        mockMvc.perform(post(MY_SECURITY_ATTRIBUTE_TOTP_VERIFY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VERIFY_REQUEST_PAYLOAD.formatted("123456"))
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(Is.is(true)));
    }

    @Test
    @WithMockUser(username = "test-user", authorities = {AUTHENTICATED_USER})
    void verify_whenCodeInvalid_shouldReturnFailure() throws Exception {
        when(totpService.verify(any(AuthUser.class), any(TotpCode.class))).thenReturn(false);
        mockMvc.perform(post(MY_SECURITY_ATTRIBUTE_TOTP_VERIFY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VERIFY_REQUEST_PAYLOAD.formatted("123456"))
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(Is.is(false)));
    }

    @Test
    @WithMockUser(username = "test-user", authorities = {AUTHENTICATED_USER})
    void deactivate_whenCodeValid_shouldDeactivateTotp() throws Exception {
        mockMvc.perform(post(MY_SECURITY_ATTRIBUTE_TOTP_DEACTIVATE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(DEACTIVATE_REQUEST_PAYLOAD.formatted("123456"))
                ).andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "test-user", authorities = {AUTHENTICATED_USER})
    void deactivate_whenCodeInvalid_shouldThrowError() throws Exception {
        doThrow(new BusinessException("mocked", "totp.code.invalid")).when(totpService).deactivateTotp(any(AuthUser.class), any(TotpCode.class));
        mockMvc.perform(post(MY_SECURITY_ATTRIBUTE_TOTP_DEACTIVATE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(DEACTIVATE_REQUEST_PAYLOAD.formatted("123456"))
                ).andDo(print())
                .andExpect(status().isBadRequest());
    }

}