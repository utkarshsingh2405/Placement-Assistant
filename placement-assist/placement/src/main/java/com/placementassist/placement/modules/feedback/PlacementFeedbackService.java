package com.placementassist.placement.modules.feedback;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PlacementFeedbackService {

    private final PlacementFeedbackRepository feedbackRepository;

    public PlacementFeedbackService(PlacementFeedbackRepository feedbackRepository) {
        this.feedbackRepository = feedbackRepository;
    }

    @Transactional(readOnly = true)
    public List<PlacementFeedback> getFeedbackForDrive(Long driveId) {
        return feedbackRepository.findByDriveIdOrderByCreatedAtDesc(driveId);
    }

    @Transactional(readOnly = true)
    public List<PlacementFeedback> getRecentFeedback() {
        return feedbackRepository.findAllByOrderByCreatedAtDesc();
    }

    @Transactional
    public PlacementFeedback submitFeedback(PlacementFeedback feedback) {
        return feedbackRepository.save(feedback);
    }
}
