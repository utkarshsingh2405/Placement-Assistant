package com.placementassist.institute.modules.companydrive;

import com.placementassist.institute.modules.companydrive.dto.CompanyResponse;
import com.placementassist.institute.modules.companydrive.dto.CreateCompanyRequest;
import com.placementassist.institute.modules.companydrive.dto.UpdateCompanyRequest;
import com.placementassist.shared.api.ApiResponse;
import com.placementassist.shared.api.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/companies")
@Tag(name = "Companies", description = "Recruiter profiles and corporate partner registry")
public class CompanyController {

    private final CompanyDriveService service;

    public CompanyController(CompanyDriveService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "List companies with search and pagination")
    public ResponseEntity<ApiResponse<PageResponse<CompanyResponse>>> listCompanies(
            @RequestParam(required = false) String search,
            @PageableDefault(size = 10, sort = "name", direction = Sort.Direction.ASC) Pageable pageable) {
        PageResponse<CompanyResponse> response = service.listCompanies(search, pageable);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get company details by ID")
    public ResponseEntity<ApiResponse<CompanyResponse>> getCompanyById(@PathVariable Long id) {
        CompanyResponse response = service.getCompanyById(id);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('TPO', 'DIRECTOR', 'ADMIN')")
    @Operation(summary = "Register a new recruiter company")
    public ResponseEntity<ApiResponse<CompanyResponse>> createCompany(@Valid @RequestBody CreateCompanyRequest request) {
        CompanyResponse response = service.createCompany(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Company created successfully", response));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('TPO', 'DIRECTOR', 'ADMIN')")
    @Operation(summary = "Update recruiter company profile")
    public ResponseEntity<ApiResponse<CompanyResponse>> updateCompany(
            @PathVariable Long id,
            @Valid @RequestBody UpdateCompanyRequest request) {
        CompanyResponse response = service.updateCompany(id, request);
        return ResponseEntity.ok(ApiResponse.ok("Company updated successfully", response));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('TPO', 'DIRECTOR', 'ADMIN')")
    @Operation(summary = "Delete company profile")
    public ResponseEntity<ApiResponse<Void>> deleteCompany(@PathVariable Long id) {
        service.deleteCompany(id);
        return ResponseEntity.ok(ApiResponse.message("Company deleted successfully"));
    }
}
