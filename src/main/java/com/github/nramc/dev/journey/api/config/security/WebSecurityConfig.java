package com.github.nramc.dev.journey.api.config.security;

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.beans.factory.annotation.Value;
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

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

import static com.github.nramc.dev.journey.api.config.security.Authority.MAINTAINER;
import static com.github.nramc.dev.journey.api.config.security.Authority.MAINTAINER_SCOPE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpMethod.PUT;
import static org.springframework.security.authorization.AuthorityAuthorizationManager.hasAnyAuthority;
import static org.springframework.security.authorization.AuthorizationManagers.anyOf;
import static org.springframework.security.oauth2.core.authorization.OAuth2AuthorizationManagers.hasAnyScope;

@Configuration(proxyBeanMethods = false)
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true)
public class WebSecurityConfig {
    @Value("${jwt.public.key}")
    RSAPublicKey key;

    @Value("${jwt.private.key}")
    RSAPrivateKey priv;

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
                        .requestMatchers(GET, "/rest/journey/*").hasAnyAuthority(MAINTAINER, MAINTAINER_SCOPE)
                        .requestMatchers(POST, "/rest/journey").hasAnyAuthority(MAINTAINER, MAINTAINER_SCOPE)
                        .requestMatchers(PUT, "/rest/journey/*").hasAnyAuthority(MAINTAINER, MAINTAINER_SCOPE)

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
    JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withPublicKey(this.key).build();
    }

    @Bean
    JwtEncoder jwtEncoder() {
        JWK jwk = new RSAKey.Builder(this.key).privateKey(this.priv).build();
        JWKSource<SecurityContext> jwks = new ImmutableJWKSet<>(new JWKSet(jwk));
        return new NimbusJwtEncoder(jwks);
    }

}
