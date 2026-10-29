package com.placementassist.institute.modules.questionbank.dto;

import com.placementassist.institute.common.enums.QuestionDifficulty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateQuestionRequest {

    private Long driveId;
    private String role;
    private String roundType;

    @NotNull(message = "Difficulty is required")
    private QuestionDifficulty difficulty;

    @NotBlank(message = "Question text is required")
    private String question;

    @NotBlank(message = "Answer / explanation is required")
    private String answer;

    private List<String> tags;
    private Integer askedYear;
}
