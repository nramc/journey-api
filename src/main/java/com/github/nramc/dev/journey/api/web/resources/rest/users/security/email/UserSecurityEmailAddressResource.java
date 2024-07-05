package com.github.nramc.dev.journey.api.web.resources.rest.users.security.email;

import com.github.nramc.dev.journey.api.models.core.SecurityAttributeType;
import com.github.nramc.dev.journey.api.repository.auth.AuthUser;
import com.github.nramc.dev.journey.api.repository.auth.UserSecurityAttributesEntity;
import com.github.nramc.dev.journey.api.web.resources.rest.doc.RestDocCommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

import static com.github.nramc.dev.journey.api.web.resources.Resources.UPDATE_MY_EMAIL_ADDRESS;

@RestController
@Slf4j
@RequiredArgsConstructor
@Tag(name = "Manage User Security Email Address Resource")
public class UserSecurityEmailAddressResource {
    private final UserDetailsService userDetailsService;

    @Operation(summary = "Add/Update Security Email Address to my account")
    @RestDocCommonResponse
    @ApiResponse(responseCode = "200", description = "Security Email Address added/updated successfully")
    @PostMapping(value = UPDATE_MY_EMAIL_ADDRESS, consumes = MediaType.APPLICATION_JSON_VALUE)
    public void addEmailAddress(@RequestBody @Valid UpdateEmailAddressRequest emailAddressRequest, Authentication authentication) {
        AuthUser authUser = (AuthUser) userDetailsService.loadUserByUsername(authentication.getName());


        UserSecurityAttributesEntity entity = UserSecurityAttributesEntity.builder()
                .type(SecurityAttributeType.EMAIL_ADDRESS)
                .userId(authUser.getId().toHexString())
                .username(authUser.getUsername())
                .creationDate(LocalDate.now())
                .lastUpdateDate(LocalDate.now())
                .enabled(true)
                .verified(false)
                .value(emailAddressRequest.emailAddress())
                .build();

    }
}
