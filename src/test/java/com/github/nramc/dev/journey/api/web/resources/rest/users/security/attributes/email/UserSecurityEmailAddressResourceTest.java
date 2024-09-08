package com.github.nramc.dev.journey.api.web.resources.rest.users.security.attributes.email;

import com.github.nramc.dev.journey.api.config.security.WebSecurityConfig;
import com.github.nramc.dev.journey.api.config.security.WebSecurityTestConfig;
import com.github.nramc.dev.journey.api.config.security.WithMockAuthenticatedUser;
import com.github.nramc.dev.journey.api.config.security.WithMockGuestUser;
import com.github.nramc.dev.journey.api.core.security.attributes.SecurityAttributeType;
import com.github.nramc.dev.journey.api.web.dto.user.security.UserSecurityAttribute;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Optional;

import static com.github.nramc.dev.journey.api.web.resources.Resources.MY_SECURITY_ATTRIBUTE_EMAIL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserSecurityEmailAddressResource.class)
@Import({WebSecurityConfig.class, WebSecurityTestConfig.class})
@ActiveProfiles({"prod", "test"})
class UserSecurityEmailAddressResourceTest {
    private static final UserSecurityAttribute EMAIL_ATTRIBUTE = UserSecurityAttribute.builder()
            .type(SecurityAttributeType.EMAIL_ADDRESS)
            .value("u***************************@gmail.com")
            .enabled(true)
            .verified(false)
            .creationDate(LocalDate.now())
            .lastUpdateDate(LocalDate.now())
            .build();
    private static final String REQUEST_PAYLOAD = """
            {
             "emailAddress": "%s"
            }
            """;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UserSecurityEmailAddressAttributeService emailAddressAttributeService;

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
    @WithMockGuestUser
    void updateEmailAddress_whenUserNotAuthorized_thenShouldThrowError() throws Exception {
        mockMvc.perform(post(MY_SECURITY_ATTRIBUTE_EMAIL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(REQUEST_PAYLOAD.formatted("example.email@example.com"))
                ).andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockAuthenticatedUser
    void updateEmailAddress_whenAnyAuthenticatedUserTryToUpdateEmailAddress_shouldBeSuccessful() throws Exception {
        when(emailAddressAttributeService.saveSecurityEmailAddress(any(), any())).thenReturn(EMAIL_ATTRIBUTE);
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
    @WithMockAuthenticatedUser
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
    @WithMockGuestUser
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
    @WithMockAuthenticatedUser
    void getEmailAddress_whenAnyAuthenticatedUserTryToGetAttribute_shouldBeSuccessful() throws Exception {
        when(emailAddressAttributeService.provideEmailAttributeIfExists(any())).thenReturn(Optional.ofNullable(EMAIL_ATTRIBUTE));

        mockMvc.perform(get(MY_SECURITY_ATTRIBUTE_EMAIL))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.type").value("EMAIL_ADDRESS"))
                .andExpect(jsonPath("$.value").value("u***************************@gmail.com"))
                .andExpect(jsonPath("$.enabled").value("true"))
                .andExpect(jsonPath("$.verified").value("false"));
    }

    @Test
    @WithMockAuthenticatedUser
    void getEmailAddress_whenAttributesNotExists_shouldBeSuccessful() throws Exception {
        mockMvc.perform(get(MY_SECURITY_ATTRIBUTE_EMAIL))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(""));
    }

}
