package com.github.nramc.dev.journey.api.account.web.auth.ott;

import com.github.nramc.dev.journey.api.account.usecase.OttLoginUseCase;
import com.github.nramc.dev.journey.api.account.web.auth.dto.LoginResponse;
import com.github.nramc.dev.journey.api.shared.web.doc.RestDocCommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static com.github.nramc.dev.journey.api.shared.web.Resources.LOGIN_OTT;

/**
 * Authenticates using a One-Time-Token (OTT) received via the account recovery email and
 * returns a standard JWT, allowing the SPA to immediately proceed to change the password.
 */
@RestController
@RequiredArgsConstructor
@Tag(name = "Login", description = "Authenticate application users")
class OttLoginResource {
    private final OttLoginUseCase ottLoginUseCase;

    @Operation(summary = "Login with One-Time-Token", description = "Exchanges a one-time token from a recovery email for a JWT.")
    @RestDocCommonResponse
    @ApiResponses(@ApiResponse(responseCode = "200", description = "Authentication successful and return JWT", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = LoginResponse.class))}))
    @PostMapping(value = LOGIN_OTT, consumes = MediaType.APPLICATION_JSON_VALUE)
    LoginResponse login(@RequestBody @Valid OttLoginRequest request) {
        return ottLoginUseCase.login(request.token());
    }
}
