package com.placementassist.institute.modules.dashboard;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.placementassist.institute.modules.auth.UserRepository;
import com.placementassist.institute.modules.dashboard.dto.DashboardSummaryResponse;
import com.placementassist.shared.security.JwtAuthFilter;
import com.placementassist.shared.security.JwtService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DashboardController.class)
@AutoConfigureMockMvc(addFilters = false)
class DashboardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DashboardService dashboardService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private JwtAuthFilter jwtAuthFilter;

    @Test
    @DisplayName("GET /api/dashboard/summary - Returns aggregated dashboard KPIs")
    void testGetDashboardSummary() throws Exception {
        DashboardSummaryResponse summary = new DashboardSummaryResponse(
                15L, 12L, 5L, 88.5, 3L, 4L,
                Map.of("CSE", 8L, "IT", 4L, "ECE", 3L),
                Map.of("UPCOMING", 5L, "COMPLETED", 10L)
        );

        when(dashboardService.getDashboardSummary()).thenReturn(summary);

        mockMvc.perform(get("/api/dashboard/summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.totalStudents").value(15))
                .andExpect(jsonPath("$.data.eligibleStudents").value(12))
                .andExpect(jsonPath("$.data.upcomingDrives").value(5))
                .andExpect(jsonPath("$.data.averageAttendance").value(88.5))
                .andExpect(jsonPath("$.data.branchDistribution.CSE").value(8));
    }
}
