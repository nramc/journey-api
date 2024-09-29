package com.github.nramc.dev.journey.api.web.resources.rest.users.delete;

import com.github.nramc.dev.journey.api.config.security.WebSecurityConfig;
import com.github.nramc.dev.journey.api.config.security.WebSecurityTestConfig;
import com.github.nramc.dev.journey.api.config.security.WithMockAdministratorUser;
import com.github.nramc.dev.journey.api.config.security.WithMockAuthenticatedUser;
import com.github.nramc.dev.journey.api.config.security.WithMockGuestUser;
import com.github.nramc.dev.journey.api.repository.auth.AuthUser;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static com.github.nramc.dev.journey.api.config.security.Role.Constants.AUTHENTICATED_USER;
import static com.github.nramc.dev.journey.api.config.security.Role.Constants.GUEST_USER;
import static com.github.nramc.dev.journey.api.config.security.Role.Constants.MAINTAINER;
import static com.github.nramc.dev.journey.api.web.resources.Resources.DELETE_MY_ACCOUNT;
import static com.github.nramc.dev.journey.api.web.resources.Resources.DELETE_USER_BY_USERNAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.assertArg;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DeleteUserResource.class)
@Import({WebSecurityConfig.class, WebSecurityTestConfig.class})
@ActiveProfiles({"prod", "test"})
class DeleteUserResourceTest {
    @Autowired
    private MockMvc mockMvc;
    @SpyBean
    private UserDetailsManager userDetailsManager;

    @Test
    void context() {
        assertDoesNotThrow(() -> {
            assertThat(mockMvc).isNotNull();
        });
    }

    @Test
    void find_whenUserNotAuthenticated_shouldThrowError() throws Exception {
        mockMvc.perform(delete(DELETE_USER_BY_USERNAME, "test-user"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "test-user", authorities = {GUEST_USER, AUTHENTICATED_USER, MAINTAINER})
    void find_whenUserDoesNotHavePermission_shouldThrowError() throws Exception {
        mockMvc.perform(delete(DELETE_USER_BY_USERNAME, "test-user"))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockAdministratorUser
    void deleteByUsername_whenUserHasPermission_thenShouldDeleteUser() throws Exception {
        mockMvc.perform(delete(DELETE_USER_BY_USERNAME, "admin-user"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void deleteMyAccount_whenUserNotAuthenticated_shouldThrowError() throws Exception {
        mockMvc.perform(delete(DELETE_MY_ACCOUNT))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockGuestUser
    void deleteMyAccount_whenUserDoesNotHavePermission_shouldThrowError() throws Exception {
        mockMvc.perform(delete(DELETE_MY_ACCOUNT))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockAuthenticatedUser
    void deleteMyAccount_whenUserAuthenticated_thenShouldDeleteUserAccount() throws Exception {
        mockMvc.perform(delete(DELETE_MY_ACCOUNT))
                .andDo(print())
                .andExpect(status().isOk());

        verify(userDetailsManager).updateUser(assertArg((AuthUser user) -> assertThat(user.isEnabled()).isFalse()));

    }


}
