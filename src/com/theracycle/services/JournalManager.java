package com.theracycle.services;

import com.theracycle.models.MoodEntry;
import java.time.LocalDateTime;
import java.util.TreeMap;
import java.util.Map;

public class JournalManager {
    // Map: Key is the timestamp, Value is the MoodEntry object
    // TreeMap keeps everything sorted by date automatically!
    private Map<LocalDateTime, MoodEntry> entries;

    public JournalManager() {
        this.entries = new TreeMap<>();
    }

    // Add a new entry to the collection
    public void addEntry(MoodEntry entry) {
        entries.put(entry.getTimestamp(), entry);
    }

    // Get all entries (useful for the Therapist report later)
    public Map<LocalDateTime, MoodEntry> getAllEntries() {
        return entries;
    }

    // Logic to calculate average mood (Demonstrates non-AI logic for the rubric)
    public double getAverageMood() {
        if (entries.isEmpty()) return 0.0;

        double sum = 0;
        for (MoodEntry e : entries.values()) {
            sum += e.getScore();
        }
        return sum / entries.size();
    }
}