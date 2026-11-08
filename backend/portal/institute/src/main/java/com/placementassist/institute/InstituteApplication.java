package com.placementassist.institute;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Main Spring Boot Application entrypoint for Placement Assistance - Institute Pillar.
 */
@SpringBootApplication(scanBasePackages = {"com.placementassist.shared", "com.placementassist.institute"})
@EnableScheduling
public class InstituteApplication {

    private static final Logger log = LoggerFactory.getLogger(InstituteApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(InstituteApplication.class, args);
    }

    /**
     * Initializes required storage and database directories on startup.
     */
    @Bean
    public CommandLineRunner initDirectories(
            @Value("${storage.upload-dir:./uploads/resumes}") String uploadDir,
            @Value("${DB_PATH:./data/institute.db}") String dbPath) {
        return args -> {
            try {
                // Ensure data directory exists for SQLite
                Path dbFile = Paths.get(dbPath);
                Path dbParent = dbFile.getParent();
                if (dbParent != null && !Files.exists(dbParent)) {
                    Files.createDirectories(dbParent);
                    log.info("Created database directory: {}", dbParent.toAbsolutePath());
                }

                // Ensure uploads directory exists
                Path uploadPath = Paths.get(uploadDir);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                    log.info("Created upload directory: {}", uploadPath.toAbsolutePath());
                }
            } catch (Exception e) {
                log.error("Failed to initialize required directories: {}", e.getMessage(), e);
            }
        };
    }
}
