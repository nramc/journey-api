package com.github.nramc.dev.journey.api.web.resources.rest.jwt;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@CrossOrigin(value = "*")
public class JwtTokenResource {
    private final JwtGenerator jwtGenerator;

    @PostMapping("/token")
    public String token(Authentication authentication) {
        return jwtGenerator.generate(authentication).getTokenValue();
    }


}
