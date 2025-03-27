package com.github.nramc.dev.journey.api.core.usecase.codes;

public sealed interface ConfirmationCode permits EmailCode, TotpCode {

}
