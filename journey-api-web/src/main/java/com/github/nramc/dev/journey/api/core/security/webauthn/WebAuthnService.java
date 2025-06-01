package com.github.nramc.dev.journey.api.core.security.webauthn;

import com.github.nramc.dev.journey.api.repository.user.AuthUser;
import com.github.nramc.dev.journey.api.web.resources.rest.auth.webauthn.HttpClientRequestInfo;
import com.yubico.webauthn.AssertionRequest;
import com.yubico.webauthn.AssertionResult;
import com.yubico.webauthn.FinishAssertionOptions;
import com.yubico.webauthn.FinishRegistrationOptions;
import com.yubico.webauthn.RegisteredCredential;
import com.yubico.webauthn.RegistrationResult;
import com.yubico.webauthn.RelyingParty;
import com.yubico.webauthn.StartAssertionOptions;
import com.yubico.webauthn.StartRegistrationOptions;
import com.yubico.webauthn.data.AuthenticatorAssertionResponse;
import com.yubico.webauthn.data.AuthenticatorAttestationResponse;
import com.yubico.webauthn.data.AuthenticatorSelectionCriteria;
import com.yubico.webauthn.data.ByteArray;
import com.yubico.webauthn.data.ClientAssertionExtensionOutputs;
import com.yubico.webauthn.data.ClientRegistrationExtensionOutputs;
import com.yubico.webauthn.data.PublicKeyCredential;
import com.yubico.webauthn.data.PublicKeyCredentialCreationOptions;
import com.yubico.webauthn.data.PublicKeyCredentialRequestOptions;
import com.yubico.webauthn.data.ResidentKeyRequirement;
import com.yubico.webauthn.data.UserIdentity;
import com.yubico.webauthn.data.UserVerificationRequirement;
import com.yubico.webauthn.exception.AssertionFailedException;
import com.yubico.webauthn.exception.RegistrationFailedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class WebAuthnService {
    private final RelyingParty relyingParty;
    private final PublicKeyCredentialRepository credentialRepository;
    private final PublicKeyCredentialCreationOptionRepository creationOptionRepository;
    private final AssertionRequestRepository assertionRequestRepository;


    public PublicKeyCredentialCreationOptions startRegistration(AuthUser user) {
        UserIdentity userIdentity = UserIdentity.builder()
                .name(user.getUsername())
                .displayName(user.getName())
                .id(WebAuthnUtils.newUserHandle())
                .build();

        AuthenticatorSelectionCriteria authenticatorSelectionCriteria = AuthenticatorSelectionCriteria.builder()
                .residentKey(ResidentKeyRequirement.REQUIRED)
                .userVerification(UserVerificationRequirement.REQUIRED)
                .build();

        StartRegistrationOptions options = StartRegistrationOptions.builder()
                .user(userIdentity)
                .authenticatorSelection(authenticatorSelectionCriteria)
                .build();

        PublicKeyCredentialCreationOptions creationOptions = relyingParty.startRegistration(options);
        log.info("Registration options created for user: {}", user.getUsername());
        creationOptionRepository.save(user, creationOptions);
        return creationOptions;
    }

    public void finishRegistration(AuthUser user,
                                   PublicKeyCredential<AuthenticatorAttestationResponse, ClientRegistrationExtensionOutputs> publicKeyCredential,
                                   HttpClientRequestInfo requestInfo)
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

        CredentialMetadata credentialMetadata = CredentialMetadata.builder()
                .createdAt(LocalDateTime.now())
                .name(creationOptions.getUser().getDisplayName())
                .deviceInfo(requestInfo.deviceInfo())
                .username(user.getUsername())
                .userHandle(creationOptions.getUser().getId())
                .build();

        credentialRepository.addCredential(credential, credentialMetadata);

        creationOptionRepository.delete(user);
        log.info("Credential added for user: {}, credential ID: {}", user.getUsername(), credential.getCredentialId());
    }

    public PublicKeyCredentialRequestOptions startAssertion(String username) {
        StartAssertionOptions options = StartAssertionOptions.builder()
                //.username(username)
                //.userVerification(UserVerificationRequirement.PREFERRED)

                .build();

        AssertionRequest request = relyingParty.startAssertion(options);

        assertionRequestRepository.save(request.getPublicKeyCredentialRequestOptions().getChallenge(), request);

        log.info("Assertion options created and saved for user: {}", username);
        return request.getPublicKeyCredentialRequestOptions();
    }

    public AssertionResult finishAssertion(PublicKeyCredential<AuthenticatorAssertionResponse, ClientAssertionExtensionOutputs> publicKeyCredential)
            throws AssertionFailedException {

        ByteArray challenge = publicKeyCredential.getResponse().getClientData().getChallenge();
        AssertionRequest request = assertionRequestRepository.get(challenge);
        if (request == null) {
            throw new IllegalStateException("No assertion request found for challenge: " + challenge);
        }

        FinishAssertionOptions options = FinishAssertionOptions.builder()
                .request(request)
                .response(publicKeyCredential)
                .build();

        AssertionResult assertionResult = relyingParty.finishAssertion(options);
        log.info("Assertion successful for user with challenge: {}", challenge);

        assertionRequestRepository.delete(challenge);
        return assertionResult;
    }

    public List<StoredCredentialInformation> listCredentials(AuthUser user) {
        return credentialRepository.getCredentials(user.getUsername());
    }

    public void deleteCredential(AuthUser userDetails, String credentialId) {
        credentialRepository.removeCredential(userDetails.getUsername(), ByteArray.fromBase64(credentialId));
    }
}
