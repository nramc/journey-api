package com.github.nramc.dev.journey.api.web.resources.rest.users;

import com.github.nramc.dev.journey.api.models.core.SecurityAttributeType;
import com.github.nramc.dev.journey.api.repository.auth.AuthUser;
import com.github.nramc.dev.journey.api.web.dto.user.security.UserSecurityAttribute;
import lombok.experimental.UtilityClass;
import org.bson.types.ObjectId;

import java.time.LocalDate;
import java.util.Set;

import static com.github.nramc.dev.journey.api.security.Role.AUTHENTICATED_USER;

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
            .type(SecurityAttributeType.EMAIL_ADDRESS)
            .verified(false)
            .enabled(true)
            .creationDate(LocalDate.now())
            .lastUpdateDate(LocalDate.now())
            .build();
}
