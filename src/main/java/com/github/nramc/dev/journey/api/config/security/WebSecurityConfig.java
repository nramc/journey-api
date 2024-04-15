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

import static com.github.nramc.dev.journey.api.config.security.Authority.MAINTAINER;
import static org.springframework.http.HttpMethod.GET;
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
                .oauth2ResourceServer(oauth2ResourceServer -> oauth2ResourceServer.jwt(Customizer.withDefaults()))
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint(new BearerTokenAuthenticationEntryPoint())
                        .accessDeniedHandler(new BearerTokenAccessDeniedHandler())
                )


                .authorizeHttpRequests(authorizeRequests -> authorizeRequests
                        .requestMatchers(GET, "/actuator/health").permitAll()
                        .requestMatchers(GET, "/").permitAll()

                        // Allowed for unauthenticated calls
                        .requestMatchers(GET, "/rest/public/journeys").permitAll()

                        // Allowed only when user authenticated
                        .requestMatchers(POST, "/token").authenticated()

                        .requestMatchers(GET, "/rest/journeys").access(anyOf(hasAnyAuthority(MAINTAINER), hasAnyScope(MAINTAINER)))
                        .requestMatchers(GET, "/rest/journey/*").access(anyOf(hasAnyAuthority(MAINTAINER), hasAnyScope(MAINTAINER)))
                        .requestMatchers(POST, "/rest/journey").access(anyOf(hasAnyAuthority(MAINTAINER), hasAnyScope(MAINTAINER)))
                        .requestMatchers(PUT, "/rest/journey/*").access(anyOf(hasAnyAuthority(MAINTAINER), hasAnyScope(MAINTAINER)))

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
