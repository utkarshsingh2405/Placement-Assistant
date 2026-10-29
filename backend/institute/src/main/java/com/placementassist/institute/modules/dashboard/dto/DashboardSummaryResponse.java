package com.placementassist.institute.modules.dashboard.dto;

import java.util.Map;

public record DashboardSummaryResponse(
        long totalStudents,
        long eligibleStudents,
        long upcomingDrives,
        Double averageAttendance,
        long pendingResumes,
        long listingsClosingSoon,
        Map<String, Long> branchDistribution,
        Map<String, Long> drivesByStatus
) {}
