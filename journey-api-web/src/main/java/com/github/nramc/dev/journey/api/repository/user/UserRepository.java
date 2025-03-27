package com.github.nramc.dev.journey.api.repository.user;

import com.github.nramc.dev.journey.api.core.domain.user.Role;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Set;

public interface UserRepository extends MongoRepository<AuthUser, String> {

    AuthUser findUserByUsername(String username);

    void deleteByUsername(String username);

    List<AuthUser> findByRolesContainingAndEnabled(Set<Role> roles, boolean enabled);

}
