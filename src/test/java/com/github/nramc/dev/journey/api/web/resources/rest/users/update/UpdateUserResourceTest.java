package com.github.nramc.dev.journey.api.web.resources.rest.users.update;

import com.github.nramc.dev.journey.api.repository.auth.AuthUser;
import com.github.nramc.dev.journey.api.repository.auth.UserRepository;
import com.github.nramc.dev.journey.api.web.resources.rest.users.security.attributes.UserSecurityAttributeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.List;

import static com.github.nramc.dev.journey.api.config.security.Role.Constants.ADMINISTRATOR;
import static com.github.nramc.dev.journey.api.config.security.Role.Constants.AUTHENTICATED_USER;
import static com.github.nramc.dev.journey.api.config.security.Role.Constants.GUEST_USER;
import static com.github.nramc.dev.journey.api.config.security.Role.Constants.MAINTAINER;
import static com.github.nramc.dev.journey.api.web.resources.Resources.MY_SECURITY_MFA;
import static com.github.nramc.dev.journey.api.web.resources.Resources.UPDATE_MY_ACCOUNT;
import static com.github.nramc.dev.journey.api.web.resources.rest.users.UsersData.EMAIL_ATTRIBUTE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles({"test"})
@AutoConfigureMockMvc
class UpdateUserResourceTest {
    private static final String UPDATE_USER_REQUEST_TEMPLATE = """
            {
             "name": "%s"
            }""";
    private static final String UPDATE_MFA_STATUS_REQUEST_TEMPLATE = """
            {
             "status": "%s"
            }""";
    @Container
    @ServiceConnection
    static MongoDBContainer mongoDBContainer = new MongoDBContainer(DockerImageName.parse("mongo:latest"))
            .withExposedPorts(27017);
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;
    @MockBean
    private UserSecurityAttributeService attributeService;

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
    @WithMockUser(username = "test-user", authorities = {GUEST_USER})
    void change_whenUserNotAuthorized_thenShouldThrowError() throws Exception {
        mockMvc.perform(post(UPDATE_MY_ACCOUNT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(UPDATE_USER_REQUEST_TEMPLATE.formatted("Valid Name"))
                ).andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", authorities = {AUTHENTICATED_USER})
    void change_whenUserAuthenticatedUser_thenCanUpdateDetails() throws Exception {
        updateAccountDetails();
    }

    @Test
    @WithMockUser(username = "admin", authorities = {MAINTAINER})
    void change_whenUserMaintainer_thenCanUpdateDetails() throws Exception {
        updateAccountDetails();
    }

    @Test
    @WithMockUser(username = "admin", authorities = {ADMINISTRATOR})
    void change_whenUserAdministrator_thenCanUpdateDetails() throws Exception {
        updateAccountDetails();
    }

    private void updateAccountDetails() throws Exception {
        mockMvc.perform(post(UPDATE_MY_ACCOUNT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(UPDATE_USER_REQUEST_TEMPLATE.formatted("Updated Valid Name"))
                ).andDo(print())
                .andExpect(status().isOk());
        AuthUser user = userRepository.findUserByUsername("admin");
        assertThat(user.getName()).isEqualTo("Updated Valid Name");
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
    @WithMockUser(username = "test-user", authorities = {GUEST_USER})
    void updateMfaStatus_whenUserNotAuthorized_thenShouldThrowError() throws Exception {
        mockMvc.perform(post(MY_SECURITY_MFA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(UPDATE_MFA_STATUS_REQUEST_TEMPLATE.formatted(true))
                ).andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", authorities = {AUTHENTICATED_USER})
    void updateMfaStatus_whenAnyAuthenticatedUserTryToActivateMfa_andUserHasValidMfaAttribute_thenShouldEnableMfa() throws Exception {
        when(attributeService.getAllAvailableUserSecurityAttributes(any(AuthUser.class)))
                .thenReturn(List.of(EMAIL_ATTRIBUTE.toBuilder().verified(true).build()));
        mockMvc.perform(post(MY_SECURITY_MFA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(UPDATE_MFA_STATUS_REQUEST_TEMPLATE.formatted(true))
                ).andDo(print())
                .andExpect(status().isOk());
        AuthUser user = userRepository.findUserByUsername("admin");
        assertThat(user.isMfaEnabled()).isTrue();
    }

    @Test
    @WithMockUser(username = "admin", authorities = {AUTHENTICATED_USER})
    void updateMfaStatus_whenAuthenticatedUserTryToActivateMfa_butDoesNotHaveValidMfaAttribute_thenShouldThrowError() throws Exception {
        mockMvc.perform(post(MY_SECURITY_MFA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(UPDATE_MFA_STATUS_REQUEST_TEMPLATE.formatted(true))
                ).andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "admin", authorities = {AUTHENTICATED_USER})
    void updateMfaStatus_whenAnyAuthenticatedUserTryToDeactivateMfa_thenShouldDisableMfa() throws Exception {
        mockMvc.perform(post(MY_SECURITY_MFA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(UPDATE_MFA_STATUS_REQUEST_TEMPLATE.formatted(false))
                ).andDo(print())
                .andExpect(status().isOk());
        AuthUser user = userRepository.findUserByUsername("admin");
        assertThat(user.isMfaEnabled()).isFalse();
    }


}