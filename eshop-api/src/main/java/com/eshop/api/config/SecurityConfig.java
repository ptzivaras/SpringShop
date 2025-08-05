package com.eshop.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/products/**").permitAll()
                        .anyRequest().authenticated()
                        // TODO: secure other endpoints when ready
                )
                .httpBasic(Customizer.withDefaults());  // or formLogin()

        return http.build();
    }
}
        //This does two things:
        //Disables CSRF (ok for a pure API).
        //Permits anyone to call GET /api/products (and other HTTP methods on that path).
        //Requires authentication on any other endpoint, via HTTP Basic.