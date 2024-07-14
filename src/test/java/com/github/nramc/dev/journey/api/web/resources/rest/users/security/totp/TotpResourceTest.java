package com.github.nramc.dev.journey.api.web.resources.rest.users.security.totp;

import com.github.nramc.dev.journey.api.repository.auth.AuthUser;
import com.github.nramc.dev.journey.api.web.dto.user.security.UserSecurityAttribute;
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
import static com.github.nramc.dev.journey.api.web.resources.Resources.MY_SECURITY_ATTRIBUTE_TOTP_STATUS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
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
    void generateTotp_whenUserNotAuthenticated_thenShouldThrowError() throws Exception {
        mockMvc.perform(get(MY_SECURITY_ATTRIBUTE_TOTP))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "test-user", authorities = {GUEST_USER})
    void generateTotp_whenUserNotAuthorized_thenShouldThrowError() throws Exception {
        mockMvc.perform(get(MY_SECURITY_ATTRIBUTE_TOTP))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "test-user", authorities = {AUTHENTICATED_USER})
    void generateTotp_whenUserAuthenticated_shouldBeSuccessful() throws Exception {
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
    void activateTotp_whenUserAuthenticated_shouldBeSuccessful() throws Exception {
        mockMvc.perform(post(MY_SECURITY_ATTRIBUTE_TOTP)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ACTIVATION_REQUEST_PAYLOAD.formatted(SECRET_KEY, TOTP_CODE))
                ).andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "test-user", authorities = {AUTHENTICATED_USER})
    void totpStatus_whenUserAuthenticated_shouldBeSuccessful() throws Exception {
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
    void totpStatus_whenUserAuthenticatedDoesNotHaveActiveTotp_shouldReturnFalse() throws Exception {
        mockMvc.perform(get(MY_SECURITY_ATTRIBUTE_TOTP_STATUS)).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.active").value(Is.is(false)));
    }

}