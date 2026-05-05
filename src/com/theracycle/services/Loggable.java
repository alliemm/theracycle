package com.theracycle.services;
/**
 * Loggable interface enforces a contract that any implementing class
 * can produce a readable log summary of its stat
 * Applied to JournalManager so the therapist report system can treat
 * any loggable data source uniformly
 */
public interface Loggable {
    // Returns formatted summary string of this object's loggable data

    String generateLogSummary();
}
