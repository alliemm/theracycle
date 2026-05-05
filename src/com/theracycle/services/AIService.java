package com.theracycle.services;

import com.theracycle.models.MoodEntry;
import java.util.Collection;

/**
 * AIService defines the contract for the AI
 * This isn't hardcoded for Gemini
 */
public interface AIService {
    /**
     * Generates a clinical summary from a collection of mood entries
     * @param entries the patient's journal entries
     * @return a plain-text clinical insight string
     */
    String generateSummary(Collection<MoodEntry> entries);
}
