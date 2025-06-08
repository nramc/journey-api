package com.github.nramc.dev.journey.api.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiDocumentationConfig {
    @Bean
    public OpenAPI customOpenAPI(@Value("${app.version}") String appVersion) {
        final String basicAuth = "basicScheme";
        final String bearerAuth = "bearerAuth";
        return new OpenAPI().info(new Info()
                        .title("Journey API")
                        .version(appVersion)
                        .description("This is Backend For Frontend(BFF) service created using SpringBoot and OAuth2 Framework"
                                + " for Journey Single Page Application(SPA).")
                        .license(new License().name("Apache 2.0").identifier("Apache-2.0"))
                        .contact(new Contact()
                                .name("Ramachandran Nellaiyappan")
                                .email("ramachandrannellai@gmail.com")
                                .url("https://journey.codewithram.dev")
                        )
                ).addSecurityItem(new SecurityRequirement().addList(basicAuth, bearerAuth))
                .components(new Components()
                        .addSecuritySchemes(bearerAuth, new SecurityScheme()
                                .name(bearerAuth)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                        )
                        .addSecuritySchemes(basicAuth, new SecurityScheme()
                                .name(basicAuth)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("basic")
                        )
                );
    }
}
