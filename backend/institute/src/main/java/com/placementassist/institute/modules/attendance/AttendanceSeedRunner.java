package com.placementassist.institute.modules.attendance;

import com.placementassist.institute.common.enums.AttendanceStatus;
import com.placementassist.institute.common.enums.SessionType;
import com.placementassist.institute.modules.attendance.classroom.ClassSession;
import com.placementassist.institute.modules.attendance.classroom.ClassSessionRepository;
import com.placementassist.institute.modules.attendance.crt.CrtSession;
import com.placementassist.institute.modules.attendance.crt.CrtSessionRepository;
import com.placementassist.institute.modules.student.Student;
import com.placementassist.institute.modules.student.StudentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Seeds realistic Campus Recruitment Training (CRT) and classroom sessions
 * with full attendance records ensuring high, medium, and defaulter (< 75%) cohorts.
 */
@Component
@Order(5)
@ConditionalOnBean(name = "entityManagerFactory")
public class AttendanceSeedRunner implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(AttendanceSeedRunner.class);

    private final CrtSessionRepository crtSessionRepository;
    private final ClassSessionRepository classSessionRepository;
    private final AttendanceRepository attendanceRepository;
    private final StudentRepository studentRepository;

    public AttendanceSeedRunner(
            CrtSessionRepository crtSessionRepository,
            ClassSessionRepository classSessionRepository,
            AttendanceRepository attendanceRepository,
            StudentRepository studentRepository) {
        this.crtSessionRepository = crtSessionRepository;
        this.classSessionRepository = classSessionRepository;
        this.attendanceRepository = attendanceRepository;
        this.studentRepository = studentRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (attendanceRepository.count() >= 30) {
            return;
        }

        List<Student> students = studentRepository.findAll();
        if (students.isEmpty()) {
            log.warn("No students found. Skipping attendance seed.");
            return;
        }

        log.info("Seeding CRT and classroom attendance sessions...");

        // 1. Seed CRT Sessions
        List<CrtSession> crtSessions = new ArrayList<>();
        Object[][] crtData = {
                {"Quantitative Aptitude - Number Systems & Percentages", "Aptitude Fundamentals", "Mr. Rakesh Sharma", LocalDate.now().minusDays(25), "09:30", "12:30", "Seminar Hall A"},
                {"DSA Masterclass - Dynamic Programming & Graphs", "Advanced Algorithms", "Ms. Ananya Roy", LocalDate.now().minusDays(22), "14:00", "17:00", "Lab 3"},
                {"Logical Reasoning - Deductive Logic & Syllogisms", "Reasoning Aptitude", "Mr. Rakesh Sharma", LocalDate.now().minusDays(19), "09:30", "12:30", "Seminar Hall A"},
                {"System Design - Scalable Microservices Architecture", "Architecture & Cloud", "Mr. Vikram Aditya", LocalDate.now().minusDays(15), "10:00", "13:00", "Audi 1"},
                {"Verbal Ability - Professional Communication & Reading Comprehension", "Verbal Skills", "Dr. Preeti Sen", LocalDate.now().minusDays(12), "14:00", "16:30", "Seminar Hall B"},
                {"Core Java & Spring Boot - RESTful Microservices", "Full Stack Development", "Mr. Arjun Nair", LocalDate.now().minusDays(8), "09:30", "13:30", "Lab 1"},
                {"Mock HR & Technical Interviews - Behavioral Competencies", "Interview Preparation", "Ms. Sunita Kapoor", LocalDate.now().minusDays(5), "10:00", "16:00", "Placement Cell"},
                {"Cloud Computing - AWS EC2, S3 & Container Deployment", "Cloud Deployment", "Mr. Arjun Nair", LocalDate.now().minusDays(2), "11:00", "14:00", "Lab 2"}
        };

        for (Object[] row : crtData) {
            CrtSession s = CrtSession.builder()
                    .title((String) row[0])
                    .topic((String) row[1])
                    .trainer((String) row[2])
                    .sessionDate((LocalDate) row[3])
                    .startTime((String) row[4])
                    .endTime((String) row[5])
                    .venue((String) row[6])
                    .build();
            crtSessions.add(crtSessionRepository.save(s));
        }

        // 2. Seed Classroom Sessions
        List<ClassSession> classSessions = new ArrayList<>();
        Object[][] classData = {
                {"Operating Systems", "Prof. S. K. Verma", LocalDate.now().minusDays(24), "10:00", "11:00", "CSE", 4, "A"},
                {"Computer Networks", "Prof. N. Mehra", LocalDate.now().minusDays(23), "11:15", "12:15", "CSE", 4, "A"},
                {"Database Management Systems", "Prof. R. Deshmukh", LocalDate.now().minusDays(21), "09:00", "10:00", "CSE", 4, "A"},
                {"Design & Analysis of Algorithms", "Prof. S. K. Verma", LocalDate.now().minusDays(18), "10:00", "11:00", "CSE", 4, "A"},
                {"Machine Learning", "Prof. A. Bansal", LocalDate.now().minusDays(14), "14:00", "15:00", "IT", 4, "A"},
                {"Web Technologies", "Prof. N. Mehra", LocalDate.now().minusDays(11), "11:15", "12:15", "IT", 4, "A"},
                {"Digital Signal Processing", "Prof. V. Raman", LocalDate.now().minusDays(7), "10:00", "11:00", "ECE", 4, "A"},
                {"VLSI Design", "Prof. V. Raman", LocalDate.now().minusDays(4), "12:00", "13:00", "ECE", 4, "A"}
        };

        for (Object[] row : classData) {
            ClassSession s = ClassSession.builder()
                    .subject((String) row[0])
                    .faculty((String) row[1])
                    .sessionDate((LocalDate) row[2])
                    .startTime((String) row[3])
                    .endTime((String) row[4])
                    .branch((String) row[5])
                    .year((Integer) row[6])
                    .section((String) row[7])
                    .build();
            classSessions.add(classSessionRepository.save(s));
        }

        // 3. Mark Attendance records for students across sessions
        // Distribute attendance rates:
        // Student index 0..9 -> High attendance (85% - 100%)
        // Student index 10..11 -> Medium attendance (75% - 80%)
        // Student index 12..14 -> Defaulters (< 75%, e.g., 50%, 35%, 20%)
        log.info("Marking attendance records across {} students...", students.size());

        for (int i = 0; i < students.size(); i++) {
            Student student = students.get(i);

            // CRT attendance marking
            for (int cIdx = 0; cIdx < crtSessions.size(); cIdx++) {
                CrtSession session = crtSessions.get(cIdx);
                AttendanceStatus status = determineStatus(i, cIdx);

                Attendance record = Attendance.builder()
                        .student(student)
                        .sessionType(SessionType.CRT)
                        .crtSession(session)
                        .status(status)
                        .markedBy("TPO_ADMIN")
                        .build();
                attendanceRepository.save(record);
            }

            // Classroom attendance marking (filter by student branch where applicable)
            for (int clIdx = 0; clIdx < classSessions.size(); clIdx++) {
                ClassSession session = classSessions.get(clIdx);
                if (!session.getBranch().equalsIgnoreCase(student.getBranch())) {
                    continue; // Student only attends their branch classes
                }

                AttendanceStatus status = determineStatus(i, clIdx + 3);

                Attendance record = Attendance.builder()
                        .student(student)
                        .sessionType(SessionType.CLASS)
                        .classSession(session)
                        .status(status)
                        .markedBy("FACULTY_DESHMUKH")
                        .build();
                attendanceRepository.save(record);
            }
        }

        log.info("Attendance seeding complete! Total records: {}", attendanceRepository.count());
    }

    private AttendanceStatus determineStatus(int studentIndex, int sessionIndex) {
        // High attendance cohort (students 0 to 9): 90%+ present
        if (studentIndex < 10) {
            return (sessionIndex % 7 == 0) ? AttendanceStatus.LATE : AttendanceStatus.PRESENT;
        }
        // Moderate attendance cohort (students 10 to 11): ~75-80% present
        if (studentIndex < 12) {
            return (sessionIndex % 4 == 0) ? AttendanceStatus.ABSENT : AttendanceStatus.PRESENT;
        }
        // Defaulter cohort (students 12 to 14): low attendance
        if (studentIndex == 12) {
            // ~50%
            return (sessionIndex % 2 == 0) ? AttendanceStatus.PRESENT : AttendanceStatus.ABSENT;
        } else if (studentIndex == 13) {
            // ~35%
            return (sessionIndex % 3 == 0) ? AttendanceStatus.PRESENT : AttendanceStatus.ABSENT;
        } else {
            // ~20%
            return (sessionIndex % 5 == 0) ? AttendanceStatus.PRESENT : AttendanceStatus.ABSENT;
        }
    }
}
