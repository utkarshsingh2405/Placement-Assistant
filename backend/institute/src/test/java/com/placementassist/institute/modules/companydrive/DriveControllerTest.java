package com.placementassist.institute.modules.companydrive;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.placementassist.institute.common.enums.DriveStatus;
import com.placementassist.institute.modules.auth.UserRepository;
import com.placementassist.institute.modules.companydrive.dto.CreateDriveRequest;
import com.placementassist.institute.modules.companydrive.dto.DriveDetailResponse;
import com.placementassist.institute.modules.companydrive.dto.DriveResponse;
import com.placementassist.shared.api.PageResponse;
import com.placementassist.shared.security.JwtAuthFilter;
import com.placementassist.shared.security.JwtService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DriveController.class)
@AutoConfigureMockMvc(addFilters = false)
class DriveControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CompanyDriveService service;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private JwtAuthFilter jwtAuthFilter;

    @Test
    @DisplayName("GET /api/drives/upcoming - Returns upcoming recruitment drives")
    void testGetUpcomingDrives() throws Exception {
        DriveResponse drive = DriveResponse.builder()
                .id(1L)
                .title("SDE-1 Hiring 2025")
                .companyName("Google India")
                .driveDate(LocalDate.now().plusDays(10))
                .lastDateToApply(LocalDate.now().plusDays(5))
                .eligibilityCgpa(8.0)
                .status(DriveStatus.UPCOMING)
                .build();

        when(service.getUpcomingDrives()).thenReturn(List.of(drive));

        mockMvc.perform(get("/api/drives/upcoming")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].title").value("SDE-1 Hiring 2025"))
                .andExpect(jsonPath("$.data[0].companyName").value("Google India"));
    }

    @Test
    @DisplayName("GET /api/drives/{id} - Returns drive detail with eligible students count")
    void testGetDriveById() throws Exception {
        DriveDetailResponse response = DriveDetailResponse.builder()
                .id(1L)
                .title("SDE-1 Hiring 2025")
                .eligibilityCgpa(8.0)
                .eligibleStudentsCount(25)
                .rounds(Collections.emptyList())
                .build();

        when(service.getDriveById(1L)).thenReturn(response);

        mockMvc.perform(get("/api/drives/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.eligibleStudentsCount").value(25));
    }

    @Test
    @DisplayName("POST /api/drives - Creates a new drive successfully")
    @WithMockUser(roles = "TPO")
    void testCreateDrive() throws Exception {
        CreateDriveRequest request = CreateDriveRequest.builder()
                .companyId(1L)
                .title("Graduate Analyst 2025")
                .driveDate(LocalDate.now().plusDays(15))
                .lastDateToApply(LocalDate.now().plusDays(8))
                .eligibilityCgpa(7.0)
                .eligibleBranches(List.of("CSE", "IT"))
                .roles(List.of("Analyst"))
                .maxBacklogs(0)
                .build();

        DriveResponse response = DriveResponse.builder()
                .id(2L)
                .title("Graduate Analyst 2025")
                .companyId(1L)
                .status(DriveStatus.UPCOMING)
                .build();

        when(service.createDrive(any(CreateDriveRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/drives")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.title").value("Graduate Analyst 2025"));
    }
}
