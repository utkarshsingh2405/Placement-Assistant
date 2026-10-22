package com.placementassist.placement.modules.placed;

import com.placementassist.shared.api.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/public")
public class PublicPlacementController {

    private final PlacedStudentService placedService;

    public PublicPlacementController(PlacedStudentService placedService) {
        this.placedService = placedService;
    }

    @GetMapping("/highlights")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getRecruitmentHighlights() {
        return ResponseEntity.ok(ApiResponse.ok(placedService.getHighlights()));
    }

    @GetMapping("/placements/by-roll/{rollNo}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getPlacementByRollNo(@PathVariable String rollNo) {
        return ResponseEntity.ok(ApiResponse.ok(placedService.checkPlacementByRollNo(rollNo)));
    }
}
