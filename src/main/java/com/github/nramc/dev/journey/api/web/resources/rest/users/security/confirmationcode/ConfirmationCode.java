package com.github.nramc.dev.journey.api.web.resources.rest.users.security.confirmationcode;

public sealed interface ConfirmationCode permits EmailCode, TotpCode {

}
