package com.placementassist.shared.security;

/**
 * User roles across the placement assistance system.
 */
public enum Role {
    STUDENT,
    ADMIN,
    TPO,
    DIRECTOR,
    COMPANY;

    public String withPrefix() {
        return "ROLE_" + this.name();
    }
}
