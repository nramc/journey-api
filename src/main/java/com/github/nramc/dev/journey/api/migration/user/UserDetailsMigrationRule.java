package com.github.nramc.dev.journey.api.migration.user;

import com.github.nramc.dev.journey.api.core.domain.EmailAddress;
import com.github.nramc.dev.journey.api.repository.user.AuthUser;
import com.github.nramc.dev.journey.api.web.resources.rest.auth.AuthUserDetailsService;
import com.github.nramc.dev.journey.api.web.resources.rest.users.security.attributes.email.UserSecurityEmailAddressAttributeService;
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
