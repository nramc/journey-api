package com.github.nramc.dev.journey.api.repository.auth;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<AuthUser, String> {

    AuthUser findUserByUsername(String username);

    void deleteByUsername(String username);

}
