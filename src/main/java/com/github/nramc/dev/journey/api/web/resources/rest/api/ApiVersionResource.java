package com.github.nramc.dev.journey.api.web.resources.rest.api;

import com.github.nramc.dev.journey.api.config.ApplicationProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.github.nramc.dev.journey.api.web.resources.Resources.API_VERSION;

@RestController
@RequiredArgsConstructor
public class ApiVersionResource {
    private final ApplicationProperties applicationProperties;

    @GetMapping(value = API_VERSION, produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiVersionResponse version() {
        return ApiVersionResponse.builder()
                .name(applicationProperties.name())
                .version(applicationProperties.version())
                .build();
    }

}
