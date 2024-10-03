package com.github.nramc.dev.journey.api.web.resources.rest.users.update;

import com.github.nramc.dev.journey.api.config.security.WebSecurityConfig;
import com.github.nramc.dev.journey.api.config.security.WebSecurityTestConfig;
import com.github.nramc.dev.journey.api.config.security.WithMockAdministratorUser;
import com.github.nramc.dev.journey.api.config.security.WithMockAuthenticatedUser;
import com.github.nramc.dev.journey.api.config.security.WithMockGuestUser;
import com.github.nramc.dev.journey.api.config.security.WithMockMaintainerUser;
import com.github.nramc.dev.journey.api.repository.user.AuthUser;
import com.github.nramc.dev.journey.api.web.resources.rest.users.security.attributes.UserSecurityAttributeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static com.github.nramc.dev.journey.api.web.resources.Resources.MY_SECURITY_MFA;
import static com.github.nramc.dev.journey.api.web.resources.Resources.UPDATE_MY_ACCOUNT;
import static com.github.nramc.dev.journey.api.web.resources.rest.users.UsersData.EMAIL_ATTRIBUTE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.assertArg;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UpdateUserResource.class)
@Import({WebSecurityConfig.class, WebSecurityTestConfig.class})
@ActiveProfiles({"prod", "test"})
@MockBean({UserSecurityAttributeService.class})
class UpdateUserResourceTest {
    private static final String UPDATE_USER_REQUEST_TEMPLATE = """
            {
             "name": "%s"
            }""";
    private static final String UPDATE_MFA_STATUS_REQUEST_TEMPLATE = """
            {
             "status": "%s"
            }""";
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserSecurityAttributeService attributeService;
    @SpyBean
    private UserDetailsManager userDetailsManager;

    @Test
    void context() {
        assertThat(mockMvc).isNotNull();
    }


    @Test
    void change_whenUserNotAuthenticated_thenShouldThrowError() throws Exception {
        mockMvc.perform(post(UPDATE_MY_ACCOUNT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(UPDATE_USER_REQUEST_TEMPLATE.formatted("Valid Name"))
                ).andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockGuestUser
    void change_whenUserNotAuthorized_thenShouldThrowError() throws Exception {
        mockMvc.perform(post(UPDATE_MY_ACCOUNT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(UPDATE_USER_REQUEST_TEMPLATE.formatted("Valid Name"))
                ).andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockAuthenticatedUser
    void change_whenUserAuthenticatedUser_thenCanUpdateDetails() throws Exception {
        updateAccountDetails();
    }

    @Test
    @WithMockMaintainerUser
    void change_whenUserMaintainer_thenCanUpdateDetails() throws Exception {
        updateAccountDetails();
    }

    @Test
    @WithMockAdministratorUser
    void change_whenUserAdministrator_thenCanUpdateDetails() throws Exception {
        updateAccountDetails();
    }

    private void updateAccountDetails() throws Exception {
        mockMvc.perform(post(UPDATE_MY_ACCOUNT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(UPDATE_USER_REQUEST_TEMPLATE.formatted("Updated Valid Name"))
                ).andDo(print())
                .andExpect(status().isOk());
        verify(userDetailsManager).updateUser(
                assertArg((AuthUser user) -> assertThat(user.getName()).isEqualTo("Updated Valid Name")));
    }

    @Test
    void updateMfaStatus_whenUserNotAuthenticated_thenShouldThrowError() throws Exception {
        mockMvc.perform(post(MY_SECURITY_MFA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(UPDATE_MFA_STATUS_REQUEST_TEMPLATE.formatted(true))
                ).andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockGuestUser
    void updateMfaStatus_whenUserNotAuthorized_thenShouldThrowError() throws Exception {
        mockMvc.perform(post(MY_SECURITY_MFA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(UPDATE_MFA_STATUS_REQUEST_TEMPLATE.formatted(true))
                ).andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockAuthenticatedUser
    void updateMfaStatus_whenAnyAuthenticatedUserTryToActivateMfa_andUserHasValidMfaAttribute_thenShouldEnableMfa() throws Exception {
        when(attributeService.getAllAvailableUserSecurityAttributes(any(AuthUser.class)))
                .thenReturn(List.of(EMAIL_ATTRIBUTE.toBuilder().verified(true).build()));
        mockMvc.perform(post(MY_SECURITY_MFA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(UPDATE_MFA_STATUS_REQUEST_TEMPLATE.formatted(true))
                ).andDo(print())
                .andExpect(status().isOk());
        verify(userDetailsManager).updateUser(assertArg((AuthUser user) -> assertThat(user.isMfaEnabled()).isTrue()));
    }

    @Test
    @WithMockAuthenticatedUser
    void updateMfaStatus_whenAuthenticatedUserTryToActivateMfa_butDoesNotHaveValidMfaAttribute_thenShouldThrowError() throws Exception {
        mockMvc.perform(post(MY_SECURITY_MFA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(UPDATE_MFA_STATUS_REQUEST_TEMPLATE.formatted(true))
                ).andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockAuthenticatedUser
    void updateMfaStatus_whenAnyAuthenticatedUserTryToDeactivateMfa_thenShouldDisableMfa() throws Exception {
        mockMvc.perform(post(MY_SECURITY_MFA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(UPDATE_MFA_STATUS_REQUEST_TEMPLATE.formatted(false))
                ).andDo(print())
                .andExpect(status().isOk());
        verify(userDetailsManager).updateUser(assertArg((AuthUser user) -> assertThat(user.isMfaEnabled()).isFalse()));
    }


}
