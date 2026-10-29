package com.placementassist.placement.modules.attendance;

import com.placementassist.placement.integration.InstituteClient;
import com.placementassist.placement.integration.dto.StudentDto;
import com.placementassist.placement.modules.stage.PlacementStage;
import com.placementassist.placement.modules.stage.PlacementStageRepository;
import com.placementassist.shared.exception.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final PlacementStageRepository stageRepository;
    private final InstituteClient instituteClient;

    public AttendanceService(
            AttendanceRepository attendanceRepository,
            PlacementStageRepository stageRepository,
            InstituteClient instituteClient) {
        this.attendanceRepository = attendanceRepository;
        this.stageRepository = stageRepository;
        this.instituteClient = instituteClient;
    }

    @Transactional
    public Attendance markCheckIn(Long stageId, String rollNo, String studentName, String checkInMode, String markedBy) {
        PlacementStage stage = stageRepository.findById(stageId)
                .orElseThrow(() -> new NotFoundException("Stage", stageId));

        String normalizedRoll = rollNo.toUpperCase().trim();

        Optional<Attendance> existing = attendanceRepository.findByStageIdAndRollNo(stageId, normalizedRoll);
        if (existing.isPresent()) {
            Attendance att = existing.get();
            att.setStatus("PRESENT");
            att.setCheckInTime(LocalDateTime.now());
            if (checkInMode != null) att.setCheckInMode(checkInMode);
            if (markedBy != null) att.setMarkedBy(markedBy);
            return attendanceRepository.save(att);
        }

        Long studentId = null;
        Optional<StudentDto> studentOpt = instituteClient.getStudentByRollNo(normalizedRoll);
        if (studentOpt.isPresent()) {
            StudentDto s = studentOpt.get();
            studentId = s.getId();
            if (studentName == null || studentName.isBlank()) {
                studentName = s.getName();
            }
        }

        Attendance newAttendance = Attendance.builder()
                .driveId(stage.getDriveId())
                .stageId(stageId)
                .studentId(studentId)
                .rollNo(normalizedRoll)
                .studentName(studentName != null ? studentName : normalizedRoll)
                .checkInTime(LocalDateTime.now())
                .checkInMode(checkInMode != null ? checkInMode : "QR_SCAN")
                .markedBy(markedBy)
                .status("PRESENT")
                .build();

        return attendanceRepository.save(newAttendance);
    }

    @Transactional(readOnly = true)
    public List<Attendance> getAttendanceForStage(Long stageId) {
        return attendanceRepository.findByStageIdOrderByCheckInTimeDesc(stageId);
    }

    @Transactional
    public Attendance toggleAttendance(Long attendanceId) {
        Attendance att = attendanceRepository.findById(attendanceId)
                .orElseThrow(() -> new NotFoundException("Attendance", attendanceId));
        att.setStatus("PRESENT".equals(att.getStatus()) ? "ABSENT" : "PRESENT");
        return attendanceRepository.save(att);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getAttendanceStats(Long stageId) {
        long present = attendanceRepository.countByStageIdAndStatus(stageId, "PRESENT");
        long absent = attendanceRepository.countByStageIdAndStatus(stageId, "ABSENT");

        Map<String, Object> stats = new HashMap<>();
        stats.put("presentCount", present);
        stats.put("absentCount", absent);
        stats.put("totalCheckedIn", present + absent);
        return stats;
    }
}
