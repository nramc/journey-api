package com.github.nramc.dev.journey.api.web.resources.rest.auth.login;

import com.github.nramc.dev.journey.api.core.domain.user.UserSecurityAttribute;
import com.github.nramc.dev.journey.api.core.domain.user.UserSecurityAttributeType;
import com.github.nramc.dev.journey.api.core.jwt.JwtGenerator;
import com.github.nramc.dev.journey.api.repository.user.AuthUser;
import com.github.nramc.dev.journey.api.repository.user.attributes.UserSecurityAttributeService;
import com.github.nramc.dev.journey.api.web.resources.rest.auth.dto.LoginResponse;
import com.github.nramc.dev.journey.api.web.resources.rest.doc.RestDocCommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.github.nramc.dev.journey.api.web.resources.Resources.LOGIN;

@RestController
@RequiredArgsConstructor
@Tag(name = "Login", description = "Login as application user")
public class LoginResource {
    private final JwtGenerator jwtGenerator;
    private final UserSecurityAttributeService attributeService;
    private final UserDetailsService userDetailsService;

    @Operation(summary = "login with credentials and retrieve JWT token")
    @RestDocCommonResponse
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Authentication successful and return JWT",
            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = LoginResponse.class))})})
    @PostMapping(LOGIN)
    public LoginResponse login(@AuthenticationPrincipal AuthUser userDetails) {
        userDetails = (AuthUser) userDetailsService.loadUserByUsername(userDetails.getUsername());
        if (userDetails.isMfaEnabled()) {
            return additionalFactorResponse(userDetails);
        } else {
            return jwtResponse(userDetails);
        }
    }

    private LoginResponse additionalFactorResponse(AuthUser userDetails) {
        List<UserSecurityAttributeType> defaultSecurityAttributeTypes = List.of(UserSecurityAttributeType.EMAIL_ADDRESS);

        Set<UserSecurityAttributeType> securityAttributes = CollectionUtils.emptyIfNull(attributeService.getAllAvailableUserSecurityAttributes(userDetails))
                .stream().map(UserSecurityAttribute::type)
                .collect(Collectors.toSet());

        Set<UserSecurityAttributeType> availableAttributes = Stream.concat(defaultSecurityAttributeTypes.stream(), securityAttributes.stream())
                .collect(Collectors.toSet());
        return LoginResponse.builder()
                .additionalFactorRequired(true)
                .securityAttributes(availableAttributes)
                .build();
    }

    private LoginResponse jwtResponse(AuthUser userDetails) {
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
