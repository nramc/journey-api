package com.github.nramc.dev.journey.api.web.resources.rest.auth.login;

import com.github.nramc.dev.journey.api.repository.auth.AuthUser;
import com.github.nramc.dev.journey.api.web.resources.rest.auth.jwt.JwtGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

import static com.github.nramc.dev.journey.api.web.resources.Resources.LOGIN;

@RestController
@RequiredArgsConstructor
public class LoginResource {
    private final JwtGenerator jwtGenerator;

    @PostMapping(LOGIN)
    public LoginResponse login(@AuthenticationPrincipal AuthUser userDetails) {
        Jwt jwt = jwtGenerator.generate(userDetails);
        return new LoginResponse(
                jwt.getTokenValue(),
                jwt.getExpiresAt(),
                Set.of(jwt.getClaimAsString("scope").split(" ")),
                userDetails.getName()
        );
    }


}
