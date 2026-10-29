package com.placementassist.institute.modules.integration;

import com.placementassist.institute.common.enums.DriveStatus;
import com.placementassist.institute.common.enums.ResumeStatus;
import com.placementassist.institute.modules.auth.User;
import com.placementassist.institute.modules.auth.UserRepository;
import com.placementassist.institute.modules.companydrive.CompanyDriveService;
import com.placementassist.institute.modules.companydrive.Drive;
import com.placementassist.institute.modules.companydrive.DriveRepository;
import com.placementassist.institute.modules.driveround.DriveRound;
import com.placementassist.institute.modules.driveround.DriveRoundRepository;
import com.placementassist.institute.modules.hackathon.Hackathon;
import com.placementassist.institute.modules.hackathon.HackathonRepository;
import com.placementassist.institute.modules.integration.dto.*;
import com.placementassist.institute.modules.internship.Internship;
import com.placementassist.institute.modules.internship.InternshipRepository;
import com.placementassist.institute.modules.offcampusjob.OffCampusJob;
import com.placementassist.institute.modules.offcampusjob.OffCampusJobRepository;
import com.placementassist.institute.modules.resume.Resume;
import com.placementassist.institute.modules.resume.ResumeRepository;
import com.placementassist.institute.modules.student.Student;
import com.placementassist.institute.modules.student.StudentRepository;
import com.placementassist.institute.modules.student.dto.StudentResponse;
import com.placementassist.shared.api.ApiResponse;
import com.placementassist.shared.exception.NotFoundException;
import com.placementassist.shared.security.Role;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Stable, documented read-only integration endpoints consumed by the
 * Recruitment & Placement pillar and Portal Single Sign-On. Secured with X-API-KEY.
 */
@RestController
@RequestMapping("/api/integration")
@Tag(name = "Placement Integration Contract", description = "Inter-service endpoints consumed by the Placement Recruitment Pillar and Portal Gateway")
public class IntegrationController {

    private final StudentRepository studentRepository;
    private final DriveRepository driveRepository;
    private final ResumeRepository resumeRepository;
    private final CompanyDriveService companyDriveService;
    private final DriveRoundRepository driveRoundRepository;
    private final InternshipRepository internshipRepository;
    private final HackathonRepository hackathonRepository;
    private final OffCampusJobRepository offCampusJobRepository;
    private final UserRepository userRepository;

    public IntegrationController(
            StudentRepository studentRepository,
            DriveRepository driveRepository,
            ResumeRepository resumeRepository,
            CompanyDriveService companyDriveService,
            DriveRoundRepository driveRoundRepository,
            InternshipRepository internshipRepository,
            HackathonRepository hackathonRepository,
            OffCampusJobRepository offCampusJobRepository,
            UserRepository userRepository) {
        this.studentRepository = studentRepository;
        this.driveRepository = driveRepository;
        this.resumeRepository = resumeRepository;
        this.companyDriveService = companyDriveService;
        this.driveRoundRepository = driveRoundRepository;
        this.internshipRepository = internshipRepository;
        this.hackathonRepository = hackathonRepository;
        this.offCampusJobRepository = offCampusJobRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/students/{id}")
    @Operation(summary = "Get student profile for placement matching by ID")
    public ResponseEntity<ApiResponse<IntegrationStudentDto>> getStudentById(@PathVariable Long id) {
        Student s = studentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Student", id));
        return ResponseEntity.ok(ApiResponse.ok(toStudentDto(s)));
    }

    @GetMapping("/students")
    @Operation(summary = "Get student profile for placement matching by roll number")
    public ResponseEntity<ApiResponse<IntegrationStudentDto>> getStudentByRollNo(@RequestParam String rollNo) {
        Student s = studentRepository.findByRollNo(rollNo)
                .orElseThrow(() -> new NotFoundException("Student with rollNo " + rollNo + " not found"));
        return ResponseEntity.ok(ApiResponse.ok(toStudentDto(s)));
    }

    @PostMapping("/students/sync")
    @Operation(summary = "Create or synchronize registered student from Portal")
    public ResponseEntity<ApiResponse<IntegrationStudentDto>> syncRegisteredStudent(
            @RequestBody Map<String, String> body) {
        String email = body.get("email");
        String name = body.get("name");
        String rollNo = body.get("rollNo");
        String branch = body.getOrDefault("branch", "CSE");

        if (email == null || rollNo == null) {
            return ResponseEntity.badRequest().body(ApiResponse.<IntegrationStudentDto>builder().success(false).message("Email and rollNo required").build());
        }

        // Check if student exists
        var existing = studentRepository.findByRollNo(rollNo.toUpperCase().trim());
        if (existing.isPresent()) {
            return ResponseEntity.ok(ApiResponse.ok(toStudentDto(existing.get())));
        }

        // Create user if not exists
        User user = userRepository.findByEmail(email.toLowerCase().trim()).orElse(null);
        if (user == null) {
            user = User.builder()
                    .email(email.toLowerCase().trim())
                    .passwordHash("$2a$10$P6oomM8lmwWfvq9L5nEJQ.f950gtEjBqFchNrNOeV3MoH2mknkaEC") // default password123
                    .role(Role.STUDENT)
                    .build();
            user = userRepository.save(user);
        }

        Student student = Student.builder()
                .user(user)
                .name(name != null ? name.trim() : rollNo)
                .rollNo(rollNo.toUpperCase().trim())
                .email(email.toLowerCase().trim())
                .phone(body.getOrDefault("phone", "+91 98765 43210"))
                .branch(branch)
                .year(4)
                .cgpa(8.50)
                .backlogs(0)
                .placementEligible(true)
                .build();

        student = studentRepository.save(student);
        return ResponseEntity.ok(ApiResponse.ok(toStudentDto(student)));
    }

    @GetMapping("/drives")
    @Operation(summary = "Get all campus drives")
    public ResponseEntity<ApiResponse<List<IntegrationDriveDto>>> getAllDrives() {
        List<Drive> drives = driveRepository.findAll();
        List<IntegrationDriveDto> dtoList = drives.stream().map(this::toDriveDto).toList();
        return ResponseEntity.ok(ApiResponse.ok(dtoList));
    }

    @GetMapping("/drives/upcoming")
    @Operation(summary = "Get upcoming campus drives for recruitment scheduling")
    public ResponseEntity<ApiResponse<List<IntegrationDriveDto>>> getUpcomingDrives() {
        List<Drive> drives = driveRepository.findByStatusAndLastDateToApplyGreaterThanEqualOrderByDriveDateAsc(
                DriveStatus.UPCOMING, LocalDate.now());
        List<IntegrationDriveDto> dtoList = drives.stream().map(this::toDriveDto).toList();
        return ResponseEntity.ok(ApiResponse.ok(dtoList));
    }

    @GetMapping("/drives/{id}")
    @Operation(summary = "Get drive specifications and eligibility constraints by ID")
    public ResponseEntity<ApiResponse<IntegrationDriveDto>> getDriveById(@PathVariable Long id) {
        Drive drive = driveRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Drive", id));
        return ResponseEntity.ok(ApiResponse.ok(toDriveDto(drive)));
    }

    @GetMapping("/drives/{id}/rounds")
    @Operation(summary = "Get evaluation rounds for a drive")
    public ResponseEntity<ApiResponse<List<IntegrationDriveRoundDto>>> getDriveRounds(@PathVariable Long id) {
        List<DriveRound> rounds = driveRoundRepository.findByDriveIdOrderByOrderNoAsc(id);
        List<IntegrationDriveRoundDto> dtoList = rounds.stream()
                .map(r -> new IntegrationDriveRoundDto(
                        r.getId(),
                        id,
                        r.getOrderNo(),
                        r.getRoundType() != null ? r.getRoundType().name() : "Round",
                        r.getRoundType() != null ? r.getRoundType().name() : "Round",
                        r.getInstructions()
                ))
                .toList();
        return ResponseEntity.ok(ApiResponse.ok(dtoList));
    }

    @GetMapping("/drives/{id}/eligible-students")
    @Operation(summary = "Get list of candidate students eligible to apply for a campus drive")
    public ResponseEntity<ApiResponse<List<StudentResponse>>> getEligibleStudentsForDrive(@PathVariable Long id) {
        List<StudentResponse> eligible = companyDriveService.getEligibleStudentsForDrive(id);
        return ResponseEntity.ok(ApiResponse.ok(eligible));
    }

    @GetMapping("/students/{id}/resume")
    @Operation(summary = "Get latest approved resume URL and verified ATS score for a student")
    public ResponseEntity<ApiResponse<IntegrationResumeDto>> getStudentApprovedResume(@PathVariable Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Student", id));

        Resume resume = resumeRepository.findTopByStudentIdAndStatusOrderByVersionDesc(id, ResumeStatus.APPROVED)
                .orElseThrow(() -> new NotFoundException("Approved resume for student", id));

        IntegrationResumeDto dto = new IntegrationResumeDto(
                student.getId(),
                student.getRollNo(),
                resume.getFileUrl(),
                resume.getAtsScore(),
                resume.getStatus(),
                resume.getVersion()
        );
        return ResponseEntity.ok(ApiResponse.ok(dto));
    }

    @GetMapping("/internships")
    @Operation(summary = "Get approved internships for student opportunities feed")
    public ResponseEntity<ApiResponse<List<IntegrationOpportunityDto>>> getInternships() {
        List<Internship> list = internshipRepository.findAll();
        List<IntegrationOpportunityDto> dtoList = list.stream()
                .map(i -> new IntegrationOpportunityDto(
                        i.getId(),
                        i.getRole(),
                        i.getCompany(),
                        "INTERNSHIP",
                        i.getLocation(),
                        i.getApplyUrl(),
                        i.getDeadline(),
                        i.getStipend() != null ? "₹" + i.getStipend().intValue() + "/mo" : "Unpaid",
                        i.getEligibility()
                ))
                .toList();
        return ResponseEntity.ok(ApiResponse.ok(dtoList));
    }

    @GetMapping("/hackathons")
    @Operation(summary = "Get active hackathons for student opportunities feed")
    public ResponseEntity<ApiResponse<List<IntegrationOpportunityDto>>> getHackathons() {
        List<Hackathon> list = hackathonRepository.findAll();
        List<IntegrationOpportunityDto> dtoList = list.stream()
                .map(h -> new IntegrationOpportunityDto(
                        h.getId(),
                        h.getName(),
                        h.getOrganizer(),
                        "HACKATHON",
                        h.getLocation(),
                        h.getLink(),
                        h.getRegistrationDeadline(),
                        h.getPrize(),
                        h.getTeamSize() != null ? "Team size: " + h.getTeamSize() : null
                ))
                .toList();
        return ResponseEntity.ok(ApiResponse.ok(dtoList));
    }

    @GetMapping("/offcampus-jobs")
    @Operation(summary = "Get off-campus job opportunities for students")
    public ResponseEntity<ApiResponse<List<IntegrationOpportunityDto>>> getOffCampusJobs() {
        List<OffCampusJob> list = offCampusJobRepository.findAll();
        List<IntegrationOpportunityDto> dtoList = list.stream()
                .map(j -> new IntegrationOpportunityDto(
                        j.getId(),
                        j.getRole(),
                        j.getCompany(),
                        "OFFCAMPUS_JOB",
                        j.getLocation(),
                        j.getApplyUrl(),
                        j.getDeadline(),
                        j.getPackageLpa() != null ? "₹" + j.getPackageLpa() + " LPA" : null,
                        j.getSkillsRequired()
                ))
                .toList();
        return ResponseEntity.ok(ApiResponse.ok(dtoList));
    }

    @GetMapping("/users/export")
    @Operation(summary = "Export users with BCrypt hashes for Portal initial single sign-on synchronization")
    public ResponseEntity<ApiResponse<List<IntegrationUserDto>>> exportUsers() {
        List<User> users = userRepository.findAll();
        List<IntegrationUserDto> exportList = new ArrayList<>();
        for (User u : users) {
            String name = u.getEmail();
            String rollNo = null;
            var studentOpt = studentRepository.findByEmail(u.getEmail());
            if (studentOpt.isPresent()) {
                name = studentOpt.get().getName();
                rollNo = studentOpt.get().getRollNo();
            }
            exportList.add(new IntegrationUserDto(
                    u.getId(),
                    u.getEmail(),
                    u.getRole(),
                    name,
                    rollNo,
                    u.getPasswordHash()
            ));
        }
        return ResponseEntity.ok(ApiResponse.ok(exportList));
    }

    private IntegrationStudentDto toStudentDto(Student s) {
        return new IntegrationStudentDto(
                s.getId(),
                s.getName(),
                s.getRollNo(),
                s.getBranch(),
                s.getYear(),
                s.getCgpa(),
                s.getBacklogs(),
                s.getPlacementEligible(),
                s.getEmail(),
                s.getPhone()
        );
    }

    private IntegrationDriveDto toDriveDto(Drive d) {
        List<String> rolesList = d.getRoles() != null
                ? Arrays.stream(d.getRoles().split(",")).map(String::trim).toList()
                : List.of();
        List<String> branchesList = d.getEligibleBranches() != null
                ? Arrays.stream(d.getEligibleBranches().split(",")).map(String::trim).toList()
                : List.of();

        return new IntegrationDriveDto(
                d.getId(),
                d.getTitle(),
                d.getCompany() != null ? d.getCompany().getName() : "Company",
                d.getCompany() != null ? d.getCompany().getWebsite() : null,
                d.getRoles(),
                rolesList,
                d.getPackageMin(),
                d.getPackageMax(),
                d.getEligibilityCgpa(),
                d.getEligibleBranches(),
                branchesList,
                d.getMaxBacklogs(),
                d.getStatus(),
                d.getLastDateToApply(),
                d.getDriveDate(),
                d.getLocation()
        );
    }
}
