package com.github.nramc.dev.journey.api.infrastructure.openapi;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiDocumentationConfig {
    private static final String BASIC_AUTH = "basicScheme";
    private static final String BEARER_AUTH = "bearerAuth";

    @Bean
    public OpenAPI customOpenAPI(@Value("${app.version}") String appVersion,
                                 @Value("${app.ui-app-url:}") String uiAppUrl) {
        return new OpenAPI()
                .info(new Info()
                        .title("Journey API")
                        .version(appVersion)
                        .description("""
                                Backend For Frontend (BFF) REST API for the Journeys Single Page Application.

                                This API powers geospatial journey management, user authentication,
                                multi-factor authentication, AI-assisted narration, and text-to-speech features.
                                """
                        )
                        .license(new License().name("Apache 2.0").identifier("Apache-2.0"))
                        .contact(new Contact()
                                .name("Ramachandran Nellaiyappan")
                                .email("ramachandrannellai@gmail.com")
                                .url("https://myprofile.codewithram.dev/")
                        )
                )
                .externalDocs(new ExternalDocumentation()
                        .description("Journeys SPA")
                        .url(uiAppUrl)
                )
                .servers(List.of(
                        new Server().url("https://journey-api.codewithram.dev").description("Production"),
                        new Server().url("https://localhost:8080").description("Local development (HTTPS)")
                ))
                .addSecurityItem(new SecurityRequirement().addList(BASIC_AUTH).addList(BEARER_AUTH))
                .components(new Components()
                        .addSecuritySchemes(BEARER_AUTH, new SecurityScheme()
                                .name(BEARER_AUTH)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("JWT token obtained from /rest/login or /rest/guestLogin")
                        )
                        .addSecuritySchemes(BASIC_AUTH, new SecurityScheme()
                                .name(BASIC_AUTH)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("basic")
                                .description("HTTP Basic authentication with username/password")
                        )
                );
    }
}
