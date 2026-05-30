package com.github.nramc.dev.journey.api.account.repository.attributes;

import com.github.nramc.dev.journey.api.shared.domain.user.UserSecurityAttributeType;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface UserSecurityAttributesRepository extends MongoRepository<UserSecurityAttributeEntity, String> {

    List<UserSecurityAttributeEntity> findAllByUserId(String userId);

    List<UserSecurityAttributeEntity> findAllByUserIdAndType(String userId, UserSecurityAttributeType type);

    void deleteAllByUserIdAndType(String userId, UserSecurityAttributeType type);

}
