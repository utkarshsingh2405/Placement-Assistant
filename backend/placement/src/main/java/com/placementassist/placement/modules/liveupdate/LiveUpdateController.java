package com.placementassist.placement.modules.liveupdate;

import com.placementassist.shared.api.ApiResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@RestController
@RequestMapping("/api/drives/{driveId}/live")
public class LiveUpdateController {

    private final LiveUpdateService liveUpdateService;
    private final SseBroadcaster sseBroadcaster;

    public LiveUpdateController(
            LiveUpdateService liveUpdateService,
            SseBroadcaster sseBroadcaster) {
        this.liveUpdateService = liveUpdateService;
        this.sseBroadcaster = sseBroadcaster;
    }

    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribeToDriveStream(@PathVariable Long driveId) {
        return sseBroadcaster.subscribe(driveId);
    }

    @GetMapping("/updates")
    public ResponseEntity<ApiResponse<List<LiveUpdate>>> getRecentUpdates(@PathVariable Long driveId) {
        return ResponseEntity.ok(ApiResponse.ok(liveUpdateService.getRecentUpdates(driveId)));
    }

    @PostMapping("/updates")
    @PreAuthorize("hasAnyRole('TPO', 'DIRECTOR', 'COMPANY', 'ADMIN')")
    public ResponseEntity<ApiResponse<LiveUpdate>> postUpdate(
            @PathVariable Long driveId,
            @RequestBody LiveUpdate update) {
        LiveUpdate created = liveUpdateService.postUpdate(driveId, update);
        return ResponseEntity.ok(ApiResponse.ok("Live update published", created));
    }
}
