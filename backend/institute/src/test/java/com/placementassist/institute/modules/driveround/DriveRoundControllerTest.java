package com.placementassist.institute.modules.driveround;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.placementassist.institute.common.enums.RoundType;
import com.placementassist.institute.modules.auth.UserRepository;
import com.placementassist.institute.modules.driveround.dto.CreateRoundRequest;
import com.placementassist.institute.modules.driveround.dto.DriveFunnelResponse;
import com.placementassist.institute.modules.driveround.dto.DriveRoundResponse;
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

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DriveRoundController.class)
@AutoConfigureMockMvc(addFilters = false)
class DriveRoundControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private DriveRoundService service;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private JwtAuthFilter jwtAuthFilter;

    @Test
    @DisplayName("GET /api/drives/{id}/rounds - Returns ordered rounds for a drive")
    void testGetRoundsForDrive() throws Exception {
        DriveRoundResponse r = DriveRoundResponse.builder()
                .id(1L)
                .driveId(1L)
                .roundType(RoundType.APTITUDE)
                .orderNo(1)
                .venue("Hall A")
                .cutoff(70.0)
                .build();

        when(service.getRoundsForDrive(1L)).thenReturn(List.of(r));

        mockMvc.perform(get("/api/drives/1/rounds"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].roundType").value("APTITUDE"))
                .andExpect(jsonPath("$.data[0].orderNo").value(1));
    }

    @Test
    @DisplayName("GET /api/drives/{id}/funnel - Returns candidate funnel progression")
    void testGetDriveFunnel() throws Exception {
        DriveFunnelResponse funnel = DriveFunnelResponse.builder()
                .driveId(1L)
                .driveTitle("SDE Drive")
                .totalEligibleStudents(100)
                .funnel(List.of(
                        DriveFunnelResponse.FunnelStage.builder().orderNo(1).roundType(RoundType.APTITUDE).passedCount(40).build(),
                        DriveFunnelResponse.FunnelStage.builder().orderNo(2).roundType(RoundType.TECHNICAL).passedCount(15).build()
                ))
                .build();

        when(service.getDriveFunnel(1L)).thenReturn(funnel);

        mockMvc.perform(get("/api/drives/1/funnel"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.funnel[0].passedCount").value(40))
                .andExpect(jsonPath("$.data.funnel[1].passedCount").value(15));
    }
}
