package com.github.nramc.dev.journey.api.core.security.webauthn;

import com.github.nramc.dev.journey.api.repository.user.AuthUser;
import com.yubico.webauthn.FinishRegistrationOptions;
import com.yubico.webauthn.RegisteredCredential;
import com.yubico.webauthn.RegistrationResult;
import com.yubico.webauthn.RelyingParty;
import com.yubico.webauthn.StartRegistrationOptions;
import com.yubico.webauthn.data.AuthenticatorAttestationResponse;
import com.yubico.webauthn.data.ClientRegistrationExtensionOutputs;
import com.yubico.webauthn.data.PublicKeyCredential;
import com.yubico.webauthn.data.PublicKeyCredentialCreationOptions;
import com.yubico.webauthn.data.UserIdentity;
import com.yubico.webauthn.exception.RegistrationFailedException;
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
    private final PublicKeyCredentialRepository credentialRepository;
    private final PublicKeyCredentialCreationOptionRepository creationOptionRepository;


    public PublicKeyCredentialCreationOptions startRegistration(AuthUser user) {
        UserIdentity userIdentity = UserIdentity.builder()
                .name(user.getUsername())
                .displayName(user.getName())
                .id(WebAuthnUtils.newUserHandle())
                .build();

        StartRegistrationOptions options = StartRegistrationOptions.builder()
                .user(userIdentity)
                .build();
        PublicKeyCredentialCreationOptions creationOptions = relyingParty.startRegistration(options);
        log.info("Registration options created for user: {}", user.getUsername());
        creationOptionRepository.save(user, creationOptions);
        return creationOptions;
    }

    public void finishRegistration(AuthUser user, PublicKeyCredential<AuthenticatorAttestationResponse, ClientRegistrationExtensionOutputs> publicKeyCredential)
            throws RegistrationFailedException {
        PublicKeyCredentialCreationOptions creationOptions = creationOptionRepository.get(user);

        FinishRegistrationOptions options = FinishRegistrationOptions.builder()
                .request(creationOptions)
                .response(publicKeyCredential)
                .build();

        RegistrationResult result = relyingParty.finishRegistration(options);
        log.info("Registration successful for user: {}, credential ID: {}", user.getUsername(), result.getKeyId());

        // Here you would typically save the result to your database
        RegisteredCredential credential = RegisteredCredential.builder()
                .credentialId(result.getKeyId().getId())
                .userHandle(creationOptions.getUser().getId())
                .publicKeyCose(result.getPublicKeyCose())
                .signatureCount(result.getSignatureCount())
                .build();

        credentialRepository.addCredential(
                user.getUsername(),
                creationOptions.getUser().getId(),
                credential
        );

        creationOptionRepository.delete(user);
        log.info("Credential added for user: {}, credential ID: {}", user.getUsername(), credential.getCredentialId());
    }
}
