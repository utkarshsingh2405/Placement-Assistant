package com.placementassist.institute.modules.integration.dto;

public record IntegrationStudentDto(
        Long id,
        String name,
        String rollNo,
        String branch,
        Integer year,
        Double cgpa,
        Integer backlogs,
        Boolean placementEligible,
        String email,
        String phone
) {}
