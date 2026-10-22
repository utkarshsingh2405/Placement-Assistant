package com.placementassist.portal.config;

import com.placementassist.shared.security.JwtService;
import com.placementassist.shared.security.Role;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;

@Configuration
public class GatewayRoutesConfig {

    private final JwtService jwtService;
    private final String instituteUrl;
    private final String placementUrl;

    public GatewayRoutesConfig(
            JwtService jwtService,
            @Value("${pillar.institute.url:http://localhost:8081}") String instituteUrl,
            @Value("${pillar.placement.url:http://localhost:8082}") String placementUrl) {
        this.jwtService = jwtService;
        this.instituteUrl = instituteUrl;
        this.placementUrl = placementUrl;
    }

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("institute-route", r -> r
                        .path("/institute/**")
                        .filters(f -> f.filter(authContextFilter()))
                        .uri(instituteUrl))
                .route("placement-route", r -> r
                        .path("/placement/**")
                        .filters(f -> f.filter(authContextFilter()))
                        .uri(placementUrl))
                .route("institute-pages-fallback", r -> r
                        .path("/pages/**")
                        .filters(f -> f.prefixPath("/institute").filter(authContextFilter()))
                        .uri(instituteUrl))
                .build();
    }

    private GatewayFilter authContextFilter() {
        return (exchange, chain) -> {
            String auth = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            if (auth != null && auth.startsWith("Bearer ")) {
                String token = auth.substring(7);
                if (jwtService.isTokenValid(token)) {
                    var builder = exchange.getRequest().mutate();
                    String email = jwtService.extractUsername(token);
                    Long uid = jwtService.extractUserId(token);
                    Role role = jwtService.extractRole(token);
                    var claims = jwtService.extractClaims(token);

                    if (email != null) builder.header("X-User-Email", email);
                    if (uid != null) builder.header("X-User-Id", String.valueOf(uid));
                    if (role != null) builder.header("X-User-Role", role.name());
                    if (claims.get("rollNo") != null) builder.header("X-User-RollNo", claims.get("rollNo").toString());
                    if (claims.get("companyId") != null) builder.header("X-User-CompanyId", claims.get("companyId").toString());

                    return chain.filter(exchange.mutate().request(builder.build()).build());
                }
            }
            return chain.filter(exchange);
        };
    }
}
