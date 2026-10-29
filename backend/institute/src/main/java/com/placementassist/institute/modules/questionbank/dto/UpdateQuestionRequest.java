package com.placementassist.institute.modules.questionbank.dto;

import com.placementassist.institute.common.enums.QuestionDifficulty;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateQuestionRequest {

    private Long driveId;
    private String role;
    private String roundType;
    private QuestionDifficulty difficulty;

    @NotBlank(message = "Question text cannot be blank")
    private String question;

    @NotBlank(message = "Answer text cannot be blank")
    private String answer;

    private List<String> tags;
    private Integer askedYear;
}
