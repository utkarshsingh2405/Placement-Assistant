package com.placementassist.placement.integration.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ResumeDto {
    private Long studentId;
    private String rollNo;
    private String fileUrl;
    private Integer atsScore;
    private String status;
    private Integer version;
}
