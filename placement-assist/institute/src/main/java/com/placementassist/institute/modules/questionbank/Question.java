package com.placementassist.institute.modules.questionbank;

import com.placementassist.institute.common.BaseEntity;
import com.placementassist.institute.common.enums.QuestionDifficulty;
import com.placementassist.institute.modules.companydrive.Drive;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Question bank repository item for interview preparation and practice mode.
 */
@Entity
@Table(name = "questions")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Question extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "drive_id")
    private Drive drive;

    @Column(name = "role")
    private String role;

    @Column(name = "round_type")
    private String roundType;

    @Enumerated(EnumType.STRING)
    @Column(name = "difficulty", nullable = false)
    @Builder.Default
    private QuestionDifficulty difficulty = QuestionDifficulty.MEDIUM;

    @Column(name = "question", length = 4000, nullable = false)
    private String question;

    @Column(name = "answer", length = 6000, nullable = false)
    private String answer;

    @Column(name = "tags")
    private String tags; // Comma separated

    @Column(name = "asked_year")
    private Integer askedYear;

    public List<String> getTagsList() {
        if (tags == null || tags.isBlank()) return new ArrayList<>();
        return Arrays.stream(tags.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();
    }

    public void setTagsList(List<String> tagList) {
        if (tagList == null || tagList.isEmpty()) {
            this.tags = "";
        } else {
            this.tags = String.join(",", tagList);
        }
    }
}
