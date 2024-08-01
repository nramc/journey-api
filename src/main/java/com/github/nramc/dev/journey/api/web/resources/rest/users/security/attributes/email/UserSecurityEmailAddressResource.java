package com.github.nramc.dev.journey.api.web.resources.rest.users.security.attributes.email;

import com.github.nramc.dev.journey.api.core.security.attributes.EmailAddress;
import com.github.nramc.dev.journey.api.repository.auth.AuthUser;
import com.github.nramc.dev.journey.api.web.dto.user.security.UserSecurityAttribute;
import com.github.nramc.dev.journey.api.web.dto.user.security.UserSecurityAttributeConverter;
import com.github.nramc.dev.journey.api.web.resources.rest.doc.RestDocCommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

import static com.github.nramc.dev.journey.api.web.resources.Resources.MY_SECURITY_ATTRIBUTE_EMAIL;

@RestController
@Slf4j
@RequiredArgsConstructor
@Tag(name = "My Account Security - Email Address Settings")
public class UserSecurityEmailAddressResource {
    private final UserDetailsService userDetailsService;
    private final UserSecurityEmailAddressAttributeService userSecurityEmailAddressAttributeService;

    @Operation(summary = "Add/Update Security Email Address to my account")
    @RestDocCommonResponse
    @ApiResponse(responseCode = "200", description = "Security Email Address added/updated successfully")
    @PostMapping(value = MY_SECURITY_ATTRIBUTE_EMAIL, consumes = MediaType.APPLICATION_JSON_VALUE)
    public UserSecurityAttribute updateEmailAddress(@RequestBody @Valid UpdateEmailAddressRequest emailAddressRequest,
                                                    Authentication authentication) {
        AuthUser authUser = (AuthUser) userDetailsService.loadUserByUsername(authentication.getName());

        UserSecurityAttribute securityAttribute = userSecurityEmailAddressAttributeService.saveSecurityEmailAddress(
                authUser, EmailAddress.valueOf(emailAddressRequest.emailAddress()));
        return UserSecurityAttributeConverter.toResponse(securityAttribute);
    }

    @Operation(summary = "Get my Security Email Address")
    @RestDocCommonResponse
    @ApiResponse(responseCode = "200", description = "Fetch Security Email Address for my account")
    @GetMapping(value = MY_SECURITY_ATTRIBUTE_EMAIL, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserSecurityAttribute> getEmailAddress(Authentication authentication) {
        AuthUser authUser = (AuthUser) userDetailsService.loadUserByUsername(authentication.getName());

        Optional<UserSecurityAttribute> emailAttributeIfExists = userSecurityEmailAddressAttributeService.provideEmailAttributeIfExists(authUser);
        return emailAttributeIfExists.map(UserSecurityAttributeConverter::toResponse)
                .map(ResponseEntity::ok).orElse(ResponseEntity.ok().build());
    }
}
