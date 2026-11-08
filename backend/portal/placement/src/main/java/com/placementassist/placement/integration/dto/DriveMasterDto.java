package com.placementassist.placement.integration.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class DriveMasterDto {
    private Long id;
    private String title;
    private String companyName;
    private String companyWebsite;
    private String roles;
    private List<String> rolesList;
    private Double packageMin;
    private Double packageMax;
    private Double eligibilityCgpa;
    private String eligibleBranches;
    private List<String> eligibleBranchesList;
    private Integer maxBacklogs;
    private String status;
    private LocalDate lastDateToApply;
    private LocalDate driveDate;
    private String location;
}
