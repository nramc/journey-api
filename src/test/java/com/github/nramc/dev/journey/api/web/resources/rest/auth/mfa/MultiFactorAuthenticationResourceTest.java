package com.github.nramc.dev.journey.api.web.resources.rest.auth.mfa;

import com.github.nramc.dev.journey.api.repository.auth.AuthUser;
import com.github.nramc.dev.journey.api.repository.auth.UserSecurityAttributesRepository;
import com.github.nramc.dev.journey.api.web.resources.rest.auth.AuthUserDetailsService;
import com.github.nramc.dev.journey.api.web.resources.rest.users.security.confirmationcode.ConfirmationCodeVerifier;
import com.github.nramc.dev.journey.api.web.resources.rest.users.security.confirmationcode.EmailCode;
import com.github.nramc.dev.journey.api.web.resources.rest.users.security.utils.SecurityAttributesUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJson;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static com.github.nramc.dev.journey.api.models.core.SecurityAttributeType.EMAIL_ADDRESS;
import static com.github.nramc.dev.journey.api.web.resources.Resources.LOGIN_MFA;
import static com.github.nramc.dev.journey.api.web.resources.rest.users.UsersData.MFA_USER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.blankOrNullString;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles({"test"})
@AutoConfigureMockMvc
@AutoConfigureJson
class MultiFactorAuthenticationResourceTest {
    private static final EmailCode EMAIL_CODE = EmailCode.valueOf(123456);
    private static final String REQUEST_PAYLOAD = """
            {
             "type": "%s",
             "value": "%s"
            }""";
    @Autowired
    private MockMvc mockMvc;
    @Container
    @ServiceConnection
    static MongoDBContainer mongoDBContainer = new MongoDBContainer(DockerImageName.parse("mongo:latest"))
            .withExposedPorts(27017);
    @Autowired
    private AuthUserDetailsService userDetailsService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserSecurityAttributesRepository attributesRepository;
    @MockBean
    private ConfirmationCodeVerifier confirmationCodeVerifier;

    @BeforeEach
    void setup() {
        if (!userDetailsService.userExists(MFA_USER.getUsername())) {
            userDetailsService.createUser(
                    MFA_USER.toBuilder().password(passwordEncoder.encode(MFA_USER.getPassword())).build()
            );
        }
    }

    @Test
    void test() {
        assertDoesNotThrow(() -> {
            assertThat(mockMvc).isNotNull();
        });
    }

    @Test
    void mfa_whenConfirmationCodeValid_shouldProvideToken() throws Exception {
        AuthUser mfaUser = (AuthUser) userDetailsService.loadUserByUsername(MFA_USER.getUsername());
        attributesRepository.save(SecurityAttributesUtils.newEmailAttribute(mfaUser).toBuilder()
                .verified(true)
                .value("example@gmaiil.com")
                .build()
        );

        when(confirmationCodeVerifier.verify(eq(EMAIL_CODE), any(AuthUser.class))).thenReturn(true);

        mockMvc.perform(MockMvcRequestBuilders.post(LOGIN_MFA)
                        .with(httpBasic(MFA_USER.getUsername(), MFA_USER.getPassword()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(REQUEST_PAYLOAD.formatted(EMAIL_ADDRESS, "123456"))
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.token").value(not(blankOrNullString())))
                .andExpect(jsonPath("$.expiredAt").value(not(blankOrNullString())))
                .andExpect(jsonPath("$.name").value(MFA_USER.getName()))
                .andExpect(jsonPath("$.authorities").value(hasItems("AUTHENTICATED_USER")))
                .andExpect(jsonPath("$.additionalFactorRequired").value(false))
                .andExpect(jsonPath("$.securityAttributes").doesNotExist());
    }

    @Test
    void mfa_whenConfirmationCodeInvalid_shouldThrowError() throws Exception {
        AuthUser mfaUser = (AuthUser) userDetailsService.loadUserByUsername(MFA_USER.getUsername());
        attributesRepository.save(SecurityAttributesUtils.newEmailAttribute(mfaUser).toBuilder()
                .verified(true)
                .value("example@gmaiil.com")
                .build()
        );

        when(confirmationCodeVerifier.verify(eq(EMAIL_CODE), any(AuthUser.class))).thenReturn(false);

        mockMvc.perform(MockMvcRequestBuilders.post(LOGIN_MFA)
                        .with(httpBasic(MFA_USER.getUsername(), MFA_USER.getPassword()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(REQUEST_PAYLOAD.formatted(EMAIL_ADDRESS, "123456"))
                ).andDo(print())
                .andExpect(status().isUnprocessableEntity());
    }

}