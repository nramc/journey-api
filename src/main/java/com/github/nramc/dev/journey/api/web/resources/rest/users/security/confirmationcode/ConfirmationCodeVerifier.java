package com.github.nramc.dev.journey.api.web.resources.rest.users.security.confirmationcode;

import com.github.nramc.dev.journey.api.repository.user.AuthUser;
import com.github.nramc.dev.journey.api.web.resources.rest.users.security.attributes.email.code.EmailConfirmationCodeService;
import com.github.nramc.dev.journey.api.core.totp.TotpUseCase;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ConfirmationCodeVerifier {
    private final TotpUseCase totpUseCase;
    private final EmailConfirmationCodeService emailConfirmationCodeService;

    public boolean verify(ConfirmationCode confirmationCode, AuthUser authUser) {
        return switch (confirmationCode) {
            case EmailCode emailCode -> emailConfirmationCodeService.verify(emailCode, authUser);
            case TotpCode totpCode -> totpUseCase.verify(authUser, totpCode);
        };
    }

}
