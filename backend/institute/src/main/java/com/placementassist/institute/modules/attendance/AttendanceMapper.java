package com.placementassist.institute.modules.attendance;

import com.placementassist.institute.common.enums.SessionType;
import com.placementassist.institute.modules.attendance.classroom.ClassSession;
import com.placementassist.institute.modules.attendance.classroom.dto.ClassSessionResponse;
import com.placementassist.institute.modules.attendance.classroom.dto.CreateClassSessionRequest;
import com.placementassist.institute.modules.attendance.crt.CrtSession;
import com.placementassist.institute.modules.attendance.crt.dto.CreateCrtSessionRequest;
import com.placementassist.institute.modules.attendance.crt.dto.CrtSessionResponse;
import com.placementassist.institute.modules.attendance.dto.AttendanceRecordResponse;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class AttendanceMapper {

    public CrtSession toCrtSession(CreateCrtSessionRequest request) {
        return CrtSession.builder()
                .title(request.title())
                .topic(request.topic())
                .trainer(request.trainer())
                .sessionDate(request.sessionDate())
                .startTime(request.startTime())
                .endTime(request.endTime())
                .venue(request.venue())
                .build();
    }

    public CrtSessionResponse toCrtSessionResponse(CrtSession session, Integer totalStudents, Integer attendedCount) {
        return new CrtSessionResponse(
                session.getId(),
                session.getTitle(),
                session.getTopic(),
                session.getTrainer(),
                session.getSessionDate(),
                session.getStartTime(),
                session.getEndTime(),
                session.getVenue(),
                totalStudents,
                attendedCount,
                session.getCreatedAt(),
                session.getUpdatedAt()
        );
    }

    public ClassSession toClassSession(CreateClassSessionRequest request) {
        return ClassSession.builder()
                .subject(request.subject())
                .faculty(request.faculty())
                .sessionDate(request.sessionDate())
                .startTime(request.startTime())
                .endTime(request.endTime())
                .branch(request.branch())
                .year(request.year())
                .section(request.section())
                .build();
    }

    public ClassSessionResponse toClassSessionResponse(ClassSession session, Integer totalStudents, Integer attendedCount) {
        return new ClassSessionResponse(
                session.getId(),
                session.getSubject(),
                session.getFaculty(),
                session.getSessionDate(),
                session.getStartTime(),
                session.getEndTime(),
                session.getBranch(),
                session.getYear(),
                session.getSection(),
                totalStudents,
                attendedCount,
                session.getCreatedAt(),
                session.getUpdatedAt()
        );
    }

    public AttendanceRecordResponse toAttendanceRecordResponse(Attendance attendance) {
        Long sessionId = null;
        String sessionTitle = null;
        LocalDate sessionDate = null;

        if (attendance.getSessionType() == SessionType.CRT && attendance.getCrtSession() != null) {
            sessionId = attendance.getCrtSession().getId();
            sessionTitle = attendance.getCrtSession().getTitle() + " - " + attendance.getCrtSession().getTopic();
            sessionDate = attendance.getCrtSession().getSessionDate();
        } else if (attendance.getSessionType() == SessionType.CLASS && attendance.getClassSession() != null) {
            sessionId = attendance.getClassSession().getId();
            sessionTitle = attendance.getClassSession().getSubject() + " (" + attendance.getClassSession().getBranch() + " " + attendance.getClassSession().getSection() + ")";
            sessionDate = attendance.getClassSession().getSessionDate();
        }

        return new AttendanceRecordResponse(
                attendance.getId(),
                attendance.getStudent().getId(),
                attendance.getStudent().getRollNo(),
                attendance.getStudent().getName(),
                attendance.getSessionType(),
                sessionId,
                sessionTitle,
                sessionDate,
                attendance.getStatus(),
                attendance.getMarkedBy(),
                attendance.getCreatedAt()
        );
    }
}
