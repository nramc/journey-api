package com.github.nramc.dev.journey.api.core.usecase.code;

public sealed interface ConfirmationCode permits EmailCode, TotpCode {

}
