package com.placementassist.institute.modules.hackathon;

import com.placementassist.institute.common.enums.OpportunityMode;
import com.placementassist.institute.common.enums.OpportunityStatus;
import com.placementassist.institute.modules.hackathon.dto.CreateHackathonRequest;
import com.placementassist.institute.modules.hackathon.dto.HackathonResponse;
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
@RequestMapping("/api/hackathons")
@Tag(name = "Hackathons", description = "National and collegiate hackathon listings, deadlines, filters, and bookmarking")
public class HackathonController {

    private final HackathonService hackathonService;
    private final StudentRepository studentRepository;

    public HackathonController(HackathonService hackathonService, StudentRepository studentRepository) {
        this.hackathonService = hackathonService;
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
    @Operation(summary = "Create a new hackathon listing")
    public ResponseEntity<ApiResponse<HackathonResponse>> createHackathon(@Valid @RequestBody CreateHackathonRequest request) {
        HackathonResponse response = hackathonService.createHackathon(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Hackathon created successfully", response));
    }

    @GetMapping
    @Operation(summary = "List hackathons with search, mode, and status filters")
    public ResponseEntity<ApiResponse<PageResponse<HackathonResponse>>> listHackathons(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) OpportunityMode mode,
            @RequestParam(required = false) OpportunityStatus status,
            @PageableDefault(size = 10, sort = "registrationDeadline", direction = Sort.Direction.ASC) Pageable pageable,
            Authentication authentication) {

        PageResponse<HackathonResponse> response = hackathonService.listHackathons(search, mode, status, getStudentId(authentication), pageable);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping("/upcoming")
    @Operation(summary = "Get upcoming open hackathons ordered by deadline")
    public ResponseEntity<ApiResponse<List<HackathonResponse>>> getUpcomingHackathons(Authentication authentication) {
        List<HackathonResponse> response = hackathonService.getUpcomingHackathons(getStudentId(authentication));
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get hackathon details and ordered how-to-apply steps")
    public ResponseEntity<ApiResponse<HackathonResponse>> getHackathonById(
            @PathVariable Long id,
            Authentication authentication) {

        HackathonResponse response = hackathonService.getHackathonById(id, getStudentId(authentication));
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('TPO', 'DIRECTOR', 'ADMIN')")
    @Operation(summary = "Update hackathon listing")
    public ResponseEntity<ApiResponse<HackathonResponse>> updateHackathon(
            @PathVariable Long id,
            @Valid @RequestBody CreateHackathonRequest request,
            Authentication authentication) {

        HackathonResponse response = hackathonService.updateHackathon(id, request, getStudentId(authentication));
        return ResponseEntity.ok(ApiResponse.ok("Hackathon updated successfully", response));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('TPO', 'DIRECTOR', 'ADMIN')")
    @Operation(summary = "Delete a hackathon listing")
    public ResponseEntity<ApiResponse<Void>> deleteHackathon(@PathVariable Long id) {
        hackathonService.deleteHackathon(id);
        return ResponseEntity.ok(ApiResponse.message("Hackathon deleted successfully"));
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
        boolean bookmarked = hackathonService.toggleBookmark(id, studentId);
        String msg = bookmarked ? "Hackathon added to bookmarks" : "Hackathon removed from bookmarks";
        return ResponseEntity.ok(ApiResponse.ok(msg, bookmarked));
    }
}
