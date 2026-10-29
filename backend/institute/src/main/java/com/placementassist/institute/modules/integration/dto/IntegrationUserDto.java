package com.placementassist.institute.modules.integration.dto;

import com.placementassist.shared.security.Role;

public record IntegrationUserDto(
        Long id,
        String email,
        Role role,
        String name,
        String rollNo,
        String passwordHash
) {}
