package com.github.nramc.dev.journey.api.repository.auth;

import com.github.nramc.dev.journey.api.core.security.attributes.SecurityAttributeType;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface UserSecurityAttributesRepository extends MongoRepository<UserSecurityAttributeEntity, String> {

    List<UserSecurityAttributeEntity> findAllByUserId(String userId);

    List<UserSecurityAttributeEntity> findAllByUserIdAndType(String userId, SecurityAttributeType type);

    void deleteAllByUserIdAndType(String userId, SecurityAttributeType type);

}
