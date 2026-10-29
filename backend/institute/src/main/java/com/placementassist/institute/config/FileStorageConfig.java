package com.placementassist.institute.config;

import com.placementassist.shared.exception.ApiException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.UUID;

/**
 * File storage management service for uploaded documents (resumes).
 */
@Configuration
public class FileStorageConfig {

    private final Path uploadLocation;

    public FileStorageConfig(@Value("${storage.upload-dir:./uploads/resumes}") String uploadDir) {
        this.uploadLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.uploadLocation);
        } catch (Exception ex) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "STORAGE_INIT_FAILED",
                    "Could not initialize upload storage folder: " + ex.getMessage());
        }
    }

    public Path getUploadLocation() {
        return uploadLocation;
    }

    /**
     * Stores a MultipartFile ensuring it is a PDF and under the 5 MB limit.
     */
    public String storeFile(MultipartFile file, String subFolder) {
        if (file.isEmpty()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "EMPTY_FILE", "Failed to store empty file");
        }

        String originalFilename = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        if (!originalFilename.toLowerCase().endsWith(".pdf")) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "INVALID_FILE_TYPE", "Only PDF files are accepted for resumes");
        }

        try {
            Path targetDir = subFolder != null ? uploadLocation.resolve(subFolder) : uploadLocation;
            Files.createDirectories(targetDir);

            String storedFilename = UUID.randomUUID() + "_" + originalFilename.replaceAll("\\s+", "_");
            Path targetPath = targetDir.resolve(storedFilename);

            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, targetPath, StandardCopyOption.REPLACE_EXISTING);
            }

            return subFolder != null ? subFolder + "/" + storedFilename : storedFilename;
        } catch (IOException ex) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "FILE_STORE_ERROR",
                    "Failed to save file: " + ex.getMessage());
        }
    }

    public Path loadFileAsPath(String relativePath) {
        return uploadLocation.resolve(relativePath).normalize();
    }
}
