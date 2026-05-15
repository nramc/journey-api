package com.github.nramc.dev.journey.api.account.web.auth.login;

import com.github.nramc.dev.journey.api.account.jwt.JwtGenerator;
import com.github.nramc.dev.journey.api.account.repository.AuthUserDetailsService;
import com.github.nramc.dev.journey.api.account.repository.UserRepository;
import com.github.nramc.dev.journey.api.account.web.auth.dto.LoginResponse;
import com.github.nramc.dev.journey.api.account.web.auth.provider.JwtResponseProvider;
import com.github.nramc.dev.journey.api.infrastructure.security.InMemoryUserDetailsConfig;
import com.github.nramc.dev.journey.api.infrastructure.security.JwtProperties;
import com.github.nramc.dev.journey.api.infrastructure.security.WebSecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJson;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import static com.github.nramc.dev.journey.api.account.web.users.UsersData.GUEST_USER;
import static com.github.nramc.dev.journey.api.shared.web.Resources.GUEST_LOGIN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.hamcrest.Matchers.blankOrNullString;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.not;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {GuestLoginResource.class})
@Import({InMemoryUserDetailsConfig.class, JwtGenerator.class, JwtResponseProvider.class, WebSecurityConfig.class})
@EnableConfigurationProperties({JwtProperties.class})
@ActiveProfiles({"test"})
@AutoConfigureJson
class GuestLoginResourceTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockitoBean
    AuthUserDetailsService userDetailsService;
    @MockitoBean
    UserRepository userRepository;

    @Test
    void test() {
        assertThatCode(() ->
                assertThat(mockMvc).isNotNull()).doesNotThrowAnyException();
    }

    @Test
    void guestLogin_thenShouldGetJwtToken() throws Exception {
        when(userDetailsService.getGuestUserDetails()).thenReturn(GUEST_USER);
        mockMvc.perform(post(GUEST_LOGIN)).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.token").value(not(blankOrNullString())))
                .andExpect(jsonPath("$.expiredAt").value(not(blankOrNullString())))
                .andExpect(jsonPath("$.name").value(GUEST_USER.getName()))
                .andExpect(jsonPath("$.authorities").value(hasItems("GUEST_USER")));
    }

    @Test
    void token_whenGuestUserAuthenticationValid_thenShouldAuthenticateSuccessfully() throws Exception {
        when(userDetailsService.getGuestUserDetails()).thenReturn(GUEST_USER);
        String response = mockMvc.perform(post(GUEST_LOGIN))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().string(not(blankOrNullString())))
                .andReturn().getResponse().getContentAsString();

        when(userRepository.findUserByUsername(GUEST_USER.getUsername())).thenReturn(GUEST_USER);
        LoginResponse loginResponse = objectMapper.readValue(response, LoginResponse.class);
        assertThat(loginResponse).isNotNull()
                .satisfies(r -> assertThat(r.token()).isNotNull());
    }

}
