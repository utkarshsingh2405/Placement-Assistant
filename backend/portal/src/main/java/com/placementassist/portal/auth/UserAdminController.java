package com.placementassist.portal.auth;

import com.placementassist.portal.audit.AuditLog;
import com.placementassist.portal.audit.AuditService;
import com.placementassist.portal.auth.dto.CreateUserRequest;
import com.placementassist.portal.auth.dto.UserResponse;
import com.placementassist.shared.api.ApiResponse;
import com.placementassist.shared.security.Role;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class UserAdminController {

    private final AuthService authService;
    private final AuditService auditService;

    public UserAdminController(AuthService authService, AuditService auditService) {
        this.authService = authService;
        this.auditService = auditService;
    }

    @GetMapping("/users")
    public ResponseEntity<ApiResponse<Page<UserResponse>>> listUsers(
            @RequestParam(required = false) Role role,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<UserResponse> users = authService.listUsers(
                role, search, PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"))
        );
        return ResponseEntity.ok(ApiResponse.ok(users));
    }

    @PostMapping("/users")
    public ResponseEntity<ApiResponse<UserResponse>> createUser(@Valid @RequestBody CreateUserRequest request) {
        UserResponse created = authService.createUser(request);
        return ResponseEntity.ok(ApiResponse.ok("User created successfully", created));
    }

    @PostMapping("/users/{id}/toggle-active")
    public ResponseEntity<ApiResponse<UserResponse>> toggleUserActive(@PathVariable Long id) {
        UserResponse updated = authService.toggleUserActive(id);
        return ResponseEntity.ok(ApiResponse.ok("User status updated", updated));
    }

    @PutMapping("/users/{id}/role")
    public ResponseEntity<ApiResponse<UserResponse>> updateUserRole(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        String roleStr = body.get("role");
        if (roleStr == null || roleStr.isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        Role newRole = Role.valueOf(roleStr.toUpperCase().trim());
        UserResponse updated = authService.updateUserRole(id, newRole);
        return ResponseEntity.ok(ApiResponse.ok("User role updated", updated));
    }

    @GetMapping("/audit-logs")
    public ResponseEntity<ApiResponse<Page<AuditLog>>> getAuditLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<AuditLog> logs = auditService.getRecentLogs(PageRequest.of(page, size));
        return ResponseEntity.ok(ApiResponse.ok(logs));
    }
}
