package com.github.nramc.dev.journey.api.account.web.auth.webauthn;

import com.github.nramc.dev.journey.api.account.repository.AuthUser;
import com.github.nramc.dev.journey.api.account.webauthn.WebAuthnService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/webauthn/credentials")
@Tag(name = "WebAuthn Management", description = "List and remove registered passkeys")
public class WebAuthnManagerResource {
    private final UserDetailsService userDetailsService;
    private final WebAuthnService webAuthnService;

    /**
     * Lists all registered WebAuthn credentials for the authenticated user.
     *
     * @param authentication the authentication object containing user details
     * @return a list of registered credentials
     */
    @Operation(summary = "List registered passkeys", description = "Returns all passkeys registered for the authenticated user.")
    @ApiResponse(responseCode = "200", description = "List of registered passkeys",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = CredentialInfo.class)))
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<CredentialInfo> listCredentials(Authentication authentication) {
        AuthUser userDetails = (AuthUser) userDetailsService.loadUserByUsername(authentication.getName());
        return webAuthnService.listCredentials(userDetails).stream()
                .map(CredentialInfo::from)
                .toList();
    }

    /**
     * Deletes a registered WebAuthn credential for the authenticated user.
     *
     * @param credentialId   the credential ID to delete
     * @param authentication the authentication object containing user details
     */
    @Operation(summary = "Delete a passkey", description = "Removes a registered passkey by its credential ID.")
    @ApiResponse(responseCode = "204", description = "Passkey deleted successfully")
    @DeleteMapping
    public void deleteCredential(Authentication authentication, @RequestParam String credentialId) {
        AuthUser userDetails = (AuthUser) userDetailsService.loadUserByUsername(authentication.getName());
        webAuthnService.deleteCredential(userDetails, credentialId);
    }
}
