package com.placementassist.shared.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when authentication fails or credentials are invalid (HTTP 401).
 */
public class UnauthorizedException extends ApiException {

    public UnauthorizedException(String message) {
        super(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", message);
    }
}
