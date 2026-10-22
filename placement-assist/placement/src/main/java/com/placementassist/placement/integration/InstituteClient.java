package com.placementassist.placement.integration;

import com.placementassist.placement.integration.dto.*;

import java.util.List;
import java.util.Optional;

/**
 * Contract for fetching academic master records and eligibility from the Institute pillar.
 */
public interface InstituteClient {

    Optional<StudentDto> getStudentByRollNo(String rollNo);

    Optional<StudentDto> getStudentById(Long id);

    List<StudentDto> getEligibleStudentsForDrive(Long driveId);

    List<DriveMasterDto> getAllDrives();

    List<DriveMasterDto> getUpcomingDrives();

    Optional<DriveMasterDto> getDriveById(Long id);

    List<DriveRoundDto> getDriveRounds(Long driveId);

    Optional<ResumeDto> getApprovedResume(Long studentId);

    List<OpportunityDto> getInternships();

    List<OpportunityDto> getHackathons();

    List<OpportunityDto> getOffCampusJobs();
}
