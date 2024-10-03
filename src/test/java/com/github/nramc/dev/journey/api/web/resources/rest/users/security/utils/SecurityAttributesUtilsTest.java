package com.github.nramc.dev.journey.api.web.resources.rest.users.security.utils;

import com.github.nramc.dev.journey.api.core.domain.user.UserSecurityAttributeType;
import com.github.nramc.dev.journey.api.repository.user.AuthUser;
import com.github.nramc.dev.journey.api.repository.user.UserSecurityAttributeEntity;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SecurityAttributesUtilsTest {
    private static final AuthUser USER = AuthUser.builder()
            .username("test-user")
            .id(ObjectId.get())
            .build();

    @Test
    void newEmailAttribute_shouldHaveExpectedValues() {
        UserSecurityAttributeEntity newEmailAttribute = SecurityAttributesUtils.newEmailAttribute(USER);
        assertThat(newEmailAttribute).isNotNull()
                .satisfies(newed -> assertThat(newed.getUsername()).isEqualTo(USER.getUsername()))
                .satisfies(newed -> assertThat(newed.getUserId()).isEqualTo(USER.getId().toHexString()))
                .satisfies(newed -> assertThat(newed.getType()).isEqualTo(UserSecurityAttributeType.EMAIL_ADDRESS))
                .satisfies(newed -> assertThat(newed.isEnabled()).isTrue())
                .satisfies(newed -> assertThat(newed.isVerified()).isFalse())
                .satisfies(newed -> assertThat(newed.getCreationDate()).isNotNull())
                .satisfies(newed -> assertThat(newed.getLastUpdateDate()).isNotNull())
                .satisfies(newed -> assertThat(newed.getId()).isNull())
                .satisfies(newed -> assertThat(newed.getValue()).isNull())
        ;
    }

}
