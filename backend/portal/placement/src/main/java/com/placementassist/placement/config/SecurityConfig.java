package com.placementassist.placement.config;

import com.placementassist.shared.security.JwtAuthFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    @Order(2)
    public SecurityFilterChain placementSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/placement/**")
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .headers(headers -> headers
                        .frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin)
                        .contentTypeOptions(Customizer.withDefaults())
                        .xssProtection(Customizer.withDefaults())
                )
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/placement/**").permitAll()
                        // Public frontend assets & pages
                        .requestMatchers(
                                "/placement",
                                "/placement/",
                                "/placement/index.html",
                                "/placement/dashboard.html",
                                "/placement/pages/**",
                                "/placement/css/**",
                                "/placement/js/**",
                                "/placement/assets/**",
                                "/placement/icons/**",
                                "/placement/textures/**",
                                "/placement/favicon.ico",
                                "/placement/manifest.json",
                                "/placement/robots.txt",
                                "/placement/sitemap.html",
                                "/placement/sitemap.xml",
                                "/placement/healthz",
                                "/placement/error"
                        ).permitAll()
                        // Public endpoints (highlights ticker, placed lookup)
                        .requestMatchers(
                                "/placement/api/public/**",
                                "/placement/actuator/**"
                        ).permitAll()
                        // All recruitment operational APIs require authenticated token
                        .requestMatchers("/placement/api/**").authenticated()
                        .anyRequest().permitAll()
                )
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setStatus(jakarta.servlet.http.HttpServletResponse.SC_UNAUTHORIZED);
                            response.setContentType("application/json");
                            response.getWriter().write("{\"success\":false,\"error\":{\"code\":\"UNAUTHORIZED\",\"message\":\"Authentication required or token expired\"}}");
                        })
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
