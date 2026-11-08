package com.placementassist.portal.auth;

import com.placementassist.shared.security.Role;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

/**
 * On first boot, attempts to import existing users from the Institute pillar's integration endpoint.
 * Waits for ApplicationReadyEvent and retries so loopback calls succeed once the embedded web server is running.
 */
@Component
public class InstituteUserImporter {

    private static final Logger log = LoggerFactory.getLogger(InstituteUserImporter.class);

    private final PortalUserRepository userRepository;
    private final RestClient restClient;
    private final String instituteUrl;
    private final String instituteApiKey;
    private final boolean enabled;

    public InstituteUserImporter(
            PortalUserRepository userRepository,
            @Value("${pillar.institute.url:http://localhost:8080}") String instituteUrl,
            @Value("${pillar.institute.api-key:}") String instituteApiKey,
            @Value("${portal.import-institute-users:true}") boolean enabled) {
        this.userRepository = userRepository;
        this.restClient = RestClient.builder().build();
        this.instituteUrl = instituteUrl;
        this.instituteApiKey = instituteApiKey;
        this.enabled = enabled;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        if (!enabled) {
            return;
        }

        String base = instituteUrl.endsWith("/")
                ? instituteUrl.substring(0, instituteUrl.length() - 1)
                : instituteUrl;
        if (!base.endsWith("/institute")) {
            base = base + "/institute";
        }
        String url = base + "/api/integration/users/export";

        // Retry loop to ensure local loopback server is accepting connections
        int maxAttempts = 3;
        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                log.info("Checking Institute pillar at {} for existing users to sync (attempt {}/{})...", url, attempt, maxAttempts);

                var response = restClient.get()
                        .uri(url)
                        .header("X-API-KEY", instituteApiKey)
                        .retrieve()
                        .body(new ParameterizedTypeReference<Map<String, Object>>() {});

                if (response != null && response.containsKey("data")) {
                    @SuppressWarnings("unchecked")
                    List<Map<String, Object>> userList = (List<Map<String, Object>>) response.get("data");
                    if (userList != null && !userList.isEmpty()) {
                        int importedCount = 0;
                        for (Map<String, Object> u : userList) {
                            String email = (String) u.get("email");
                            if (email != null && !userRepository.existsByEmail(email.toLowerCase().trim())) {
                                String passwordHash = (String) u.get("passwordHash");
                                String roleStr = (String) u.get("role");
                                String name = (String) u.get("name");
                                String rollNo = (String) u.get("rollNo");

                                Role role = Role.STUDENT;
                                if (roleStr != null) {
                                    try {
                                        role = Role.valueOf(roleStr.toUpperCase().trim());
                                    } catch (IllegalArgumentException ignored) {}
                                }

                                PortalUser user = PortalUser.builder()
                                        .email(email.toLowerCase().trim())
                                        .passwordHash(passwordHash != null ? passwordHash : "$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.AQubh4a")
                                        .role(role)
                                        .name(name != null ? name : email)
                                        .rollNo(rollNo)
                                        .active(true)
                                        .build();
                                userRepository.save(user);
                                importedCount++;
                            }
                        }
                        log.info("Institute user sync complete: {} new users imported into Portal.", importedCount);
                    }
                }
                return; // Succeeded!
            } catch (Exception ex) {
                if (attempt < maxAttempts) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ignored) {}
                } else {
                    log.info("Institute service was not reachable during startup user sync ({}). Skipping initial import.", ex.getMessage());
                }
            }
        }
    }
}
