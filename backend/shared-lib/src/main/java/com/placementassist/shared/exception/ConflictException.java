package com.placementassist.shared.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when a resource conflicts with existing data, e.g. duplicate unique key (HTTP 409).
 */
public class ConflictException extends ApiException {

    public ConflictException(String message) {
        super(HttpStatus.CONFLICT, "DUPLICATE_RESOURCE", message);
    }

    public ConflictException(String resourceName, String field, Object value) {
        super(HttpStatus.CONFLICT, "DUPLICATE_RESOURCE",
                String.format("%s with %s '%s' already exists", resourceName, field, value));
    }
}
