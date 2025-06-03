package com.github.nramc.dev.journey.api.web.resources.rest.auth.webauthn;

import com.github.nramc.dev.journey.api.config.security.InMemoryUserDetailsConfig;
import com.github.nramc.dev.journey.api.config.security.WebSecurityConfig;
import com.github.nramc.dev.journey.api.config.security.WithMockAuthenticatedUser;
import com.github.nramc.dev.journey.api.core.security.webauthn.WebAuthnService;
import com.yubico.webauthn.data.PublicKeyCredentialCreationOptions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
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
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {WebAuthnRegistrationResource.class})
@Import({InMemoryUserDetailsConfig.class, WebSecurityConfig.class})
@ActiveProfiles({"test"})
@AutoConfigureJson
class WebAuthnRegistrationResourceTest {
    private static final String CREATION_OPTIONS_JSON = """
            {
              "rp": {
                "name": "Example Corp",
                "id": "example.com"
              },
              "user": {
                "id": "dXNlcklE",
                "name": "johndoe",
                "displayName": "John Doe"
              },
              "challenge": "5kH1hHkzT74Uq9F5Uu5K5g",
              "pubKeyCredParams": [
                {
                  "type": "public-key",
                  "alg": -7
                },
                {
                  "type": "public-key",
                  "alg": -257
                }
              ],
              "authenticatorSelection": {
                "authenticatorAttachment": "platform",
                "residentKey": "preferred",
                "userVerification": "preferred"
              },
              "timeout": 60000,
              "attestation": "none",
              "excludeCredentials": [],
              "extensions": {}
            }
            """;

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    WebAuthnService webAuthnService;

    @Test
    void test() {
        assertDoesNotThrow(() -> assertThat(mockMvc).isNotNull());
    }

    @Test
    void startRegistration_whenUserAuthenticated_thenShouldGetCreateOptions() throws Exception {
        Mockito.when(webAuthnService.startRegistration(any())).thenReturn(PublicKeyCredentialCreationOptions.fromJson(CREATION_OPTIONS_JSON));
        mockMvc.perform(MockMvcRequestBuilders.post("/webauthn/register/start")
                        .with(httpBasic(WithMockAuthenticatedUser.USERNAME, WithMockAuthenticatedUser.PASSWORD))
                ).andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void finishRegistration_whenUserAuthenticated_thenShouldCompleteRegistration() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/webauthn/register/finish")
                        .with(httpBasic(WithMockAuthenticatedUser.USERNAME, WithMockAuthenticatedUser.PASSWORD))
                        .header("User-Agent", "TestUserAgent")
                        .contentType("application/json")
                        .content("""
                                {"type":"public-key","id":"BXcFzYFax4P75xdYKyXlpA","rawId":"BXcFzYFax4P75xdYKyXlpA","authenticatorAttachment":"platform","response":{"clientDataJSON":"eyJ0eXBlIjoid2ViYXV0aG4uY3JlYXRlIiwiY2hhbGxlbmdlIjoiY0R2eGFhSkhPNEV6SnBSanNBd2I1RDVZeUxPS1hveVBSQ0JqYVJ5VjJJayIsIm9yaWdpbiI6Imh0dHBzOi8vbG9jYWxob3N0OjQyMDAiLCJjcm9zc09yaWdpbiI6ZmFsc2V9","attestationObject":"o2NmbXRkbm9uZWdhdHRTdG10oGhhdXRoRGF0YViUSZYN5YgOjGh0NBcPZHZgW4_krrmihjLHmVzzuoMdl2NdAAAAAOqbjWZNAR0hPOS2tIy1ddQAEAV3Bc2BWseD--cXWCsl5aSlAQIDJiABIVggeoSqqwoxWtfK6g-wXEOmVhb6tIR3mHSSCAFkt8wCI8giWCA05VJ8-siXNdLsSI-GwOQIk-rzvTob3TLTPS3P1pHoHQ","transports":["hybrid","internal"]},"clientExtensionResults":{"credProps":{"rk":true}}}
                                """) // Replace with actual JSON content
                ).andDo(print())
                .andExpect(status().isOk());
    }

}
