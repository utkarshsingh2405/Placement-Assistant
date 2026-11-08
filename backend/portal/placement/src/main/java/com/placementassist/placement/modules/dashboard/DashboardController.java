package com.placementassist.placement.modules.dashboard;

import com.placementassist.placement.modules.application.PlacementApplicationRepository;
import com.placementassist.placement.modules.company.CompanyRepository;
import com.placementassist.placement.modules.drive.PlacementDriveRepository;
import com.placementassist.placement.modules.placed.PlacedStudentRepository;
import com.placementassist.shared.api.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final PlacementDriveRepository driveRepository;
    private final PlacementApplicationRepository applicationRepository;
    private final PlacedStudentRepository placedRepository;
    private final CompanyRepository companyRepository;

    public DashboardController(
            PlacementDriveRepository driveRepository,
            PlacementApplicationRepository applicationRepository,
            PlacedStudentRepository placedRepository,
            CompanyRepository companyRepository) {
        this.driveRepository = driveRepository;
        this.applicationRepository = applicationRepository;
        this.placedRepository = placedRepository;
        this.companyRepository = companyRepository;
    }

    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getDashboardStats(
            @org.springframework.web.bind.annotation.RequestParam(required = false) String rollNo,
            @org.springframework.web.bind.annotation.RequestHeader(value = "X-User-RollNo", required = false) String rollNoHeader,
            @org.springframework.web.bind.annotation.RequestHeader(value = "X-User-Email", required = false) String emailHeader) {
        long ongoingDrives = driveRepository.countByStatus("ONGOING");
        long totalDrives = driveRepository.count();
        long totalApplications = applicationRepository.count();
        long placedCount = placedRepository.count();
        long companiesCount = companyRepository.count();
        Double highestPkg = placedRepository.findMaxPackage();
        Double avgPkg = placedRepository.findAvgPackage();

        Map<String, Object> stats = new HashMap<>();
        stats.put("ongoingDrives", ongoingDrives);
        stats.put("totalDrives", totalDrives);
        stats.put("totalApplications", totalApplications);
        stats.put("placedCount", placedCount);
        stats.put("companiesCount", companiesCount);
        stats.put("highestPackage", highestPkg != null ? highestPkg : 0.0);
        stats.put("averagePackage", avgPkg != null ? Math.round(avgPkg * 10.0) / 10.0 : 0.0);

        String effRoll = rollNo != null && !rollNo.isBlank() ? rollNo : rollNoHeader;
        if (effRoll != null && !effRoll.isBlank()) {
            long myApps = applicationRepository.countByRollNo(effRoll.toUpperCase().trim());
            stats.put("myApplicationsCount", myApps);
        } else if (emailHeader != null && !emailHeader.isBlank()) {
            long myApps = applicationRepository.countByEmail(emailHeader.toLowerCase().trim());
            stats.put("myApplicationsCount", myApps);
        }

        return ResponseEntity.ok(ApiResponse.ok(stats));
    }
}
