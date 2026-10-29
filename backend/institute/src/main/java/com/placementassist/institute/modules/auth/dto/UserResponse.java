package com.placementassist.institute.modules.auth.dto;

import com.placementassist.shared.security.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

    private Long id;
    private String email;
    private Role role;
    private String name;
    private Long studentId;
    private String rollNo;
    private String branch;
    private Integer year;
    private Double cgpa;
    private Integer backlogs;
    private Boolean placementEligible;
    private LocalDateTime createdAt;
}
