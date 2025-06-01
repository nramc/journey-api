package com.github.nramc.dev.journey.api.web.resources.rest.auth.webauthn;

import com.github.nramc.dev.journey.api.config.security.InMemoryUserDetailsConfig;
import com.github.nramc.dev.journey.api.config.security.WebSecurityConfig;
import com.github.nramc.dev.journey.api.config.security.WithMockAuthenticatedUser;
import com.github.nramc.dev.journey.api.core.security.webauthn.WebAuthnService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJson;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {WebAuthnManagerResource.class})
@Import({InMemoryUserDetailsConfig.class, WebSecurityConfig.class})
@ActiveProfiles({"test"})
@AutoConfigureJson
class WebAuthnManagerResourceTest {
    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    WebAuthnService webAuthnService;

    @Test
    void test() {
        assertDoesNotThrow(() -> assertThat(mockMvc).isNotNull());
    }

    @Test
    void listCredentials_whenUserAuthenticated_thenShouldListCredentials() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/webauthn/credentials")
                        .with(httpBasic(WithMockAuthenticatedUser.USERNAME, WithMockAuthenticatedUser.PASSWORD))
                ).andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void deleteCredential_whenUserAuthenticated_thenShouldDeleteCredential() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/webauthn/credentials")
                        .with(httpBasic(WithMockAuthenticatedUser.USERNAME, WithMockAuthenticatedUser.PASSWORD))
                        .param("credentialId", "test-credential-id")
                ).andDo(print())
                .andExpect(status().isOk());
    }

}
