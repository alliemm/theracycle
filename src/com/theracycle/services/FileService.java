package com.theracycle.services;

import com.theracycle.models.MoodEntry;
import java.io.*;
import java.util.Collection;

/**
 * FileService handles storage of patient journal data to CSV
 * Each save overwrites the patient's file to prevent duplicate entries accumulating across sessions
 * Files are named by patient ID to avoid collisions between patients.
 */
public class FileService {
    private static final String DATA_DIR = "data/";
    private static final String CSV_HEADER = "PatientName,Timestamp,Score,Note";
    public FileService() {
        // Ensure the data directory exists
        File dir = new File(DATA_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }
    /**
     * Saves all journal entries for a patient to a CSV file
     * Overwrites any previous file for this patient ID
     * @param patientId   used as the filename (e.g., "P001.csv")
     * @param patientName stored in each row for readability
     * @param entries     the full collection of mood entries to write
     */
    public void saveJournal(String patientId, String patientName, Collection<MoodEntry> entries) {
        String fileName = DATA_DIR + patientId + "_journal.csv";
        // false = overwrite (not append) — prevents duplicate rows on re-save
        try (PrintWriter writer = new PrintWriter(new FileWriter(fileName, false))) {
            writer.println(CSV_HEADER);
            for (MoodEntry entry : entries) {
                writer.printf("%s,%s,%d,%s%n",
                        patientName,
                        entry.getTimestamp(),
                        entry.getScore(),
                        entry.getNote());
            }
            System.out.println("Data saved to " + fileName + " (" + entries.size() + " entries).");
        } catch (IOException e) {
            System.out.println("Error saving journal: " + e.getMessage());
        }
    }
    // Loads and prints a patient's saved CSV for display in the console
    // Returns false if the file doesn't exist yet
    public boolean loadAndPrintJournal(String patientId) {
        String fileName = DATA_DIR + patientId + "_journal.csv";
        File file = new File(fileName);
        if (!file.exists()) {
            System.out.println("No saved journal found for patient " + patientId + ".");
            return false;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            System.out.println("\n--- Saved Journal: " + patientId + " ---");
            String line;
            boolean firstLine = true;
            while ((line = reader.readLine()) != null) {
                if (firstLine) { firstLine = false; continue; } // skip header
                System.out.println(line);
            }
            return true;
        } catch (IOException e) {
            System.out.println("Error reading journal: " + e.getMessage());
            return false;
        }
    }
}
