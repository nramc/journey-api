package com.github.nramc.dev.journey.api.account.repository;

import com.github.nramc.dev.journey.api.shared.domain.user.security.Role;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Set;

public interface UserRepository extends MongoRepository<AuthUser, String> {

    AuthUser findUserByUsername(String username);

    void deleteByUsername(String username);

    List<AuthUser> findByRolesContainingAndEnabled(Set<Role> roles, boolean enabled);

}
