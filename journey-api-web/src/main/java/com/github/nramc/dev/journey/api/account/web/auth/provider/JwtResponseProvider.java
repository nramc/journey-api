package com.github.nramc.dev.journey.api.account.web.auth.provider;

import com.github.nramc.dev.journey.api.account.jwt.JwtGenerator;
import com.github.nramc.dev.journey.api.account.repository.AuthUser;
import com.github.nramc.dev.journey.api.account.web.auth.dto.LoginResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Set;

@Slf4j
@RequiredArgsConstructor
public class JwtResponseProvider {
    private final JwtGenerator jwtGenerator;

    public LoginResponse jwtResponse(AuthUser userDetails) {
        Jwt jwt = jwtGenerator.generate(userDetails);

        return LoginResponse.builder()
                .additionalFactorRequired(false)
                .token(jwt.getTokenValue())
                .expiredAt(jwt.getExpiresAt())
                .name(userDetails.getName())
                .authorities(Set.of(jwt.getClaimAsString("scope").split(" ")))
                .build();
    }
}
