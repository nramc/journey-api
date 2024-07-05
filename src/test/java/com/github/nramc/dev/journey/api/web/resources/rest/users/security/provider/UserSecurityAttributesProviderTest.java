package com.github.nramc.dev.journey.api.web.resources.rest.users.security.provider;

import com.github.nramc.dev.journey.api.models.core.SecurityAttributeType;
import com.github.nramc.dev.journey.api.repository.auth.AuthUser;
import com.github.nramc.dev.journey.api.repository.auth.UserSecurityAttributesEntity;
import com.github.nramc.dev.journey.api.repository.auth.UserSecurityAttributesRepository;
import com.github.nramc.dev.journey.api.web.resources.rest.users.security.utils.SecurityAttributesUtils;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles({"test"})
@AutoConfigureMockMvc
class UserSecurityAttributesProviderTest {
    private final static AuthUser USER = AuthUser.builder()
            .username("test-user")
            .id(ObjectId.get())
            .build();
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    UserSecurityAttributesRepository userSecurityAttributesRepository;
    @Container
    @ServiceConnection
    static MongoDBContainer mongoDBContainer = new MongoDBContainer(DockerImageName.parse("mongo:latest"))
            .withExposedPorts(27017);

    private UserSecurityAttributesProvider provider;

    @BeforeEach
    void setup() {
        provider = new UserSecurityAttributesProvider(userSecurityAttributesRepository);
        userSecurityAttributesRepository.deleteAll();
    }

    @Test
    void context() {
        assertThat(mockMvc).isNotNull();
    }

    @Test
    void provide_whenNoAttributesExists_shouldReturnEmptyList() {
        List<UserSecurityAttributesEntity> attributesEntities = provider.provide(USER);
        assertThat(attributesEntities).isNotNull().isEmpty();
    }

    @Test
    void provide_whenAlreadyAttributesExists_shouldReturnList() {
        UserSecurityAttributesEntity emailAddressAttribute = SecurityAttributesUtils.newEmailAttribute(USER).toBuilder()
                .type(SecurityAttributeType.EMAIL_ADDRESS)
                .value("test-email-addressgmail.com")
                .build();

        userSecurityAttributesRepository.save(emailAddressAttribute);

        List<UserSecurityAttributesEntity> attributesEntities = provider.provide(USER);
        assertThat(attributesEntities).isNotNull().isNotEmpty().hasSize(1);
    }

    @Test
    void provideEmailAttributeIfExists_whenAlreadyAttributesExists_shouldReturnIt() {
        UserSecurityAttributesEntity emailAddressAttribute = SecurityAttributesUtils.newEmailAttribute(USER).toBuilder()
                .value("test-email-address@gmail.com")
                .build();

        userSecurityAttributesRepository.save(emailAddressAttribute);

        Optional<UserSecurityAttributesEntity> entityOptional = provider.provideEmailAttributeIfExists(USER);
        assertThat(entityOptional).isNotEmpty().get()
                .satisfies(attribute -> assertThat(attribute.getType()).isEqualTo(SecurityAttributeType.EMAIL_ADDRESS))
                .satisfies(attribute -> assertThat(attribute.getValue()).isEqualTo(emailAddressAttribute.getValue()));
    }

    @Test
    void provideEmailAttributeIfExists_whenAttributesNotExists_shouldReturnEmptyOptional() {
        Optional<UserSecurityAttributesEntity> entityOptional = provider.provideEmailAttributeIfExists(USER);
        assertThat(entityOptional).isEmpty();
    }

}