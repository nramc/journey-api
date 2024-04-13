package com.github.nramc.dev.journey.api.config.security;

import com.github.nramc.dev.journey.api.repository.auth.UserRepository;
import com.github.nramc.dev.journey.api.services.AuthUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import static com.github.nramc.dev.journey.api.config.security.Authority.MAINTAINER;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpMethod.PUT;

@Configuration(proxyBeanMethods = false)
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true)
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

                // http basic authentication
                .httpBasic(Customizer.withDefaults())

                .authorizeHttpRequests(authorizeRequests -> authorizeRequests
                        .requestMatchers(GET, "/actuator/health").permitAll()
                        .requestMatchers(GET, "/").permitAll()

                        // Allowed for unauthenticated calls
                        .requestMatchers(GET, "/rest/public/journeys").permitAll()

                        // Allowed only when user authenticated
                        .requestMatchers(GET, "/rest/journeys").hasAnyAuthority(MAINTAINER)
                        .requestMatchers(GET, "/rest/journey/*").hasAnyAuthority(Authority.MAINTAINER)
                        .requestMatchers(POST, "/rest/journey").hasAnyAuthority(Authority.MAINTAINER)
                        .requestMatchers(PUT, "/rest/journey/*").hasAnyAuthority(Authority.MAINTAINER)

                        // disallow other paths, or authenticated(), permitAll()
                        .anyRequest().denyAll()
                );

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService(UserRepository userRepository) {
        return new AuthUserDetailsService(userRepository);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
