package com.placementassist.institute.modules.attendance;

import com.placementassist.institute.common.enums.AttendanceStatus;
import com.placementassist.institute.common.enums.SessionType;
import com.placementassist.institute.modules.attendance.classroom.ClassSession;
import com.placementassist.institute.modules.attendance.classroom.ClassSessionRepository;
import com.placementassist.institute.modules.attendance.classroom.dto.ClassSessionResponse;
import com.placementassist.institute.modules.attendance.classroom.dto.CreateClassSessionRequest;
import com.placementassist.institute.modules.attendance.crt.CrtSession;
import com.placementassist.institute.modules.attendance.crt.CrtSessionRepository;
import com.placementassist.institute.modules.attendance.crt.dto.CreateCrtSessionRequest;
import com.placementassist.institute.modules.attendance.crt.dto.CrtSessionResponse;
import com.placementassist.institute.modules.attendance.dto.*;
import com.placementassist.institute.modules.student.Student;
import com.placementassist.institute.modules.student.StudentRepository;
import com.placementassist.shared.exception.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service managing CRT and classroom attendance sessions, bulk marking,
 * dynamic percentage computations, and defaulter tracking.
 */
@Service
public class AttendanceService {

    private final CrtSessionRepository crtSessionRepository;
    private final ClassSessionRepository classSessionRepository;
    private final AttendanceRepository attendanceRepository;
    private final StudentRepository studentRepository;
    private final AttendanceMapper mapper;

    public AttendanceService(
            CrtSessionRepository crtSessionRepository,
            ClassSessionRepository classSessionRepository,
            AttendanceRepository attendanceRepository,
            StudentRepository studentRepository,
            AttendanceMapper mapper) {
        this.crtSessionRepository = crtSessionRepository;
        this.classSessionRepository = classSessionRepository;
        this.attendanceRepository = attendanceRepository;
        this.studentRepository = studentRepository;
        this.mapper = mapper;
    }

    // =========================================================================
    // CRT Session Operations
    // =========================================================================

    @Transactional
    public CrtSessionResponse createCrtSession(CreateCrtSessionRequest request) {
        CrtSession session = mapper.toCrtSession(request);
        CrtSession saved = crtSessionRepository.save(session);
        return mapper.toCrtSessionResponse(saved, 0, 0);
    }

    @Transactional(readOnly = true)
    public CrtSessionResponse getCrtSession(Long id) {
        CrtSession session = crtSessionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("CrtSession", id));
        List<Attendance> attendances = attendanceRepository.findByCrtSessionId(id);
        int total = attendances.size();
        int attended = (int) attendances.stream()
                .filter(a -> a.getStatus() == AttendanceStatus.PRESENT || a.getStatus() == AttendanceStatus.LATE)
                .count();
        return mapper.toCrtSessionResponse(session, total, attended);
    }

    @Transactional(readOnly = true)
    public List<CrtSessionResponse> getAllCrtSessions() {
        return crtSessionRepository.findAllByOrderBySessionDateDescStartTimeDesc().stream()
                .map(s -> {
                    List<Attendance> attendances = attendanceRepository.findByCrtSessionId(s.getId());
                    int total = attendances.size();
                    int attended = (int) attendances.stream()
                            .filter(a -> a.getStatus() == AttendanceStatus.PRESENT || a.getStatus() == AttendanceStatus.LATE)
                            .count();
                    return mapper.toCrtSessionResponse(s, total, attended);
                })
                .toList();
    }

    @Transactional
    public void deleteCrtSession(Long id) {
        if (!crtSessionRepository.existsById(id)) {
            throw new NotFoundException("CrtSession", id);
        }
        crtSessionRepository.deleteById(id);
    }

    // =========================================================================
    // Class Session Operations
    // =========================================================================

    @Transactional
    public ClassSessionResponse createClassSession(CreateClassSessionRequest request) {
        ClassSession session = mapper.toClassSession(request);
        ClassSession saved = classSessionRepository.save(session);
        return mapper.toClassSessionResponse(saved, 0, 0);
    }

    @Transactional(readOnly = true)
    public ClassSessionResponse getClassSession(Long id) {
        ClassSession session = classSessionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("ClassSession", id));
        List<Attendance> attendances = attendanceRepository.findByClassSessionId(id);
        int total = attendances.size();
        int attended = (int) attendances.stream()
                .filter(a -> a.getStatus() == AttendanceStatus.PRESENT || a.getStatus() == AttendanceStatus.LATE)
                .count();
        return mapper.toClassSessionResponse(session, total, attended);
    }

    @Transactional(readOnly = true)
    public List<ClassSessionResponse> getAllClassSessions(String branch, Integer year, String section) {
        List<ClassSession> list;
        if (branch != null && year != null && section != null) {
            list = classSessionRepository.findByBranchAndYearAndSectionOrderBySessionDateDescStartTimeDesc(branch, year, section);
        } else if (branch != null && year != null) {
            list = classSessionRepository.findByBranchAndYearOrderBySessionDateDescStartTimeDesc(branch, year);
        } else {
            list = classSessionRepository.findAllByOrderBySessionDateDescStartTimeDesc();
        }
        return list.stream()
                .map(s -> {
                    List<Attendance> attendances = attendanceRepository.findByClassSessionId(s.getId());
                    int total = attendances.size();
                    int attended = (int) attendances.stream()
                            .filter(a -> a.getStatus() == AttendanceStatus.PRESENT || a.getStatus() == AttendanceStatus.LATE)
                            .count();
                    return mapper.toClassSessionResponse(s, total, attended);
                })
                .toList();
    }

    @Transactional
    public void deleteClassSession(Long id) {
        if (!classSessionRepository.existsById(id)) {
            throw new NotFoundException("ClassSession", id);
        }
        classSessionRepository.deleteById(id);
    }

    // =========================================================================
    // Bulk Attendance Marking
    // =========================================================================

    @Transactional
    public List<AttendanceRecordResponse> markCrtAttendance(Long sessionId, MarkAttendanceRequest request, String markedBy) {
        CrtSession session = crtSessionRepository.findById(sessionId)
                .orElseThrow(() -> new NotFoundException("CrtSession", sessionId));

        List<Attendance> results = new ArrayList<>();
        for (StudentAttendanceItemDto item : request.records()) {
            Student student = studentRepository.findById(item.studentId())
                    .orElseThrow(() -> new NotFoundException("Student", item.studentId()));

            Optional<Attendance> existing = attendanceRepository.findByCrtSessionIdAndStudentId(sessionId, item.studentId());
            Attendance record;
            if (existing.isPresent()) {
                record = existing.get();
                record.setStatus(item.status());
                record.setMarkedBy(markedBy);
            } else {
                record = Attendance.builder()
                        .student(student)
                        .sessionType(SessionType.CRT)
                        .crtSession(session)
                        .status(item.status())
                        .markedBy(markedBy)
                        .build();
            }
            results.add(attendanceRepository.save(record));
        }
        return results.stream().map(mapper::toAttendanceRecordResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<AttendanceRecordResponse> getCrtAttendance(Long sessionId) {
        if (!crtSessionRepository.existsById(sessionId)) {
            throw new NotFoundException("CrtSession", sessionId);
        }
        return attendanceRepository.findByCrtSessionId(sessionId).stream()
                .map(mapper::toAttendanceRecordResponse)
                .toList();
    }

    @Transactional
    public List<AttendanceRecordResponse> markClassAttendance(Long sessionId, MarkAttendanceRequest request, String markedBy) {
        ClassSession session = classSessionRepository.findById(sessionId)
                .orElseThrow(() -> new NotFoundException("ClassSession", sessionId));

        List<Attendance> results = new ArrayList<>();
        for (StudentAttendanceItemDto item : request.records()) {
            Student student = studentRepository.findById(item.studentId())
                    .orElseThrow(() -> new NotFoundException("Student", item.studentId()));

            Optional<Attendance> existing = attendanceRepository.findByClassSessionIdAndStudentId(sessionId, item.studentId());
            Attendance record;
            if (existing.isPresent()) {
                record = existing.get();
                record.setStatus(item.status());
                record.setMarkedBy(markedBy);
            } else {
                record = Attendance.builder()
                        .student(student)
                        .sessionType(SessionType.CLASS)
                        .classSession(session)
                        .status(item.status())
                        .markedBy(markedBy)
                        .build();
            }
            results.add(attendanceRepository.save(record));
        }
        return results.stream().map(mapper::toAttendanceRecordResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<AttendanceRecordResponse> getClassAttendance(Long sessionId) {
        if (!classSessionRepository.existsById(sessionId)) {
            throw new NotFoundException("ClassSession", sessionId);
        }
        return attendanceRepository.findByClassSessionId(sessionId).stream()
                .map(mapper::toAttendanceRecordResponse)
                .toList();
    }

    // =========================================================================
    // Student Attendance Statistics
    // =========================================================================

    @Transactional(readOnly = true)
    public StudentAttendanceStatsResponse getStudentAttendanceStats(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new NotFoundException("Student", studentId));

        long totalSessions = attendanceRepository.countTotalByStudentId(studentId);
        long attendedSessions = attendanceRepository.countAttendedByStudentId(studentId);
        Double overallPercentage = calculatePercentage(attendedSessions, totalSessions);

        long crtTotal = attendanceRepository.countTotalByStudentIdAndSessionType(studentId, SessionType.CRT);
        long crtAttended = attendanceRepository.countAttendedByStudentIdAndSessionType(studentId, SessionType.CRT);
        Double crtPercentage = calculatePercentage(crtAttended, crtTotal);

        long classTotal = attendanceRepository.countTotalByStudentIdAndSessionType(studentId, SessionType.CLASS);
        long classAttended = attendanceRepository.countAttendedByStudentIdAndSessionType(studentId, SessionType.CLASS);
        Double classPercentage = calculatePercentage(classAttended, classTotal);

        boolean isDefaulter = totalSessions > 0 && overallPercentage < 75.0;

        List<AttendanceRecordResponse> recentRecords = attendanceRepository
                .findByStudentIdOrderByCreatedAtDesc(studentId).stream()
                .limit(20)
                .map(mapper::toAttendanceRecordResponse)
                .toList();

        return new StudentAttendanceStatsResponse(
                student.getId(),
                student.getRollNo(),
                student.getName(),
                student.getBranch(),
                student.getYear(),
                totalSessions,
                attendedSessions,
                overallPercentage,
                crtTotal,
                crtAttended,
                crtPercentage,
                classTotal,
                classAttended,
                classPercentage,
                isDefaulter,
                recentRecords
        );
    }

    // =========================================================================
    // Defaulter & Periodic Attendance Reports
    // =========================================================================

    @Transactional(readOnly = true)
    public AttendanceReportResponse getAttendanceReport(LocalDate from, LocalDate to, String branch) {
        LocalDate startDate = from != null ? from : LocalDate.now().minusDays(90);
        LocalDate endDate = to != null ? to : LocalDate.now();

        List<Student> students = (branch != null && !branch.isBlank())
                ? studentRepository.findAll().stream().filter(s -> branch.equalsIgnoreCase(s.getBranch())).toList()
                : studentRepository.findAll();

        List<DefaulterStudentDto> defaulters = new ArrayList<>();
        double sumPercentages = 0.0;
        int evaluatedCount = 0;

        for (Student student : students) {
            long total = attendanceRepository.countTotalByStudentId(student.getId());
            long attended = attendanceRepository.countAttendedByStudentId(student.getId());
            Double pct = calculatePercentage(attended, total);

            if (total > 0) {
                evaluatedCount++;
                sumPercentages += pct;
                if (pct < 75.0) {
                    defaulters.add(new DefaulterStudentDto(
                            student.getId(),
                            student.getRollNo(),
                            student.getName(),
                            student.getEmail(),
                            student.getBranch(),
                            student.getYear(),
                            attended,
                            total,
                            pct
                    ));
                }
            }
        }

        Double avgPercentage = evaluatedCount > 0
                ? BigDecimal.valueOf(sumPercentages / evaluatedCount).setScale(2, RoundingMode.HALF_UP).doubleValue()
                : 0.0;

        return new AttendanceReportResponse(
                startDate,
                endDate,
                branch,
                evaluatedCount,
                defaulters.size(),
                avgPercentage,
                defaulters
        );
    }

    private Double calculatePercentage(long attended, long total) {
        if (total == 0) {
            return 0.0;
        }
        return BigDecimal.valueOf(((double) attended / total) * 100.0)
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();
    }
}
