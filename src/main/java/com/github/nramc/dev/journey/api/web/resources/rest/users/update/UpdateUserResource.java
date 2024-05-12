package com.github.nramc.dev.journey.api.web.resources.rest.users.update;

import com.github.nramc.dev.journey.api.repository.auth.AuthUser;
import com.github.nramc.dev.journey.api.services.AuthUserDetailsService;
import com.github.nramc.dev.journey.api.web.resources.rest.doc.RestDocCommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static com.github.nramc.dev.journey.api.web.resources.Resources.UPDATE_MY_ACCOUNT;

@RestController
@Slf4j
@RequiredArgsConstructor
@Tag(name = "Update User Details Resource")
public class UpdateUserResource {
    private final AuthUserDetailsService userDetailsService;

    @Operation(summary = "Update my account details")
    @RestDocCommonResponse
    @ApiResponse(responseCode = "200", description = "User details updated successfully")
    @PostMapping(value = UPDATE_MY_ACCOUNT, consumes = MediaType.APPLICATION_JSON_VALUE)
    public void change(@RequestBody @Valid UpdateUserRequest updateUserRequest, Authentication authentication) {
        AuthUser authUser = (AuthUser) userDetailsService.loadUserByUsername(authentication.getName());
        AuthUser updatedDetails = updateWith(authUser, updateUserRequest);
        userDetailsService.updateUser(updatedDetails);
    }

    private AuthUser updateWith(AuthUser authUser, UpdateUserRequest request) {
        return authUser.toBuilder()
                .name(request.name())
                .emailAddress(request.emailAddress())
                .build();
    }
}
