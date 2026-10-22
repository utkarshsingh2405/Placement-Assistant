package com.placementassist.placement.modules.application;

import com.placementassist.placement.integration.InstituteClient;
import com.placementassist.placement.integration.dto.ResumeDto;
import com.placementassist.placement.integration.dto.StudentDto;
import com.placementassist.placement.modules.drive.PlacementDrive;
import com.placementassist.placement.modules.drive.PlacementDriveRepository;
import com.placementassist.placement.modules.stage.PlacementStage;
import com.placementassist.placement.modules.stage.PlacementStageRepository;
import com.placementassist.shared.exception.ConflictException;
import com.placementassist.shared.exception.NotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class PlacementApplicationService {

    private final PlacementApplicationRepository applicationRepository;
    private final PlacementDriveRepository driveRepository;
    private final PlacementStageRepository stageRepository;
    private final InstituteClient instituteClient;

    public PlacementApplicationService(
            PlacementApplicationRepository applicationRepository,
            PlacementDriveRepository driveRepository,
            PlacementStageRepository stageRepository,
            InstituteClient instituteClient) {
        this.applicationRepository = applicationRepository;
        this.driveRepository = driveRepository;
        this.stageRepository = stageRepository;
        this.instituteClient = instituteClient;
    }

    @Transactional
    public PlacementApplicationEntity apply(Long driveId, String rollNo, String studentName, String email) {
        String normalizedRoll = rollNo.toUpperCase().trim();

        if (applicationRepository.existsByDriveIdAndRollNo(driveId, normalizedRoll)) {
            throw new ConflictException("You have already submitted an application for this drive.");
        }

        PlacementDrive drive = driveRepository.findById(driveId)
                .orElseThrow(() -> new NotFoundException("Placement Drive", driveId));

        // Attempt to fetch student info & resume snapshot from Institute integration
        Long studentId = null;
        String resumeUrl = null;
        Integer atsScore = null;

        Optional<StudentDto> studentOpt = instituteClient.getStudentByRollNo(normalizedRoll);
        if (studentOpt.isPresent()) {
            StudentDto s = studentOpt.get();
            studentId = s.getId();
            if (studentName == null || studentName.isBlank()) {
                studentName = s.getName();
            }
            if (email == null || email.isBlank()) {
                email = s.getEmail();
            }

            Optional<ResumeDto> resumeOpt = instituteClient.getApprovedResume(s.getId());
            if (resumeOpt.isPresent()) {
                resumeUrl = resumeOpt.get().getFileUrl();
                atsScore = resumeOpt.get().getAtsScore();
            }
        }

        // Get initial stage for this drive
        PlacementStage initialStage = stageRepository.findByDriveIdAndStageOrder(driveId, 1)
                .orElse(null);

        PlacementApplicationEntity app = PlacementApplicationEntity.builder()
                .drive(drive)
                .studentId(studentId)
                .rollNo(normalizedRoll)
                .studentName(studentName != null ? studentName : normalizedRoll)
                .email(email != null ? email : normalizedRoll + "@college.edu")
                .status("APPLIED")
                .currentStage(initialStage)
                .resumeUrl(resumeUrl)
                .atsScore(atsScore)
                .build();

        return applicationRepository.save(app);
    }

    @Transactional(readOnly = true)
    public Page<PlacementApplicationEntity> getApplicationsForDrive(Long driveId, String status, Pageable pageable) {
        if (status != null && !status.isBlank()) {
            return applicationRepository.findByDriveIdAndStatus(driveId, status.toUpperCase().trim(), pageable);
        }
        return applicationRepository.findByDriveId(driveId, pageable);
    }

    @Transactional(readOnly = true)
    public List<PlacementApplicationEntity> getAllApplications() {
        return applicationRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<PlacementApplicationEntity> getMyApplications(String rollNo) {
        return applicationRepository.findByRollNo(rollNo.toUpperCase().trim());
    }

    @Transactional
    public PlacementApplicationEntity updateStatus(Long applicationId, String newStatus, Long nextStageId) {
        PlacementApplicationEntity app = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new NotFoundException("Application", applicationId));

        app.setStatus(newStatus.toUpperCase().trim());

        if (nextStageId != null) {
            PlacementStage nextStage = stageRepository.findById(nextStageId)
                    .orElseThrow(() -> new NotFoundException("Stage", nextStageId));
            app.setCurrentStage(nextStage);
        }

        return applicationRepository.save(app);
    }
}
