package com.placementassist.institute.modules.driveround.dto;

import com.placementassist.institute.common.enums.RoundType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DriveRoundResponse {

    private Long id;
    private Long driveId;
    private String driveTitle;
    private RoundType roundType;
    private Integer orderNo;
    private LocalDate roundDate;
    private String venue;
    private Integer durationMins;
    private Double cutoff;
    private String instructions;
    private LocalDateTime createdAt;
}
