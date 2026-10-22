package com.placementassist.institute.modules.companydrive;

import com.placementassist.institute.common.BaseEntity;
import com.placementassist.institute.common.enums.DriveStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Campus placement drive entity.
 */
@Entity
@Table(name = "drives")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Drive extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "drive_date", nullable = false)
    private LocalDate driveDate;

    @Column(name = "last_date_to_apply", nullable = false)
    private LocalDate lastDateToApply;

    @Column(name = "eligibility_cgpa", nullable = false)
    @Builder.Default
    private Double eligibilityCgpa = 6.0;

    @Column(name = "eligible_branches", nullable = false)
    private String eligibleBranches; // Stored as comma-separated: CSE,IT,ECE

    @Column(name = "max_backlogs", nullable = false)
    @Builder.Default
    private Integer maxBacklogs = 0;

    @Column(name = "roles", nullable = false)
    private String roles; // Stored as comma-separated: Software Engineer, QA Analyst

    @Column(name = "package_min")
    private Double packageMin;

    @Column(name = "package_max")
    private Double packageMax;

    @Column(name = "location")
    private String location;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private DriveStatus status = DriveStatus.UPCOMING;

    @Column(name = "description", length = 3000)
    private String description;

    public List<String> getEligibleBranchesList() {
        if (eligibleBranches == null || eligibleBranches.isBlank()) return new ArrayList<>();
        return Arrays.stream(eligibleBranches.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();
    }

    public void setEligibleBranchesList(List<String> branches) {
        if (branches == null || branches.isEmpty()) {
            this.eligibleBranches = "";
        } else {
            this.eligibleBranches = String.join(",", branches);
        }
    }

    public List<String> getRolesList() {
        if (roles == null || roles.isBlank()) return new ArrayList<>();
        return Arrays.stream(roles.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();
    }

    public void setRolesList(List<String> roleList) {
        if (roleList == null || roleList.isEmpty()) {
            this.roles = "";
        } else {
            this.roles = String.join(",", roleList);
        }
    }
}
