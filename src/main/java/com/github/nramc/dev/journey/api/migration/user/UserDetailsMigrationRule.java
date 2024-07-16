package com.github.nramc.dev.journey.api.migration.user;

import com.github.nramc.dev.journey.api.models.core.EmailAddress;
import com.github.nramc.dev.journey.api.repository.auth.AuthUser;
import com.github.nramc.dev.journey.api.services.AuthUserDetailsService;
import com.github.nramc.dev.journey.api.web.resources.rest.users.security.email.UserSecurityEmailAddressAttributeService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UserDetailsMigrationRule implements Runnable {
    private final AuthUserDetailsService userDetailsService;
    private final UserSecurityEmailAddressAttributeService attributeService;

    @Override
    public void run() {
        AuthUser guestUserDetails = userDetailsService.getGuestUserDetails();
        attributeService.saveSecurityEmailAddress(guestUserDetails, EmailAddress.valueOf("example@mail.com"));
    }
}
