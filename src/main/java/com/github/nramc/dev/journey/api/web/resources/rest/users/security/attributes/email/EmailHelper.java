package com.github.nramc.dev.journey.api.web.resources.rest.users.security.attributes.email;

import com.github.nramc.dev.journey.api.web.resources.rest.users.security.confirmationcode.ConfirmationUseCase;
import lombok.experimental.UtilityClass;

@UtilityClass
public class EmailHelper {

    public static String getSubject(ConfirmationUseCase useCase) {
        return switch (useCase) {
            case UNKNOWN -> "Journey: Confirmation Required";
            case VERIFY_EMAIL_ADDRESS -> "Journey: Email Verification Request";
        };
    }

}
