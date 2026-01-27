package com.github.nramc.dev.journey.api.web.resources.rest.auth.login;

import com.github.nramc.dev.journey.api.repository.user.AuthUser;
import com.github.nramc.dev.journey.api.repository.user.AuthUserDetailsService;
import com.github.nramc.dev.journey.api.web.resources.rest.auth.dto.LoginResponse;
import com.github.nramc.dev.journey.api.web.resources.rest.auth.provider.JwtResponseProvider;
import com.github.nramc.dev.journey.api.web.resources.rest.doc.RestDocCommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.github.nramc.dev.journey.api.web.resources.Resources.GUEST_LOGIN;

@RestController
@RequiredArgsConstructor
@Tag(name = "Guest Login", description = "Login as Guest user")
public class GuestLoginResource {
    private final JwtResponseProvider jwtResponseProvider;
    private final AuthUserDetailsService authUserDetailsService;

    @Operation(summary = "login as Guest user without credential and retrieve JWT token")
    @RestDocCommonResponse
    @ApiResponses(@ApiResponse(responseCode = "200", description = "Authentication successful and return JWT", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = LoginResponse.class))}))
    @PostMapping(GUEST_LOGIN)
    public LoginResponse guestLogin() {
        AuthUser userDetails = authUserDetailsService.getGuestUserDetails();
        return jwtResponseProvider.jwtResponse(userDetails);
    }
}
