package com.github.nramc.dev.journey.api.account.web.users.find;

import com.github.nramc.dev.journey.api.account.repository.UserRepository;
import com.github.nramc.dev.journey.api.account.web.users.UsersData;
import com.github.nramc.dev.journey.api.infrastructure.security.InMemoryUserDetailsConfig;
import com.github.nramc.dev.journey.api.infrastructure.security.WebSecurityConfig;
import com.github.nramc.dev.journey.api.infrastructure.security.WithMockAdministratorUser;
import com.github.nramc.dev.journey.api.infrastructure.security.WithMockAuthenticatedUser;
import com.github.nramc.dev.journey.api.infrastructure.security.WithMockGuestUser;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static com.github.nramc.dev.journey.api.shared.domain.user.security.Role.Constants.AUTHENTICATED_USER;
import static com.github.nramc.dev.journey.api.shared.domain.user.security.Role.Constants.GUEST_USER;
import static com.github.nramc.dev.journey.api.shared.domain.user.security.Role.Constants.MAINTAINER;
import static com.github.nramc.dev.journey.api.shared.web.Resources.FIND_MY_ACCOUNT;
import static com.github.nramc.dev.journey.api.shared.web.Resources.FIND_USERS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
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
        assertThatCode(() ->
                assertThat(mockMvc).isNotNull()).doesNotThrowAnyException();
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
