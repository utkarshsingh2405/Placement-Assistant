package com.placementassist.placement.modules.placed;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "placement_placed_students")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlacedStudent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "drive_id")
    private Long driveId;

    @Column(name = "student_id")
    private Long studentId;

    @Column(name = "roll_no", nullable = false)
    private String rollNo;

    @Column(name = "student_name", nullable = false)
    private String studentName;

    @Column(name = "company_name", nullable = false)
    private String companyName;

    @Column(name = "role_offered", nullable = false)
    private String roleOffered;

    @Column(name = "package_lpa", nullable = false)
    private Double packageLpa;

    @Column(name = "offer_letter_url")
    private String offerLetterUrl;

    @CreationTimestamp
    @Column(name = "offered_at", nullable = false)
    private LocalDateTime offeredAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
