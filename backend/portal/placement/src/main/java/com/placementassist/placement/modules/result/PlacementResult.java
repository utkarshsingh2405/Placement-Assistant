package com.placementassist.placement.modules.result;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "placement_results")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlacementResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "drive_id", nullable = false)
    private Long driveId;

    @Column(name = "stage_id", nullable = false)
    private Long stageId;

    @Column(name = "student_id")
    private Long studentId;

    @Column(name = "roll_no", nullable = false)
    private String rollNo;

    @Column(name = "student_name", nullable = false)
    private String studentName;

    @Column(name = "result_status", nullable = false)
    private String resultStatus; // SELECTED, NON_SELECTED, WAITLIST

    @Column(name = "round_feedback", columnDefinition = "TEXT")
    private String roundFeedback;

    @Column(name = "marked_by", nullable = false)
    private String markedBy;

    @CreationTimestamp
    @Column(name = "marked_at", nullable = false)
    private LocalDateTime markedAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
