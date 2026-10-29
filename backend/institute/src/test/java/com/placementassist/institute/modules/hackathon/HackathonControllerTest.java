package com.placementassist.institute.modules.hackathon;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.placementassist.institute.common.enums.OpportunityMode;
import com.placementassist.institute.common.enums.OpportunityStatus;
import com.placementassist.institute.modules.auth.UserRepository;
import com.placementassist.institute.modules.hackathon.dto.CreateHackathonRequest;
import com.placementassist.institute.modules.hackathon.dto.HackathonResponse;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(HackathonController.class)
@AutoConfigureMockMvc(addFilters = false)
class HackathonControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private HackathonService hackathonService;

    @MockBean
    private StudentRepository studentRepository;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private JwtAuthFilter jwtAuthFilter;

    @Test
    @DisplayName("GET /api/hackathons - Returns paginated list of hackathons")
    void testListHackathons() throws Exception {
        HackathonResponse response = new HackathonResponse(
                1L, "SIH 2025", "AICTE", OpportunityMode.HYBRID,
                LocalDate.now().plusDays(30), LocalDate.now().plusDays(32), LocalDate.now().plusDays(10),
                "₹1,00,000", 6, "New Delhi", "https://sih.gov.in", "Description",
                "1. Apply online\n2. Submit PPT", List.of("Apply online", "Submit PPT"),
                OpportunityStatus.OPEN, false, LocalDateTime.now(), LocalDateTime.now()
        );

        PageResponse<HackathonResponse> pageResponse = PageResponse.<HackathonResponse>builder()
                .content(List.of(response))
                .page(0)
                .size(10)
                .totalElements(1)
                .totalPages(1)
                .build();

        when(hackathonService.listHackathons(any(), any(), any(), any(), any())).thenReturn(pageResponse);

        mockMvc.perform(get("/api/hackathons"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content[0].name").value("SIH 2025"))
                .andExpect(jsonPath("$.data.content[0].howToApplySteps[0]").value("Apply online"));
    }

    @Test
    @DisplayName("POST /api/hackathons - Creates a new hackathon")
    void testCreateHackathon() throws Exception {
        CreateHackathonRequest request = new CreateHackathonRequest(
                "Flipkart GRiD", "Flipkart", OpportunityMode.ONLINE,
                LocalDate.of(2025, 8, 1), LocalDate.of(2025, 8, 5), LocalDate.of(2025, 7, 20),
                "₹5,00,000", 3, "Online", "https://unstop.com", "Challenge", "Step 1", OpportunityStatus.OPEN
        );

        HackathonResponse response = new HackathonResponse(
                2L, "Flipkart GRiD", "Flipkart", OpportunityMode.ONLINE,
                LocalDate.of(2025, 8, 1), LocalDate.of(2025, 8, 5), LocalDate.of(2025, 7, 20),
                "₹5,00,000", 3, "Online", "https://unstop.com", "Challenge", "Step 1",
                List.of("Step 1"), OpportunityStatus.OPEN, false, LocalDateTime.now(), LocalDateTime.now()
        );

        when(hackathonService.createHackathon(any())).thenReturn(response);

        mockMvc.perform(post("/api/hackathons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.name").value("Flipkart GRiD"));
    }

    @Test
    @DisplayName("POST /api/hackathons/{id}/bookmark - Toggles student bookmark")
    @WithMockUser(username = "aarav@example.com", roles = "STUDENT")
    void testToggleBookmark() throws Exception {
        Student student = Student.builder().id(1L).email("aarav@example.com").build();
        when(studentRepository.findByEmail("aarav@example.com")).thenReturn(Optional.of(student));
        when(hackathonService.toggleBookmark(1L, 1L)).thenReturn(true);

        mockMvc.perform(post("/api/hackathons/1/bookmark").with(user("aarav@example.com").roles("STUDENT")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value(true));
    }
}
