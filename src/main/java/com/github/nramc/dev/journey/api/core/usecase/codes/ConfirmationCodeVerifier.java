package com.github.nramc.dev.journey.api.core.usecase.codes;

import com.github.nramc.dev.journey.api.core.usecase.codes.emailcode.EmailCodeUseCase;
import com.github.nramc.dev.journey.api.core.usecase.codes.totp.TotpUseCase;
import com.github.nramc.dev.journey.api.repository.user.AuthUser;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ConfirmationCodeVerifier {
    private final TotpUseCase totpUseCase;
    private final EmailCodeUseCase emailCodeUseCase;

    public boolean verify(ConfirmationCode confirmationCode, AuthUser authUser) {
        return switch (confirmationCode) {
            case EmailCode emailCode -> emailCodeUseCase.verify(emailCode, authUser);
            case TotpCode totpCode -> totpUseCase.verify(authUser, totpCode);
        };
    }

}
