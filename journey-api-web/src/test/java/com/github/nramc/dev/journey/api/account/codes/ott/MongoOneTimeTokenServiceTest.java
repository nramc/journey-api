package com.github.nramc.dev.journey.api.account.codes.ott;

import com.github.nramc.dev.journey.api.account.repository.code.ConfirmationCodeEntity;
import com.github.nramc.dev.journey.api.account.repository.code.ConfirmationCodeRepository;
import com.github.nramc.dev.journey.api.infrastructure.config.TestContainersConfiguration;
import com.github.nramc.dev.journey.api.shared.domain.user.security.ConfirmationCodeType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.mongodb.test.autoconfigure.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.ott.GenerateOneTimeTokenRequest;
import org.springframework.security.authentication.ott.OneTimeToken;
import org.springframework.security.authentication.ott.OneTimeTokenAuthenticationToken;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
@Import(TestContainersConfiguration.class)
class MongoOneTimeTokenServiceTest {
    private static final String USERNAME = "john@example.com";
    private static final Duration TOKEN_VALIDITY = Duration.ofMinutes(15);

    @Autowired
    private ConfirmationCodeRepository codeRepository;

    private MongoOneTimeTokenService oneTimeTokenService;

    @BeforeEach
    void setUp() {
        codeRepository.deleteAll();
        OttProperties ottProperties = OttProperties.builder()
                .tokenValidity(TOKEN_VALIDITY)
                .recoveryPath("/account/recover/login")
                .tokenQueryPram("token")
                .build();
        oneTimeTokenService = new MongoOneTimeTokenService(codeRepository, ottProperties);
    }

    @Test
    void generate_shouldPersistActiveOneTimeToken() {
        OneTimeToken token = oneTimeTokenService.generate(new GenerateOneTimeTokenRequest(USERNAME));

        assertThat(token.getUsername()).isEqualTo(USERNAME);
        assertThat(token.getTokenValue()).isNotBlank();
        assertThat(token.getExpiresAt()).isAfter(LocalDateTime.now(ZoneOffset.UTC).toInstant(ZoneOffset.UTC));

        var entities = codeRepository.findAllByUsername(USERNAME);
        assertThat(entities).hasSize(1);
        assertThat(entities.getFirst())
                .satisfies(entity -> assertThat(entity.getType()).isEqualTo(ConfirmationCodeType.ONE_TIME_TOKEN))
                .satisfies(entity -> assertThat(entity.getCode()).isEqualTo(token.getTokenValue()))
                .satisfies(entity -> assertThat(entity.isActive()).isTrue());
    }

    @Test
    void consume_whenTokenValidAndActive_shouldReturnTokenAndDeleteAllUserTokens() {
        OneTimeToken generated = oneTimeTokenService.generate(new GenerateOneTimeTokenRequest(USERNAME));

        OneTimeToken consumed = oneTimeTokenService.consume(new OneTimeTokenAuthenticationToken(generated.getTokenValue()));

        assertThat(consumed).isNotNull();
        assertThat(consumed.getTokenValue()).isEqualTo(generated.getTokenValue());
        assertThat(consumed.getUsername()).isEqualTo(USERNAME);
        assertThat(codeRepository.findAllByUsername(USERNAME)).isEmpty();
    }

    @Test
    void consume_whenTokenAlreadyConsumed_shouldReturnNull() {
        OneTimeToken generated = oneTimeTokenService.generate(new GenerateOneTimeTokenRequest(USERNAME));
        oneTimeTokenService.consume(new OneTimeTokenAuthenticationToken(generated.getTokenValue()));

        OneTimeToken consumedAgain = oneTimeTokenService.consume(new OneTimeTokenAuthenticationToken(generated.getTokenValue()));

        assertThat(consumedAgain).isNull();
    }

    @Test
    void consume_whenTokenExpired_shouldReturnNull() {
        ConfirmationCodeEntity expiredEntity = ConfirmationCodeEntity.builder()
                .id(UUID.randomUUID().toString())
                .type(ConfirmationCodeType.ONE_TIME_TOKEN)
                .code(UUID.randomUUID().toString())
                .username(USERNAME)
                .isActive(true)
                .createdAt(LocalDateTime.now(ZoneOffset.UTC).minus(TOKEN_VALIDITY.plusMinutes(1)))
                .build();
        codeRepository.save(expiredEntity);

        OneTimeToken consumed = oneTimeTokenService.consume(new OneTimeTokenAuthenticationToken(expiredEntity.getCode()));

        assertThat(consumed).isNull();
    }

    @Test
    void consume_whenTokenDoesNotExist_shouldReturnNull() {
        OneTimeToken consumed = oneTimeTokenService.consume(new OneTimeTokenAuthenticationToken("non-existent-token"));

        assertThat(consumed).isNull();
    }

    @Test
    void consume_whenTokenExistsButInactive_shouldReturnNull() {
        ConfirmationCodeEntity inactiveEntity = ConfirmationCodeEntity.builder()
                .id(UUID.randomUUID().toString())
                .type(ConfirmationCodeType.ONE_TIME_TOKEN)
                .code(UUID.randomUUID().toString())
                .username(USERNAME)
                .isActive(false)
                .createdAt(LocalDateTime.now(ZoneOffset.UTC))
                .build();
        codeRepository.save(inactiveEntity);

        OneTimeToken consumed = oneTimeTokenService.consume(new OneTimeTokenAuthenticationToken(inactiveEntity.getCode()));

        assertThat(consumed).isNull();
    }
}
