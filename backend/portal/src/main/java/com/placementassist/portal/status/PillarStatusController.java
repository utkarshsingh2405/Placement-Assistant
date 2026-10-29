package com.placementassist.portal.status;

import com.placementassist.shared.api.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/status")
public class PillarStatusController {

    private final PillarHealthService pillarHealthService;

    public PillarStatusController(PillarHealthService pillarHealthService) {
        this.pillarHealthService = pillarHealthService;
    }

    @GetMapping("/pillars")
    public ResponseEntity<ApiResponse<Map<String, PillarHealthService.PillarHealth>>> getPillarsStatus() {
        var status = pillarHealthService.checkAllPillars();
        return ResponseEntity.ok(ApiResponse.ok(status));
    }
}
