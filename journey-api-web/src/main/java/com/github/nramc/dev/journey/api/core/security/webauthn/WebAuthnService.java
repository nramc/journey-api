package com.github.nramc.dev.journey.api.core.security.webauthn;

import com.github.nramc.dev.journey.api.repository.user.AuthUser;
import com.yubico.webauthn.RelyingParty;
import com.yubico.webauthn.StartRegistrationOptions;
import com.yubico.webauthn.data.PublicKeyCredentialCreationOptions;
import com.yubico.webauthn.data.UserIdentity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/*
    todo:
     - add javadoc
     - add tests
 */
@Slf4j
@RequiredArgsConstructor
public class WebAuthnService {
    private final RelyingParty relyingParty;


    public PublicKeyCredentialCreationOptions startRegistration(AuthUser user) {
        UserIdentity userIdentity = UserIdentity.builder()
                .name(user.getUsername())
                .displayName(user.getName())
                .id(WebAuthnUtils.newUserHandle())
                .build();

        StartRegistrationOptions options = StartRegistrationOptions.builder()
                .user(userIdentity)
                .build();
        log.info("Starting registration for user: {}", user.getUsername());
        return relyingParty.startRegistration(options);
    }
}
