package com.placementassist.institute.modules.integration.dto;

public record IntegrationDriveRoundDto(
        Long id,
        Long driveId,
        Integer orderNo,
        String name,
        String roundType,
        String description
) {}
