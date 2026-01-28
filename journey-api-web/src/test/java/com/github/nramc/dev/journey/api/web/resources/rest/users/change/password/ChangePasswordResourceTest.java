package com.github.nramc.dev.journey.api.web.resources.rest.users.change.password;

import com.github.nramc.dev.journey.api.config.security.InMemoryUserDetailsConfig;
import com.github.nramc.dev.journey.api.config.security.WebSecurityConfig;
import com.github.nramc.dev.journey.api.config.security.WithMockAdministratorUser;
import com.github.nramc.dev.journey.api.config.security.WithMockAuthenticatedUser;
import com.github.nramc.dev.journey.api.config.security.WithMockGuestUser;
import com.github.nramc.dev.journey.api.config.security.WithMockMaintainerUser;
import com.github.nramc.dev.journey.api.repository.user.AuthUserDetailsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static com.github.nramc.dev.journey.api.web.resources.Resources.CHANGE_MY_PASSWORD;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.assertArg;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ChangePasswordResource.class)
@Import({WebSecurityConfig.class, InMemoryUserDetailsConfig.class, BCryptPasswordEncoder.class})
@ActiveProfiles({"prod", "test"})
class ChangePasswordResourceTest {
    private static final String REQUEST_TEMPLATE = """
            {
             "newPassword": "%s"
            }""";
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @MockitoBean
    AuthUserDetailsService authUserDetailsService;


    @Test
    void context() {
        assertThat(mockMvc).isNotNull();
    }

    @Test
    void find_whenUserNotAuthenticated_shouldThrowError() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post(CHANGE_MY_PASSWORD)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(REQUEST_TEMPLATE.formatted("valid-new-password"))
                ).andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockGuestUser
    void find_whenUserNotAuthorized_shouldThrowError() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post(CHANGE_MY_PASSWORD)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(REQUEST_TEMPLATE.formatted("valid-new-password"))
                ).andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockMaintainerUser
    void find_whenUserMaintainer_thenCanChangePassword() throws Exception {
        changePassword();
    }

    @Test
    @WithMockAuthenticatedUser
    void find_whenUserAuthenticatedUser_thenCanChangePassword() throws Exception {
        changePassword();
    }

    @Test
    @WithMockAdministratorUser
    void find_whenUserAdministrator_thenCanChangePassword() throws Exception {
        changePassword();
    }

    private void changePassword() throws Exception {
        String newPassword = "valid-new-password";
        mockMvc.perform(MockMvcRequestBuilders.post(CHANGE_MY_PASSWORD)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(REQUEST_TEMPLATE.formatted(newPassword))
                ).andDo(print())
                .andExpect(status().isOk());

        verify(authUserDetailsService).updatePassword(
                any(),
                assertArg(password -> assertThat(passwordEncoder.matches(newPassword, password)).isTrue())
        );
    }

}
