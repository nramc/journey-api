package com.github.nramc.dev.journey.api.repository.auth;

import com.github.nramc.dev.journey.api.models.core.SecurityAttributeType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.time.LocalDate;

@Document("user_security_attributes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class UserSecurityAttributesEntity {
    private @MongoId ObjectId id;
    private String userId;
    private String username;
    private SecurityAttributeType type;
    private String value;
    private boolean enabled = true;
    private boolean verified = false;
    private LocalDate creationDate;
    private LocalDate lastUpdateDate;
}
