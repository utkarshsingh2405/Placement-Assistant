package com.placementassist.portal.auth;

import com.placementassist.shared.security.Role;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

/**
 * On first boot, attempts to import existing users from the Institute pillar's integration endpoint.
 * Preserves pre-hashed BCrypt passwords so existing students and staff can log in with their existing credentials.
 */
@Component
public class InstituteUserImporter implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(InstituteUserImporter.class);

    private final PortalUserRepository userRepository;
    private final RestClient restClient;
    private final String instituteUrl;
    private final String instituteApiKey;
    private final boolean enabled;

    public InstituteUserImporter(
            PortalUserRepository userRepository,
            @Value("${pillar.institute.url:http://localhost:8081}") String instituteUrl,
            @Value("${pillar.institute.api-key:inst-sec-key-7f9a12b4e8c345d6}") String instituteApiKey,
            @Value("${portal.import-institute-users:true}") boolean enabled) {
        this.userRepository = userRepository;
        this.restClient = RestClient.builder().build();
        this.instituteUrl = instituteUrl;
        this.instituteApiKey = instituteApiKey;
        this.enabled = enabled;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (!enabled) {
            return;
        }

        try {
            String base = instituteUrl.endsWith("/")
                    ? instituteUrl.substring(0, instituteUrl.length() - 1)
                    : instituteUrl;
            if (!base.endsWith("/institute")) {
                base = base + "/institute";
            }
            String url = base + "/api/integration/users/export";

            log.info("Checking Institute pillar at {} for existing users to sync...", url);

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
        } catch (Exception ex) {
            log.info("Institute service was not reachable during startup user sync ({}). Skipping initial import.", ex.getMessage());
        }
    }
}
