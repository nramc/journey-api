package com.github.nramc.dev.journey.api.shared.domain.user.security;

public sealed interface ConfirmationCode permits EmailCode, TotpCode {

}
