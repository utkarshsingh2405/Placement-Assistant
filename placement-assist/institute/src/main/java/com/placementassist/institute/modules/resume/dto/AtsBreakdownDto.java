package com.placementassist.institute.modules.resume.dto;

import java.util.List;

public record AtsBreakdownDto(
        int totalScore,
        int keywordScore,
        int sectionsScore,
        int formattingScore,
        int actionVerbsScore,
        List<String> matchedKeywords,
        List<String> missingKeywords,
        List<String> presentSections,
        List<String> missingSections,
        List<String> suggestions,
        int wordCount
) {}
