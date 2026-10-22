package com.placementassist.placement.modules.notification;

import com.placementassist.shared.api.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final WebPushService webPushService;

    public NotificationController(WebPushService webPushService) {
        this.webPushService = webPushService;
    }

    @PostMapping("/subscribe")
    public ResponseEntity<ApiResponse<PushSubscriptionEntity>> subscribe(
            @RequestBody Map<String, String> body,
            @RequestHeader(value = "X-User-RollNo", required = false) String rollNoHeader,
            @RequestHeader(value = "X-User-Id", required = false) String userIdHeader,
            Principal principal) {

        String endpoint = body.get("endpoint");
        String p256dh = body.get("p256dh");
        String auth = body.get("auth");
        String rollNo = body.get("rollNo") != null ? body.get("rollNo") : rollNoHeader;
        Long userId = userIdHeader != null ? Long.parseLong(userIdHeader) : null;

        PushSubscriptionEntity sub = webPushService.saveSubscription(userId, rollNo, endpoint, p256dh, auth);
        return ResponseEntity.ok(ApiResponse.ok("Subscription registered", sub));
    }
}
