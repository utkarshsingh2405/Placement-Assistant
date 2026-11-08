package com.placementassist.placement.modules.drive;

import com.placementassist.placement.modules.stage.PlacementStage;
import com.placementassist.placement.modules.stage.PlacementStageRepository;
import com.placementassist.shared.exception.NotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PlacementDriveService {

    private final PlacementDriveRepository driveRepository;
    private final PlacementStageRepository stageRepository;

    public PlacementDriveService(
            PlacementDriveRepository driveRepository,
            PlacementStageRepository stageRepository) {
        this.driveRepository = driveRepository;
        this.stageRepository = stageRepository;
    }

    @Transactional(readOnly = true)
    public Page<PlacementDrive> searchDrives(String search, Pageable pageable) {
        if (search != null && !search.isBlank()) {
            return driveRepository.findByTitleContainingIgnoreCaseOrJobRolesContainingIgnoreCase(search.trim(), search.trim(), pageable);
        }
        return driveRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public List<PlacementDrive> getDrivesByStatus(String status) {
        return driveRepository.findByStatus(status.toUpperCase().trim());
    }

    @Transactional(readOnly = true)
    public List<PlacementDrive> getAllDrives() {
        return driveRepository.findAll();
    }

    @Transactional(readOnly = true)
    public PlacementDrive getDriveById(Long id) {
        return driveRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Placement Drive", id));
    }

    @Transactional
    public PlacementDrive createDrive(PlacementDrive drive) {
        PlacementDrive saved = driveRepository.save(drive);

        // Auto-seed standard recruitment stages
        List<PlacementStage> defaultStages = List.of(
                PlacementStage.builder().driveId(saved.getId()).stageOrder(1).stageName("Registration & Resume Screening").stageType("REGISTRATION").status("ACTIVE").venueOrLink("Portal").build(),
                PlacementStage.builder().driveId(saved.getId()).stageOrder(2).stageName("Pre-Placement Talk (PPT)").stageType("PPT").status("PENDING").venueOrLink("Auditorium / Online").build(),
                PlacementStage.builder().driveId(saved.getId()).stageOrder(3).stageName("Online Technical Assessment").stageType("ONLINE_TEST").status("PENDING").venueOrLink("Testing Portal").build(),
                PlacementStage.builder().driveId(saved.getId()).stageOrder(4).stageName("Technical Interview Round 1").stageType("TECHNICAL_INTERVIEW_1").status("PENDING").venueOrLink("Virtual Rooms").build(),
                PlacementStage.builder().driveId(saved.getId()).stageOrder(5).stageName("Technical Interview Round 2").stageType("TECHNICAL_INTERVIEW_2").status("PENDING").venueOrLink("Virtual Rooms").build(),
                PlacementStage.builder().driveId(saved.getId()).stageOrder(6).stageName("HR & Leadership Discussion").stageType("HR_INTERVIEW").status("PENDING").venueOrLink("Panel Rooms").build()
        );
        stageRepository.saveAll(defaultStages);

        return saved;
    }

    @Transactional
    public PlacementDrive updateDrive(Long id, PlacementDrive req) {
        PlacementDrive existing = getDriveById(id);
        existing.setTitle(req.getTitle());
        existing.setDescription(req.getDescription());
        existing.setJobRoles(req.getJobRoles());
        existing.setPackageDetails(req.getPackageDetails());
        existing.setStatus(req.getStatus());
        existing.setDriveDate(req.getDriveDate());
        if (req.getCompany() != null) {
            existing.setCompany(req.getCompany());
        }
        return driveRepository.save(existing);
    }

    @Transactional
    public PlacementDrive updateDriveStatus(Long id, String status) {
        PlacementDrive existing = getDriveById(id);
        existing.setStatus(status.toUpperCase().trim());
        return driveRepository.save(existing);
    }

    @Transactional
    public void deleteDrive(Long id) {
        PlacementDrive existing = getDriveById(id);
        driveRepository.delete(existing);
    }
}
