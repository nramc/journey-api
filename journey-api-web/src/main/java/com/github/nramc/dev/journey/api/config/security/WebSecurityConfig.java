package com.github.nramc.dev.journey.api.config.security;

import com.github.nramc.dev.journey.api.core.jwt.JwtProperties;
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
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import static com.github.nramc.dev.journey.api.core.domain.user.Role.ADMINISTRATOR;
import static com.github.nramc.dev.journey.api.core.domain.user.Role.AUTHENTICATED_USER;
import static com.github.nramc.dev.journey.api.core.domain.user.Role.GUEST_USER;
import static com.github.nramc.dev.journey.api.core.domain.user.Role.MAINTAINER;
import static com.github.nramc.dev.journey.api.web.resources.Resources.ACTIVATE_ACCOUNT;
import static com.github.nramc.dev.journey.api.web.resources.Resources.API_VERSION;
import static com.github.nramc.dev.journey.api.web.resources.Resources.CHANGE_MY_PASSWORD;
import static com.github.nramc.dev.journey.api.web.resources.Resources.DELETE_JOURNEY;
import static com.github.nramc.dev.journey.api.web.resources.Resources.DELETE_MY_ACCOUNT;
import static com.github.nramc.dev.journey.api.web.resources.Resources.DELETE_USER_BY_USERNAME;
import static com.github.nramc.dev.journey.api.web.resources.Resources.FETCH_ALL_CATEGORIES;
import static com.github.nramc.dev.journey.api.web.resources.Resources.FIND_JOURNEYS;
import static com.github.nramc.dev.journey.api.web.resources.Resources.FIND_JOURNEY_BY_ID;
import static com.github.nramc.dev.journey.api.web.resources.Resources.FIND_MY_ACCOUNT;
import static com.github.nramc.dev.journey.api.web.resources.Resources.FIND_PUBLISHED_JOURNEYS;
import static com.github.nramc.dev.journey.api.web.resources.Resources.FIND_UPCOMING_ANNIVERSARY;
import static com.github.nramc.dev.journey.api.web.resources.Resources.FIND_USERS;
import static com.github.nramc.dev.journey.api.web.resources.Resources.GET_STATISTICS;
import static com.github.nramc.dev.journey.api.web.resources.Resources.GET_TIMELINE_DATA;
import static com.github.nramc.dev.journey.api.web.resources.Resources.GUEST_LOGIN;
import static com.github.nramc.dev.journey.api.web.resources.Resources.HEALTH_CHECK;
import static com.github.nramc.dev.journey.api.web.resources.Resources.HOME;
import static com.github.nramc.dev.journey.api.web.resources.Resources.LOGIN;
import static com.github.nramc.dev.journey.api.web.resources.Resources.LOGIN_MFA;
import static com.github.nramc.dev.journey.api.web.resources.Resources.MY_SECURITY_ATTRIBUTE_EMAIL;
import static com.github.nramc.dev.journey.api.web.resources.Resources.MY_SECURITY_ATTRIBUTE_TOTP;
import static com.github.nramc.dev.journey.api.web.resources.Resources.MY_SECURITY_ATTRIBUTE_TOTP_STATUS;
import static com.github.nramc.dev.journey.api.web.resources.Resources.MY_SECURITY_ATTRIBUTE_TOTP_VERIFY;
import static com.github.nramc.dev.journey.api.web.resources.Resources.MY_SECURITY_MFA;
import static com.github.nramc.dev.journey.api.web.resources.Resources.NEW_JOURNEY;
import static com.github.nramc.dev.journey.api.web.resources.Resources.PROMETHEUS;
import static com.github.nramc.dev.journey.api.web.resources.Resources.REST_DOC;
import static com.github.nramc.dev.journey.api.web.resources.Resources.SEND_EMAIL_CODE;
import static com.github.nramc.dev.journey.api.web.resources.Resources.SIGNUP;
import static com.github.nramc.dev.journey.api.web.resources.Resources.UPDATE_JOURNEY;
import static com.github.nramc.dev.journey.api.web.resources.Resources.UPDATE_MY_ACCOUNT;
import static com.github.nramc.dev.journey.api.web.resources.Resources.VERIFY_EMAIL_CODE;
import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpMethod.PUT;
import static org.springframework.security.authorization.AuthorityAuthorizationManager.hasAnyAuthority;
import static org.springframework.security.authorization.AuthorizationManagers.anyOf;
import static org.springframework.security.oauth2.core.authorization.OAuth2AuthorizationManagers.hasAnyScope;

@Configuration(proxyBeanMethods = false)
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true)
@EnableConfigurationProperties({JwtProperties.class, CorsProperties.class})
public class WebSecurityConfig {
    AuthorizationManager<RequestAuthorizationContext> authenticatedUserAuthorizationManager = anyOf(
            hasAnyAuthority(AUTHENTICATED_USER.name(), MAINTAINER.name(), ADMINISTRATOR.name()),
            hasAnyScope(AUTHENTICATED_USER.name(), MAINTAINER.name(), ADMINISTRATOR.name())
    );
    AuthorizationManager<RequestAuthorizationContext> readAccessAuthorizationManager = anyOf(
            hasAnyAuthority(GUEST_USER.name(), AUTHENTICATED_USER.name(), MAINTAINER.name(), ADMINISTRATOR.name()),
            hasAnyScope(GUEST_USER.name(), AUTHENTICATED_USER.name(), MAINTAINER.name(), ADMINISTRATOR.name())
    );
    AuthorizationManager<RequestAuthorizationContext> adminAccessAuthorizationManager = anyOf(
            hasAnyAuthority(ADMINISTRATOR.name()), hasAnyScope(ADMINISTRATOR.name())
    );

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // configure CORS security
                .cors(Customizer.withDefaults())

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
                        .requestMatchers(GET, PROMETHEUS).permitAll()

                        .requestMatchers(HOME).permitAll()
                        .requestMatchers(GET, API_VERSION).permitAll()
                        .requestMatchers(REST_DOC).permitAll()

                        // Registration Resources
                        .requestMatchers(SIGNUP).permitAll()
                        .requestMatchers(ACTIVATE_ACCOUNT).permitAll()

                        // Login resources
                        .requestMatchers(POST, GUEST_LOGIN).permitAll()
                        .requestMatchers(POST, "/webauthn/authenticate/*").permitAll()
                        .requestMatchers(POST, LOGIN).authenticated()
                        .requestMatchers(POST, LOGIN_MFA).authenticated()

                        .requestMatchers(GET, FIND_JOURNEYS).access(readAccessAuthorizationManager)
                        .requestMatchers(GET, FIND_JOURNEY_BY_ID).access(readAccessAuthorizationManager)
                        .requestMatchers(GET, FIND_PUBLISHED_JOURNEYS).access(readAccessAuthorizationManager)
                        .requestMatchers(GET, FIND_UPCOMING_ANNIVERSARY).access(readAccessAuthorizationManager)

                        .requestMatchers(GET, GET_STATISTICS).access(readAccessAuthorizationManager)

                        .requestMatchers(GET, GET_TIMELINE_DATA).access(readAccessAuthorizationManager)


                        .requestMatchers(POST, NEW_JOURNEY).access(authenticatedUserAuthorizationManager)
                        .requestMatchers(PUT, UPDATE_JOURNEY).access(authenticatedUserAuthorizationManager)
                        .requestMatchers(DELETE, DELETE_JOURNEY).access(authenticatedUserAuthorizationManager)

                        // Users resources
                        .requestMatchers(GET, FIND_USERS).access(adminAccessAuthorizationManager)
                        .requestMatchers(DELETE, DELETE_USER_BY_USERNAME).access(adminAccessAuthorizationManager)


                        .requestMatchers(GET, FIND_MY_ACCOUNT).access(readAccessAuthorizationManager)
                        .requestMatchers(DELETE, DELETE_MY_ACCOUNT).access(authenticatedUserAuthorizationManager)
                        .requestMatchers(POST, CHANGE_MY_PASSWORD).access(authenticatedUserAuthorizationManager)
                        .requestMatchers(POST, UPDATE_MY_ACCOUNT).access(authenticatedUserAuthorizationManager)
                        .requestMatchers(POST, MY_SECURITY_ATTRIBUTE_EMAIL).access(authenticatedUserAuthorizationManager)
                        .requestMatchers(GET, MY_SECURITY_ATTRIBUTE_EMAIL).access(readAccessAuthorizationManager)
                        .requestMatchers(POST, "/webauthn/register/*").access(authenticatedUserAuthorizationManager)
                        .requestMatchers(GET, "/webauthn/credentials").access(readAccessAuthorizationManager)
                        .requestMatchers(DELETE, "/webauthn/credentials").access(authenticatedUserAuthorizationManager)

                        .requestMatchers(POST, MY_SECURITY_MFA).access(authenticatedUserAuthorizationManager)
                        .requestMatchers(POST, SEND_EMAIL_CODE).access(authenticatedUserAuthorizationManager)
                        .requestMatchers(POST, VERIFY_EMAIL_CODE).access(authenticatedUserAuthorizationManager)
                        .requestMatchers(MY_SECURITY_ATTRIBUTE_TOTP).access(authenticatedUserAuthorizationManager)
                        .requestMatchers(MY_SECURITY_ATTRIBUTE_TOTP_STATUS).access(readAccessAuthorizationManager)
                        .requestMatchers(MY_SECURITY_ATTRIBUTE_TOTP_VERIFY).access(authenticatedUserAuthorizationManager)

                        .requestMatchers(GET, FETCH_ALL_CATEGORIES).access(readAccessAuthorizationManager)


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

    @Bean
    CorsConfigurationSource corsConfigurationSource(CorsProperties corsProperties) {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        corsProperties.properties().forEach(corsProperty -> {
            CorsConfiguration configuration = new CorsConfiguration();
            configuration.setAllowedOrigins(corsProperty.allowedOrigins());
            configuration.setAllowedMethods(corsProperty.allowedMethods());
            configuration.setAllowedHeaders(corsProperty.allowedHeaders());
            configuration.setAllowCredentials(corsProperty.allowCredentials());
            source.registerCorsConfiguration(corsProperty.path(), configuration);
        });
        return source;
    }

}
