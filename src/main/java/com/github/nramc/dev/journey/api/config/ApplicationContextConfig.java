package com.github.nramc.dev.journey.api.config;

import com.github.nramc.dev.journey.api.config.security.JwtProperties;
import com.github.nramc.dev.journey.api.web.resources.rest.auth.jwt.JwtGenerator;
import com.github.nramc.dev.journey.api.web.resources.rest.journeys.update.validator.JourneyValidator;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import jakarta.validation.Validator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtEncoder;


@Configuration
@EnableConfigurationProperties({ApplicationProperties.class})
public class ApplicationContextConfig {

    @Bean
    public JourneyValidator journeyValidator(Validator validator) {
        return new JourneyValidator(validator);
    }

    @Bean
    public JwtGenerator jwtGenerator(JwtProperties jwtProperties, JwtEncoder jwtEncoder) {
        return new JwtGenerator(jwtProperties, jwtEncoder);
    }

    @Bean
    public OpenAPI customOpenAPI(@Value("${app.version}") String appVersion) {
        final String basicAuth = "basicScheme";
        final String bearerAuth = "bearerAuth";
        return new OpenAPI().info(new Info()
                        .title("Journey API")
                        .version(appVersion)
                        .description("This is Backend For Frontend(BFF) service created using SpringBoot and OAuth2 Framework for Journey Single Page Application(SPA).")
                        .license(new License().name("Apache 2.0"))
                        .contact(new Contact()
                                .name("Ramachandran Nellaiyappan")
                                .email("ramachandrannellai@gmail.com")
                                .url("https://nramc.github.io/journeys")
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
