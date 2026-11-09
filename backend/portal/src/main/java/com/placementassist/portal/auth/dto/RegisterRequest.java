package com.placementassist.portal.auth.dto;

import com.placementassist.shared.security.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Valid email is required")
    private String email;

    // Roll number (mandatory for students, optional for staff/recruiters)
    private String rollNo;

    // Selected role: STUDENT, TPO, DIRECTOR, COMPANY, or ADMIN (defaults to
    // STUDENT)
    private Role role;

    // Optional organizational fields
    private String department;
    private String companyName;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;
}
