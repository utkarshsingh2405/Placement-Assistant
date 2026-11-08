package com.placementassist.shared.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * Filter that authenticates requests using JWT Bearer token or service API key for integration endpoints.
 */
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final String integrationApiKey;

    public JwtAuthFilter(
            JwtService jwtService,
            @Value("${institute.integration.api-key:}") String integrationApiKey) {
        this.jwtService = jwtService;
        this.integrationApiKey = integrationApiKey;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        // Check for Inter-Service Integration API Key (for Placement pillar consumption)
        String apiKeyHeader = request.getHeader("X-API-KEY");
        if (apiKeyHeader != null && integrationApiKey != null && !integrationApiKey.isBlank() && apiKeyHeader.equals(integrationApiKey)) {
            var auth = new UsernamePasswordAuthenticationToken(
                    "service-placement",
                    null,
                    List.of(new SimpleGrantedAuthority("ROLE_TPO"), new SimpleGrantedAuthority("ROLE_ADMIN"))
            );
            auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(auth);
            filterChain.doFilter(request, response);
            return;
        }

        // Bearer token authentication
        final String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String jwt = authHeader.substring(7).trim();
        if (jwtService.isTokenValid(jwt)) {
            String username = jwtService.extractUsername(jwt);
            Role role = jwtService.extractRole(jwt);
            Long userId = jwtService.extractUserId(jwt);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                var authorities = resolveAuthorities(role);

                var authToken = new UsernamePasswordAuthenticationToken(username, userId, authorities);
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request, response);
    }

    private List<SimpleGrantedAuthority> resolveAuthorities(Role role) {
        if (role == null) {
            return Collections.emptyList();
        }
        if (role == Role.DIRECTOR) {
            return List.of(
                    new SimpleGrantedAuthority("ROLE_DIRECTOR"),
                    new SimpleGrantedAuthority("ROLE_TPO"),
                    new SimpleGrantedAuthority("ROLE_ADMIN")
            );
        }
        if (role == Role.ADMIN) {
            return List.of(
                    new SimpleGrantedAuthority("ROLE_ADMIN"),
                    new SimpleGrantedAuthority("ROLE_TPO"),
                    new SimpleGrantedAuthority("ROLE_DIRECTOR")
            );
        }
        if (role == Role.TPO) {
            return List.of(
                    new SimpleGrantedAuthority("ROLE_TPO"),
                    new SimpleGrantedAuthority("ROLE_ADMIN")
            );
        }
        return List.of(new SimpleGrantedAuthority(role.withPrefix()));
    }
}
