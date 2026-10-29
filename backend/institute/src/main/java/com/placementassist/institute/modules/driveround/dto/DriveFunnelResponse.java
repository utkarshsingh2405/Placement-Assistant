package com.placementassist.institute.modules.driveround.dto;

import com.placementassist.institute.common.enums.RoundType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DriveFunnelResponse {

    private Long driveId;
    private String driveTitle;
    private long totalEligibleStudents;
    private List<FunnelStage> funnel;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FunnelStage {
        private Integer orderNo;
        private RoundType roundType;
        private long passedCount;
    }
}
