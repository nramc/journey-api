package com.github.nramc.dev.journey.api.repository.user.credential;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface UserPublicKeyCredentialRepository extends MongoRepository<UserPublicKeyCredentialEntity, String> {

    List<UserPublicKeyCredentialEntity> findByUsername(String username);

    List<UserPublicKeyCredentialEntity> findByCredentialId(String credentialId);

    Optional<UserPublicKeyCredentialEntity> findByCredentialIdAndUserHandle(String credentialId, String userHandle);

    Optional<UserPublicKeyCredentialEntity> findByUserHandle(String userHandle);

    void deleteByUsernameAndCredentialId(String username, String credentialId);

}
