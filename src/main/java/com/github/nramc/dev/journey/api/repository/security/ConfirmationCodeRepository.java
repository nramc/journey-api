package com.github.nramc.dev.journey.api.repository.security;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ConfirmationCodeRepository extends MongoRepository<ConfirmationCodeEntity, String> {

    ConfirmationCodeEntity findByUsernameAndCode(String username, String code);

    List<ConfirmationCodeEntity> findAllByUsername(String username);

}
