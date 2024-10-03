package com.github.nramc.dev.journey.api.web.resources.rest.users.update;

import com.github.nramc.dev.journey.api.repository.user.AuthUser;
import com.github.nramc.dev.journey.api.web.dto.user.security.UserSecurityAttribute;
import com.github.nramc.dev.journey.api.web.exceptions.BusinessException;
import com.github.nramc.dev.journey.api.web.resources.rest.doc.RestDocCommonResponse;
import com.github.nramc.dev.journey.api.web.resources.rest.users.security.attributes.UserSecurityAttributeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.github.nramc.dev.journey.api.web.resources.Resources.MY_SECURITY_MFA;
import static com.github.nramc.dev.journey.api.web.resources.Resources.UPDATE_MY_ACCOUNT;

@RestController
@Slf4j
@RequiredArgsConstructor
public class UpdateUserResource {
    private final UserDetailsManager userDetailsManager;
    private final UserSecurityAttributeService attributeService;

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

        if (valid(mfaStatus, authUser)) {
            AuthUser updatedDetails = authUser.toBuilder().mfaEnabled(mfaStatus.status()).build();
            userDetailsManager.updateUser(updatedDetails);
        } else {
            throw new BusinessException("Unable to enable mf due to absence of security attributes", "mfa.invalid.attributes");
        }
    }

    private boolean valid(MfaStatus mfaStatus, AuthUser authUser) {
        return !mfaStatus.status() || isMfaAttributeAvailable(authUser);

    }

    private boolean isMfaAttributeAvailable(AuthUser authUser) {
        List<UserSecurityAttribute> attributes = attributeService.getAllAvailableUserSecurityAttributes(authUser);
        return CollectionUtils.emptyIfNull(attributes).stream().anyMatch(attribute -> attribute.enabled() && attribute.verified());
    }

    private AuthUser updateWith(AuthUser authUser, UpdateUserRequest request) {
        return authUser.toBuilder()
                .name(request.name())
                .build();
    }
}
