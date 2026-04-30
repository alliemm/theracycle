package com.theracycle.models;
import java.time.LocalDateTime;
public class MoodEntry {
    private int score;
    private String note;
    private LocalDateTime timestamp;
    public MoodEntry(int score, String note) {
        setScore(score);
        this.note = note;
        this.timestamp = LocalDateTime.now();
    }
    private void setScore(int score) {
        if (score < 1 || score > 10) {
            // standard java exception
            throw new IllegalArgumentException("Mood score must be between 1 and 10. Received: " + score);
        }
        this.score = score;
    }
    public int getScore() { return score; }
    public String getNote() { return note; }
    public LocalDateTime getTimestamp() { return timestamp; }
    @Override
    public String toString() {
        return "[" + timestamp + "] Mood: " + score + "/10 | Note: " + note;
    }
 }
