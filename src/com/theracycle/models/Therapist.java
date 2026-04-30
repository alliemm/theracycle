package com.theracycle.models;
import com.theracycle.services.AIService;
public class Therapist extends User {
    private String specialization;
    public Therapist(String name, String id, String specialization) {
        super(name, id);
        this.specialization = specialization;
    }
    public String getSpecialization() {
        return specialization;
    }
    @Override
    public void displayDashboard() {
        System.out.println("--- Therapist Portal ---");
        System.out.println("Dr. " + getName());
        System.out.println("Specialization: " + specialization);
        System.out.println("Status: Reviewing weekly patient briefs.");
    }
    public void reviewPatient(Patient patient, AIService ai) {
        System.out.println("\nReviewing Patient: " + patient.getName());
        String insight = ai.generateSummary(patient.getJournal().getAllEntries().values());
        System.out.println(insight);
    }
}
