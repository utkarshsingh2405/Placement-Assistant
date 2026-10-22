package com.placementassist.institute.modules.driveround.dto;

import com.placementassist.institute.common.enums.RoundResultStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoundResultResponse {

    private Long id;
    private Long roundId;
    private Long studentId;
    private String studentRollNo;
    private String studentName;
    private String studentBranch;
    private Double score;
    private RoundResultStatus result;
    private String remarks;
    private LocalDateTime createdAt;
}
