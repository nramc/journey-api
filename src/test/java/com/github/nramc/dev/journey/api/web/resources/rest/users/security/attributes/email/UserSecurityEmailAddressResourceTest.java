package com.github.nramc.dev.journey.api.web.resources.rest.users.security.attributes.email;

import com.github.nramc.dev.journey.api.config.TestContainersConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static com.github.nramc.dev.journey.api.config.security.Role.Constants.AUTHENTICATED_USER;
import static com.github.nramc.dev.journey.api.config.security.Role.Constants.GUEST_USER;
import static com.github.nramc.dev.journey.api.web.resources.Resources.MY_SECURITY_ATTRIBUTE_EMAIL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestContainersConfiguration.class)
@ActiveProfiles({"test"})
@AutoConfigureMockMvc
class UserSecurityEmailAddressResourceTest {
    private static final String REQUEST_PAYLOAD = """
            {
             "emailAddress": "%s"
            }
            """;
    @Autowired
    private MockMvc mockMvc;

    @Test
    void context() {
        assertThat(mockMvc).isNotNull();
    }

    @Test
    void updateEmailAddress_whenUserNotAuthenticated_thenShouldThrowError() throws Exception {
        mockMvc.perform(post(MY_SECURITY_ATTRIBUTE_EMAIL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(REQUEST_PAYLOAD.formatted("example.email@example.com"))
                ).andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "test-user", authorities = {GUEST_USER})
    void updateEmailAddress_whenUserNotAuthorized_thenShouldThrowError() throws Exception {
        mockMvc.perform(post(MY_SECURITY_ATTRIBUTE_EMAIL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(REQUEST_PAYLOAD.formatted("example.email@example.com"))
                ).andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "test-user", authorities = {AUTHENTICATED_USER})
    void updateEmailAddress_whenAnyAuthenticatedUserTryToUpdateEmailAddress_shouldBeSuccessful() throws Exception {
        mockMvc.perform(post(MY_SECURITY_ATTRIBUTE_EMAIL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(REQUEST_PAYLOAD.formatted("updated-valid-email-addresss@gmail.com"))
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.type").value("EMAIL_ADDRESS"))
                .andExpect(jsonPath("$.value").value("u***************************@gmail.com"))
                .andExpect(jsonPath("$.enabled").value("true"))
                .andExpect(jsonPath("$.verified").value("false"));
    }

    @Test
    @WithMockUser(username = "test-user", authorities = {AUTHENTICATED_USER})
    void updateEmailAddress_whenEmailAddressNotValid_shouldThrowError() throws Exception {
        mockMvc.perform(post(MY_SECURITY_ATTRIBUTE_EMAIL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(REQUEST_PAYLOAD.formatted("invalid-email-address"))
                ).andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void getEmailAddress_whenUserNotAuthenticated_thenShouldThrowError() throws Exception {
        mockMvc.perform(get(MY_SECURITY_ATTRIBUTE_EMAIL))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "test-user", authorities = {GUEST_USER})
    void getEmailAddress_whenUserGuest_thenShouldBePermitted() throws Exception {
        mockMvc.perform(get(MY_SECURITY_ATTRIBUTE_EMAIL))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "test-user", authorities = {"ROLE_USER"})
    void getEmailAddress_whenUserNotAuthorized_thenShouldThrowError() throws Exception {
        mockMvc.perform(get(MY_SECURITY_ATTRIBUTE_EMAIL))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "test-user", authorities = {AUTHENTICATED_USER})
    void getEmailAddress_whenAnyAuthenticatedUserTryToGetAttribute_shouldBeSuccessful() throws Exception {
        mockMvc.perform(post(MY_SECURITY_ATTRIBUTE_EMAIL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(REQUEST_PAYLOAD.formatted("example.email@example.com"))
                ).andDo(print())
                .andExpect(status().isOk());

        mockMvc.perform(get(MY_SECURITY_ATTRIBUTE_EMAIL))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.type").value("EMAIL_ADDRESS"))
                .andExpect(jsonPath("$.value").value("e************@example.com"))
                .andExpect(jsonPath("$.enabled").value("true"))
                .andExpect(jsonPath("$.verified").value("false"));
    }

    @Test
    @WithMockUser(username = "test-user", authorities = {AUTHENTICATED_USER})
    void getEmailAddress_whenAttributesNotExists_shouldBeSuccessful() throws Exception {
        mockMvc.perform(get(MY_SECURITY_ATTRIBUTE_EMAIL))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(""));
    }

}
