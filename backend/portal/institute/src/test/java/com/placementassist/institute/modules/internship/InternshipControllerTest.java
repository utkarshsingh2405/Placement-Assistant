package com.placementassist.institute.modules.internship;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.placementassist.institute.common.enums.OpportunityMode;
import com.placementassist.institute.common.enums.OpportunityStatus;
import com.placementassist.institute.modules.auth.UserRepository;
import com.placementassist.institute.modules.internship.dto.CreateInternshipRequest;
import com.placementassist.institute.modules.internship.dto.InternshipResponse;
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

@WebMvcTest(InternshipController.class)
@AutoConfigureMockMvc(addFilters = false)
class InternshipControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private InternshipService internshipService;

    @MockBean
    private StudentRepository studentRepository;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private JwtAuthFilter jwtAuthFilter;

    @Test
    @DisplayName("GET /api/internships - Returns paginated list of internships")
    void testListInternships() throws Exception {
        InternshipResponse response = new InternshipResponse(
                1L, "Google India", "SDE Intern", 110000.0, 3, "Bengaluru",
                OpportunityMode.HYBRID, "CGPA >= 8.0", LocalDate.now().plusDays(20),
                "https://careers.google.com", "1. Apply online\n2. Coding test",
                List.of("Apply online", "Coding test"), "Java, DSA",
                OpportunityStatus.OPEN, false, LocalDateTime.now(), LocalDateTime.now()
        );

        PageResponse<InternshipResponse> pageResponse = PageResponse.<InternshipResponse>builder()
                .content(List.of(response))
                .page(0)
                .size(10)
                .totalElements(1)
                .totalPages(1)
                .build();

        when(internshipService.listInternships(any(), any(), any(), any(), any())).thenReturn(pageResponse);

        mockMvc.perform(get("/api/internships"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content[0].company").value("Google India"))
                .andExpect(jsonPath("$.data.content[0].stipend").value(110000.0));
    }

    @Test
    @DisplayName("POST /api/internships - Creates a new internship listing")
    void testCreateInternship() throws Exception {
        CreateInternshipRequest request = new CreateInternshipRequest(
                "Microsoft", "Software Engineer Intern", 125000.0, 2, "Hyderabad",
                OpportunityMode.HYBRID, "Pre-final year", LocalDate.of(2025, 6, 30),
                "https://careers.microsoft.com", "Step 1", "C++, Cloud", OpportunityStatus.OPEN
        );

        InternshipResponse response = new InternshipResponse(
                2L, "Microsoft", "Software Engineer Intern", 125000.0, 2, "Hyderabad",
                OpportunityMode.HYBRID, "Pre-final year", LocalDate.of(2025, 6, 30),
                "https://careers.microsoft.com", "Step 1", List.of("Step 1"), "C++, Cloud",
                OpportunityStatus.OPEN, false, LocalDateTime.now(), LocalDateTime.now()
        );

        when(internshipService.createInternship(any())).thenReturn(response);

        mockMvc.perform(post("/api/internships")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.company").value("Microsoft"));
    }

    @Test
    @DisplayName("POST /api/internships/{id}/bookmark - Toggles student bookmark")
    @WithMockUser(username = "aarav@example.com", roles = "STUDENT")
    void testToggleBookmark() throws Exception {
        Student student = Student.builder().id(1L).email("aarav@example.com").build();
        when(studentRepository.findByEmail("aarav@example.com")).thenReturn(Optional.of(student));
        when(internshipService.toggleBookmark(1L, 1L)).thenReturn(true);

        mockMvc.perform(post("/api/internships/1/bookmark").with(user("aarav@example.com").roles("STUDENT")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value(true));
    }
}
