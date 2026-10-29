package com.placementassist.placement.modules.attendance;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "placement_attendance")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Attendance {

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

    @CreationTimestamp
    @Column(name = "check_in_time", nullable = false)
    private LocalDateTime checkInTime;

    @Column(name = "check_in_mode", nullable = false)
    @Builder.Default
    private String checkInMode = "QR_SCAN"; // QR_SCAN, MANUAL_TPO

    @Column(name = "marked_by")
    private String markedBy;

    @Column(nullable = false)
    @Builder.Default
    private String status = "PRESENT"; // PRESENT, ABSENT

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
