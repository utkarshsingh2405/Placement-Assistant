package com.placementassist.institute.modules.student;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.placementassist.institute.modules.auth.UserRepository;
import com.placementassist.institute.modules.student.dto.CreateStudentRequest;
import com.placementassist.institute.modules.student.dto.StudentResponse;
import com.placementassist.shared.api.PageResponse;
import com.placementassist.shared.security.JwtAuthFilter;
import com.placementassist.shared.security.JwtService;
import com.placementassist.shared.security.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StudentController.class)
@AutoConfigureMockMvc(addFilters = false)
class StudentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private StudentService studentService;

    @MockBean
    private StudentRepository studentRepository;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private JwtAuthFilter jwtAuthFilter;

    @Test
    @DisplayName("GET /api/students - Returns paginated list of students")
    void testListStudents() throws Exception {
        StudentResponse student = StudentResponse.builder()
                .id(1L)
                .name("Aarav Sharma")
                .rollNo("21CS01")
                .email("aarav.sharma@college.edu")
                .branch("CSE")
                .year(2025)
                .cgpa(8.85)
                .backlogs(0)
                .placementEligible(true)
                .build();

        PageResponse<StudentResponse> pageResponse = PageResponse.<StudentResponse>builder()
                .content(List.of(student))
                .page(0)
                .size(10)
                .totalElements(1)
                .totalPages(1)
                .isFirst(true)
                .isLast(true)
                .build();

        when(studentService.listStudents(any(), any(), any(), any(), any(), any(Pageable.class)))
                .thenReturn(pageResponse);

        mockMvc.perform(get("/api/students")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content[0].rollNo").value("21CS01"))
                .andExpect(jsonPath("$.data.content[0].placementEligible").value(true));
    }

    @Test
    @DisplayName("POST /api/students - Creates a new student successfully")
    @WithMockUser(roles = "TPO")
    void testCreateStudent() throws Exception {
        CreateStudentRequest request = CreateStudentRequest.builder()
                .name("Diya Patel")
                .rollNo("21CS02")
                .email("diya.patel@college.edu")
                .password("Password@123")
                .branch("CSE")
                .year(2025)
                .cgpa(9.20)
                .backlogs(0)
                .skills(List.of("Python", "FastAPI"))
                .build();

        StudentResponse response = StudentResponse.builder()
                .id(2L)
                .name("Diya Patel")
                .rollNo("21CS02")
                .email("diya.patel@college.edu")
                .branch("CSE")
                .year(2025)
                .cgpa(9.20)
                .backlogs(0)
                .placementEligible(true)
                .build();

        when(studentService.createStudent(any(CreateStudentRequest.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/students")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.rollNo").value("21CS02"));
    }

    @Test
    @DisplayName("GET /api/students/export - Exports CSV file")
    @WithMockUser(roles = "TPO")
    void testExportStudentsCsv() throws Exception {
        byte[] sampleCsv = "ID,Roll Number,Name\n1,21CS01,Aarav Sharma\n".getBytes();
        when(studentService.exportStudentsCsv(any(), any(), any(), any()))
                .thenReturn(sampleCsv);

        mockMvc.perform(get("/api/students/export"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", "attachment; filename=\"students_export.csv\""))
                .andExpect(content().contentType("text/csv"));
    }
}
