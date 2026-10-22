package com.placementassist.portal.status;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Service
public class PillarHealthService {

    private final RestClient restClient;
    private final String instituteUrl;
    private final String placementUrl;

    public PillarHealthService(
            @Value("${pillar.institute.url:http://localhost:8081}") String instituteUrl,
            @Value("${pillar.placement.url:http://localhost:8082}") String placementUrl) {
        this.instituteUrl = instituteUrl;
        this.placementUrl = placementUrl;
        this.restClient = RestClient.builder().build();
    }

    public Map<String, PillarHealth> checkAllPillars() {
        Map<String, PillarHealth> result = new HashMap<>();
        result.put("institute", checkPillarWithFallback("Institute", instituteUrl, "/institute/actuator/health", "/institute/index.html"));
        result.put("placement", checkPillarWithFallback("Placement", placementUrl, "/placement/actuator/health", "/placement/index.html"));
        return result;
    }

    public PillarHealth checkPillarWithFallback(String name, String baseUrl, String primaryPath, String fallbackPath) {
        PillarHealth health = checkPillar(name, baseUrl, primaryPath);
        if ("UP".equals(health.getStatus())) {
            return health;
        }
        PillarHealth fallback = checkPillar(name, baseUrl, fallbackPath);
        if ("UP".equals(fallback.getStatus())) {
            return fallback;
        }
        return health;
    }

    public PillarHealth checkPillar(String name, String baseUrl, String healthPath) {
        long start = System.currentTimeMillis();
        try {
            String target = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
            var response = restClient.get()
                    .uri(target + healthPath)
                    .retrieve()
                    .toBodilessEntity();

            long latency = System.currentTimeMillis() - start;
            boolean up = response.getStatusCode().is2xxSuccessful();

            return PillarHealth.builder()
                    .name(name)
                    .status(up ? "UP" : "DEGRADED")
                    .latencyMs(latency)
                    .url(baseUrl)
                    .build();
        } catch (Exception ex) {
            long latency = System.currentTimeMillis() - start;
            return PillarHealth.builder()
                    .name(name)
                    .status("DOWN")
                    .latencyMs(latency)
                    .url(baseUrl)
                    .error(ex.getMessage())
                    .build();
        }
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PillarHealth {
        private String name;
        private String status;
        private long latencyMs;
        private String url;
        private String error;
    }
}
