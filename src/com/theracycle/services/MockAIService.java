package com.theracycle.services;

import com.theracycle.models.MoodEntry;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
/**
 * MockAIService is a deterministic stand-in for GeminiService
 * Used in unit tests so that AI behavior is predictable and repeatable
 * without requiring a live API
 */
public class MockAIService implements AIService {
    @Override
    public String generateSummary(Collection<MoodEntry> entries) {
        if (entries.isEmpty()) {
            return "No data available for analysis.";
        }
        List<MoodEntry> flagged = entries.stream()
                .filter(MoodEntry::isConcerning)
                .collect(Collectors.toList());
        double avg = entries.stream()
                .mapToInt(MoodEntry::getScore)
                .average()
                .orElse(0.0);
        StringBuilder summary = new StringBuilder();
        summary.append(String.format("[MOCK AI] Average mood this week: %.1f/10. ", avg));
        summary.append(String.format("Total entries reviewed: %d. ", entries.size()));
        if (!flagged.isEmpty()) {
            summary.append(String.format(
                    "⚠ %d entry/entries flagged as low mood — therapist follow-up recommended.", flagged.size()));
        } else {
            summary.append("No entries flagged. Mood appears stable.");
        }
        return summary.toString();
    }
}
