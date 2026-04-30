package com.theracycle;

import com.theracycle.models.*;
import com.theracycle.services.*;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        // 1. Initialize Services
        AuthService auth = new AuthService();
        AIService therapyAI = new MockAIService();
        FileService fileService = new FileService();
        Scanner scanner = new Scanner(System.in);

        // 2. Pre-register Users (Simulating a database)
        auth.registerUser(new Patient("Alma", "P001"));
        auth.registerUser(new Therapist("Dr. Arfaoui", "T101", "Clinical Psychology"));

        boolean running = true;

        System.out.println("=== TheraCycle AI System Initialized ===");

        // 3. The Main Application Loop
        while (running) {
            System.out.println("\n------------------------------------");
            System.out.println("MAIN MENU");
            System.out.print("Enter User ID to Login (or type 'exit' to quit): ");
            String input = scanner.nextLine();

            // Check if user wants to quit
            if (input.equalsIgnoreCase("exit")) {
                running = false;
                continue;
            }

            // 4. Authentication Logic
            User currentUser = auth.login(input);

            if (currentUser != null) {
                // Polymorphism: displayDashboard() behaves differently for each user type
                System.out.println("\n--- Dashboard ---");
                currentUser.displayDashboard();
                System.out.println("-----------------");

                // 5. Patient Flow
                if (currentUser instanceof Patient) {
                    Patient patient = (Patient) currentUser;
                    handlePatientActions(patient, scanner, fileService);
                }
                // 6. Therapist Flow
                else if (currentUser instanceof Therapist) {
                    Therapist therapist = (Therapist) currentUser;
                    handleTherapistActions(therapist, auth, therapyAI, scanner);
                }

                System.out.println("\nLogging out " + currentUser.getName() + "...");
            } else {
                System.out.println("Access Denied: Invalid User ID.");
            }
        }

        System.out.println("\nSaving system state... Goodbye!");
        scanner.close();
    }

    /**
     * Helper method to keep Main clean - Handles Patient mood logging
     */
    private static void handlePatientActions(Patient p, Scanner scanner, FileService fs) {
        System.out.println("\nActions: [1] Log Mood | [2] Logout");
        System.out.print("Selection: ");
        String choice = scanner.nextLine();

        if (choice.equals("1")) {
            try {
                System.out.print("Enter Mood Score (1-10): ");
                int score = Integer.parseInt(scanner.nextLine());
                System.out.print("Enter a note: ");
                String note = scanner.nextLine();

                p.addNewJournalEntry(score, note);

                // Save to CSV so it persists on your hard drive
                fs.saveJournal(p.getName(), p.getJournal().getAllEntries().values());
            } catch (NumberFormatException e) {
                System.out.println("Error: Please enter a valid number for the score.");
            }
        }
    }

    /**
     * Helper method to keep Main clean - Handles Therapist AI review
     */
    private static void handleTherapistActions(Therapist t, AuthService auth, AIService ai, Scanner scanner) {
        System.out.println("\nActions: [1] Review Patient Alma (P001) | [2] Logout");
        System.out.print("Selection: ");
        String choice = scanner.nextLine();

        if (choice.equals("1")) {
            // Find Alma in the system
            User patientUser = auth.getUserById("P001");
            if (patientUser instanceof Patient) {
                t.reviewPatient((Patient) patientUser, ai);
            } else {
                System.out.println("Patient P001 not found in current session.");
            }
        }
    }
}