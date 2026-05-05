package com.theracycle.models;

import com.theracycle.exceptions.InvalidMoodException;
import com.theracycle.services.JournalManager;

/**
 * Represents a therapy patient in the TheraCycle system.
 * Extends User via inheritance and owns a JournalManager instance
 * Each Patient has exactly one journal - enforced at construction time
 */
public class Patient extends User {
    private final JournalManager journal;
    public Patient(String name, String id) {
        super(name, id);
        this.journal = new JournalManager();
    }
    public JournalManager getJournal() {
        return journal;
    }
    /**
     * Validates and stores a new mood entry
     * Catches InvalidMoodException so the application never crashes on bad input
     * @param score 1–10 mood rating
     * @param note  patient's free-text note
     */
    public void addNewJournalEntry(int score, String note) {
        try {
            MoodEntry entry = new MoodEntry(score, note);
            journal.addEntry(entry);
        } catch (InvalidMoodException e) {
            System.out.println("Failed to save entry: " + e.getMessage());
        }
    }
    // Dashboard display lists it out
    @Override
    public void displayDashboard() {
        System.out.println("=== Patient Dashboard ===");
        System.out.println("Name            : " + getName());
        System.out.println("ID              : " + getId());
        System.out.println("Total Entries   : " + journal.size());
        System.out.printf ("Average Mood    : %.1f / 10%n", journal.getAverageMood());
        System.out.println("Flagged Entries : " + journal.getConcerningEntries().size());
    }
}
