package com.placementassist.placement.modules.talk;

import com.placementassist.shared.exception.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PlacementTalkService {

    private final PlacementTalkRepository talkRepository;
    private final PlacementTalkQuestionRepository questionRepository;

    public PlacementTalkService(
            PlacementTalkRepository talkRepository,
            PlacementTalkQuestionRepository questionRepository) {
        this.talkRepository = talkRepository;
        this.questionRepository = questionRepository;
    }

    @Transactional(readOnly = true)
    public List<PlacementTalk> getAllTalks() {
        return talkRepository.findAll();
    }

    @Transactional(readOnly = true)
    public PlacementTalk getTalkById(Long id) {
        return talkRepository.findById(id).orElse(null);
    }

    @Transactional(readOnly = true)
    public PlacementTalk getTalkForDrive(Long driveId) {
        return talkRepository.findByDriveId(driveId)
                .orElse(null);
    }

    @Transactional
    public PlacementTalk saveOrUpdateTalk(Long driveId, PlacementTalk req) {
        PlacementTalk talk = talkRepository.findByDriveId(driveId)
                .orElseGet(() -> PlacementTalk.builder().driveId(driveId).build());

        talk.setScheduledAt(req.getScheduledAt());
        talk.setVenueOrUrl(req.getVenueOrUrl());
        talk.setPresentationUrl(req.getPresentationUrl());
        talk.setRecordingUrl(req.getRecordingUrl());
        talk.setOverview(req.getOverview());
        talk.setSpeakerName(req.getSpeakerName());
        talk.setSpeakerTitle(req.getSpeakerTitle());

        return talkRepository.save(talk);
    }

    @Transactional(readOnly = true)
    public List<PlacementTalkQuestion> getQuestionsForTalk(Long talkId) {
        return questionRepository.findByTalkIdOrderByCreatedAtDesc(talkId);
    }

    @Transactional
    public PlacementTalkQuestion askQuestion(Long talkId, String rollNo, String studentName, String question) {
        PlacementTalkQuestion q = PlacementTalkQuestion.builder()
                .talkId(talkId)
                .rollNo(rollNo)
                .studentName(studentName)
                .question(question)
                .build();
        return questionRepository.save(q);
    }

    @Transactional
    public PlacementTalkQuestion answerQuestion(Long questionId, String answer, String answeredBy) {
        PlacementTalkQuestion q = questionRepository.findById(questionId)
                .orElseThrow(() -> new NotFoundException("Question", questionId));
        q.setAnswer(answer);
        q.setAnsweredBy(answeredBy);
        return questionRepository.save(q);
    }
}
