package com.github.nramc.dev.journey.api.web.resources.rest.users;

import com.github.nramc.dev.journey.api.core.user.security.Role;
import com.github.nramc.dev.journey.api.repository.auth.AuthUser;
import com.github.nramc.dev.journey.api.web.dto.user.security.UserSecurityAttribute;
import lombok.experimental.UtilityClass;
import org.bson.types.ObjectId;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

import static com.github.nramc.dev.journey.api.core.user.security.Role.AUTHENTICATED_USER;
import static com.github.nramc.dev.journey.api.core.security.attributes.SecurityAttributeType.EMAIL_ADDRESS;
import static com.github.nramc.dev.journey.api.core.security.attributes.SecurityAttributeType.TOTP;

@UtilityClass
public class UsersData {
    public static final AuthUser AUTH_USER = AuthUser.builder()
            .id(new ObjectId("665b1b94bd24ff59695e1d04"))
            .username("test-user")
            .password("test")
            .name("Test User")
            .roles(Set.of(AUTHENTICATED_USER))
            .enabled(true)
            .build();
    public static final UserSecurityAttribute EMAIL_ATTRIBUTE = UserSecurityAttribute.builder()
            .value("test.user@gmail.com")
            .type(EMAIL_ADDRESS)
            .verified(false)
            .enabled(true)
            .creationDate(LocalDate.now())
            .lastUpdateDate(LocalDate.now())
            .build();
    public static final UserSecurityAttribute TOTP_ATTRIBUTE = UserSecurityAttribute.builder()
            .value("SECRET_KEY")
            .type(TOTP)
            .verified(false)
            .enabled(true)
            .creationDate(LocalDate.now())
            .lastUpdateDate(LocalDate.now())
            .build();
    public static final AuthUser MFA_USER = AuthUser.builder()
            .username("mfa-user")
            .password("test")
            .roles(Set.of(Role.AUTHENTICATED_USER))
            .name("Multi Factor User")
            .enabled(true)
            .mfaEnabled(true)
            .createdDate(LocalDateTime.now())
            .build();
}
