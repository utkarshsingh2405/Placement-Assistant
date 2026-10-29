package com.placementassist.institute.modules.student.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateStudentRequest {

    @NotBlank(message = "Name cannot be blank")
    private String name;

    private String phone;

    private String branch;

    private Integer year;

    @DecimalMin(value = "0.0", message = "CGPA must be at least 0.0")
    @DecimalMax(value = "10.0", message = "CGPA cannot exceed 10.0")
    private Double cgpa;

    @Min(value = 0, message = "Backlogs cannot be negative")
    private Integer backlogs;

    private List<String> skills;

    private String linkedin;

    private String github;

    private String photoUrl;
}
