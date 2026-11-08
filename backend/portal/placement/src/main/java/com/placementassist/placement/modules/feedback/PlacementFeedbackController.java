package com.placementassist.placement.modules.feedback;

import com.placementassist.shared.api.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api")
public class PlacementFeedbackController {

    private final PlacementFeedbackService feedbackService;

    public PlacementFeedbackController(PlacementFeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }

    @GetMapping("/drives/{driveId}/feedback")
    public ResponseEntity<ApiResponse<List<PlacementFeedback>>> getDriveFeedback(@PathVariable Long driveId) {
        return ResponseEntity.ok(ApiResponse.ok(feedbackService.getFeedbackForDrive(driveId)));
    }

    @PostMapping({"/feedback", "/drives/{driveId}/feedback"})
    public ResponseEntity<ApiResponse<PlacementFeedback>> submitFeedback(
            @PathVariable(required = false) Long driveId,
            @RequestBody PlacementFeedback feedback,
            @RequestHeader(value = "X-User-RollNo", required = false) String rollNoHeader,
            Principal principal) {

        if (driveId != null) {
            feedback.setDriveId(driveId);
        } else if (feedback.getDriveId() == null) {
            feedback.setDriveId(1L);
        }

        if (feedback.getRollNo() == null || feedback.getRollNo().isBlank()) {
            feedback.setRollNo(rollNoHeader != null ? rollNoHeader : (principal != null ? principal.getName().split("@")[0].toUpperCase() : "21CS001"));
        }

        PlacementFeedback saved = feedbackService.submitFeedback(feedback);
        return ResponseEntity.ok(ApiResponse.ok("Feedback submitted", saved));
    }

    @GetMapping({"/feedback", "/feedback/my", "/feedback/recent"})
    public ResponseEntity<ApiResponse<List<PlacementFeedback>>> getRecentFeedback() {
        return ResponseEntity.ok(ApiResponse.ok(feedbackService.getRecentFeedback()));
    }
}
