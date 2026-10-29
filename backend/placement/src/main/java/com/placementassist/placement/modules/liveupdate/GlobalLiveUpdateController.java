package com.placementassist.placement.modules.liveupdate;

import com.placementassist.shared.api.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/live-updates")
public class GlobalLiveUpdateController {

    private final LiveUpdateService liveUpdateService;

    public GlobalLiveUpdateController(LiveUpdateService liveUpdateService) {
        this.liveUpdateService = liveUpdateService;
    }

    @GetMapping("/recent")
    public ResponseEntity<ApiResponse<List<LiveUpdate>>> getRecentUpdates() {
        return ResponseEntity.ok(ApiResponse.ok(liveUpdateService.getAllRecentUpdates()));
    }
}
