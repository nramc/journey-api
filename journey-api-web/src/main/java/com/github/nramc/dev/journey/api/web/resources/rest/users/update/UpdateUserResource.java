package com.github.nramc.dev.journey.api.web.resources.rest.users.update;

import com.github.nramc.dev.journey.api.repository.user.AuthUser;
import com.github.nramc.dev.journey.api.web.resources.rest.doc.RestDocCommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static com.github.nramc.dev.journey.api.web.resources.Resources.MY_SECURITY_MFA;
import static com.github.nramc.dev.journey.api.web.resources.Resources.UPDATE_MY_ACCOUNT;

@RestController
@Slf4j
@RequiredArgsConstructor
public class UpdateUserResource {
    private final UserDetailsManager userDetailsManager;

    @Operation(summary = "Update my account details", tags = {"My Account Features"})
    @RestDocCommonResponse
    @ApiResponse(responseCode = "200", description = "User details updated successfully")
    @PostMapping(value = UPDATE_MY_ACCOUNT, consumes = MediaType.APPLICATION_JSON_VALUE)
    public void change(@RequestBody @Valid UpdateUserRequest updateUserRequest, Authentication authentication) {
        AuthUser authUser = (AuthUser) userDetailsManager.loadUserByUsername(authentication.getName());
        AuthUser updatedDetails = updateWith(authUser, updateUserRequest);
        userDetailsManager.updateUser(updatedDetails);
    }

    @Operation(summary = "Enable/Disable Multi-factor authentication", tags = {"My Account Features"})
    @RestDocCommonResponse
    @ApiResponse(responseCode = "200", description = "MFA feature updated successfully")
    @PostMapping(value = MY_SECURITY_MFA, consumes = MediaType.APPLICATION_JSON_VALUE)
    public void updateMfaStatus(@RequestBody @Valid MfaStatus mfaStatus, Authentication authentication) {
        AuthUser authUser = (AuthUser) userDetailsManager.loadUserByUsername(authentication.getName());

        AuthUser updatedDetails = authUser.toBuilder().mfaEnabled(mfaStatus.status()).build();
        userDetailsManager.updateUser(updatedDetails);
    }


    private AuthUser updateWith(AuthUser authUser, UpdateUserRequest request) {
        return authUser.toBuilder()
                .name(request.name())
                .build();
    }
}
