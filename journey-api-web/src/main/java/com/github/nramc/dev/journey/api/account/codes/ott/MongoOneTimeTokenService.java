package com.github.nramc.dev.journey.api.account.codes.ott;

import com.github.nramc.dev.journey.api.account.repository.code.ConfirmationCodeEntity;
import com.github.nramc.dev.journey.api.account.repository.code.ConfirmationCodeRepository;
import com.github.nramc.dev.journey.api.shared.domain.user.security.ConfirmationCodeType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.security.authentication.ott.DefaultOneTimeToken;
import org.springframework.security.authentication.ott.GenerateOneTimeTokenRequest;
import org.springframework.security.authentication.ott.OneTimeToken;
import org.springframework.security.authentication.ott.OneTimeTokenAuthenticationToken;
import org.springframework.security.authentication.ott.OneTimeTokenService;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * MongoDB-backed {@link OneTimeTokenService} implementation.
 *
 * <p>Tokens are persisted in the shared {@code confirmation_code} collection via
 * {@link ConfirmationCodeRepository}, type {@link ConfirmationCodeType#ONE_TIME_TOKEN}.
 *
 * <p>Expiry is enforced at query time (no dedicated {@code expiresAt} field / TTL index):
 * a token is considered valid when it is still marked active and its {@code createdAt}
 * timestamp is within the configured {@link OttProperties#tokenValidity()} window.
 * Consuming a token deactivates it, making it single-use.
 */
@Slf4j
@RequiredArgsConstructor
@Transactional
public class MongoOneTimeTokenService implements OneTimeTokenService {
    private final ConfirmationCodeRepository codeRepository;
    private final OttProperties ottProperties;

    @Override
    public @NonNull OneTimeToken generate(@NonNull GenerateOneTimeTokenRequest request) {
        String tokenValue = UUID.randomUUID().toString();
        LocalDateTime createdAt = LocalDateTime.now(ZoneOffset.UTC);

        ConfirmationCodeEntity entity = ConfirmationCodeEntity.builder()
                .id(UUID.randomUUID().toString())
                .type(ConfirmationCodeType.ONE_TIME_TOKEN)
                .code(tokenValue)
                .username(request.getUsername())
                .isActive(true)
                .createdAt(createdAt)
                .build();
        codeRepository.save(entity);

        log.debug("One-Time-Token generated for user: {}", request.getUsername());
        return new DefaultOneTimeToken(tokenValue, request.getUsername(), expiryOf(createdAt));
    }

    @Override
    public @Nullable OneTimeToken consume(@NonNull OneTimeTokenAuthenticationToken authenticationToken) {
        String tokenValue = authenticationToken.getTokenValue();
        Objects.requireNonNull(tokenValue, "token value must not be null");

        Optional<ConfirmationCodeEntity> entity = codeRepository.findByCodeAndType(tokenValue, ConfirmationCodeType.ONE_TIME_TOKEN)
                .filter(ConfirmationCodeEntity::isActive)
                .filter(this::isNotExpired);

        if (entity.isEmpty()) {
            log.debug("One-Time-Token is invalid, expired or already consumed");
            return null;
        }

        ConfirmationCodeEntity consumedEntity = entity.get();
        codeRepository.deleteAllByUsernameAndType(consumedEntity.getUsername(), ConfirmationCodeType.ONE_TIME_TOKEN);

        log.debug("One-Time-Token consumed successfully for user: {}", consumedEntity.getUsername());
        return new DefaultOneTimeToken(tokenValue, consumedEntity.getUsername(), expiryOf(consumedEntity.getCreatedAt()));
    }

    @SuppressWarnings("java:S8700")
    private boolean isNotExpired(ConfirmationCodeEntity entity) {
        return Duration.between(entity.getCreatedAt(), LocalDateTime.now(ZoneOffset.UTC))
                .compareTo(ottProperties.tokenValidity()) <= 0;
    }

    private Instant expiryOf(LocalDateTime createdAt) {
        return createdAt.plus(ottProperties.tokenValidity()).toInstant(ZoneOffset.UTC);
    }
}



