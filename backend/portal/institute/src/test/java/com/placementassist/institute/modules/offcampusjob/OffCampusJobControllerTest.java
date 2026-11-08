package com.placementassist.institute.modules.offcampusjob;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.placementassist.institute.common.enums.OpportunityStatus;
import com.placementassist.institute.modules.auth.UserRepository;
import com.placementassist.institute.modules.offcampusjob.dto.CreateOffCampusJobRequest;
import com.placementassist.institute.modules.offcampusjob.dto.OffCampusJobResponse;
import com.placementassist.institute.modules.student.Student;
import com.placementassist.institute.modules.student.StudentRepository;
import com.placementassist.shared.api.PageResponse;
import com.placementassist.shared.security.JwtAuthFilter;
import com.placementassist.shared.security.JwtService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OffCampusJobController.class)
@AutoConfigureMockMvc(addFilters = false)
class OffCampusJobControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private OffCampusJobService jobService;

    @MockBean
    private StudentRepository studentRepository;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private JwtAuthFilter jwtAuthFilter;

    @Test
    @DisplayName("GET /api/jobs - Returns paginated list of off-campus jobs")
    void testListJobs() throws Exception {
        OffCampusJobResponse response = new OffCampusJobResponse(
                1L, "Atlassian", "Associate Software Engineer", 16.0, "Bengaluru",
                "2024, 2025", "Fresher", "Java, React", LocalDate.now().plusDays(20),
                "https://atlassian.com", "1. Apply online\n2. HackerRank",
                List.of("Apply online", "HackerRank"),
                OpportunityStatus.OPEN, false, LocalDateTime.now(), LocalDateTime.now()
        );

        PageResponse<OffCampusJobResponse> pageResponse = PageResponse.<OffCampusJobResponse>builder()
                .content(List.of(response))
                .page(0)
                .size(10)
                .totalElements(1)
                .totalPages(1)
                .build();

        when(jobService.listJobs(any(), any(), any(), any(), any())).thenReturn(pageResponse);

        mockMvc.perform(get("/api/jobs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content[0].company").value("Atlassian"))
                .andExpect(jsonPath("$.data.content[0].packageLpa").value(16.0));
    }

    @Test
    @DisplayName("POST /api/jobs - Creates a new job opening")
    void testCreateJob() throws Exception {
        CreateOffCampusJobRequest request = new CreateOffCampusJobRequest(
                "CRED", "Backend Engineer", 18.0, "Bengaluru", "2025", "Fresher",
                "Java, Spring", LocalDate.of(2025, 9, 30), "https://cred.club",
                "Step 1", OpportunityStatus.OPEN
        );

        OffCampusJobResponse response = new OffCampusJobResponse(
                2L, "CRED", "Backend Engineer", 18.0, "Bengaluru", "2025", "Fresher",
                "Java, Spring", LocalDate.of(2025, 9, 30), "https://cred.club",
                "Step 1", List.of("Step 1"), OpportunityStatus.OPEN, false,
                LocalDateTime.now(), LocalDateTime.now()
        );

        when(jobService.createJob(any())).thenReturn(response);

        mockMvc.perform(post("/api/jobs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.company").value("CRED"));
    }

    @Test
    @DisplayName("POST /api/jobs/{id}/bookmark - Toggles student bookmark")
    @WithMockUser(username = "aarav@example.com", roles = "STUDENT")
    void testToggleBookmark() throws Exception {
        Student student = Student.builder().id(1L).email("aarav@example.com").build();
        when(studentRepository.findByEmail("aarav@example.com")).thenReturn(Optional.of(student));
        when(jobService.toggleBookmark(1L, 1L)).thenReturn(true);

        mockMvc.perform(post("/api/jobs/1/bookmark").with(user("aarav@example.com").roles("STUDENT")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value(true));
    }
}
