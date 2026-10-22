package com.placementassist.institute.modules.internship;

import com.placementassist.institute.common.enums.OpportunityMode;
import com.placementassist.institute.common.enums.OpportunityStatus;
import com.placementassist.institute.modules.internship.dto.CreateInternshipRequest;
import com.placementassist.institute.modules.internship.dto.InternshipResponse;
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
@RequestMapping("/api/internships")
@Tag(name = "Internships", description = "Corporate and research internship listings, stipend information, filters, and bookmarking")
public class InternshipController {

    private final InternshipService internshipService;
    private final StudentRepository studentRepository;

    public InternshipController(InternshipService internshipService, StudentRepository studentRepository) {
        this.internshipService = internshipService;
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
    @Operation(summary = "Create a new internship opportunity")
    public ResponseEntity<ApiResponse<InternshipResponse>> createInternship(@Valid @RequestBody CreateInternshipRequest request) {
        InternshipResponse response = internshipService.createInternship(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Internship created successfully", response));
    }

    @GetMapping
    @Operation(summary = "List internships with search, mode, and status filters")
    public ResponseEntity<ApiResponse<PageResponse<InternshipResponse>>> listInternships(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) OpportunityMode mode,
            @RequestParam(required = false) OpportunityStatus status,
            @PageableDefault(size = 10, sort = "deadline", direction = Sort.Direction.ASC) Pageable pageable,
            Authentication authentication) {

        PageResponse<InternshipResponse> response = internshipService.listInternships(search, mode, status, getStudentId(authentication), pageable);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping("/upcoming")
    @Operation(summary = "Get upcoming open internships ordered by deadline")
    public ResponseEntity<ApiResponse<List<InternshipResponse>>> getUpcomingInternships(Authentication authentication) {
        List<InternshipResponse> response = internshipService.getUpcomingInternships(getStudentId(authentication));
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get internship details and ordered how-to-apply steps")
    public ResponseEntity<ApiResponse<InternshipResponse>> getInternshipById(
            @PathVariable Long id,
            Authentication authentication) {

        InternshipResponse response = internshipService.getInternshipById(id, getStudentId(authentication));
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('TPO', 'DIRECTOR', 'ADMIN')")
    @Operation(summary = "Update internship listing")
    public ResponseEntity<ApiResponse<InternshipResponse>> updateInternship(
            @PathVariable Long id,
            @Valid @RequestBody CreateInternshipRequest request,
            Authentication authentication) {

        InternshipResponse response = internshipService.updateInternship(id, request, getStudentId(authentication));
        return ResponseEntity.ok(ApiResponse.ok("Internship updated successfully", response));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('TPO', 'DIRECTOR', 'ADMIN')")
    @Operation(summary = "Delete an internship listing")
    public ResponseEntity<ApiResponse<Void>> deleteInternship(@PathVariable Long id) {
        internshipService.deleteInternship(id);
        return ResponseEntity.ok(ApiResponse.message("Internship deleted successfully"));
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
        boolean bookmarked = internshipService.toggleBookmark(id, studentId);
        String msg = bookmarked ? "Internship added to bookmarks" : "Internship removed from bookmarks";
        return ResponseEntity.ok(ApiResponse.ok(msg, bookmarked));
    }
}
