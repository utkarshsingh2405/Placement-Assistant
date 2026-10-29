package com.placementassist.institute.modules.resume;

import com.placementassist.institute.common.BaseEntity;
import com.placementassist.institute.common.enums.ResumeStatus;
import com.placementassist.institute.modules.student.Student;
import jakarta.persistence.*;
import lombok.*;

/**
 * Student resume entity containing PDF file storage reference,
 * versioning, ATS analysis score and breakdown, and staff review.
 */
@Entity
@Table(name = "resumes")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Resume extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @Column(name = "file_url", nullable = false)
    private String fileUrl;

    @Column(name = "version", nullable = false)
    private Integer version;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ResumeStatus status;

    @Column(name = "ats_score")
    private Integer atsScore;

    @Column(name = "ats_breakdown", columnDefinition = "TEXT")
    private String atsBreakdown; // Stored JSON breakdown

    @Column(name = "feedback", columnDefinition = "TEXT")
    private String feedback;

    @Column(name = "reviewed_by")
    private String reviewedBy;
}
