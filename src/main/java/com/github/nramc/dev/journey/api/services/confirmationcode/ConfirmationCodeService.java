package com.github.nramc.dev.journey.api.services.confirmationcode;

import com.github.nramc.dev.journey.api.repository.auth.AuthUser;

public interface ConfirmationCodeService {

    void send(AuthUser authUser, String useCase);

    boolean verify(ConfirmationCode confirmationCode, AuthUser authUser);

}
