package com.placementassist.institute.modules.student;

import com.placementassist.institute.modules.student.dto.CreateStudentRequest;
import com.placementassist.institute.modules.student.dto.StudentResponse;
import com.placementassist.institute.modules.student.dto.UpdateStudentRequest;
import com.placementassist.shared.api.ApiResponse;
import com.placementassist.shared.api.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for Student profile management and search.
 */
@RestController
@RequestMapping("/api/students")
@Tag(name = "Students", description = "Student management, directory search, profile updates, and CSV export")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping
    @Operation(summary = "List and search students with filters and pagination")
    public ResponseEntity<ApiResponse<PageResponse<StudentResponse>>> listStudents(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String branch,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Double minCgpa,
            @RequestParam(required = false) Boolean eligible,
            @PageableDefault(size = 10, sort = "rollNo", direction = Sort.Direction.ASC) Pageable pageable) {

        PageResponse<StudentResponse> response = studentService.listStudents(search, branch, year, minCgpa, eligible, pageable);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping("/export")
    @PreAuthorize("hasAnyRole('TPO', 'DIRECTOR', 'ADMIN')")
    @Operation(summary = "Export students directory as a CSV spreadsheet")
    public ResponseEntity<byte[]> exportStudents(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String branch,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Boolean eligible) {

        byte[] csvData = studentService.exportStudentsCsv(search, branch, year, eligible);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"students_export.csv\"")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csvData);
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Get current student's personal profile")
    public ResponseEntity<ApiResponse<StudentResponse>> getMyProfile() {
        StudentResponse response = studentService.getCurrentStudentProfile();
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PutMapping("/me")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Update current student's personal profile")
    public ResponseEntity<ApiResponse<StudentResponse>> updateMyProfile(
            @Valid @RequestBody UpdateStudentRequest request) {
        StudentResponse response = studentService.updateCurrentStudentProfile(request);
        return ResponseEntity.ok(ApiResponse.ok("Profile updated successfully", response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get student details by ID")
    public ResponseEntity<ApiResponse<StudentResponse>> getStudentById(@PathVariable Long id) {
        StudentResponse response = studentService.getStudentById(id);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('TPO', 'DIRECTOR', 'ADMIN')")
    @Operation(summary = "Create a new student (Staff only)")
    public ResponseEntity<ApiResponse<StudentResponse>> createStudent(@Valid @RequestBody CreateStudentRequest request) {
        StudentResponse response = studentService.createStudent(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Student created successfully", response));
    }

    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Update student profile (Student owner or Staff)")
    public ResponseEntity<ApiResponse<StudentResponse>> updateStudent(
            @PathVariable Long id,
            @Valid @RequestBody UpdateStudentRequest request) {
        StudentResponse response = studentService.updateStudent(id, request);
        return ResponseEntity.ok(ApiResponse.ok("Student updated successfully", response));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('TPO', 'DIRECTOR', 'ADMIN')")
    @Operation(summary = "Delete student and linked user account")
    public ResponseEntity<ApiResponse<Void>> deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);
        return ResponseEntity.ok(ApiResponse.message("Student deleted successfully"));
    }
}
