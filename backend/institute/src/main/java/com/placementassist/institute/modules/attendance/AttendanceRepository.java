package com.placementassist.institute.modules.attendance;

import com.placementassist.institute.common.enums.SessionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long>, JpaSpecificationExecutor<Attendance> {

    List<Attendance> findByCrtSessionId(Long crtSessionId);

    List<Attendance> findByClassSessionId(Long classSessionId);

    Optional<Attendance> findByCrtSessionIdAndStudentId(Long crtSessionId, Long studentId);

    Optional<Attendance> findByClassSessionIdAndStudentId(Long classSessionId, Long studentId);

    List<Attendance> findByStudentIdOrderByCreatedAtDesc(Long studentId);

    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.student.id = :studentId")
    long countTotalByStudentId(@Param("studentId") Long studentId);

    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.student.id = :studentId AND (a.status = com.placementassist.institute.common.enums.AttendanceStatus.PRESENT OR a.status = com.placementassist.institute.common.enums.AttendanceStatus.LATE)")
    long countAttendedByStudentId(@Param("studentId") Long studentId);

    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.student.id = :studentId AND a.sessionType = :sessionType")
    long countTotalByStudentIdAndSessionType(@Param("studentId") Long studentId, @Param("sessionType") SessionType sessionType);

    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.student.id = :studentId AND a.sessionType = :sessionType AND (a.status = com.placementassist.institute.common.enums.AttendanceStatus.PRESENT OR a.status = com.placementassist.institute.common.enums.AttendanceStatus.LATE)")
    long countAttendedByStudentIdAndSessionType(@Param("studentId") Long studentId, @Param("sessionType") SessionType sessionType);

    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.crtSession.id = :crtSessionId AND (a.status = com.placementassist.institute.common.enums.AttendanceStatus.PRESENT OR a.status = com.placementassist.institute.common.enums.AttendanceStatus.LATE)")
    long countAttendedByCrtSessionId(@Param("crtSessionId") Long crtSessionId);

    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.classSession.id = :classSessionId AND (a.status = com.placementassist.institute.common.enums.AttendanceStatus.PRESENT OR a.status = com.placementassist.institute.common.enums.AttendanceStatus.LATE)")
    long countAttendedByClassSessionId(@Param("classSessionId") Long classSessionId);

    @Query("SELECT a FROM Attendance a LEFT JOIN a.crtSession crt LEFT JOIN a.classSession cls " +
           "WHERE (crt.sessionDate BETWEEN :from AND :to) OR (cls.sessionDate BETWEEN :from AND :to)")
    List<Attendance> findAttendancesInDateRange(@Param("from") LocalDate from, @Param("to") LocalDate to);
}
