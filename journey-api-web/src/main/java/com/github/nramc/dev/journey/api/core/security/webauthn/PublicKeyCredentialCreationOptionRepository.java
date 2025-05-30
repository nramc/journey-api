package com.github.nramc.dev.journey.api.core.security.webauthn;

import com.github.nramc.dev.journey.api.repository.user.AuthUser;
import com.yubico.webauthn.data.PublicKeyCredentialCreationOptions;

public interface PublicKeyCredentialCreationOptionRepository {

    void save(AuthUser user, PublicKeyCredentialCreationOptions options);

    PublicKeyCredentialCreationOptions get(AuthUser user);

    void delete(AuthUser user);
}
