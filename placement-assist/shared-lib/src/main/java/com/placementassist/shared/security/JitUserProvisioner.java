package com.placementassist.shared.security;

import org.springframework.security.core.userdetails.UserDetails;

/**
 * Interface enabling downstream pillars (Institute and Placement) to dynamically
 * look up or provision a local user account on-the-fly when presented with a valid Portal JWT.
 */
public interface JitUserProvisioner {

    /**
     * Provisions or loads a local UserDetails based on Portal JWT claims.
     *
     * @param email User email (sub)
     * @param uid Unique portal user id
     * @param role User role
     * @param rollNo Student roll number (if student)
     * @param name User full name
     * @param companyId Company ID (if company user)
     * @return Initialized UserDetails for SecurityContext
     */
    UserDetails provisionOrGetUser(String email, Long uid, Role role, String rollNo, String name, Long companyId);
}
