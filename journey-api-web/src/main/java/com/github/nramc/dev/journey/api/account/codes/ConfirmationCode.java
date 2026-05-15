package com.github.nramc.dev.journey.api.account.codes;

public sealed interface ConfirmationCode permits EmailCode, TotpCode {

}
