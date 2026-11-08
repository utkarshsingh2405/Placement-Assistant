package com.placementassist.institute.config;

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

/**
 * Spring Security configuration for Institute Pillar.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    @Order(1)
    public SecurityFilterChain instituteSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/institute/**")
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .headers(headers -> headers
                        .frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin)
                        .contentTypeOptions(Customizer.withDefaults())
                        .xssProtection(Customizer.withDefaults())
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/institute/**").permitAll()
                        // Public frontend static files & assets
                        .requestMatchers(
                                "/institute",
                                "/institute/",
                                "/institute/index.html",
                                "/institute/login.html",
                                "/institute/register.html",
                                "/institute/dashboard.html",
                                "/institute/pages/**",
                                "/institute/css/**",
                                "/institute/js/**",
                                "/institute/assets/**",
                                "/institute/icons/**",
                                "/institute/textures/**",
                                "/institute/favicon.ico",
                                "/institute/manifest.json",
                                "/institute/robots.txt",
                                "/institute/sitemap.html",
                                "/institute/sitemap.xml",
                                "/institute/v3/api-docs/**",
                                "/institute/swagger-ui/**",
                                "/institute/swagger-ui.html",
                                "/institute/error",
                                "/institute/healthz",
                                "/institute/actuator/**"
                        ).permitAll()

                        // Public authentication endpoints
                        .requestMatchers(
                                "/institute/api/auth/login",
                                "/institute/api/auth/register",
                                "/institute/api/auth/refresh"
                        ).permitAll()

                        // Integration endpoints (secured by API key or JWT inside JwtAuthFilter)
                        .requestMatchers("/institute/api/integration/**").authenticated()

                        // All other institute APIs require authentication
                        .requestMatchers("/institute/api/**").authenticated()

                        // Fallback
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
