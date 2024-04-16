package com.github.nramc.dev.journey.api.web.resources.rest.auth.jwt;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

import static com.github.nramc.dev.journey.api.web.resources.Resources.LOGIN;

@RestController
@RequiredArgsConstructor
@CrossOrigin(value = "*")
public class JwtTokenResource {
    private final JwtGenerator jwtGenerator;

    @PostMapping(LOGIN)
    public LoginResponse token(Authentication authentication) {
        Jwt jwt = jwtGenerator.generate(authentication);
        return new LoginResponse(
                jwt.getTokenValue(),
                jwt.getExpiresAt(),
                Set.of(jwt.getClaimAsString("scope").split(" "))
        );
    }


}
