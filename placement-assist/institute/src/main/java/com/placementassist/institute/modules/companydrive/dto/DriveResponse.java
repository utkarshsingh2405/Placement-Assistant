package com.placementassist.institute.modules.companydrive.dto;

import com.placementassist.institute.common.enums.DriveStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DriveResponse {

    private Long id;
    private Long companyId;
    private String companyName;
    private String companyLogoUrl;
    private String title;
    private LocalDate driveDate;
    private LocalDate lastDateToApply;
    private Double eligibilityCgpa;
    private List<String> eligibleBranches;
    private Integer maxBacklogs;
    private List<String> roles;
    private Double packageMin;
    private Double packageMax;
    private String location;
    private DriveStatus status;
    private String description;
    private LocalDateTime createdAt;
}
