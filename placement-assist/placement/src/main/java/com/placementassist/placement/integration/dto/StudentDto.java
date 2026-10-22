package com.placementassist.placement.integration.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class StudentDto {
    private Long id;
    private String name;
    private String rollNo;
    private String branch;
    private Integer year;
    private Double cgpa;
    private Integer backlogs;
    private Boolean placementEligible;
    private String email;
    private String phone;
}
