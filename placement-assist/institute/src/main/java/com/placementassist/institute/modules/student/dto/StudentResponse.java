package com.placementassist.institute.modules.student.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentResponse {

    private Long id;
    private Long userId;
    private String name;
    private String rollNo;
    private String email;
    private String phone;
    private String branch;
    private Integer year;
    private Double cgpa;
    private Integer backlogs;
    private List<String> skills;
    private String linkedin;
    private String github;
    private String photoUrl;
    private Boolean placementEligible;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
