package com.theracycle.services;

import com.theracycle.models.MoodEntry;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * Manages a patient's chronological collection of MoodEntry records
 * Uses a TreeMap keyed by LocalDateTime so entries are always sorted
 * by date - no manual sorting required when generating therapist reports
 * Implements Loggable to satisfy the interface contract defined in the proposal
 * and to decouple report generation from the Therapist class
 */
public class JournalManager implements Loggable {
    // TreeMap: sorted by timestamp automatically
    private final Map<LocalDateTime, MoodEntry> entries;
    public JournalManager() {
        this.entries = new TreeMap<>();
    }
    // Adds a new entry. Duplicate timestamps are rejected
    public void addEntry(MoodEntry entry) {
        if (entries.containsKey(entry.getTimestamp())) {
            System.out.println("Warning: An entry at this exact timestamp already exists. Skipped.");
            return;
        }
        entries.put(entry.getTimestamp(), entry);
        System.out.println("Entry saved: " + entry);
    }
    // Returns an unmodifiable view
    public Map<LocalDateTime, MoodEntry> getAllEntries() {
        return Collections.unmodifiableMap(entries);
    }
    // Average mood score across all entries. Returns 0.0 if no entries exist.
    public double getAverageMood() {
        if (entries.isEmpty()) return 0.0;
        double sum = 0;
        for (MoodEntry e : entries.values()) {
            sum += e.getScore();
        }
        return Math.round((sum / entries.size()) * 10.0) / 10.0;
    }
    /**
     * Returns all entries where isConcerning() is true (score <= 3).
     * Used by the AI service and therapist dashboard
     */
    public List<MoodEntry> getConcerningEntries() {
        return entries.values().stream()
                .filter(MoodEntry::isConcerning)
                .collect(Collectors.toList());
    }
    public boolean isEmpty() {
        return entries.isEmpty();
    }
    public int size() {
        return entries.size();
    }
    // Loggable contract: produces a plain-text summary for report generation
    @Override
    public String generateLogSummary() {
        if (entries.isEmpty()) {
            return "No journal entries recorded this week.";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("=== Weekly Journal Summary ===\n");
        sb.append(String.format("Total Entries : %d%n", entries.size()));
        sb.append(String.format("Average Mood  : %.1f / 10%n", getAverageMood()));
        sb.append(String.format("Flagged Entries: %d%n", getConcerningEntries().size()));
        sb.append("\n--- Entry Log ---\n");
        for (MoodEntry e : entries.values()) {
            sb.append(e.toString()).append("\n");
        }
        return sb.toString();
    }
}
