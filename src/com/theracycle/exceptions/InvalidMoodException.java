package com.theracycle.exceptions;

//This exception triggers when someone puts a score out of bounds
public class InvalidMoodException extends Exception {

    private final int attemptedScore;

    public InvalidMoodException(int score) {
        super("Mood score must be between 1 and 10. Received: " + score);
        this.attemptedScore = score;
    }

    public int getAttemptedScore() {
        return attemptedScore;
    }
}
