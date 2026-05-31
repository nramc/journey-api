package com.github.nramc.dev.journey.api.account.web.users;

import com.github.nramc.dev.journey.api.account.repository.AuthUser;
import com.github.nramc.dev.journey.api.infrastructure.security.WithMockAdministratorUser;
import com.github.nramc.dev.journey.api.infrastructure.security.WithMockAuthenticatedUser;
import com.github.nramc.dev.journey.api.infrastructure.security.WithMockGuestUser;
import com.github.nramc.dev.journey.api.shared.domain.AppUser;
import com.github.nramc.dev.journey.api.shared.domain.user.security.UserSecurityAttribute;
import org.bson.types.ObjectId;

import java.time.LocalDate;

import static com.github.nramc.dev.journey.api.shared.domain.user.security.UserSecurityAttributeType.TOTP;

public final class UsersData {
    public static final AuthUser AUTHENTICATED_USER = AuthUser.builder()
            .id(new ObjectId("665b1b94bd24ff59695e1d04"))
            .username(WithMockAuthenticatedUser.USERNAME)
            .password(WithMockAuthenticatedUser.PASSWORD)
            .name(WithMockAuthenticatedUser.USER_DETAILS.name())
            .roles(WithMockAuthenticatedUser.USER_DETAILS.roles())
            .enabled(true)
            .build();
    public static final AppUser AUTHENTICATED_APP_USER = AppUser.builder()
            .username(WithMockAuthenticatedUser.USERNAME)
            .password(WithMockAuthenticatedUser.PASSWORD)
            .roles(WithMockAuthenticatedUser.USER_DETAILS.roles())
            .name(WithMockAuthenticatedUser.USER_DETAILS.name())
            .enabled(true)
            .mfaEnabled(false)
            .build();
    public static final AuthUser GUEST_USER = AuthUser.builder()
            .id(new ObjectId("665b1b94bd24ff59695e1d01"))
            .username(WithMockGuestUser.USERNAME)
            .password(WithMockGuestUser.PASSWORD)
            .name(WithMockGuestUser.USER_DETAILS.name())
            .roles(WithMockGuestUser.USER_DETAILS.roles())
            .enabled(true)
            .build();
    public static final AuthUser ADMINISTRATOR_USER = AuthUser.builder()
            .username(WithMockAdministratorUser.USERNAME)
            .password(WithMockAdministratorUser.PASSWORD)
            .roles(WithMockAdministratorUser.USER_DETAILS.roles())
            .name(WithMockAdministratorUser.USER_DETAILS.name())
            .enabled(true)
            .build();

    public static final UserSecurityAttribute TOTP_ATTRIBUTE = UserSecurityAttribute.builder()
            .value("SECRET_KEY")
            .type(TOTP)
            .verified(false)
            .enabled(true)
            .creationDate(LocalDate.now())
            .lastUpdateDate(LocalDate.now())
            .build();

    private UsersData() {
        throw new IllegalStateException("Utility class");
    }
}
