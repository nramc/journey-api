package com.github.nramc.dev.journey.api.repository.security;

import com.github.nramc.dev.journey.api.config.TestContainersConfiguration;
import com.github.nramc.dev.journey.api.models.core.ConfirmationCodeType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;

import java.time.LocalDateTime;
import java.util.List;

import static com.github.nramc.dev.journey.api.web.resources.rest.users.UsersData.EMAIL_ATTRIBUTE;
import static com.github.nramc.dev.journey.api.web.resources.rest.users.security.confirmationcode.ConfirmationUseCase.VERIFY_EMAIL_ADDRESS;
import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
@Import(TestContainersConfiguration.class)
class ConfirmationCodeRepositoryTest {
    private static final String EMAIL_CODE = "123456";
    private static final String USERNAME = "test-username";
    private static final ConfirmationCodeEntity EMAIL_CODE_ENTITY = ConfirmationCodeEntity.builder()
            .id("ecc76991-0137-4152-b3b2-efce70a37ed0")
            .isActive(true)
            .username(USERNAME)
            .type(ConfirmationCodeType.EMAIL_CODE)
            .code(EMAIL_CODE)
            .receiver(EMAIL_ATTRIBUTE.value())
            .useCase(VERIFY_EMAIL_ADDRESS)
            .createdAt(LocalDateTime.now())
            .build();
    @Autowired
    private ConfirmationCodeRepository confirmationCodeRepository;

    @BeforeEach
    void setUp() {
        confirmationCodeRepository.deleteAll();
    }

    @Test
    void context() {
        assertThat(confirmationCodeRepository).isNotNull();
    }

    @Test
    void findAllByUsername_whenCodeExists_thenShouldReturnValue() {
        confirmationCodeRepository.save(EMAIL_CODE_ENTITY);

        List<ConfirmationCodeEntity> entities = confirmationCodeRepository.findAllByUsername(USERNAME);
        assertThat(entities).isNotNull().hasSize(1);
    }

    @Test
    void findAllByUsername_whenCodeNotExists_thenShouldReturnEmptyList() {
        List<ConfirmationCodeEntity> entities = confirmationCodeRepository.findAllByUsername(USERNAME);
        assertThat(entities).isNotNull().isEmpty();
    }


}