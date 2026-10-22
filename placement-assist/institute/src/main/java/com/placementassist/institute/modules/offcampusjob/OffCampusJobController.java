package com.placementassist.institute.modules.offcampusjob;

import com.placementassist.institute.common.enums.OpportunityStatus;
import com.placementassist.institute.modules.offcampusjob.dto.CreateOffCampusJobRequest;
import com.placementassist.institute.modules.offcampusjob.dto.OffCampusJobResponse;
import com.placementassist.institute.modules.student.Student;
import com.placementassist.institute.modules.student.StudentRepository;
import com.placementassist.shared.api.ApiResponse;
import com.placementassist.shared.api.PageResponse;
import com.placementassist.shared.exception.ApiException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/jobs")
@Tag(name = "Off-Campus Jobs", description = "Verified off-campus freshers job listings, CTC packages, filters, and bookmarking")
public class OffCampusJobController {

    private final OffCampusJobService jobService;
    private final StudentRepository studentRepository;

    public OffCampusJobController(OffCampusJobService jobService, StudentRepository studentRepository) {
        this.jobService = jobService;
        this.studentRepository = studentRepository;
    }

    private Long getStudentId(Authentication authentication) {
        if (authentication == null) {
            authentication = SecurityContextHolder.getContext().getAuthentication();
        }
        if (authentication == null || authentication.getName() == null) {
            return null;
        }
        return studentRepository.findByEmail(authentication.getName()).map(Student::getId).orElse(null);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('TPO', 'DIRECTOR', 'ADMIN')")
    @Operation(summary = "Create a new off-campus job opening")
    public ResponseEntity<ApiResponse<OffCampusJobResponse>> createJob(@Valid @RequestBody CreateOffCampusJobRequest request) {
        OffCampusJobResponse response = jobService.createJob(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Off-campus job listing created successfully", response));
    }

    @GetMapping
    @Operation(summary = "List off-campus jobs with search, package, and status filters")
    public ResponseEntity<ApiResponse<PageResponse<OffCampusJobResponse>>> listJobs(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) OpportunityStatus status,
            @RequestParam(required = false) Double minPackage,
            @PageableDefault(size = 10, sort = "deadline", direction = Sort.Direction.ASC) Pageable pageable,
            Authentication authentication) {

        PageResponse<OffCampusJobResponse> response = jobService.listJobs(search, status, minPackage, getStudentId(authentication), pageable);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping("/upcoming")
    @Operation(summary = "Get upcoming open off-campus jobs ordered by deadline")
    public ResponseEntity<ApiResponse<List<OffCampusJobResponse>>> getUpcomingJobs(Authentication authentication) {
        List<OffCampusJobResponse> response = jobService.getUpcomingJobs(getStudentId(authentication));
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get off-campus job details and ordered how-to-apply steps")
    public ResponseEntity<ApiResponse<OffCampusJobResponse>> getJobById(
            @PathVariable Long id,
            Authentication authentication) {

        OffCampusJobResponse response = jobService.getJobById(id, getStudentId(authentication));
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('TPO', 'DIRECTOR', 'ADMIN')")
    @Operation(summary = "Update off-campus job listing")
    public ResponseEntity<ApiResponse<OffCampusJobResponse>> updateJob(
            @PathVariable Long id,
            @Valid @RequestBody CreateOffCampusJobRequest request,
            Authentication authentication) {

        OffCampusJobResponse response = jobService.updateJob(id, request, getStudentId(authentication));
        return ResponseEntity.ok(ApiResponse.ok("Job listing updated successfully", response));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('TPO', 'DIRECTOR', 'ADMIN')")
    @Operation(summary = "Delete an off-campus job listing")
    public ResponseEntity<ApiResponse<Void>> deleteJob(@PathVariable Long id) {
        jobService.deleteJob(id);
        return ResponseEntity.ok(ApiResponse.message("Job listing deleted successfully"));
    }

    @PostMapping("/{id}/bookmark")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Toggle bookmark for current student")
    public ResponseEntity<ApiResponse<Boolean>> toggleBookmark(
            @PathVariable Long id,
            Authentication authentication) {

        Long studentId = getStudentId(authentication);
        if (studentId == null) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "STUDENT_REQUIRED", "Only enrolled students can bookmark listings");
        }
        boolean bookmarked = jobService.toggleBookmark(id, studentId);
        String msg = bookmarked ? "Job added to bookmarks" : "Job removed from bookmarks";
        return ResponseEntity.ok(ApiResponse.ok(msg, bookmarked));
    }
}
