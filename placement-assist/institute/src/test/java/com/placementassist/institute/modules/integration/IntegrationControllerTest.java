package com.placementassist.institute.modules.integration;

import com.placementassist.institute.common.enums.DriveStatus;
import com.placementassist.institute.common.enums.ResumeStatus;
import com.placementassist.institute.modules.auth.UserRepository;
import com.placementassist.institute.modules.companydrive.Company;
import com.placementassist.institute.modules.companydrive.CompanyDriveService;
import com.placementassist.institute.modules.companydrive.Drive;
import com.placementassist.institute.modules.companydrive.DriveRepository;
import com.placementassist.institute.modules.driveround.DriveRoundRepository;
import com.placementassist.institute.modules.hackathon.HackathonRepository;
import com.placementassist.institute.modules.internship.InternshipRepository;
import com.placementassist.institute.modules.offcampusjob.OffCampusJobRepository;
import com.placementassist.institute.modules.resume.Resume;
import com.placementassist.institute.modules.resume.ResumeRepository;
import com.placementassist.institute.modules.student.Student;
import com.placementassist.institute.modules.student.StudentRepository;
import com.placementassist.shared.security.JwtAuthFilter;
import com.placementassist.shared.security.JwtService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(IntegrationController.class)
@AutoConfigureMockMvc(addFilters = false)
class IntegrationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StudentRepository studentRepository;

    @MockBean
    private DriveRepository driveRepository;

    @MockBean
    private ResumeRepository resumeRepository;

    @MockBean
    private CompanyDriveService companyDriveService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private DriveRoundRepository driveRoundRepository;

    @MockBean
    private InternshipRepository internshipRepository;

    @MockBean
    private HackathonRepository hackathonRepository;

    @MockBean
    private OffCampusJobRepository offCampusJobRepository;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private JwtAuthFilter jwtAuthFilter;

    @Test
    @DisplayName("GET /api/integration/students/{id} - Returns student integration data")
    void testGetStudentById() throws Exception {
        Student student = Student.builder()
                .id(1L)
                .name("Aarav Sharma")
                .rollNo("21CS001")
                .branch("CSE")
                .year(4)
                .cgpa(8.8)
                .backlogs(0)
                .placementEligible(true)
                .email("aarav@example.com")
                .phone("9876543210")
                .build();

        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));

        mockMvc.perform(get("/api/integration/students/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.name").value("Aarav Sharma"))
                .andExpect(jsonPath("$.data.placementEligible").value(true));
    }

    @Test
    @DisplayName("GET /api/integration/drives/upcoming - Returns upcoming drives")
    void testGetUpcomingDrives() throws Exception {
        Company company = Company.builder().id(1L).name("Google").website("https://google.com").build();
        Drive drive = Drive.builder()
                .id(1L)
                .title("Google SDE Drive 2025")
                .company(company)
                .roles("Software Engineer")
                .packageMin(28.0)
                .packageMax(32.0)
                .eligibilityCgpa(8.0)
                .eligibleBranches("CSE,IT")
                .maxBacklogs(0)
                .status(DriveStatus.UPCOMING)
                .lastDateToApply(LocalDate.now().plusDays(10))
                .driveDate(LocalDate.now().plusDays(15))
                .location("Hyderabad")
                .build();

        when(driveRepository.findByStatusAndLastDateToApplyGreaterThanEqualOrderByDriveDateAsc(
                org.mockito.ArgumentMatchers.eq(DriveStatus.UPCOMING), any()))
                .thenReturn(List.of(drive));

        mockMvc.perform(get("/api/integration/drives/upcoming"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].title").value("Google SDE Drive 2025"))
                .andExpect(jsonPath("$.data[0].companyName").value("Google"));
    }

    @Test
    @DisplayName("GET /api/integration/students/{id}/resume - Returns latest approved resume")
    void testGetStudentApprovedResume() throws Exception {
        Student student = Student.builder().id(1L).rollNo("21CS001").build();
        Resume resume = Resume.builder()
                .id(10L)
                .student(student)
                .fileUrl("student_21CS001/resume_v1.pdf")
                .version(1)
                .status(ResumeStatus.APPROVED)
                .atsScore(90)
                .build();

        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(resumeRepository.findTopByStudentIdAndStatusOrderByVersionDesc(1L, ResumeStatus.APPROVED))
                .thenReturn(Optional.of(resume));

        mockMvc.perform(get("/api/integration/students/1/resume"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.atsScore").value(90))
                .andExpect(jsonPath("$.data.status").value("APPROVED"));
    }
}
