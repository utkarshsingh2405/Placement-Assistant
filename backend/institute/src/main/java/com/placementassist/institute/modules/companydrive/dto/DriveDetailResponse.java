package com.placementassist.institute.modules.companydrive.dto;

import com.placementassist.institute.common.enums.DriveStatus;
import com.placementassist.institute.common.enums.RoundType;
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
public class DriveDetailResponse {

    private Long id;
    private CompanyResponse company;
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
    private long eligibleStudentsCount;
    private List<RoundSummary> rounds;
    private LocalDateTime createdAt;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RoundSummary {
        private Long id;
        private RoundType roundType;
        private Integer orderNo;
        private String roundDate;
        private String venue;
        private Integer durationMins;
        private Double cutoff;
        private String instructions;
    }
}
