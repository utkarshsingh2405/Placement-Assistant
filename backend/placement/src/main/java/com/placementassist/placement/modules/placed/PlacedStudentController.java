package com.placementassist.placement.modules.placed;

import com.placementassist.shared.api.ApiResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/placed")
public class PlacedStudentController {

    private final PlacedStudentService placedService;

    public PlacedStudentController(PlacedStudentService placedService) {
        this.placedService = placedService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<PlacedStudent>>> getPlacedWall(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<PlacedStudent> res = placedService.getPlacedWall(PageRequest.of(page, size));
        return ResponseEntity.ok(ApiResponse.ok(res));
    }

    @GetMapping("/highlights")
    public ResponseEntity<ApiResponse<java.util.Map<String, Object>>> getHighlights() {
        return ResponseEntity.ok(ApiResponse.ok(placedService.getHighlights()));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('TPO', 'DIRECTOR', 'ADMIN')")
    public ResponseEntity<ApiResponse<PlacedStudent>> addPlacedStudent(@RequestBody PlacedStudent placedStudent) {
        PlacedStudent saved = placedService.addPlacedStudent(placedStudent);
        return ResponseEntity.ok(ApiResponse.ok("Candidate added to Placed Honor Roll", saved));
    }
}
