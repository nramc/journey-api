package com.github.nramc.dev.journey.api.web.resources.rest.auth.login;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.nramc.dev.journey.api.repository.auth.AuthUser;
import com.github.nramc.dev.journey.api.repository.auth.UserSecurityAttributesRepository;
import com.github.nramc.dev.journey.api.web.resources.rest.auth.AuthUserDetailsService;
import com.github.nramc.dev.journey.api.web.resources.rest.auth.dto.LoginResponse;
import com.github.nramc.dev.journey.api.web.resources.rest.users.security.utils.SecurityAttributesUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJson;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.HttpHeaders;
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
import static com.github.nramc.dev.journey.api.models.core.SecurityAttributeType.TOTP;
import static com.github.nramc.dev.journey.api.web.resources.Resources.FIND_JOURNEYS;
import static com.github.nramc.dev.journey.api.web.resources.Resources.LOGIN;
import static com.github.nramc.dev.journey.api.web.resources.rest.users.UsersData.MFA_USER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.blankOrNullString;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
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
class LoginResourceTest {
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
    private ObjectMapper objectMapper;
    @Autowired
    private UserSecurityAttributesRepository attributesRepository;

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
    void operationalUserShouldExistsInDatabase() {
        assertDoesNotThrow(() -> {
            AuthUser operationalUser = (AuthUser) userDetailsService.loadUserByUsername("admin");
            assertThat(operationalUser).isNotNull();
        });
    }

    @Test
    void login_whenUserAuthenticated_thenShouldGetJwtToken() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post(LOGIN)
                        .with(httpBasic("admin", "password"))
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.token").value(not(blankOrNullString())))
                .andExpect(jsonPath("$.expiredAt").value(not(blankOrNullString())))
                .andExpect(jsonPath("$.name").value("Administrator"))
                .andExpect(jsonPath("$.authorities").value(hasItems("MAINTAINER", "AUTHENTICATED_USER")))
                .andExpect(jsonPath("$.additionalFactorRequired").value(false))
                .andExpect(jsonPath("$.securityAttributes").doesNotExist());
    }

    @Test
    void login_whenUserHasActiveMfa_thenShouldAskMfa() throws Exception {
        AuthUser mfaUser = (AuthUser) userDetailsService.loadUserByUsername(MFA_USER.getUsername());
        attributesRepository.save(SecurityAttributesUtils.newEmailAttribute(mfaUser).toBuilder()
                .verified(true)
                .value("example@gmaiil.com")
                .build()
        );

        mockMvc.perform(MockMvcRequestBuilders.post(LOGIN)
                        .with(httpBasic(MFA_USER.getUsername(), MFA_USER.getPassword()))
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.token").doesNotExist())
                .andExpect(jsonPath("$.additionalFactorRequired").value(true))
                .andExpect(jsonPath("$.securityAttributes").value(hasItems(EMAIL_ADDRESS.name())));
    }

    @Test
    void login_whenUserHasActiveMfaAndHaveMultipleAttributes_thenShouldList() throws Exception {
        AuthUser mfaUser = (AuthUser) userDetailsService.loadUserByUsername(MFA_USER.getUsername());
        attributesRepository.save(SecurityAttributesUtils.newEmailAttribute(mfaUser).toBuilder()
                .verified(false)
                .value("example@gmaiil.com")
                .build()
        );
        attributesRepository.save(SecurityAttributesUtils.newTotpAttribute(mfaUser).toBuilder()
                .value("example-secret-key")
                .build()
        );

        mockMvc.perform(MockMvcRequestBuilders.post(LOGIN)
                        .with(httpBasic(MFA_USER.getUsername(), MFA_USER.getPassword()))
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.token").doesNotExist())
                .andExpect(jsonPath("$.additionalFactorRequired").value(true))
                .andExpect(jsonPath("$.securityAttributes").value(hasItems(EMAIL_ADDRESS.name(), TOTP.name())));
    }

    @Test
    void login_whenJwtValid_thenShouldAuthenticateSuccessfully() throws Exception {
        String response = mockMvc.perform(MockMvcRequestBuilders.post(LOGIN)
                        .with(httpBasic("admin", "password"))
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().string(not(blankOrNullString())))
                .andReturn().getResponse().getContentAsString();

        LoginResponse loginResponse = objectMapper.readValue(response, LoginResponse.class);
        mockMvc.perform(
                        MockMvcRequestBuilders.get(FIND_JOURNEYS)
                                .accept(MediaType.APPLICATION_JSON)
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + loginResponse.token())
                ).andDo(print())
                .andExpect(status().isOk());
    }

}