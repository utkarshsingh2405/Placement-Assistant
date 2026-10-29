package com.placementassist.institute.modules.resume;

import com.placementassist.institute.modules.resume.dto.AtsBreakdownDto;
import com.placementassist.institute.modules.resume.dto.ResumeResponse;
import org.springframework.stereotype.Component;

@Component
public class ResumeMapper {

    private final AtsScorer atsScorer;

    public ResumeMapper(AtsScorer atsScorer) {
        this.atsScorer = atsScorer;
    }

    public ResumeResponse toResponse(Resume resume) {
        AtsBreakdownDto breakdown = atsScorer.fromJson(resume.getAtsBreakdown());
        return toResponse(resume, breakdown);
    }

    public ResumeResponse toResponse(Resume resume, AtsBreakdownDto breakdown) {
        return new ResumeResponse(
                resume.getId(),
                resume.getStudent().getId(),
                resume.getStudent().getRollNo(),
                resume.getStudent().getName(),
                resume.getFileUrl(),
                resume.getVersion(),
                resume.getStatus(),
                resume.getAtsScore(),
                breakdown,
                resume.getFeedback(),
                resume.getReviewedBy(),
                resume.getCreatedAt(),
                resume.getUpdatedAt()
        );
    }
}
