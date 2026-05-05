package com.theracycle.models;

import com.theracycle.services.AIService;
import com.theracycle.services.JournalManager;

/**
 * Represents a therapist in the TheraCycle system
 * Extends User through inheritance. Responsible for reviewing patient journals
 * and requesting AI-generated clinical summaries via the AIService interface
 * Uses AIService as a dependency so the real Gemini implementation and the MockAIService are interchangeable
 */
public class Therapist extends User {
    private final String specialization;
    public Therapist(String name, String id, String specialization) {
        super(name, id);
        if (specialization == null || specialization.isBlank()) {
            throw new IllegalArgumentException("Specialization cannot be blank.");
        }
        this.specialization = specialization.trim();
    }
    public String getSpecialization() {
        return specialization;
    }
    /**
     * Pulls the patient's full journal log and sends it to the AI service
     * Also surfaces any flagged entries separately so they aren't buried in the summary.
     * @param patient the patient to review
     * @param ai      the AI service (real or mock)
     */
    public void reviewPatient(Patient patient, AIService ai) {
        JournalManager journal = patient.getJournal();
        System.out.println("\n====================================");
        System.out.println("Therapist Review: " + patient.getName());
        System.out.println("====================================");
        // Print the structured Java-generated log first (non-AI logic)
        System.out.println(journal.generateLogSummary());
        // Highlight concerning entries before AI summary
        if (!journal.getConcerningEntries().isEmpty()) {
            System.out.println("⚠ FLAGGED ENTRIES REQUIRING ATTENTION:");
            for (MoodEntry entry : journal.getConcerningEntries()) {
                System.out.println("  " + entry);
            }
            System.out.println();
        }
        // AI-generated clinical insight (augments, does not replace, the Java logic)
        System.out.println("--- AI Clinical Summary ---");
        String insight = ai.generateSummary(journal.getAllEntries().values());
        System.out.println(insight);
        System.out.println("====================================\n");
    }
    // dashboard display
    @Override
    public void displayDashboard() {
        System.out.println("=== Therapist Portal ===");
        System.out.println("Name            : Dr. " + getName());
        System.out.println("ID              : " + getId());
        System.out.println("Specialization  : " + specialization);
        System.out.println("Status          : Ready to review weekly patient briefs.");
    }
}
