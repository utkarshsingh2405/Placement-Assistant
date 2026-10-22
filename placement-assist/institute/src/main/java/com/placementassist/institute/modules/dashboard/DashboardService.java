package com.placementassist.institute.modules.dashboard;

import com.placementassist.institute.common.enums.AttendanceStatus;
import com.placementassist.institute.common.enums.DriveStatus;
import com.placementassist.institute.common.enums.OpportunityStatus;
import com.placementassist.institute.common.enums.ResumeStatus;
import com.placementassist.institute.modules.attendance.Attendance;
import com.placementassist.institute.modules.attendance.AttendanceRepository;
import com.placementassist.institute.modules.companydrive.Drive;
import com.placementassist.institute.modules.companydrive.DriveRepository;
import com.placementassist.institute.modules.dashboard.dto.DashboardSummaryResponse;
import com.placementassist.institute.modules.hackathon.HackathonRepository;
import com.placementassist.institute.modules.internship.InternshipRepository;
import com.placementassist.institute.modules.offcampusjob.OffCampusJobRepository;
import com.placementassist.institute.modules.resume.ResumeRepository;
import com.placementassist.institute.modules.student.Student;
import com.placementassist.institute.modules.student.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service aggregating campus placement analytics, student demographics,
 * drive funnels, and operational KPIs for the executive dashboard.
 */
@Service
public class DashboardService {

    private final StudentRepository studentRepository;
    private final DriveRepository driveRepository;
    private final AttendanceRepository attendanceRepository;
    private final ResumeRepository resumeRepository;
    private final HackathonRepository hackathonRepository;
    private final InternshipRepository internshipRepository;
    private final OffCampusJobRepository jobRepository;

    public DashboardService(
            StudentRepository studentRepository,
            DriveRepository driveRepository,
            AttendanceRepository attendanceRepository,
            ResumeRepository resumeRepository,
            HackathonRepository hackathonRepository,
            InternshipRepository internshipRepository,
            OffCampusJobRepository jobRepository) {
        this.studentRepository = studentRepository;
        this.driveRepository = driveRepository;
        this.attendanceRepository = attendanceRepository;
        this.resumeRepository = resumeRepository;
        this.hackathonRepository = hackathonRepository;
        this.internshipRepository = internshipRepository;
        this.jobRepository = jobRepository;
    }

    @Transactional(readOnly = true)
    public DashboardSummaryResponse getDashboardSummary() {
        // 1. Student counts
        List<Student> students = studentRepository.findAll();
        long totalStudents = students.size();
        long eligibleStudents = students.stream().filter(Student::getPlacementEligible).count();

        // 2. Upcoming drives
        long upcomingDrives = driveRepository.countByStatus(DriveStatus.UPCOMING);

        // 3. Average attendance calculation
        List<Attendance> attendances = attendanceRepository.findAll();
        long totalAttendanceRecords = attendances.size();
        long attendedRecords = attendances.stream()
                .filter(a -> a.getStatus() == AttendanceStatus.PRESENT || a.getStatus() == AttendanceStatus.LATE)
                .count();

        Double averageAttendance = totalAttendanceRecords > 0
                ? BigDecimal.valueOf(((double) attendedRecords / totalAttendanceRecords) * 100.0)
                    .setScale(2, RoundingMode.HALF_UP).doubleValue()
                : 0.0;

        // 4. Pending resumes
        long pendingResumes = resumeRepository.countByStatus(ResumeStatus.PENDING);

        // 5. Listings closing within 7 days
        LocalDate today = LocalDate.now();
        LocalDate weekAhead = today.plusDays(7);
        long closingHackathons = hackathonRepository.findByRegistrationDeadlineBetweenAndStatus(today, weekAhead, OpportunityStatus.OPEN).size();
        long closingInternships = internshipRepository.findByDeadlineBetweenAndStatus(today, weekAhead, OpportunityStatus.OPEN).size();
        long closingJobs = jobRepository.findByDeadlineBetweenAndStatus(today, weekAhead, OpportunityStatus.OPEN).size();
        long listingsClosingSoon = closingHackathons + closingInternships + closingJobs;

        // 6. Visual charts data
        Map<String, Long> branchDistribution = students.stream()
                .collect(Collectors.groupingBy(Student::getBranch, Collectors.counting()));

        List<Drive> allDrives = driveRepository.findAll();
        Map<String, Long> drivesByStatus = allDrives.stream()
                .collect(Collectors.groupingBy(d -> d.getStatus().name(), Collectors.counting()));

        return new DashboardSummaryResponse(
                totalStudents,
                eligibleStudents,
                upcomingDrives,
                averageAttendance,
                pendingResumes,
                listingsClosingSoon,
                branchDistribution,
                drivesByStatus
        );
    }
}
