package com.placementassist.portal.config;

import com.placementassist.shared.security.JwtService;
import com.placementassist.shared.security.Role;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.List;

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class SecurityConfig {

    private final JwtService jwtService;

    public SecurityConfig(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public org.springframework.web.cors.reactive.CorsConfigurationSource corsConfigurationSource() {
        org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource source =
                new org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource();
        org.springframework.web.cors.CorsConfiguration config = new org.springframework.web.cors.CorsConfiguration();
        config.addAllowedOriginPattern("*");
        config.setAllowedHeaders(List.of("*"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS", "HEAD"));
        config.setExposedHeaders(List.of("Authorization", "Content-Disposition", "X-Total-Count"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeExchange(exchanges -> exchanges
                        // Public Portal Static Pages & Assets
                        .pathMatchers(
                                "/",
                                "/index.html",
                                "/login.html",
                                "/register.html",
                                "/forgot-password.html",
                                "/reset-password.html",
                                "/choose-domain.html",
                                "/unavailable.html",
                                "/profile.html",
                                "/offline.html",
                                "/manifest.webmanifest",
                                "/sw.js",
                                "/favicon.ico",
                                "/css/**",
                                "/js/**",
                                "/assets/**"
                        ).permitAll()
                        // Public Auth, Health and Status Endpoints
                        .pathMatchers(
                                "/api/auth/login",
                                "/api/auth/register",
                                "/api/auth/refresh",
                                "/api/auth/forgot-password",
                                "/api/auth/reset-password",
                                "/api/status/**",
                                "/healthz",
                                "/actuator/**",
                                "/fallback/**",
                                "/error"
                        ).permitAll()
                        // Downstream pillar routes - permitted through gateway to let pillar security handle them
                        .pathMatchers("/institute/**", "/placement/**").permitAll()
                        // Admin restricted endpoints
                        .pathMatchers("/api/admin/**", "/admin/**").hasRole("ADMIN")
                        // Authenticated endpoints
                        .pathMatchers("/api/auth/me", "/api/auth/change-password").authenticated()
                        .anyExchange().permitAll()
                )
                .addFilterAt(jwtAuthenticationWebFilter(), SecurityWebFiltersOrder.AUTHENTICATION)
                .build();
    }

    private WebFilter jwtAuthenticationWebFilter() {
        return (ServerWebExchange exchange, WebFilterChain chain) -> {
            String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                if (jwtService.isTokenValid(token)) {
                    try {
                        String username = jwtService.extractUsername(token);
                        Role role = jwtService.extractRole(token);
                        List<GrantedAuthority> authorities = role != null
                                ? List.of(new SimpleGrantedAuthority("ROLE_" + role.name()))
                                : List.of();
                        Authentication auth = new UsernamePasswordAuthenticationToken(username, null, authorities);
                        return chain.filter(exchange)
                                .contextWrite(ReactiveSecurityContextHolder.withAuthentication(auth));
                    } catch (Exception ignored) {}
                }
            }
            return chain.filter(exchange);
        };
    }
}
