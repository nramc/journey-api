package com.github.nramc.dev.journey.api.repository.user;

import com.github.nramc.dev.journey.api.core.domain.user.ConfirmationCodeType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document("confirmation_code")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class ConfirmationCodeEntity {
    @Id
    private String id;
    private String username;
    private String code;
    private ConfirmationCodeType type;
    private LocalDateTime createdAt;
    private boolean isActive;
    private String receiver;
}
