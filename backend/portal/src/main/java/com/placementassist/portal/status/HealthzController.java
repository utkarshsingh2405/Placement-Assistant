package com.placementassist.portal.status;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * Universal liveness endpoint for Portal Gateway.
 */
@RestController
public class HealthzController {

    @GetMapping({"/healthz", "/api/health"})
    public Mono<ResponseEntity<Map<String, Object>>> healthz() {
        return Mono.just(ResponseEntity.ok(Map.of(
                "status", "UP",
                "service", "placement-assist-portal",
                "timestamp", System.currentTimeMillis()
        )));
    }
}
