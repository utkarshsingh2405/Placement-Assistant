package com.placementassist.shared.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when an authenticated user lacks required permissions (HTTP 403).
 */
public class ForbiddenException extends ApiException {

    public ForbiddenException(String message) {
        super(HttpStatus.FORBIDDEN, "FORBIDDEN", message);
    }
}
