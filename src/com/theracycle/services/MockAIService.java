package com.theracycle.services;

import com.theracycle.models.MoodEntry;
import java.util.Collection;

// This MUST be a class that IMPLEMENTS the interface
public class MockAIService implements AIService {
    @Override
    public String generateSummary(Collection<MoodEntry> entries) {
        if (entries.isEmpty()) return "No data available.";
        return "AI Insight: Mood is stable and positive.";
    }
}