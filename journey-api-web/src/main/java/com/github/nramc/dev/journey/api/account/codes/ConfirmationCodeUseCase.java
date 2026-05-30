package com.github.nramc.dev.journey.api.account.codes;

import com.github.nramc.dev.journey.api.account.codes.emailcode.EmailCodeUseCase;
import com.github.nramc.dev.journey.api.account.codes.totp.TotpUseCase;
import com.github.nramc.dev.journey.api.account.repository.AuthUser;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ConfirmationCodeUseCase {
    private final TotpUseCase totpUseCase;
    private final EmailCodeUseCase emailCodeUseCase;

    public boolean verify(ConfirmationCode confirmationCode, AuthUser authUser) {
        return switch (confirmationCode) {
            case EmailCode emailCode -> emailCodeUseCase.verify(emailCode, authUser);
            case TotpCode totpCode -> totpUseCase.verify(authUser, totpCode);
        };
    }

}
