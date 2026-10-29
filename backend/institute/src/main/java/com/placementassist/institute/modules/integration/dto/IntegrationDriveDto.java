package com.placementassist.institute.modules.integration.dto;

import com.placementassist.institute.common.enums.DriveStatus;

import java.time.LocalDate;
import java.util.List;

public record IntegrationDriveDto(
        Long id,
        String title,
        String companyName,
        String companyWebsite,
        String roles,
        List<String> rolesList,
        Double packageMin,
        Double packageMax,
        Double eligibilityCgpa,
        String eligibleBranches,
        List<String> eligibleBranchesList,
        Integer maxBacklogs,
        DriveStatus status,
        LocalDate lastDateToApply,
        LocalDate driveDate,
        String location
) {}
