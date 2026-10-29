package com.placementassist.placement.modules.result;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.placementassist.shared.exception.ForbiddenException;
import com.placementassist.shared.security.JwtAuthFilter;
import com.placementassist.shared.security.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PlacementResultController.class)
@AutoConfigureMockMvc(addFilters = false)
class PlacementResultControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PlacementResultService resultService;

    @MockBean
    private JwtAuthFilter jwtAuthFilter;

    @MockBean
    private com.placementassist.shared.security.PortalJwtFilter portalJwtFilter;

    @MockBean
    private com.placementassist.shared.security.JwtService jwtService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("GET /api/stages/{stageId}/results - Returns 200 and result list")
    void testGetStageResults() throws Exception {
        PlacementResult r = PlacementResult.builder()
                .id(1L)
                .driveId(1L)
                .stageId(1L)
                .rollNo("21CS001")
                .studentName("Aarav Sharma")
                .resultStatus("SELECTED")
                .markedBy("tpo@college.edu")
                .build();

        when(resultService.getResultsForStage(1L)).thenReturn(List.of(r));

        mockMvc.perform(get("/api/stages/1/results"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].rollNo").value("21CS001"))
                .andExpect(jsonPath("$.data[0].resultStatus").value("SELECTED"));
    }

    @Test
    @DisplayName("POST /api/stages/{stageId}/results - TPO role successfully marks candidate as SELECTED")
    void testMarkResultAsTpo() throws Exception {
        PlacementResult r = PlacementResult.builder()
                .id(1L)
                .driveId(1L)
                .stageId(1L)
                .rollNo("21CS001")
                .studentName("Aarav Sharma")
                .resultStatus("SELECTED")
                .markedBy("tpo@college.edu")
                .build();

        when(resultService.markResult(eq(Role.TPO), eq(1L), eq("21CS001"), any(), eq("SELECTED"), any(), any()))
                .thenReturn(r);

        Map<String, String> body = Map.of(
                "rollNo", "21CS001",
                "resultStatus", "SELECTED",
                "roundFeedback", "Optimal problem solving"
        );

        mockMvc.perform(post("/api/stages/1/results")
                        .header("X-User-Role", "TPO")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.resultStatus").value("SELECTED"));
    }

    @Test
    @DisplayName("POST /api/stages/{stageId}/results - DIRECTOR role successfully marks candidate as SELECTED")
    void testMarkResultAsDirector() throws Exception {
        PlacementResult r = PlacementResult.builder()
                .id(1L)
                .driveId(1L)
                .stageId(1L)
                .rollNo("21CS001")
                .studentName("Aarav Sharma")
                .resultStatus("SELECTED")
                .markedBy("director@college.edu")
                .build();

        when(resultService.markResult(eq(Role.DIRECTOR), eq(1L), eq("21CS001"), any(), eq("SELECTED"), any(), any()))
                .thenReturn(r);

        Map<String, String> body = Map.of(
                "rollNo", "21CS001",
                "resultStatus", "SELECTED"
        );

        mockMvc.perform(post("/api/stages/1/results")
                        .header("X-User-Role", "DIRECTOR")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("POST /api/stages/{stageId}/results - STUDENT role is strictly FORBIDDEN (HTTP 403)")
    void testMarkResultAsStudentForbidden() throws Exception {
        when(resultService.markResult(eq(Role.STUDENT), eq(1L), any(), any(), any(), any(), any()))
                .thenThrow(new ForbiddenException("Only TPO and Director roles are authorized"));

        Map<String, String> body = Map.of(
                "rollNo", "21CS001",
                "resultStatus", "SELECTED"
        );

        mockMvc.perform(post("/api/stages/1/results")
                        .header("X-User-Role", "STUDENT")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("POST /api/stages/{stageId}/results - COMPANY role is strictly FORBIDDEN (HTTP 403)")
    void testMarkResultAsCompanyForbidden() throws Exception {
        when(resultService.markResult(eq(Role.COMPANY), eq(1L), any(), any(), any(), any(), any()))
                .thenThrow(new ForbiddenException("Only TPO and Director roles are authorized"));

        Map<String, String> body = Map.of(
                "rollNo", "21CS001",
                "resultStatus", "SELECTED"
        );

        mockMvc.perform(post("/api/stages/1/results")
                        .header("X-User-Role", "COMPANY")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isForbidden());
    }
}
