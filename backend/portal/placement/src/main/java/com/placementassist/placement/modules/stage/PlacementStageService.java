package com.placementassist.placement.modules.stage;

import com.placementassist.shared.exception.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PlacementStageService {

    private final PlacementStageRepository stageRepository;

    public PlacementStageService(PlacementStageRepository stageRepository) {
        this.stageRepository = stageRepository;
    }

    @Transactional(readOnly = true)
    public List<PlacementStage> getStagesForDrive(Long driveId) {
        return stageRepository.findByDriveIdOrderByStageOrderAsc(driveId);
    }

    @Transactional(readOnly = true)
    public PlacementStage getStageById(Long id) {
        return stageRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Stage", id));
    }

    @Transactional
    public PlacementStage updateStage(Long id, PlacementStage req) {
        PlacementStage existing = getStageById(id);
        existing.setStageName(req.getStageName());
        existing.setStageOrder(req.getStageOrder());
        existing.setStageType(req.getStageType());
        existing.setStatus(req.getStatus());
        existing.setVenueOrLink(req.getVenueOrLink());
        existing.setInstructions(req.getInstructions());
        existing.setStartTime(req.getStartTime());
        existing.setEndTime(req.getEndTime());
        return stageRepository.save(existing);
    }

    @Transactional
    public PlacementStage activateStage(Long id) {
        PlacementStage current = getStageById(id);
        List<PlacementStage> allStages = stageRepository.findByDriveIdOrderByStageOrderAsc(current.getDriveId());

        for (PlacementStage s : allStages) {
            if (s.getStageOrder() < current.getStageOrder()) {
                s.setStatus("COMPLETED");
            } else if (s.getId().equals(current.getId())) {
                s.setStatus("ACTIVE");
            } else {
                s.setStatus("PENDING");
            }
        }
        stageRepository.saveAll(allStages);
        return current;
    }

    @Transactional
    public PlacementStage completeStage(Long id) {
        PlacementStage stage = getStageById(id);
        stage.setStatus("COMPLETED");
        return stageRepository.save(stage);
    }

    @Transactional
    public PlacementStage createStage(Long driveId, PlacementStage req) {
        req.setDriveId(driveId);
        if (req.getStatus() == null) req.setStatus("UPCOMING");
        if (req.getStageOrder() == null) {
            List<PlacementStage> existing = stageRepository.findByDriveIdOrderByStageOrderAsc(driveId);
            req.setStageOrder(existing.size() + 1);
        }
        return stageRepository.save(req);
    }

    @Transactional
    public void deleteStage(Long id) {
        stageRepository.deleteById(id);
    }
}
