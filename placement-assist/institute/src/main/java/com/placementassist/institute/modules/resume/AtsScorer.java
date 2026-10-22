package com.placementassist.institute.modules.resume;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.placementassist.institute.modules.resume.dto.AtsBreakdownDto;
import com.placementassist.shared.exception.ApiException;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 100-Point ATS Resume Scoring Engine with Apache PDFBox text extraction:
 * - Keyword match against target JD (40 points)
 * - Section completeness (30 points)
 * - Formatting and length checks (20 points)
 * - Action verbs and quantified achievements (10 points)
 */
@Component
public class AtsScorer {

    private static final Logger log = LoggerFactory.getLogger(AtsScorer.class);

    private final ObjectMapper objectMapper;

    public AtsScorer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    private static final Set<String> DEFAULT_TECH_KEYWORDS = Set.of(
            "java", "spring", "sql", "database", "api", "rest", "git", "docker",
            "python", "javascript", "react", "algorithms", "data structures", "linux",
            "microservices", "testing", "oop", "aws", "cloud", "backend"
    );

    private static final Set<String> ACTION_VERBS = Set.of(
            "developed", "designed", "implemented", "built", "optimized", "created",
            "engineered", "spearheaded", "orchestrated", "deployed", "integrated",
            "automated", "led", "resolved", "maintained", "collaborated", "reduced", "increased"
    );

    private static final Map<String, List<String>> SECTION_ALIASES = Map.of(
            "Education", List.of("education", "academic", "academics", "b.tech", "btech", "degree", "university", "college"),
            "Skills", List.of("skills", "technical skills", "technologies", "competencies", "tools", "tech stack"),
            "Projects", List.of("projects", "academic projects", "personal projects", "key projects"),
            "Experience", List.of("experience", "work experience", "internship", "internships", "employment"),
            "Contact", List.of("email", "phone", "mobile", "linkedin", "github", "contact")
    );

    /**
     * Extracts plain text from raw PDF document bytes using Apache PDFBox.
     */
    public String extractTextFromPdf(byte[] pdfBytes) {
        if (pdfBytes == null || pdfBytes.length == 0) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "EMPTY_PDF", "PDF content is empty");
        }
        try (PDDocument document = Loader.loadPDF(pdfBytes)) {
            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setSortByPosition(true);
            String text = stripper.getText(document);
            if (text == null || text.trim().isEmpty()) {
                throw new ApiException(HttpStatus.BAD_REQUEST, "SCANNED_OR_EMPTY_PDF",
                        "Could not extract text. Please ensure the PDF is not an image scan.");
            }
            return text;
        } catch (IOException e) {
            log.error("Failed to extract text from PDF: {}", e.getMessage(), e);
            throw new ApiException(HttpStatus.BAD_REQUEST, "PDF_PARSE_FAILED",
                    "Failed to parse PDF document: " + e.getMessage());
        }
    }

    /**
     * Computes the 100-point ATS evaluation breakdown.
     */
    public AtsBreakdownDto scoreResume(String text, String targetJobDescription) {
        if (text == null || text.isBlank()) {
            return new AtsBreakdownDto(0, 0, 0, 0, 0, List.of(), List.of(), List.of(), List.of(), List.of("Resume is empty"), 0);
        }

        String lowerText = text.toLowerCase(Locale.ROOT);
        String[] words = text.trim().split("\\s+");
        int wordCount = words.length;

        // 1. Keyword match (40 points)
        Set<String> targetKeywords = (targetJobDescription != null && !targetJobDescription.isBlank())
                ? extractKeywordsFromJd(targetJobDescription)
                : DEFAULT_TECH_KEYWORDS;

        List<String> matchedKeywords = new ArrayList<>();
        List<String> missingKeywords = new ArrayList<>();

        for (String kw : targetKeywords) {
            if (lowerText.contains(kw.toLowerCase(Locale.ROOT))) {
                matchedKeywords.add(kw);
            } else {
                missingKeywords.add(kw);
            }
        }

        double kwRatio = targetKeywords.isEmpty() ? 1.0 : (double) matchedKeywords.size() / targetKeywords.size();
        int keywordScore = (int) Math.round(kwRatio * 40.0);

        // 2. Section completeness (30 points - 6 points per section across 5 sections)
        List<String> presentSections = new ArrayList<>();
        List<String> missingSections = new ArrayList<>();

        SECTION_ALIASES.forEach((sectionName, aliases) -> {
            boolean found = aliases.stream().anyMatch(lowerText::contains);
            if (found) {
                presentSections.add(sectionName);
            } else {
                missingSections.add(sectionName);
            }
        });

        int sectionsScore = presentSections.size() * 6;

        // 3. Formatting and length checks (20 points)
        int lengthScore;
        if (wordCount >= 350 && wordCount <= 850) {
            lengthScore = 12;
        } else if ((wordCount >= 250 && wordCount < 350) || (wordCount > 850 && wordCount <= 1100)) {
            lengthScore = 8;
        } else {
            lengthScore = 4;
        }

        // Structure: bullet point or list check
        boolean hasBulletPoints = text.contains("•") || text.contains("- ") || text.contains("* ") || lowerText.contains("key achievements");
        int structureScore = hasBulletPoints ? 8 : 4;
        int formattingScore = lengthScore + structureScore;

        // 4. Action verbs & quantified achievements (10 points)
        long actionVerbMatches = ACTION_VERBS.stream().filter(lowerText::contains).count();
        int verbsScore = actionVerbMatches >= 5 ? 5 : (actionVerbMatches >= 3 ? 3 : (actionVerbMatches >= 1 ? 2 : 0));

        // Metric pattern: numbers with %, x, +, or standalone numbers indicating impact
        Pattern metricPattern = Pattern.compile("(\\b\\d+[%xX+]?\\b|\\$\\d+)");
        Matcher metricMatcher = metricPattern.matcher(text);
        int metricCount = 0;
        while (metricMatcher.find()) {
            metricCount++;
        }
        int metricsScore = metricCount >= 4 ? 5 : (metricCount >= 2 ? 3 : (metricCount >= 1 ? 2 : 0));
        int actionVerbsScore = verbsScore + metricsScore;

        // Total Score
        int totalScore = Math.min(100, keywordScore + sectionsScore + formattingScore + actionVerbsScore);

        // Actionable suggestions
        List<String> suggestions = new ArrayList<>();
        if (!missingSections.isEmpty()) {
            suggestions.add("Add missing essential sections: " + String.join(", ", missingSections) + ".");
        }
        if (!missingKeywords.isEmpty()) {
            List<String> sampleMissing = missingKeywords.stream().limit(5).toList();
            suggestions.add("Incorporate relevant technical keywords: " + String.join(", ", sampleMissing) + ".");
        }
        if (wordCount < 350) {
            suggestions.add("Resume is concise (" + wordCount + " words). Expand on project details and technical responsibilities.");
        } else if (wordCount > 850) {
            suggestions.add("Resume is long (" + wordCount + " words). Aim for concise, impact-driven bullet points within 1-2 pages.");
        }
        if (!hasBulletPoints) {
            suggestions.add("Use clear bullet points ('•' or '-') to make project achievements scannable for recruiters.");
        }
        if (actionVerbsScore < 8) {
            suggestions.add("Start bullet points with strong action verbs (e.g., 'Engineered', 'Optimized') and quantify achievements with metrics (e.g., 'improved latency by 30%').");
        }
        if (suggestions.isEmpty()) {
            suggestions.add("Excellent resume formatting! High ATS compliance across all technical and structural categories.");
        }

        return new AtsBreakdownDto(
                totalScore,
                keywordScore,
                sectionsScore,
                formattingScore,
                actionVerbsScore,
                matchedKeywords,
                missingKeywords,
                presentSections,
                missingSections,
                suggestions,
                wordCount
        );
    }

    public String toJson(AtsBreakdownDto dto) {
        try {
            return objectMapper.writeValueAsString(dto);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize ATS breakdown: {}", e.getMessage());
            return "{}";
        }
    }

    public AtsBreakdownDto fromJson(String json) {
        if (json == null || json.isBlank()) {
            return new AtsBreakdownDto(0, 0, 0, 0, 0, List.of(), List.of(), List.of(), List.of(), List.of(), 0);
        }
        try {
            return objectMapper.readValue(json, AtsBreakdownDto.class);
        } catch (JsonProcessingException e) {
            log.error("Failed to parse ATS breakdown JSON: {}", e.getMessage());
            return new AtsBreakdownDto(0, 0, 0, 0, 0, List.of(), List.of(), List.of(), List.of(), List.of(), 0);
        }
    }

    private Set<String> extractKeywordsFromJd(String jd) {
        Set<String> stopWords = Set.of(
                "and", "the", "with", "for", "that", "this", "from", "have", "will", "our", "you",
                "your", "are", "about", "who", "what", "which", "into", "more", "their", "must", "should"
        );
        String[] tokens = jd.toLowerCase(Locale.ROOT).split("[^a-zA-Z0-9+#]+");
        return Arrays.stream(tokens)
                .filter(t -> t.length() > 2 && !stopWords.contains(t))
                .collect(Collectors.toSet());
    }
}
