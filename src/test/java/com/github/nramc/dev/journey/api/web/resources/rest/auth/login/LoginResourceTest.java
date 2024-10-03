package com.github.nramc.dev.journey.api.web.resources.rest.auth.login;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.nramc.dev.journey.api.config.TestConfig;
import com.github.nramc.dev.journey.api.core.jwt.JwtProperties;
import com.github.nramc.dev.journey.api.config.security.WebSecurityConfig;
import com.github.nramc.dev.journey.api.core.jwt.JwtGenerator;
import com.github.nramc.dev.journey.api.repository.auth.AuthUser;
import com.github.nramc.dev.journey.api.repository.auth.UserRepository;
import com.github.nramc.dev.journey.api.web.resources.rest.auth.dto.LoginResponse;
import com.github.nramc.dev.journey.api.web.resources.rest.users.find.FindUsersResource;
import com.github.nramc.dev.journey.api.web.resources.rest.users.security.attributes.UserSecurityAttributeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJson;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static com.github.nramc.dev.journey.api.config.TestConfig.ADMIN_USER;
import static com.github.nramc.dev.journey.api.config.TestConfig.TEST_USER;
import static com.github.nramc.dev.journey.api.core.domain.user.UserSecurityAttributeType.EMAIL_ADDRESS;
import static com.github.nramc.dev.journey.api.core.domain.user.UserSecurityAttributeType.TOTP;
import static com.github.nramc.dev.journey.api.web.resources.Resources.FIND_MY_ACCOUNT;
import static com.github.nramc.dev.journey.api.web.resources.Resources.LOGIN;
import static com.github.nramc.dev.journey.api.web.resources.rest.users.UsersData.EMAIL_ATTRIBUTE;
import static com.github.nramc.dev.journey.api.web.resources.rest.users.UsersData.TOTP_ATTRIBUTE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.blankOrNullString;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {LoginResource.class, FindUsersResource.class})
@Import({TestConfig.class, JwtGenerator.class, WebSecurityConfig.class})
@EnableConfigurationProperties({JwtProperties.class})
@ActiveProfiles({"test"})
@AutoConfigureJson
class LoginResourceTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private UserSecurityAttributeService attributeService;
    @MockBean
    private UserRepository userRepository;

    @Test
    void test() {
        assertDoesNotThrow(() -> {
            assertThat(mockMvc).isNotNull();
        });
    }

    @Test
    void login_whenUserAuthenticated_thenShouldGetJwtToken() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post(LOGIN)
                        .with(httpBasic(TEST_USER.getUsername(), TEST_USER.getPassword()))
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.token").value(not(blankOrNullString())))
                .andExpect(jsonPath("$.expiredAt").value(not(blankOrNullString())))
                .andExpect(jsonPath("$.name").value("USER"))
                .andExpect(jsonPath("$.authorities").value(hasItems("AUTHENTICATED_USER")))
                .andExpect(jsonPath("$.additionalFactorRequired").value(false))
                .andExpect(jsonPath("$.securityAttributes").doesNotExist());
    }

    @Test
    void login_whenUserHasActiveMfa_thenShouldAskMfa() throws Exception {
        when(attributeService.getAllAvailableUserSecurityAttributes(any(AuthUser.class)))
                .thenReturn(List.of(EMAIL_ATTRIBUTE));

        mockMvc.perform(MockMvcRequestBuilders.post(LOGIN)
                        .with(httpBasic(ADMIN_USER.getUsername(), ADMIN_USER.getPassword()))
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.token").doesNotExist())
                .andExpect(jsonPath("$.additionalFactorRequired").value(true))
                .andExpect(jsonPath("$.securityAttributes").value(hasItems(EMAIL_ADDRESS.name())));
    }

    @Test
    void login_whenUserHasActiveMfaAndHaveMultipleAttributes_thenShouldList() throws Exception {
        when(attributeService.getAllAvailableUserSecurityAttributes(any(AuthUser.class)))
                .thenReturn(List.of(EMAIL_ATTRIBUTE, TOTP_ATTRIBUTE));

        mockMvc.perform(MockMvcRequestBuilders.post(LOGIN)
                        .with(httpBasic(ADMIN_USER.getUsername(), ADMIN_USER.getPassword()))
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
                        .with(httpBasic(TEST_USER.getUsername(), TEST_USER.getPassword()))
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().string(not(blankOrNullString())))
                .andReturn().getResponse().getContentAsString();

        when(userRepository.findUserByUsername(TEST_USER.getUsername())).thenReturn(TEST_USER);
        LoginResponse loginResponse = objectMapper.readValue(response, LoginResponse.class);
        mockMvc.perform(get(FIND_MY_ACCOUNT)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + loginResponse.token())
                ).andDo(print())
                .andExpect(status().isOk());
    }

}
