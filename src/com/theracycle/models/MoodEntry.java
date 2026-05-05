package com.theracycle.models;

import com.theracycle.exceptions.InvalidMoodException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

// Represents a single patient's mood log entry
// Encapsulates score validation, a free-text note, and a timestamp
// Immutable after construction - score is validated through a custom exception

public class MoodEntry {

    private final int score;
    private final String note;
    private final LocalDateTime timestamp;

    private static final DateTimeFormatter DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm");

    /**
     * @param score 1–10 mood rating
     * @param note  free-text patient note
     * @throws InvalidMoodException if score is outside 1–10 range
     */
    public MoodEntry(int score, String note) throws InvalidMoodException {
        if (score < 1 || score > 10) {
            throw new InvalidMoodException(score);
        }
        this.score = score;
        // Trim whitespace, replace commas for CSV safety
        this.note = (note == null || note.isBlank()) ? "(no note)" : note.trim().replace(",", ";");
        this.timestamp = LocalDateTime.now();
    }

    public int getScore() { return score; }
    public String getNote() { return note; }
    public LocalDateTime getTimestamp() { return timestamp; }

    // A score of 3 or below is flagged as potentially concerning
    public boolean isConcerning() {
        return score <= 3;
    }

    // string that displays the flagged mood warning
    @Override
    public String toString() {
        return "[" + timestamp.format(DISPLAY_FORMAT) + "] Mood: "
                + score + "/10 | Note: " + note
                + (isConcerning() ? " ⚠ FLAGGED" : "");
    }
}
