package com.theracycle.services;

import com.theracycle.models.MoodEntry;
import java.util.Collection;

// This MUST be an interface
public interface AIService {
    String generateSummary(Collection<MoodEntry> entries);
}