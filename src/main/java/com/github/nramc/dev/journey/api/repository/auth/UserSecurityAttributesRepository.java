package com.github.nramc.dev.journey.api.repository.auth;

import com.github.nramc.dev.journey.api.models.core.SecurityAttributeType;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface UserSecurityAttributesRepository extends MongoRepository<UserSecurityAttributesEntity, String> {

    List<UserSecurityAttributesEntity> findAllByUserId(String userId);

    List<UserSecurityAttributesEntity> findAllByUserIdAndType(String userId, SecurityAttributeType type);

}
