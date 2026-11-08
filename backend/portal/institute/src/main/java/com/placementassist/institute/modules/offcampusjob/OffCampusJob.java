package com.placementassist.institute.modules.offcampusjob;

import com.placementassist.institute.common.BaseEntity;
import com.placementassist.institute.common.enums.OpportunityStatus;
import com.placementassist.institute.modules.listing.ListingItem;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "offcampus_jobs")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OffCampusJob extends BaseEntity implements ListingItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "company", nullable = false)
    private String company;

    @Column(name = "role", nullable = false)
    private String role;

    @Column(name = "package_lpa")
    private Double packageLpa; // Annual package in Lakhs Per Annum (e.g. 14.5)

    @Column(name = "location")
    private String location;

    @Column(name = "batch_eligible")
    private String batchEligible; // e.g. "2024, 2025"

    @Column(name = "experience")
    private String experience; // e.g. "Fresher / 0-1 Years"

    @Column(name = "skills_required")
    private String skillsRequired;

    @Column(name = "deadline", nullable = false)
    private LocalDate deadline;

    @Column(name = "apply_url")
    private String applyUrl;

    @Column(name = "how_to_apply", columnDefinition = "TEXT")
    private String howToApply;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private OpportunityStatus status;
}
