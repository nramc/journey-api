package com.github.nramc.dev.journey.api.core.jwt;

import com.github.nramc.dev.journey.api.config.security.JwtProperties;
import com.github.nramc.dev.journey.api.repository.auth.AuthUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;

import java.time.Instant;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class JwtGenerator {
    private final JwtProperties jwtProperties;
    private final JwtEncoder jwtEncoder;

    public Jwt generate(AuthUser user) {
        Instant now = Instant.now();
        String scope = user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(jwtProperties.issuer())
                .issuedAt(now)
                .expiresAt(now.plus(jwtProperties.ttl()))
                .subject(user.getUsername())
                .claim("scope", scope)
                .build();
        return jwtEncoder.encode(JwtEncoderParameters.from(claims));
    }


}
