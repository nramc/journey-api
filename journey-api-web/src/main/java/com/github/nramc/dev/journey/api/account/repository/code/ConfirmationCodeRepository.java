package com.github.nramc.dev.journey.api.account.repository.code;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ConfirmationCodeRepository extends MongoRepository<ConfirmationCodeEntity, String> {

    List<ConfirmationCodeEntity> findAllByUsername(String username);

}
