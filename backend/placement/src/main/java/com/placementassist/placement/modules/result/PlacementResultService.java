package com.placementassist.placement.modules.result;

import com.placementassist.placement.integration.InstituteClient;
import com.placementassist.placement.integration.dto.StudentDto;
import com.placementassist.placement.modules.stage.PlacementStage;
import com.placementassist.placement.modules.stage.PlacementStageRepository;
import com.placementassist.shared.exception.ForbiddenException;
import com.placementassist.shared.exception.NotFoundException;
import com.placementassist.shared.security.Role;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class PlacementResultService {

    private final PlacementResultRepository resultRepository;
    private final PlacementStageRepository stageRepository;
    private final InstituteClient instituteClient;

    public PlacementResultService(
            PlacementResultRepository resultRepository,
            PlacementStageRepository stageRepository,
            InstituteClient instituteClient) {
        this.resultRepository = resultRepository;
        this.stageRepository = stageRepository;
        this.instituteClient = instituteClient;
    }

    public void validateTpoOrDirector(Role actorRole) {
        if (actorRole != Role.TPO && actorRole != Role.DIRECTOR) {
            throw new ForbiddenException("Only TPO and Director roles are authorized to mark or modify candidate selection lists.");
        }
    }

    @Transactional
    public PlacementResult markResult(
            Role actorRole,
            Long stageId,
            String rollNo,
            String studentName,
            String resultStatus,
            String roundFeedback,
            String markedBy) {

        // Strict permission check
        validateTpoOrDirector(actorRole);

        PlacementStage stage = stageRepository.findById(stageId)
                .orElseThrow(() -> new NotFoundException("Stage", stageId));

        String normalizedRoll = rollNo.toUpperCase().trim();
        String normalizedStatus = resultStatus.toUpperCase().trim();

        Optional<PlacementResult> existing = resultRepository.findByStageIdAndRollNo(stageId, normalizedRoll);
        if (existing.isPresent()) {
            PlacementResult r = existing.get();
            r.setResultStatus(normalizedStatus);
            r.setRoundFeedback(roundFeedback);
            r.setMarkedBy(markedBy);
            r.setMarkedAt(LocalDateTime.now());
            return resultRepository.save(r);
        }

        Long studentId = null;
        Optional<StudentDto> studentOpt = instituteClient.getStudentByRollNo(normalizedRoll);
        if (studentOpt.isPresent()) {
            StudentDto s = studentOpt.get();
            studentId = s.getId();
            if (studentName == null || studentName.isBlank()) {
                studentName = s.getName();
            }
        }

        PlacementResult newResult = PlacementResult.builder()
                .driveId(stage.getDriveId())
                .stageId(stageId)
                .studentId(studentId)
                .rollNo(normalizedRoll)
                .studentName(studentName != null ? studentName : normalizedRoll)
                .resultStatus(normalizedStatus)
                .roundFeedback(roundFeedback)
                .markedBy(markedBy)
                .markedAt(LocalDateTime.now())
                .build();

        return resultRepository.save(newResult);
    }

    @Transactional
    public int bulkMarkResults(
            Role actorRole,
            Long stageId,
            List<Map<String, String>> items,
            String markedBy) {

        validateTpoOrDirector(actorRole);

        int count = 0;
        for (Map<String, String> item : items) {
            String rollNo = item.get("rollNo");
            String status = item.get("resultStatus");
            String feedback = item.get("roundFeedback");
            String name = item.get("studentName");
            if (rollNo != null && status != null) {
                markResult(actorRole, stageId, rollNo, name, status, feedback, markedBy);
                count++;
            }
        }
        return count;
    }

    @Transactional(readOnly = true)
    public List<PlacementResult> getResultsForStage(Long stageId) {
        return resultRepository.findByStageIdOrderByRollNoAsc(stageId);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getResultStats(Long stageId) {
        long selected = resultRepository.countByStageIdAndResultStatus(stageId, "SELECTED");
        long nonSelected = resultRepository.countByStageIdAndResultStatus(stageId, "NON_SELECTED");
        long waitlist = resultRepository.countByStageIdAndResultStatus(stageId, "WAITLIST");

        Map<String, Object> map = new HashMap<>();
        map.put("selectedCount", selected);
        map.put("nonSelectedCount", nonSelected);
        map.put("waitlistCount", waitlist);
        map.put("totalEvaluated", selected + nonSelected + waitlist);
        return map;
    }
}
