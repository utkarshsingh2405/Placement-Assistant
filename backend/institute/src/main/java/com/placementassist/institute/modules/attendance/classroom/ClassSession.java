package com.placementassist.institute.modules.attendance.classroom;

import com.placementassist.institute.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

/**
 * Regular classroom academic session entity.
 */
@Entity
@Table(name = "class_sessions")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClassSession extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "subject", nullable = false)
    private String subject;

    @Column(name = "faculty", nullable = false)
    private String faculty;

    @Column(name = "session_date", nullable = false)
    private LocalDate sessionDate;

    @Column(name = "start_time", nullable = false)
    private String startTime;

    @Column(name = "end_time", nullable = false)
    private String endTime;

    @Column(name = "branch", nullable = false)
    private String branch;

    @Column(name = "year", nullable = false)
    private Integer year;

    @Column(name = "section", nullable = false)
    private String section;
}
