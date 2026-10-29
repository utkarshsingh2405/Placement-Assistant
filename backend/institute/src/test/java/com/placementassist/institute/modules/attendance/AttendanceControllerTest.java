package com.placementassist.institute.modules.attendance;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.placementassist.institute.common.enums.AttendanceStatus;
import com.placementassist.institute.modules.attendance.crt.CrtSessionController;
import com.placementassist.institute.modules.attendance.crt.dto.CreateCrtSessionRequest;
import com.placementassist.institute.modules.attendance.crt.dto.CrtSessionResponse;
import com.placementassist.institute.modules.attendance.dto.*;
import com.placementassist.institute.modules.auth.UserRepository;
import com.placementassist.shared.security.JwtAuthFilter;
import com.placementassist.shared.security.JwtService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {AttendanceController.class, CrtSessionController.class})
@AutoConfigureMockMvc(addFilters = false)
class AttendanceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AttendanceService attendanceService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private JwtAuthFilter jwtAuthFilter;

    @Test
    @DisplayName("GET /api/attendance/student/{id} - Returns student attendance statistics and percentage")
    void testGetStudentAttendanceStats() throws Exception {
        StudentAttendanceStatsResponse stats = new StudentAttendanceStatsResponse(
                1L, "21CS001", "Aarav Sharma", "CSE", 4,
                20L, 18L, 90.0,
                8L, 7L, 87.5,
                12L, 11L, 91.67,
                false,
                List.of()
        );

        when(attendanceService.getStudentAttendanceStats(1L)).thenReturn(stats);

        mockMvc.perform(get("/api/attendance/student/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.rollNo").value("21CS001"))
                .andExpect(jsonPath("$.data.overallPercentage").value(90.0))
                .andExpect(jsonPath("$.data.isDefaulter").value(false));
    }

    @Test
    @DisplayName("GET /api/attendance/report - Returns report with defaulters (< 75%)")
    void testGetAttendanceReport() throws Exception {
        DefaulterStudentDto defaulter = new DefaulterStudentDto(
                15L, "21CS015", "Kiran Patel", "kiran@example.com", "CSE", 4,
                4L, 20L, 20.0
        );

        AttendanceReportResponse report = new AttendanceReportResponse(
                LocalDate.now().minusDays(30),
                LocalDate.now(),
                "CSE",
                15,
                1,
                81.5,
                List.of(defaulter)
        );

        when(attendanceService.getAttendanceReport(any(), any(), eq("CSE"))).thenReturn(report);

        mockMvc.perform(get("/api/attendance/report").param("branch", "CSE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.defaultersCount").value(1))
                .andExpect(jsonPath("$.data.defaulters[0].rollNo").value("21CS015"))
                .andExpect(jsonPath("$.data.defaulters[0].attendancePercentage").value(20.0));
    }

    @Test
    @DisplayName("POST /api/crt/sessions - Creates a new CRT session")
    void testCreateCrtSession() throws Exception {
        CreateCrtSessionRequest request = new CreateCrtSessionRequest(
                "Aptitude Crash Course",
                "Percentages & Ratios",
                "Mr. Sharma",
                LocalDate.of(2025, 4, 10),
                "10:00",
                "13:00",
                "Seminar Hall 1"
        );

        CrtSessionResponse response = new CrtSessionResponse(
                10L,
                "Aptitude Crash Course",
                "Percentages & Ratios",
                "Mr. Sharma",
                LocalDate.of(2025, 4, 10),
                "10:00",
                "13:00",
                "Seminar Hall 1",
                0, 0,
                LocalDateTime.now(), LocalDateTime.now()
        );

        when(attendanceService.createCrtSession(any())).thenReturn(response);

        mockMvc.perform(post("/api/crt/sessions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.title").value("Aptitude Crash Course"));
    }

    @Test
    @DisplayName("POST /api/crt/sessions/{id}/mark - Marks bulk attendance")
    void testMarkCrtAttendance() throws Exception {
        MarkAttendanceRequest request = new MarkAttendanceRequest(List.of(
                new StudentAttendanceItemDto(1L, AttendanceStatus.PRESENT),
                new StudentAttendanceItemDto(2L, AttendanceStatus.ABSENT)
        ));

        when(attendanceService.markCrtAttendance(eq(10L), any(), any()))
                .thenReturn(List.of());

        mockMvc.perform(post("/api/crt/sessions/10/mark")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }
}
