package com.github.nramc.dev.journey.api.web.resources.rest.api;

import com.github.nramc.dev.journey.api.core.app.ApplicationProperties;
import com.github.nramc.dev.journey.api.web.resources.rest.doc.RestDocCommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.github.nramc.dev.journey.api.web.resources.Resources.API_VERSION;

@RestController
@RequiredArgsConstructor
@Tag(name = "API Info", description = "Get Information about deployed API")
class ApiVersionResource {
    private final ApplicationProperties applicationProperties;

    @Operation(summary = "Get application name and version")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Return application Name and Version", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = ApiVersionResponse.class))})})
    @RestDocCommonResponse
    @GetMapping(value = API_VERSION, produces = MediaType.APPLICATION_JSON_VALUE)
    ApiVersionResponse version() {
        return ApiVersionResponse.builder()
                .name(applicationProperties.name())
                .version(applicationProperties.version())
                .build();
    }

}
