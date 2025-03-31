package com.github.nramc.dev.journey.api.web.resources.rest.users.find;

import com.github.nramc.dev.journey.api.config.security.InMemoryUserDetailsConfig;
import com.github.nramc.dev.journey.api.config.security.WebSecurityConfig;
import com.github.nramc.dev.journey.api.config.security.WithMockAdministratorUser;
import com.github.nramc.dev.journey.api.config.security.WithMockAuthenticatedUser;
import com.github.nramc.dev.journey.api.config.security.WithMockGuestUser;
import com.github.nramc.dev.journey.api.repository.user.UserRepository;
import com.github.nramc.dev.journey.api.web.resources.rest.users.UsersData;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static com.github.nramc.dev.journey.api.core.domain.user.Role.Constants.AUTHENTICATED_USER;
import static com.github.nramc.dev.journey.api.core.domain.user.Role.Constants.GUEST_USER;
import static com.github.nramc.dev.journey.api.core.domain.user.Role.Constants.MAINTAINER;
import static com.github.nramc.dev.journey.api.web.resources.Resources.FIND_MY_ACCOUNT;
import static com.github.nramc.dev.journey.api.web.resources.Resources.FIND_USERS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FindUsersResource.class)
@Import({WebSecurityConfig.class, InMemoryUserDetailsConfig.class})
@ActiveProfiles({"prod", "test"})
class FindUsersResourceTest {
    @Autowired
    MockMvc mockMvc;
    @MockitoBean
    UserRepository userRepository;

    @Test
    void context() {
        assertDoesNotThrow(() ->
            assertThat(mockMvc).isNotNull());
    }

    @Test
    @WithAnonymousUser
    void find_whenUserNotAuthenticated_shouldThrowError() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post(FIND_USERS))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "non-admin-user@example.com", authorities = {GUEST_USER, AUTHENTICATED_USER, MAINTAINER})
    void find_whenUserDoesNotHavePermission_shouldThrowError() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post(FIND_USERS))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockAdministratorUser
    void find_whenUserHasPermission_shouldReturnUsersDetails() throws Exception {
        when(userRepository.findAll()).thenReturn(List.of(UsersData.AUTHENTICATED_USER));
        mockMvc.perform(MockMvcRequestBuilders.get(FIND_USERS))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].username").hasJsonPath())
                .andExpect(jsonPath("$[0].name").hasJsonPath())
                .andExpect(jsonPath("$[0].createdDate").hasJsonPath())
                .andExpect(jsonPath("$[0].passwordChangedAt").hasJsonPath())
                .andExpect(jsonPath("$[0].enabled").hasJsonPath())
                .andExpect(jsonPath("$[0].roles").hasJsonPath())
                .andExpect(jsonPath("$[0].password").doesNotHaveJsonPath())
                .andExpect(jsonPath("$[0].secret").doesNotHaveJsonPath())
                .andExpect(jsonPath("$[0].mfaEnabled").hasJsonPath());
    }

    @Test
    @WithAnonymousUser
    void findMyAccount_whenUserNotAuthenticated_shouldThrowError() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post(FIND_MY_ACCOUNT))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockGuestUser
    void findMyAccount_whenUserDoesNotHavePermission_shouldThrowError() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post(FIND_MY_ACCOUNT))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockAuthenticatedUser
    void findMyAccount_whenUserHasPermission_shouldReturnUsersDetails() throws Exception {
        when(userRepository.findUserByUsername(UsersData.AUTHENTICATED_USER.getUsername())).thenReturn(UsersData.AUTHENTICATED_USER);
        mockMvc.perform(MockMvcRequestBuilders.get(FIND_MY_ACCOUNT))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.username").hasJsonPath())
                .andExpect(jsonPath("$.name").hasJsonPath())
                .andExpect(jsonPath("$.createdDate").hasJsonPath())
                .andExpect(jsonPath("$.passwordChangedAt").hasJsonPath())
                .andExpect(jsonPath("$.enabled").hasJsonPath())
                .andExpect(jsonPath("$.roles").hasJsonPath())
                .andExpect(jsonPath("$.password").doesNotHaveJsonPath())
                .andExpect(jsonPath("$.secret").doesNotHaveJsonPath())
                .andExpect(jsonPath("$.mfaEnabled").hasJsonPath());
    }

}
