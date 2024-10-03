package com.github.nramc.dev.journey.api.web.resources.rest.users.security.confirmationcode;

import com.github.nramc.dev.journey.api.repository.user.AuthUser;
import com.github.nramc.dev.journey.api.web.resources.rest.users.security.attributes.email.code.EmailConfirmationCodeService;
import com.github.nramc.dev.journey.api.web.resources.rest.users.security.attributes.totp.TotpService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ConfirmationCodeVerifier {
    private final TotpService totpService;
    private final EmailConfirmationCodeService emailConfirmationCodeService;

    public boolean verify(ConfirmationCode confirmationCode, AuthUser authUser) {
        return switch (confirmationCode) {
            case EmailCode emailCode -> emailConfirmationCodeService.verify(emailCode, authUser);
            case TotpCode totpCode -> totpService.verify(authUser, totpCode);
        };
    }

}
