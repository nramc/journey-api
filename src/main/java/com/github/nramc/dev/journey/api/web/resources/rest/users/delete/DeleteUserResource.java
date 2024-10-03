package com.github.nramc.dev.journey.api.web.resources.rest.users.delete;

import com.github.nramc.dev.journey.api.repository.user.AuthUser;
import com.github.nramc.dev.journey.api.web.resources.rest.doc.RestDocCommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import static com.github.nramc.dev.journey.api.web.resources.Resources.DELETE_MY_ACCOUNT;
import static com.github.nramc.dev.journey.api.web.resources.Resources.DELETE_USER_BY_USERNAME;

@RestController
@Slf4j
@RequiredArgsConstructor
class DeleteUserResource {
    private final UserDetailsManager userDetailsManager;

    @Operation(summary = "Delete application user", tags = {"Administrator Features"})
    @RestDocCommonResponse
    @DeleteMapping(DELETE_USER_BY_USERNAME)
    @ApiResponse(responseCode = "200", description = "Application User deleted permanently")
    void deleteByUsername(@PathVariable String username) {
        userDetailsManager.deleteUser(username);
    }

    @Operation(summary = "Delete my account", tags = {"My Account Features"})
    @ApiResponse(responseCode = "200", description = "Application User inactivated and soon will be removed from system")
    @RestDocCommonResponse
    @DeleteMapping(DELETE_MY_ACCOUNT)
    void deleteMyAccount(Authentication authentication) {
        AuthUser userDetails = (AuthUser) userDetailsManager.loadUserByUsername(authentication.getName());
        userDetails.setEnabled(false);
        userDetailsManager.updateUser(userDetails);
    }
}
