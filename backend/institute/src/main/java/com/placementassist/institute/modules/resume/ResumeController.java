package com.placementassist.institute.modules.resume;

import com.placementassist.institute.common.enums.ResumeStatus;
import com.placementassist.institute.modules.resume.dto.RescoreResumeRequest;
import com.placementassist.institute.modules.resume.dto.ResumeResponse;
import com.placementassist.institute.modules.resume.dto.ReviewResumeRequest;
import com.placementassist.institute.modules.student.Student;
import com.placementassist.institute.modules.student.StudentRepository;
import com.placementassist.shared.api.ApiResponse;
import com.placementassist.shared.api.PageResponse;
import com.placementassist.shared.exception.ApiException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/resumes")
@Tag(name = "Resume & ATS", description = "Student resume upload (PDF only, max 5MB), automated 100-pt ATS scoring, TPO review, and re-scoring")
public class ResumeController {

    private final ResumeService resumeService;
    private final StudentRepository studentRepository;

    public ResumeController(ResumeService resumeService, StudentRepository studentRepository) {
        this.resumeService = resumeService;
        this.studentRepository = studentRepository;
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload resume PDF, parse text with PDFBox, and compute ATS score")
    public ResponseEntity<ApiResponse<ResumeResponse>> uploadResume(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "studentId", required = false) Long studentId,
            @RequestParam(value = "jobDescription", required = false) String jobDescription,
            Authentication authentication) {

        Long targetStudentId = studentId;
        if (targetStudentId == null && authentication != null) {
            Student student = studentRepository.findByEmail(authentication.getName())
                    .orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "STUDENT_NOT_FOUND",
                            "Current logged in user is not linked to a student profile. Provide studentId parameter."));
            targetStudentId = student.getId();
        }

        if (targetStudentId == null) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "MISSING_STUDENT_ID", "studentId is required");
        }

        ResumeResponse response = resumeService.uploadResume(targetStudentId, file, jobDescription);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Resume uploaded and analyzed successfully", response));
    }

    @GetMapping
    @Operation(summary = "List resumes with optional student and status filters")
    public ResponseEntity<ApiResponse<PageResponse<ResumeResponse>>> listResumes(
            @RequestParam(required = false) Long studentId,
            @RequestParam(required = false) ResumeStatus status,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        PageResponse<ResumeResponse> response = resumeService.listResumes(studentId, status, pageable);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get resume details and full ATS breakdown by ID")
    public ResponseEntity<ApiResponse<ResumeResponse>> getResumeById(@PathVariable Long id) {
        ResumeResponse response = resumeService.getResumeById(id);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping("/student/{studentId}")
    @Operation(summary = "Get all resume versions for a specific student")
    public ResponseEntity<ApiResponse<List<ResumeResponse>>> getStudentResumes(@PathVariable Long studentId) {
        List<ResumeResponse> list = resumeService.getStudentResumes(studentId);
        return ResponseEntity.ok(ApiResponse.ok(list));
    }

    @PutMapping("/{id}/review")
    @PreAuthorize("hasAnyRole('TPO', 'DIRECTOR', 'ADMIN')")
    @Operation(summary = "Review a resume (Approve/Reject with feedback) - TPO/Admin only")
    public ResponseEntity<ApiResponse<ResumeResponse>> reviewResume(
            @PathVariable Long id,
            @Valid @RequestBody ReviewResumeRequest request,
            Authentication authentication) {

        String reviewer = authentication != null ? authentication.getName() : "TPO_STAFF";
        ResumeResponse response = resumeService.reviewResume(id, request, reviewer);
        return ResponseEntity.ok(ApiResponse.ok("Resume review updated successfully", response));
    }

    @PostMapping("/{id}/rescore")
    @Operation(summary = "Re-score existing resume against a specific job description")
    public ResponseEntity<ApiResponse<ResumeResponse>> rescoreResume(
            @PathVariable Long id,
            @Valid @RequestBody RescoreResumeRequest request) {

        ResumeResponse response = resumeService.rescoreResume(id, request);
        return ResponseEntity.ok(ApiResponse.ok("Resume re-scored successfully", response));
    }

    @GetMapping("/{id}/download")
    @Operation(summary = "Download the original PDF file of a resume")
    public ResponseEntity<Resource> downloadResume(@PathVariable Long id) {
        Resource resource = resumeService.loadResumeAsResource(id);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }
}
