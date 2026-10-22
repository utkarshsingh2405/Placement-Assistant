package com.placementassist.portal.config;

import com.placementassist.shared.api.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Map;

@RestController
@RequestMapping("/fallback")
public class FallbackController {

    @RequestMapping(value = "/{pillar}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<Map<String, String>>> jsonFallback(
            @PathVariable String pillar,
            @RequestParam(required = false, defaultValue = "Service temporarily unavailable") String message) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(ApiResponse.<Map<String, String>>builder()
                        .success(false)
                        .message("The " + pillar + " service is temporarily unreachable.")
                        .data(Map.of("pillar", pillar, "reason", message))
                        .build());
    }

    @GetMapping(value = "/{pillar}", produces = MediaType.TEXT_HTML_VALUE)
    public Mono<Void> htmlFallback(@PathVariable String pillar, ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.TEMPORARY_REDIRECT);
        exchange.getResponse().getHeaders().setLocation(URI.create("/unavailable.html?pillar=" + pillar));
        return exchange.getResponse().setComplete();
    }
}
