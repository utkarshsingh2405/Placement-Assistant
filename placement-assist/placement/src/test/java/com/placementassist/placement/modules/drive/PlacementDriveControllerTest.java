package com.placementassist.placement.modules.drive;

import com.placementassist.placement.modules.company.Company;
import com.placementassist.shared.security.JwtAuthFilter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PlacementDriveController.class)
@AutoConfigureMockMvc(addFilters = false)
class PlacementDriveControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PlacementDriveService driveService;

    @MockBean
    private JwtAuthFilter jwtAuthFilter;

    @MockBean
    private com.placementassist.shared.security.PortalJwtFilter portalJwtFilter;

    @MockBean
    private com.placementassist.shared.security.JwtService jwtService;

    @Test
    @DisplayName("GET /api/drives/all - Returns all placement drives")
    void testGetAllDrives() throws Exception {
        Company google = Company.builder().id(1L).name("Google").build();
        PlacementDrive drive = PlacementDrive.builder()
                .id(1L)
                .company(google)
                .title("Google Campus Recruitment 2026")
                .status("ONGOING")
                .build();

        when(driveService.getAllDrives()).thenReturn(List.of(drive));

        mockMvc.perform(get("/api/drives/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].title").value("Google Campus Recruitment 2026"));
    }

    @Test
    @DisplayName("GET /api/drives/{id} - Returns specific drive by ID")
    void testGetDriveById() throws Exception {
        Company google = Company.builder().id(1L).name("Google").build();
        PlacementDrive drive = PlacementDrive.builder()
                .id(1L)
                .company(google)
                .title("Google Campus Recruitment 2026")
                .status("ONGOING")
                .build();

        when(driveService.getDriveById(1L)).thenReturn(drive);

        mockMvc.perform(get("/api/drives/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1L));
    }
}
