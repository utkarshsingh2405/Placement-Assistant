package com.placementassist.shared.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

/**
 * Reusable JWT security filter that validates Portal-issued JWTs across all pillars.
 * Optionally integrates with JitUserProvisioner to dynamically provision local entities.
 */
@Component
public class PortalJwtFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(PortalJwtFilter.class);

    private final JwtService jwtService;
    private final ObjectProvider<JitUserProvisioner> jitUserProvisionerProvider;
    private final String integrationApiKey;

    public PortalJwtFilter(
            JwtService jwtService,
            ObjectProvider<JitUserProvisioner> jitUserProvisionerProvider,
            @Value("${institute.integration.api-key:inst-sec-key-7f9a12b4e8c345d6}") String integrationApiKey) {
        this.jwtService = jwtService;
        this.jitUserProvisionerProvider = jitUserProvisionerProvider;
        this.integrationApiKey = integrationApiKey;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        // Check for Integration API Key (inter-service communication)
        String apiKey = request.getHeader("X-API-KEY");
        if (apiKey != null && apiKey.equals(integrationApiKey)) {
            UserDetails apiUser = User.builder()
                    .username("system-integration")
                    .password("")
                    .authorities(new SimpleGrantedAuthority("ROLE_ADMIN"), new SimpleGrantedAuthority("ROLE_TPO"))
                    .build();
            var authToken = new UsernamePasswordAuthenticationToken(apiUser, null, apiUser.getAuthorities());
            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authToken);
            filterChain.doFilter(request, response);
            return;
        }

        // Process Bearer JWT Token
        final String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String jwt = authHeader.substring(7);

        try {
            if (jwtService.isTokenValid(jwt)) {
                String email = jwtService.extractUsername(jwt);
                Long uid = jwtService.extractUid(jwt);
                Role role = jwtService.extractRole(jwt);
                String rollNo = jwtService.extractRollNo(jwt);
                String name = jwtService.extractName(jwt);
                Long companyId = jwtService.extractCompanyId(jwt);

                if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    UserDetails userDetails;

                    JitUserProvisioner provisioner = jitUserProvisionerProvider.getIfAvailable();
                    if (provisioner != null && role != null) {
                        userDetails = provisioner.provisionOrGetUser(email, uid, role, rollNo, name, companyId);
                    } else {
                        var authorities = role != null
                                ? Collections.singletonList(new SimpleGrantedAuthority(role.withPrefix()))
                                : Collections.singletonList(new SimpleGrantedAuthority("ROLE_STUDENT"));

                        userDetails = User.builder()
                                .username(email)
                                .password("")
                                .authorities(authorities)
                                .build();
                    }

                    var authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (Exception ex) {
            log.debug("Failed to authenticate JWT: {}", ex.getMessage());
        }

        filterChain.doFilter(request, response);
    }
}
