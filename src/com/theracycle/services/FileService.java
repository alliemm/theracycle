package com.theracycle.services;

import com.theracycle.models.MoodEntry;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;

public class FileService {
    private static final String FILE_NAME = "mood_history.csv";

    public void saveJournal(String patientName, Collection<MoodEntry> entries) {
        // 'true' means it will APPEND to the file instead of overwriting it
        try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_NAME, true))) {
            for (MoodEntry entry : entries) {
                // Formatting data as: Patient, Date, Score, Note
                writer.println(patientName + "," + entry.getTimestamp() + "," +
                        entry.getScore() + "," + entry.getNote());
            }
            System.out.println("System: Data successfully backed up to " + FILE_NAME);
        } catch (IOException e) {
            System.out.println("Error saving to file: " + e.getMessage());
        }
    }
}