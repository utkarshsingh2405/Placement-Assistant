package com.placementassist.institute.modules.attendance;

import com.placementassist.institute.common.BaseEntity;
import com.placementassist.institute.common.enums.AttendanceStatus;
import com.placementassist.institute.common.enums.SessionType;
import com.placementassist.institute.modules.attendance.classroom.ClassSession;
import com.placementassist.institute.modules.attendance.crt.CrtSession;
import com.placementassist.institute.modules.student.Student;
import jakarta.persistence.*;
import lombok.*;

/**
 * Attendance record per student per session.
 */
@Entity
@Table(name = "attendances")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Attendance extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @Enumerated(EnumType.STRING)
    @Column(name = "session_type", nullable = false)
    private SessionType sessionType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "crt_session_id")
    private CrtSession crtSession;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_session_id")
    private ClassSession classSession;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private AttendanceStatus status;

    @Column(name = "marked_by", nullable = false)
    private String markedBy;
}
