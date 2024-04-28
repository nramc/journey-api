package com.github.nramc.dev.journey.api.config.security;

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationEntryPoint;
import org.springframework.security.oauth2.server.resource.web.access.BearerTokenAccessDeniedHandler;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;

import static com.github.nramc.dev.journey.api.security.Roles.ADMINISTRATOR;
import static com.github.nramc.dev.journey.api.security.Roles.AUTHENTICATED_USER;
import static com.github.nramc.dev.journey.api.security.Roles.GUEST;
import static com.github.nramc.dev.journey.api.security.Roles.MAINTAINER;
import static com.github.nramc.dev.journey.api.web.resources.Resources.ALL_REQUESTS;
import static com.github.nramc.dev.journey.api.web.resources.Resources.CHANGE_MY_PASSWORD;
import static com.github.nramc.dev.journey.api.web.resources.Resources.DELETE_MY_ACCOUNT;
import static com.github.nramc.dev.journey.api.web.resources.Resources.DELETE_USER_BY_USERNAME;
import static com.github.nramc.dev.journey.api.web.resources.Resources.FIND_JOURNEYS;
import static com.github.nramc.dev.journey.api.web.resources.Resources.FIND_JOURNEY_BY_ID;
import static com.github.nramc.dev.journey.api.web.resources.Resources.FIND_MY_ACCOUNT;
import static com.github.nramc.dev.journey.api.web.resources.Resources.FIND_PUBLISHED_JOURNEYS;
import static com.github.nramc.dev.journey.api.web.resources.Resources.FIND_USERS;
import static com.github.nramc.dev.journey.api.web.resources.Resources.HEALTH_CHECK;
import static com.github.nramc.dev.journey.api.web.resources.Resources.HOME;
import static com.github.nramc.dev.journey.api.web.resources.Resources.LOGIN;
import static com.github.nramc.dev.journey.api.web.resources.Resources.NEW_JOURNEY;
import static com.github.nramc.dev.journey.api.web.resources.Resources.NEW_USER;
import static com.github.nramc.dev.journey.api.web.resources.Resources.UPDATE_JOURNEY;
import static com.github.nramc.dev.journey.api.web.resources.Resources.UPDATE_MY_ACCOUNT;
import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.OPTIONS;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpMethod.PUT;
import static org.springframework.security.authorization.AuthorityAuthorizationManager.hasAnyAuthority;
import static org.springframework.security.authorization.AuthorizationManagers.anyOf;
import static org.springframework.security.oauth2.core.authorization.OAuth2AuthorizationManagers.hasAnyScope;

@Configuration(proxyBeanMethods = false)
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true)
@EnableConfigurationProperties(JwtProperties.class)
public class WebSecurityConfig {
    AuthorizationManager<RequestAuthorizationContext> authenticatedUserAuthorizationManager = anyOf(
            hasAnyAuthority(AUTHENTICATED_USER.name(), MAINTAINER.name(), ADMINISTRATOR.name()),
            hasAnyScope(AUTHENTICATED_USER.name(), MAINTAINER.name(), ADMINISTRATOR.name())
    );
    AuthorizationManager<RequestAuthorizationContext> readOnlyAuthorizationManager = anyOf(
            hasAnyAuthority(GUEST.name(), AUTHENTICATED_USER.name(), MAINTAINER.name(), ADMINISTRATOR.name()),
            hasAnyScope(GUEST.name(), AUTHENTICATED_USER.name(), MAINTAINER.name(), ADMINISTRATOR.name())
    );
    AuthorizationManager<RequestAuthorizationContext> readAndWriteAuthorizationManager = anyOf(
            hasAnyAuthority(MAINTAINER.name(), ADMINISTRATOR.name()),
            hasAnyScope(MAINTAINER.name(), ADMINISTRATOR.name())
    );
    AuthorizationManager<RequestAuthorizationContext> adminOnlyAuthorizationManager = anyOf(
            hasAnyAuthority(ADMINISTRATOR.name()), hasAnyScope(ADMINISTRATOR.name())
    );

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Configure session management to create stateless sessions
                .sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Disable CSRF Protection as application sessions are Stateless
                .csrf(CsrfConfigurer::disable)

                // configure http headers with customizer
                .headers(new SecurityHeadersCustomizer())

                // HTTP Basic authentication
                .httpBasic(Customizer.withDefaults())

                // OAuth2 JWT authentication
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()))
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint(new BearerTokenAuthenticationEntryPoint())
                        .accessDeniedHandler(new BearerTokenAccessDeniedHandler())
                )


                .authorizeHttpRequests(authorizeRequests -> authorizeRequests
                        .requestMatchers(GET, HEALTH_CHECK).permitAll()
                        .requestMatchers(GET, HOME).permitAll()

                        // Allow Preflight requests
                        .requestMatchers(OPTIONS, ALL_REQUESTS).permitAll()

                        // Journeys resources
                        .requestMatchers(POST, LOGIN).authenticated()

                        .requestMatchers(GET, FIND_JOURNEYS).access(readOnlyAuthorizationManager)
                        .requestMatchers(GET, FIND_JOURNEY_BY_ID).access(readOnlyAuthorizationManager)
                        .requestMatchers(GET, FIND_PUBLISHED_JOURNEYS).access(readOnlyAuthorizationManager)

                        .requestMatchers(POST, NEW_JOURNEY).access(readAndWriteAuthorizationManager)
                        .requestMatchers(PUT, UPDATE_JOURNEY).access(readAndWriteAuthorizationManager)

                        // Users resources
                        .requestMatchers(POST, NEW_USER).access(adminOnlyAuthorizationManager)
                        .requestMatchers(GET, FIND_USERS).access(adminOnlyAuthorizationManager)
                        .requestMatchers(DELETE, DELETE_USER_BY_USERNAME).access(adminOnlyAuthorizationManager)


                        .requestMatchers(GET, FIND_MY_ACCOUNT).access(authenticatedUserAuthorizationManager)
                        .requestMatchers(DELETE, DELETE_MY_ACCOUNT).access(authenticatedUserAuthorizationManager)
                        .requestMatchers(POST, CHANGE_MY_PASSWORD).access(authenticatedUserAuthorizationManager)
                        .requestMatchers(POST, UPDATE_MY_ACCOUNT).access(authenticatedUserAuthorizationManager)


                        // disallow other paths, or authenticated(), permitAll()
                        .anyRequest().denyAll()
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    JwtDecoder jwtDecoder(JwtProperties jwtProperties) {
        return NimbusJwtDecoder.withPublicKey(jwtProperties.publicKey()).build();
    }

    @Bean
    JwtEncoder jwtEncoder(JwtProperties jwtProperties) {
        JWK jwk = new RSAKey.Builder(jwtProperties.publicKey()).privateKey(jwtProperties.privateKey()).build();
        JWKSource<SecurityContext> jwks = new ImmutableJWKSet<>(new JWKSet(jwk));
        return new NimbusJwtEncoder(jwks);
    }

}
