package com.github.nramc.dev.journey.api.account.repository.code;

import com.github.nramc.dev.journey.api.shared.domain.user.security.ConfirmationCodeType;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface ConfirmationCodeRepository extends MongoRepository<ConfirmationCodeEntity, String> {

    List<ConfirmationCodeEntity> findAllByUsername(String username);

    Optional<ConfirmationCodeEntity> findByCodeAndType(String code, ConfirmationCodeType type);

    void deleteAllByUsernameAndType(String username, ConfirmationCodeType type);

}
