package com.github.nramc.dev.journey.api.web.resources.rest.users.security.email;

import com.github.nramc.dev.journey.api.models.core.EmailAddress;
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

import static com.github.nramc.dev.journey.api.web.resources.Resources.GET_MY_EMAIL_ADDRESS;
import static com.github.nramc.dev.journey.api.web.resources.Resources.UPDATE_MY_EMAIL_ADDRESS;

@RestController
@Slf4j
@RequiredArgsConstructor
@Tag(name = "Manage User Security Email Address Resource")
public class UserSecurityEmailAddressResource {
    private final UserDetailsService userDetailsService;
    private final UserSecurityEmailAddressAttributeService userSecurityEmailAddressAttributeService;

    @Operation(summary = "Add/Update Security Email Address to my account")
    @RestDocCommonResponse
    @ApiResponse(responseCode = "200", description = "Security Email Address added/updated successfully")
    @PostMapping(value = UPDATE_MY_EMAIL_ADDRESS, consumes = MediaType.APPLICATION_JSON_VALUE)
    public UserSecurityAttribute addEmailAddress(@RequestBody @Valid UpdateEmailAddressRequest emailAddressRequest,
                                                 Authentication authentication) {
        AuthUser authUser = (AuthUser) userDetailsService.loadUserByUsername(authentication.getName());

        return userSecurityEmailAddressAttributeService.saveSecurityEmailAddress(
                authUser, EmailAddress.valueOf(emailAddressRequest.emailAddress()));
    }

    @Operation(summary = "Get my Security Email Address")
    @RestDocCommonResponse
    @ApiResponse(responseCode = "200", description = "Fetch Security Email Address for my account")
    @GetMapping(value = GET_MY_EMAIL_ADDRESS, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserSecurityAttribute> getEmailAddress(Authentication authentication) {
        AuthUser authUser = (AuthUser) userDetailsService.loadUserByUsername(authentication.getName());

        return userSecurityEmailAddressAttributeService.provideEmailAttributeIfExists(authUser)
                .map(UserSecurityAttributeConverter::toModel)
                .map(ResponseEntity::ok).orElse(ResponseEntity.ok().build());
    }
}
