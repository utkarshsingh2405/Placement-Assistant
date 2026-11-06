package com.placementassist.portal.status;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * PillarHealthService: Probes Institute (8081) and Placement (8082) health status.
 * Uses standard Java SE HttpClient (no external HTTP dependencies) with strict timeouts.
 */
@Service
public class PillarHealthService {

    private final HttpClient httpClient;
    private final String instituteUrl;
    private final String placementUrl;

    public PillarHealthService(
            @Value("${pillar.institute.url:http://localhost:8081}") String instituteUrl,
            @Value("${pillar.placement.url:http://localhost:8082}") String placementUrl) {
        this.instituteUrl = instituteUrl;
        this.placementUrl = placementUrl;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofMillis(800))
                .build();
    }

    public Map<String, PillarHealth> checkAllPillars() {
        Map<String, PillarHealth> result = new HashMap<>();
        result.put("institute", checkPillarWithFallback("Institute", instituteUrl, "/institute/healthz", "/institute/index.html"));
        result.put("placement", checkPillarWithFallback("Placement", placementUrl, "/placement/healthz", "/placement/index.html"));
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
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(target + healthPath))
                    .timeout(Duration.ofMillis(1200))
                    .GET()
                    .build();

            HttpResponse<Void> response = httpClient.send(request, HttpResponse.BodyHandlers.discarding());

            long latency = System.currentTimeMillis() - start;
            boolean up = response.statusCode() >= 200 && response.statusCode() < 300;

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

    public static class PillarHealth {
        private String name;
        private String status;
        private long latencyMs;
        private String url;
        private String error;

        public PillarHealth() {}

        public PillarHealth(String name, String status, long latencyMs, String url, String error) {
            this.name = name;
            this.status = status;
            this.latencyMs = latencyMs;
            this.url = url;
            this.error = error;
        }

        public static Builder builder() {
            return new Builder();
        }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }

        public long getLatencyMs() { return latencyMs; }
        public void setLatencyMs(long latencyMs) { this.latencyMs = latencyMs; }

        public String getUrl() { return url; }
        public void setUrl(String url) { this.url = url; }

        public String getError() { return error; }
        public void setError(String error) { this.error = error; }

        public static class Builder {
            private String name;
            private String status;
            private long latencyMs;
            private String url;
            private String error;

            public Builder name(String name) { this.name = name; return this; }
            public Builder status(String status) { this.status = status; return this; }
            public Builder latencyMs(long latencyMs) { this.latencyMs = latencyMs; return this; }
            public Builder url(String url) { this.url = url; return this; }
            public Builder error(String error) { this.error = error; return this; }

            public PillarHealth build() {
                return new PillarHealth(name, status, latencyMs, url, error);
            }
        }
    }
}
