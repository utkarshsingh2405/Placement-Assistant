package com.placementassist.institute.modules.integration.dto;

import java.time.LocalDate;

public record IntegrationOpportunityDto(
        Long id,
        String title,
        String organization,
        String type,
        String location,
        String applyUrl,
        LocalDate deadline,
        String stipendOrPackage,
        String eligibility
) {}
