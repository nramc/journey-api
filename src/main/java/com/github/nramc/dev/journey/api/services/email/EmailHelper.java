package com.github.nramc.dev.journey.api.services.email;

import com.github.nramc.dev.journey.api.services.confirmationcode.ConfirmationUseCase;
import lombok.experimental.UtilityClass;

@UtilityClass
public class EmailHelper {

    public static String getSubject(ConfirmationUseCase useCase) {
        return switch (useCase) {
            case VERIFY_EMAIL_ADDRESS -> "Journey: Email Verification Request";
        };
    }

}
