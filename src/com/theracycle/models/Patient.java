package com.theracycle.models;

import com.theracycle.services.JournalManager;

public class Patient extends User {
    private JournalManager journal;

    public Patient(String name, String id) {
        super(name, id);
        this.journal = new JournalManager(); // Each patient gets their own journal
    }

    public JournalManager getJournal() {
        return journal;
    }

    @Override
    public void displayDashboard() {
        System.out.println("Patient: " + getName());
        System.out.println("Average Weekly Mood: " + journal.getAverageMood());
    }
    public void addNewJournalEntry(int score, String note) {
        try {
            MoodEntry newEntry = new MoodEntry(score, note);
            this.journal.addEntry(newEntry);
            System.out.println("Entry saved successfully for " + getName());
        } catch (IllegalArgumentException e) {
            System.out.println("Failed to save entry: " + e.getMessage());
        }
    }
}