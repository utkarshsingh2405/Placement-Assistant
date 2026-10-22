package com.placementassist.placement.modules.talk;

import com.placementassist.shared.api.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class PlacementTalkController {

    private final PlacementTalkService talkService;

    public PlacementTalkController(PlacementTalkService talkService) {
        this.talkService = talkService;
    }

    @GetMapping("/talks")
    public ResponseEntity<ApiResponse<List<PlacementTalk>>> listTalks() {
        return ResponseEntity.ok(ApiResponse.ok(talkService.getAllTalks()));
    }

    @GetMapping("/talks/{id}")
    public ResponseEntity<ApiResponse<PlacementTalk>> getTalkById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(talkService.getTalkById(id)));
    }

    @GetMapping("/drives/{driveId}/talk")
    public ResponseEntity<ApiResponse<PlacementTalk>> getTalk(@PathVariable Long driveId) {
        PlacementTalk talk = talkService.getTalkForDrive(driveId);
        return ResponseEntity.ok(ApiResponse.ok(talk));
    }

    @PutMapping("/drives/{driveId}/talk")
    @PreAuthorize("hasAnyRole('TPO', 'DIRECTOR', 'COMPANY', 'ADMIN')")
    public ResponseEntity<ApiResponse<PlacementTalk>> saveTalk(
            @PathVariable Long driveId,
            @RequestBody PlacementTalk talk) {
        PlacementTalk saved = talkService.saveOrUpdateTalk(driveId, talk);
        return ResponseEntity.ok(ApiResponse.ok("PPT details updated", saved));
    }

    @GetMapping("/talks/{talkId}/questions")
    public ResponseEntity<ApiResponse<List<PlacementTalkQuestion>>> getQuestions(@PathVariable Long talkId) {
        return ResponseEntity.ok(ApiResponse.ok(talkService.getQuestionsForTalk(talkId)));
    }

    @PostMapping("/talks/{talkId}/questions")
    public ResponseEntity<ApiResponse<PlacementTalkQuestion>> askQuestion(
            @PathVariable Long talkId,
            @RequestBody Map<String, String> body,
            @RequestHeader(value = "X-User-RollNo", required = false) String rollNoHeader,
            Principal principal) {
        String question = body.get("question");
        String rollNo = body.get("rollNo") != null ? body.get("rollNo") : (rollNoHeader != null ? rollNoHeader : "21CS001");
        String name = body.get("name") != null ? body.get("name") : (principal != null ? principal.getName() : "Student");

        PlacementTalkQuestion created = talkService.askQuestion(talkId, rollNo, name, question);
        return ResponseEntity.ok(ApiResponse.ok("Question submitted", created));
    }

    @PutMapping("/talks/questions/{questionId}/answer")
    @PreAuthorize("hasAnyRole('TPO', 'DIRECTOR', 'COMPANY', 'ADMIN')")
    public ResponseEntity<ApiResponse<PlacementTalkQuestion>> answerQuestion(
            @PathVariable Long questionId,
            @RequestBody Map<String, String> body,
            Principal principal) {
        String answer = body.get("answer");
        String answeredBy = body.get("answeredBy") != null ? body.get("answeredBy") : (principal != null ? principal.getName() : "Panel");

        PlacementTalkQuestion updated = talkService.answerQuestion(questionId, answer, answeredBy);
        return ResponseEntity.ok(ApiResponse.ok("Answer published", updated));
    }
}
