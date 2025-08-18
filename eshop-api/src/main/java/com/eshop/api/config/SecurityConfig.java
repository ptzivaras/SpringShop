package com.eshop.api.config;

import com.eshop.api.service.impl.CustomUserDetailsService;
import com.eshop.api.util.JwtUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder; // <-- inject
    //private final SecurityHeadersFilter securityHeadersFilter;

    public SecurityConfig(CustomUserDetailsService uds, JwtUtil ju, PasswordEncoder pe) {
        this.userDetailsService = uds;
        this.jwtUtil = ju;
        this.passwordEncoder = pe;
    }


/*
    public SecurityConfig(CustomUserDetailsService uds, JwtUtil ju,
                          PasswordEncoder pe, SecurityHeadersFilter shf) {
        this.userDetailsService = uds;
        this.jwtUtil = ju;
        this.passwordEncoder = pe;
        this.securityHeadersFilter = shf;
    }*/

    @Bean
    public AuthenticationManager authManager(HttpSecurity http) throws Exception {
       /* return http
                .getSharedObject(AuthenticationManagerBuilder.class)
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder) // <-- use injected bean
                .and()
                .build();*/
        AuthenticationManagerBuilder authBuilder =
                http.getSharedObject(AuthenticationManagerBuilder.class);
        authBuilder
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder);
        return authBuilder.build();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtUtil, userDetailsService);
    }

    //Το filter ως ξεχωριστό bean (όχι constructor)
    @Bean
    public SecurityHeadersFilter securityHeadersFilter() {
        return new SecurityHeadersFilter();
    }
    //Ζήτησέ το ως method parameter
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,
                                           CorsConfigurationSource corsConfigurationSource,
                                           SecurityHeadersFilter securityHeadersFilter) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(c -> c.configurationSource(corsConfigurationSource))
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**", "/api/auth/**",
                                "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                        .requestMatchers("/api/products/**", "/api/categories/**").permitAll()
                        .requestMatchers("/uploads/**").permitAll()   // αν σερβίρεις images από το filesystem
                        .requestMatchers(org.springframework.http.HttpMethod.OPTIONS, "/**").permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(securityHeadersFilter, UsernamePasswordAuthenticationFilter.class)
                .httpBasic(httpBasic -> httpBasic.disable());

        return http.build();
    }
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        var cfg = new org.springframework.web.cors.CorsConfiguration();
        cfg.setAllowedOrigins(java.util.List.of("http://localhost:5173"));
        cfg.setAllowedMethods(java.util.List.of("GET","POST","PUT","PATCH","DELETE","OPTIONS"));
        cfg.setAllowedHeaders(java.util.List.of("Authorization","Content-Type"));
        cfg.setAllowCredentials(true);
        var source = new org.springframework.web.cors.UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", cfg);
        return source;
    }

    //@Bean
    //public SecurityHeadersFilter securityHeadersFilter() {
     //   return new SecurityHeadersFilter();
    //}
}



//This does two things:
        //Disables CSRF (ok for a pure API).
        //Permits anyone to call GET /api/products (and other HTTP methods on that path).
        //Requires authentication on any other endpoint, via HTTP Basic.