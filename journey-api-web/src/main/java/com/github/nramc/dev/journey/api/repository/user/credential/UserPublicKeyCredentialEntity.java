package com.github.nramc.dev.journey.api.repository.user.credential;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "public-key-credentials")
public class UserPublicKeyCredentialEntity {
    @Id
    private String id;
    private String username;
    private String userHandle;
    private String credentialId;
    private String publicKeyCose;
    private long signatureCount;
}
