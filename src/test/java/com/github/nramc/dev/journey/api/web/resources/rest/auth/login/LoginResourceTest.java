package com.github.nramc.dev.journey.api.web.resources.rest.auth.login;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.nramc.dev.journey.api.config.security.WebSecurityConfig;
import com.github.nramc.dev.journey.api.config.security.InMemoryUserDetailsConfig;
import com.github.nramc.dev.journey.api.config.security.WithMockAuthenticatedUser;
import com.github.nramc.dev.journey.api.core.jwt.JwtGenerator;
import com.github.nramc.dev.journey.api.core.jwt.JwtProperties;
import com.github.nramc.dev.journey.api.repository.user.AuthUser;
import com.github.nramc.dev.journey.api.repository.user.attributes.UserSecurityAttributeService;
import com.github.nramc.dev.journey.api.web.resources.rest.auth.dto.LoginResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJson;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static com.github.nramc.dev.journey.api.core.domain.user.UserSecurityAttributeType.EMAIL_ADDRESS;
import static com.github.nramc.dev.journey.api.core.domain.user.UserSecurityAttributeType.TOTP;
import static com.github.nramc.dev.journey.api.web.resources.Resources.LOGIN;
import static com.github.nramc.dev.journey.api.web.resources.rest.users.UsersData.TOTP_ATTRIBUTE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.blankOrNullString;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {LoginResource.class})
@Import({InMemoryUserDetailsConfig.class, JwtGenerator.class, WebSecurityConfig.class})
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

    @Test
    void test() {
        assertDoesNotThrow(() -> {
            assertThat(mockMvc).isNotNull();
        });
    }

    @Test
    void login_whenUserAuthenticated_thenShouldGetJwtToken() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post(LOGIN)
                        .with(httpBasic(WithMockAuthenticatedUser.USERNAME, WithMockAuthenticatedUser.PASSWORD))
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.token").value(not(blankOrNullString())))
                .andExpect(jsonPath("$.expiredAt").value(not(blankOrNullString())))
                .andExpect(jsonPath("$.name").value(WithMockAuthenticatedUser.USER_DETAILS.name()))
                .andExpect(jsonPath("$.authorities").value(hasItems("AUTHENTICATED_USER")))
                .andExpect(jsonPath("$.additionalFactorRequired").value(false))
                .andExpect(jsonPath("$.securityAttributes").doesNotExist());
    }

    @Test
    void login_whenUserHasActiveMfa_thenShouldAskMfa() throws Exception {
        when(attributeService.getAllAvailableUserSecurityAttributes(any()))
                .thenReturn(List.of());

        mockMvc.perform(MockMvcRequestBuilders.post(LOGIN)
                        .with(httpBasic(WithMockAuthenticatedUser.MFA_USERNAME, WithMockAuthenticatedUser.PASSWORD))
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
                .thenReturn(List.of(TOTP_ATTRIBUTE));

        mockMvc.perform(MockMvcRequestBuilders.post(LOGIN)
                        .with(httpBasic(WithMockAuthenticatedUser.MFA_USERNAME, WithMockAuthenticatedUser.PASSWORD))
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.token").doesNotExist())
                .andExpect(jsonPath("$.additionalFactorRequired").value(true))
                .andExpect(jsonPath("$.securityAttributes").value(hasItems(TOTP.name(), EMAIL_ADDRESS.name())));
    }

    @Test
    void login_whenJwtValid_thenShouldAuthenticateSuccessfully() throws Exception {
        String response = mockMvc.perform(MockMvcRequestBuilders.post(LOGIN)
                        .with(httpBasic(WithMockAuthenticatedUser.USERNAME, WithMockAuthenticatedUser.PASSWORD))
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().string(not(blankOrNullString())))
                .andReturn().getResponse().getContentAsString();

        LoginResponse loginResponse = objectMapper.readValue(response, LoginResponse.class);
        assertThat(loginResponse).isNotNull().satisfies(r -> assertThat(r.token()).isNotNull());
    }

}
