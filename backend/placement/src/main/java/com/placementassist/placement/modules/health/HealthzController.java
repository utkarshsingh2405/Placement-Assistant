package com.placementassist.placement.modules.health;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Universal liveness and health check endpoint for cloud hosting platforms (Render, Railway, AWS, Docker).
 * Always responds with 200 OK as long as the HTTP container is running.
 */
@RestController
public class HealthzController {

    @GetMapping({"/healthz", "/api/health", "/actuator/health/liveness"})
    public ResponseEntity<Map<String, Object>> health() {
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "service", "placement-assist-placement",
                "timestamp", System.currentTimeMillis()
        ));
    }
}
