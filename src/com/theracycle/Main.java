package com.theracycle;

import com.theracycle.exceptions.DuplicateUserException;
import com.theracycle.models.Patient;
import com.theracycle.models.Therapist;
import com.theracycle.models.User;
import com.theracycle.services.AIService;
import com.theracycle.services.AuthService;
import com.theracycle.services.FileService;
import com.theracycle.services.GeminiService;
import java.util.Scanner;

/**
 * TheraCycle AI — Main Entry Point
 * Initializes all services, demo users, and runs the main
 * application loop. Delegates patient and therapist flows to
 * helper methods to keep this class clean and focused.
 */
public class Main {

    public static void main(String[] args) {
        // 1. Initialize services
        AuthService auth     = new AuthService();
        AIService   ai       = new GeminiService();   // swap to MockAIService for offline use
        FileService fileService = new FileService();
        Scanner     scanner  = new Scanner(System.in);
        // 2. Create demo users
        try {
            auth.registerUser(new Patient("Alma", "P001"));
            auth.registerUser(new Therapist("Arfaoui", "T101", "Clinical Psychology"));
        } catch (DuplicateUserException e) {
            System.out.println("Setup error: " + e.getMessage());
        }
        System.out.println("\n╔════════════════════════════════╗");
        System.out.println("║   TheraCycle AI — v1.0         ║");
        System.out.println("╚════════════════════════════════╝");
        boolean running = true;
        while (running) {
            System.out.println("\n──────────────────────────────────");
            System.out.println("MAIN MENU");
            System.out.print("Enter User ID to login (or 'exit'): ");
            String input = scanner.nextLine().trim();
            if (input.equalsIgnoreCase("exit")) {
                running = false;
                continue;
            }
            User currentUser = auth.login(input);
            if (currentUser == null) continue;
            // Polymorphism — displayDashboard() dispatches to Patient or Therapist impl
            System.out.println();
            currentUser.displayDashboard();
            if (currentUser instanceof Patient) {
                handlePatientFlow((Patient) currentUser, scanner, fileService);
            } else if (currentUser instanceof Therapist) {
                handleTherapistFlow((Therapist) currentUser, auth, ai, scanner);
            }
            System.out.println("\nLogging out " + currentUser.getName() + "...");
        }
        System.out.println("\nGoodbye! System state saved.");
        scanner.close();
    }
    // Patient menu: log a mood entry or view journal history
    private static void handlePatientFlow(Patient p, Scanner scanner, FileService fs) {
        System.out.println("\n[1] Log mood entry");
        System.out.println("[2] View past entries");
        System.out.println("[3] Logout");
        System.out.print("Selection: ");
        String choice = scanner.nextLine().trim();
        switch (choice) {
            case "1":
                System.out.print("Mood score (1–10): ");
                String scoreInput = scanner.nextLine().trim();
                try {
                    int score = Integer.parseInt(scoreInput);
                    System.out.print("Note: ");
                    String note = scanner.nextLine();
                    p.addNewJournalEntry(score, note);
                    fs.saveJournal(p.getId(), p.getName(), p.getJournal().getAllEntries().values());
                } catch (NumberFormatException e) {
                    System.out.println("Error: Please enter a whole number between 1 and 10.");
                }
                break;
            case "2":
                System.out.println(p.getJournal().generateLogSummary());
                break;
            default:
                break;
        }
    }
    // Therapist menu: review any registered patient
    private static void handleTherapistFlow(Therapist t, AuthService auth, AIService ai, Scanner scanner) {
        System.out.println("\n[1] Review patient by ID");
        System.out.println("[2] Logout");
        System.out.print("Selection: ");
        String choice = scanner.nextLine().trim();
        if (choice.equals("1")) {
            System.out.print("Enter patient ID: ");
            String patientId = scanner.nextLine().trim();
            User found = auth.getUserById(patientId);
            if (found instanceof Patient) {
                t.reviewPatient((Patient) found, ai);
            } else {
                System.out.println("No patient found with ID '" + patientId + "'.");
            }
        }
    }
}
