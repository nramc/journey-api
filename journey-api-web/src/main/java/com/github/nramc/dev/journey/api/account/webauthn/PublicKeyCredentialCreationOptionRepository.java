package com.github.nramc.dev.journey.api.account.webauthn;

import com.github.nramc.dev.journey.api.account.repository.AuthUser;
import com.yubico.webauthn.data.PublicKeyCredentialCreationOptions;

public interface PublicKeyCredentialCreationOptionRepository {

    void save(AuthUser user, PublicKeyCredentialCreationOptions options);

    PublicKeyCredentialCreationOptions get(AuthUser user);

    void delete(AuthUser user);
}
