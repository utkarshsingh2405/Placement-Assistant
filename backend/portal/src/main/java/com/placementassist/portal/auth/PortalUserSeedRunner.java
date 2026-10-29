package com.placementassist.portal.auth;

import com.placementassist.shared.security.Role;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Seeds default administrative, institutional, and student users if not already present.
 * Ensures the platform is immediately operational on MySQL and SQLite alike.
 */
@Component
@Order(1)
public class PortalUserSeedRunner implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(PortalUserSeedRunner.class);

    private final PortalUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public PortalUserSeedRunner(PortalUserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        seedIfMissing("admin@college.edu", "Admin@123", Role.ADMIN, "System Administrator", null);
        seedIfMissing("tpo@college.edu", "Tpo@123", Role.TPO, "Prof. Rajesh Sharma", null);
        seedIfMissing("director@college.edu", "Director@123", Role.DIRECTOR, "Dr. V. K. Rao", null);
        seedIfMissing("hr@google.com", "Company@123", Role.COMPANY, "Priya Menon (Google HR)", null);
        seedIfMissing("aarav@college.edu", "Student@123", Role.STUDENT, "Aarav Sharma", "21CS001");
    }

    private void seedIfMissing(String email, String rawPassword, Role role, String name, String rollNo) {
        if (!userRepository.existsByEmail(email.toLowerCase().trim())) {
            PortalUser user = PortalUser.builder()
                    .email(email.toLowerCase().trim())
                    .passwordHash(passwordEncoder.encode(rawPassword))
                    .role(role)
                    .name(name)
                    .rollNo(rollNo)
                    .active(true)
                    .build();
            userRepository.save(user);
            log.info("Seeded default portal user: {} ({})", email, role);
        }
    }
}
