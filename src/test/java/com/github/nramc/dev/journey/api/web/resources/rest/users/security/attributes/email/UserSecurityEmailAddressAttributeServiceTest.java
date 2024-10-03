package com.github.nramc.dev.journey.api.web.resources.rest.users.security.attributes.email;

import com.github.nramc.dev.journey.api.config.TestContainersConfiguration;
import com.github.nramc.dev.journey.api.core.domain.EmailAddress;
import com.github.nramc.dev.journey.api.core.domain.user.UserSecurityAttributeType;
import com.github.nramc.dev.journey.api.repository.user.AuthUser;
import com.github.nramc.dev.journey.api.repository.user.UserSecurityAttributeEntity;
import com.github.nramc.dev.journey.api.repository.user.UserSecurityAttributesRepository;
import com.github.nramc.dev.journey.api.web.dto.user.security.UserSecurityAttribute;
import com.github.nramc.dev.journey.api.web.resources.rest.users.security.utils.SecurityAttributesUtils;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestContainersConfiguration.class)
@ActiveProfiles({"test"})
@AutoConfigureMockMvc
class UserSecurityEmailAddressAttributeServiceTest {
    private static final AuthUser USER = AuthUser.builder()
            .username("test-user")
            .id(ObjectId.get())
            .build();
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    UserSecurityAttributesRepository userSecurityAttributesRepository;
    private UserSecurityEmailAddressAttributeService service;

    @BeforeEach
    void setup() {
        service = new UserSecurityEmailAddressAttributeService(userSecurityAttributesRepository);
        userSecurityAttributesRepository.deleteAll();
    }

    @Test
    void context() {
        assertThat(mockMvc).isNotNull();
    }

    @Test
    void provide_whenNoAttributesExists_shouldReturnEmptyList() {
        List<UserSecurityAttributeEntity> attributesEntities = service.provide(USER);
        assertThat(attributesEntities).isNotNull().isEmpty();
    }

    @Test
    void provide_whenAlreadyAttributesExists_shouldReturnList() {
        UserSecurityAttributeEntity emailAddressAttribute = SecurityAttributesUtils.newEmailAttribute(USER).toBuilder()
                .type(UserSecurityAttributeType.EMAIL_ADDRESS)
                .value("test-email-addressgmail.com")
                .build();

        userSecurityAttributesRepository.save(emailAddressAttribute);

        List<UserSecurityAttributeEntity> attributesEntities = service.provide(USER);
        assertThat(attributesEntities).isNotNull().isNotEmpty().hasSize(1);
    }

    @Test
    void provideEmailAttributeIfExists_whenAlreadyAttributesExists_shouldReturnIt() {
        UserSecurityAttributeEntity emailAddressAttribute = SecurityAttributesUtils.newEmailAttribute(USER).toBuilder()
                .value("test-email-address@gmail.com")
                .build();

        userSecurityAttributesRepository.save(emailAddressAttribute);

        Optional<UserSecurityAttribute> entityOptional = service.provideEmailAttributeIfExists(USER);
        assertThat(entityOptional).isNotEmpty().get()
                .satisfies(attribute -> assertThat(attribute.type()).isEqualTo(UserSecurityAttributeType.EMAIL_ADDRESS))
                .satisfies(attribute -> assertThat(attribute.value()).isEqualTo(emailAddressAttribute.getValue()));
    }

    @Test
    void provideEmailAttributeIfExists_whenAttributesNotExists_shouldReturnEmptyOptional() {
        Optional<UserSecurityAttribute> entityOptional = service.provideEmailAttributeIfExists(USER);
        assertThat(entityOptional).isEmpty();
    }

    @Test
    void saveSecurityEmailAddress_shouldSaveAttribute() {
        EmailAddress emailAddress = EmailAddress.valueOf("example.email.address@gmail.com");
        UserSecurityAttribute saved = service.saveSecurityEmailAddress(USER, emailAddress);
        assertThat(saved).isNotNull()
                .satisfies(attribute -> assertThat(attribute.value()).isEqualTo(emailAddress.value()))
                .satisfies(attribute -> assertThat(attribute.type()).isEqualTo(UserSecurityAttributeType.EMAIL_ADDRESS))
                .satisfies(attribute -> assertThat(attribute.enabled()).isTrue())
                .satisfies(attribute -> assertThat(attribute.verified()).isFalse())
                .satisfies(attribute -> assertThat(attribute.creationDate()).isNotNull())
                .satisfies(attribute -> assertThat(attribute.lastUpdateDate()).isNotNull());
    }

}
