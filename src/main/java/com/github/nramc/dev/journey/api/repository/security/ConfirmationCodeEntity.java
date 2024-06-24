package com.github.nramc.dev.journey.api.repository.security;

import com.github.nramc.dev.journey.api.models.core.ConfirmationCodeType;
import com.github.nramc.dev.journey.api.services.confirmationcode.ConfirmationUseCase;
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
    private ConfirmationUseCase useCase;
}
