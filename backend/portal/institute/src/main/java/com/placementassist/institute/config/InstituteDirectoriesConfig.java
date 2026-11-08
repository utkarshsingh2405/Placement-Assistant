package com.placementassist.institute.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class InstituteDirectoriesConfig {

    private static final Logger log = LoggerFactory.getLogger(InstituteDirectoriesConfig.class);

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
