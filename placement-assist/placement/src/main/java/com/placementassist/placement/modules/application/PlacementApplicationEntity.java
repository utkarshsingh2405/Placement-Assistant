package com.placementassist.placement.modules.application;

import com.placementassist.placement.modules.drive.PlacementDrive;
import com.placementassist.placement.modules.stage.PlacementStage;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "placement_applications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlacementApplicationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "drive_id", nullable = false)
    private PlacementDrive drive;

    @Column(name = "student_id")
    private Long studentId;

    @Column(name = "roll_no", nullable = false)
    private String rollNo;

    @Column(name = "student_name", nullable = false)
    private String studentName;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    @Builder.Default
    private String status = "APPLIED"; // APPLIED, ELIGIBLE, INELIGIBLE, SHORTLISTED, SELECTED, REJECTED, WITHDRAWN

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "current_stage_id")
    private PlacementStage currentStage;

    @Column(name = "resume_url")
    private String resumeUrl;

    @Column(name = "ats_score")
    private Integer atsScore;

    @CreationTimestamp
    @Column(name = "applied_at", nullable = false, updatable = false)
    private LocalDateTime appliedAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
