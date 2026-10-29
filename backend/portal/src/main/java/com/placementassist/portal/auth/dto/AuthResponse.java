package com.placementassist.portal.auth.dto;

import com.placementassist.shared.security.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private String accessToken;
    private String refreshToken;
    private Long uid;
    private String email;
    private Role role;
    private String name;
    private String rollNo;
    private Long companyId;
}
