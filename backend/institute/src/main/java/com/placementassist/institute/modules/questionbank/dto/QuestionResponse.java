package com.placementassist.institute.modules.questionbank.dto;

import com.placementassist.institute.common.enums.QuestionDifficulty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionResponse {

    private Long id;
    private Long driveId;
    private String driveTitle;
    private String companyName;
    private String role;
    private String roundType;
    private QuestionDifficulty difficulty;
    private String question;
    private String answer;
    private List<String> tags;
    private Integer askedYear;
    private LocalDateTime createdAt;
}
