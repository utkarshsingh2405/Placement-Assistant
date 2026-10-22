package com.placementassist.institute.modules.resume;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.placementassist.institute.common.enums.ResumeStatus;
import com.placementassist.institute.modules.auth.UserRepository;
import com.placementassist.institute.modules.resume.dto.*;
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
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ResumeController.class)
@AutoConfigureMockMvc(addFilters = false)
class ResumeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ResumeService resumeService;

    @MockBean
    private StudentRepository studentRepository;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private JwtAuthFilter jwtAuthFilter;

    @Test
    @DisplayName("POST /api/resumes/upload - Successfully uploads and parses PDF resume")
    void testUploadResume() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test_resume.pdf",
                "application/pdf",
                "%PDF-1.4 sample content".getBytes()
        );

        AtsBreakdownDto breakdown = new AtsBreakdownDto(
                85, 35, 25, 15, 10,
                List.of("java", "spring"), List.of(),
                List.of("Education", "Skills"), List.of(),
                List.of("Good job!"), 450
        );

        ResumeResponse response = new ResumeResponse(
                1L, 1L, "21CS001", "Aarav Sharma",
                "student_21CS001/test_resume.pdf", 1,
                ResumeStatus.PENDING, 85, breakdown, null, null,
                LocalDateTime.now(), LocalDateTime.now()
        );

        when(resumeService.uploadResume(eq(1L), any(), any())).thenReturn(response);

        mockMvc.perform(multipart("/api/resumes/upload")
                        .file(file)
                        .param("studentId", "1")
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.atsScore").value(85))
                .andExpect(jsonPath("$.data.version").value(1));
    }

    @Test
    @DisplayName("GET /api/resumes/{id} - Returns resume details and ATS breakdown")
    void testGetResumeById() throws Exception {
        ResumeResponse response = new ResumeResponse(
                1L, 1L, "21CS001", "Aarav Sharma",
                "student_21CS001/resume.pdf", 1,
                ResumeStatus.APPROVED, 90, null, "Approved!", "tpo@test.com",
                LocalDateTime.now(), LocalDateTime.now()
        );

        when(resumeService.getResumeById(1L)).thenReturn(response);

        mockMvc.perform(get("/api/resumes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.atsScore").value(90))
                .andExpect(jsonPath("$.data.status").value("APPROVED"));
    }

    @Test
    @DisplayName("PUT /api/resumes/{id}/review - TPO reviews and approves resume")
    void testReviewResume() throws Exception {
        ReviewResumeRequest request = new ReviewResumeRequest(ResumeStatus.APPROVED, "Excellent resume format.");

        ResumeResponse response = new ResumeResponse(
                1L, 1L, "21CS001", "Aarav Sharma",
                "student_21CS001/resume.pdf", 1,
                ResumeStatus.APPROVED, 90, null, "Excellent resume format.", "TPO_STAFF",
                LocalDateTime.now(), LocalDateTime.now()
        );

        when(resumeService.reviewResume(eq(1L), any(), any())).thenReturn(response);

        mockMvc.perform(put("/api/resumes/1/review")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("APPROVED"))
                .andExpect(jsonPath("$.data.feedback").value("Excellent resume format."));
    }
}
