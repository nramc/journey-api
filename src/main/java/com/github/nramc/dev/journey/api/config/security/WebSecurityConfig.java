package com.github.nramc.dev.journey.api.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpMethod.PUT;

@Configuration(proxyBeanMethods = false)
@EnableWebSecurity
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

                .authorizeHttpRequests(authorizeRequests -> authorizeRequests
                        .requestMatchers(GET, "/").permitAll()

                        // Allowed for unauthenticated calls
                        .requestMatchers(GET, "/rest/journeys").permitAll()

                        // Allowed only when user authenticated
                        .requestMatchers(GET, "/rest/journey/*").permitAll()
                        .requestMatchers(PUT, "/rest/journey/*").permitAll()
                        .requestMatchers(POST, "/rest/journey").permitAll()

                        // disallow other paths, or authenticated(), permitAll()
                        .anyRequest().denyAll()
                );

        return http.build();
    }

}
