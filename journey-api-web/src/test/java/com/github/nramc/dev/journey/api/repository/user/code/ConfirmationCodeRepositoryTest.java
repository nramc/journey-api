package com.github.nramc.dev.journey.api.repository.user.code;

import com.github.nramc.dev.journey.api.config.TestContainersConfiguration;
import com.github.nramc.dev.journey.api.core.domain.user.ConfirmationCodeType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.mongodb.test.autoconfigure.DataMongoTest;
import org.springframework.context.annotation.Import;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
@Import(TestContainersConfiguration.class)
class ConfirmationCodeRepositoryTest {
    private static final String EMAIL_CODE = "123456";
    private static final String USERNAME = "test-user@example.com";
    private static final ConfirmationCodeEntity EMAIL_CODE_ENTITY = ConfirmationCodeEntity.builder()
            .id("ecc76991-0137-4152-b3b2-efce70a37ed0")
            .isActive(true)
            .username(USERNAME)
            .type(ConfirmationCodeType.EMAIL_CODE)
            .code(EMAIL_CODE)
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
