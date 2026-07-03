package com.github.nramc.dev.journey.api.account.web.auth.recovery;

import com.github.nramc.dev.journey.api.account.usecase.PasswordRecoveryUseCase;
import com.github.nramc.dev.journey.api.shared.web.doc.RestDocCommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static com.github.nramc.dev.journey.api.shared.web.Resources.SEND_ACCOUNT_RECOVERY;

/**
 * Sends an account/password recovery email containing a One-Time-Token (OTT) link to the
 * user's registered email address (username).
 *
 * <p>Always responds with {@code 200 OK}, regardless of whether the username exists, to
 * prevent user-enumeration attacks.
 */
@RestController
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Account Recovery", description = "Recover account access via One-Time-Token email")
class SendPasswordRecoveryResource {
    private final PasswordRecoveryUseCase passwordRecoveryUseCase;

    @Operation(summary = "Send account recovery email with a One-Time-Token link to registered email address")
    @ApiResponse(responseCode = "200", description = "Recovery email has been triggered (if the username exists)")
    @RestDocCommonResponse
    @PostMapping(value = SEND_ACCOUNT_RECOVERY, consumes = MediaType.APPLICATION_JSON_VALUE)
    void recover(@RequestBody @Valid SendPasswordRecoveryRequest request) {
        passwordRecoveryUseCase.sendRecoveryEmail(request.username());
        log.info("Account recovery request has been processed");
    }
}

