package com.github.nramc.dev.journey.api.web.resources.rest.auth.login;

import com.github.nramc.dev.journey.api.repository.auth.AuthUser;
import com.github.nramc.dev.journey.api.services.AuthUserDetailsService;
import com.github.nramc.dev.journey.api.web.resources.rest.auth.jwt.JwtGenerator;
import com.github.nramc.dev.journey.api.web.resources.rest.auth.jwt.LoginResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

import static com.github.nramc.dev.journey.api.web.resources.Resources.GUEST_LOGIN;

@RestController
@RequiredArgsConstructor
@CrossOrigin(value = "*")
public class GuestLoginResource {
    private final JwtGenerator jwtGenerator;
    private final AuthUserDetailsService authUserDetailsService;

    @PostMapping(GUEST_LOGIN)
    public LoginResponse guestLogin() {
        AuthUser userDetails = authUserDetailsService.getGuestUserDetails();
        Jwt jwt = jwtGenerator.generate(userDetails);
        return new LoginResponse(
                jwt.getTokenValue(),
                jwt.getExpiresAt(),
                Set.of(jwt.getClaimAsString("scope").split(" ")),
                userDetails.getName()
        );
    }
}
