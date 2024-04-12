package com.github.nramc.dev.journey.api.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
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

                // http basic authentication
                .httpBasic(Customizer.withDefaults())

                .authorizeHttpRequests(authorizeRequests -> authorizeRequests
                        .requestMatchers(GET, "/").permitAll()

                        // Allowed for unauthenticated calls
                        .requestMatchers(GET, "/rest/journeys").permitAll()

                        // Allowed only when user authenticated
                        .requestMatchers(POST, "/rest/journey").hasAuthority("CREATE_JOURNEY")
                        .requestMatchers(GET, "/rest/journey/*").permitAll()
                        .requestMatchers(PUT, "/rest/journey/*").permitAll()

                        // disallow other paths, or authenticated(), permitAll()
                        .anyRequest().denyAll()
                );

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        // The builder will ensure the passwords are encoded before saving in memory
        User.UserBuilder users = User.withDefaultPasswordEncoder();
        UserDetails admin = users
                .username("admin")
                .password("password")
                .roles("USER", "ADMIN")
                .build();
        return new InMemoryUserDetailsManager(admin);
    }

}
