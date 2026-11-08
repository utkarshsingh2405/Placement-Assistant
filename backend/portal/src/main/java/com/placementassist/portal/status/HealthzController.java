package com.placementassist.portal.status;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Universal liveness endpoint for Portal.
 */
@RestController
public class HealthzController {

    @GetMapping({"/healthz", "/api/health"})
    public ResponseEntity<Map<String, Object>> healthz() {
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "service", "placement-assist-portal",
                "timestamp", System.currentTimeMillis()
        ));
    }
}
