package com.github.nramc.dev.journey.api.config.security;

import com.github.nramc.dev.journey.api.core.security.webauthn.AssertionRequestRepository;
import com.github.nramc.dev.journey.api.core.security.webauthn.InMemoryAssertionRequestRepository;
import com.github.nramc.dev.journey.api.core.security.webauthn.InMemoryPublicKeyCredentialCreationOptionRepository;
import com.github.nramc.dev.journey.api.core.security.webauthn.PublicKeyCredentialCreationOptionRepository;
import com.github.nramc.dev.journey.api.core.security.webauthn.PublicKeyCredentialRepository;
import com.github.nramc.dev.journey.api.core.security.webauthn.WebAuthnConfigurationProperties;
import com.github.nramc.dev.journey.api.core.security.webauthn.WebAuthnService;
import com.github.nramc.dev.journey.api.repository.user.PersistencePublicKeyCredentialRepository;
import com.github.nramc.dev.journey.api.repository.user.credential.UserPublicKeyCredentialRepository;
import com.yubico.webauthn.CredentialRepository;
import com.yubico.webauthn.RelyingParty;
import com.yubico.webauthn.data.RelyingPartyIdentity;
import com.yubico.webauthn.extension.appid.AppId;
import com.yubico.webauthn.extension.appid.InvalidAppIdException;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Set;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties({WebAuthnConfigurationProperties.class})
public class WebAuthnConfig {

    @Bean
    RelyingParty relyingParty(
            WebAuthnConfigurationProperties properties,
            CredentialRepository credentialRepository) throws InvalidAppIdException {

        RelyingPartyIdentity identity = RelyingPartyIdentity.builder()
                .id(properties.rpId())
                .name(properties.rpName())
                .build();

        return RelyingParty.builder()
                .identity(identity)
                .credentialRepository(credentialRepository)
                .appId(new AppId(properties.origin()))
                .allowOriginPort(true)
                .origins(Set.of(properties.origin()))
                .build();
    }

    @Bean
    PublicKeyCredentialRepository publicKeyCredentialRepository(UserPublicKeyCredentialRepository credentialRepository) {
        return new PersistencePublicKeyCredentialRepository(credentialRepository);
    }

    @Bean
    PublicKeyCredentialCreationOptionRepository publicKeyCredentialCreationOptionRepository() {
        return new InMemoryPublicKeyCredentialCreationOptionRepository();
    }

    @Bean
    AssertionRequestRepository assertionRequestRepository() {
        return new InMemoryAssertionRequestRepository();
    }

    @Bean
    WebAuthnService webAuthnService(RelyingParty relyingParty,
                                    PublicKeyCredentialRepository publicKeyCredentialRepository,
                                    PublicKeyCredentialCreationOptionRepository creationOptionRepository,
                                    AssertionRequestRepository assertionRequestRepository) {
        return new WebAuthnService(relyingParty, publicKeyCredentialRepository, creationOptionRepository, assertionRequestRepository);
    }
}
