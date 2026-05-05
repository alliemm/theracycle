package com.theracycle.tests;

import com.theracycle.exceptions.DuplicateUserException;
import com.theracycle.exceptions.InvalidMoodException;
import com.theracycle.models.MoodEntry;
import com.theracycle.models.Patient;
import com.theracycle.models.Therapist;
import com.theracycle.services.AuthService;
import com.theracycle.services.JournalManager;
import com.theracycle.services.MockAIService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for TheraCycle AI core logic.
 *  - MoodEntry validation (normal, edge, failure)
 *  - JournalManager aggregation logic
 *  - Concerning entry flagging
 *  - AuthService registration and login
 *  - Patient entry flow
 *  - Therapist review (with MockAIService)
 *  - Edge cases: empty journal, boundary scores
 */
class TheraCycleTest {
    private Patient patient;
    private Therapist therapist;
    private AuthService authService;
    private MockAIService mockAI;
    @BeforeEach
    void setUp() throws DuplicateUserException {
        patient = new Patient("Alma", "P001");
        therapist = new Therapist("Arfaoui", "T101", "Clinical Psychology");
        authService = new AuthService();
        mockAI = new MockAIService();
        authService.registerUser(patient);
        authService.registerUser(therapist);
    }
    // tests
    @Test
    void testValidMoodEntry_lowBoundary() throws InvalidMoodException {
        MoodEntry entry = new MoodEntry(1, "Very bad day");
        assertEquals(1, entry.getScore());
        assertTrue(entry.isConcerning(), "Score of 1 should be flagged as concerning");
    }

    @Test
    void testValidMoodEntry_highBoundary() throws InvalidMoodException {
        MoodEntry entry = new MoodEntry(10, "Amazing day!");
        assertEquals(10, entry.getScore());
        assertFalse(entry.isConcerning(), "Score of 10 should not be flagged");
    }
    @Test
    void testValidMoodEntry_midRange() throws InvalidMoodException {
        MoodEntry entry = new MoodEntry(5, "Okay day");
        assertEquals(5, entry.getScore());
        assertFalse(entry.isConcerning());
    }
    @Test
    void testInvalidMoodEntry_scoreTooHigh() {
        // Score of 11 should throw InvalidMoodException
        InvalidMoodException ex = assertThrows(InvalidMoodException.class,
                () -> new MoodEntry(11, "Note"));
        assertEquals(11, ex.getAttemptedScore());
    }
    @Test
    void testInvalidMoodEntry_scoreTooLow() {
        InvalidMoodException ex = assertThrows(InvalidMoodException.class,
                () -> new MoodEntry(0, "Note"));
        assertEquals(0, ex.getAttemptedScore());
    }
    @Test
    void testInvalidMoodEntry_negativeScore() {
        assertThrows(InvalidMoodException.class, () -> new MoodEntry(-5, "Note"));
    }
    @Test
    void testMoodEntry_concerningThreshold() throws InvalidMoodException {
        // Score of 3 is exactly at the threshold — should be flagged
        MoodEntry borderline = new MoodEntry(3, "Rough");
        assertTrue(borderline.isConcerning());
        // Score of 4 is just above — should NOT be flagged
        MoodEntry safe = new MoodEntry(4, "Okay");
        assertFalse(safe.isConcerning());
    }
    @Test
    void testMoodEntry_nullNote_defaultsToPlaceholder() throws InvalidMoodException {
        MoodEntry entry = new MoodEntry(7, null);
        assertEquals("(no note)", entry.getNote());
    }
    @Test
    void testMoodEntry_blankNote_defaultsToPlaceholder() throws InvalidMoodException {
        MoodEntry entry = new MoodEntry(6, "   ");
        assertEquals("(no note)", entry.getNote());
    }
    @Test
    void testMoodEntry_timestampNotNull() throws InvalidMoodException {
        MoodEntry entry = new MoodEntry(5, "Test");
        assertNotNull(entry.getTimestamp());
    }
    // for journalmanager
    @Test
    void testJournalManager_emptyJournalAverageMood() {
        JournalManager journal = new JournalManager();
        assertEquals(0.0, journal.getAverageMood(), "Empty journal should return 0.0");
    }
    @Test
    void testJournalManager_singleEntry() throws InvalidMoodException {
        JournalManager journal = new JournalManager();
        journal.addEntry(new MoodEntry(7, "Good day"));
        assertEquals(7.0, journal.getAverageMood());
        assertEquals(1, journal.size());
    }
    @Test
    void testJournalManager_averageMoodCalculation() throws InvalidMoodException, InterruptedException {
        JournalManager journal = new JournalManager();
        journal.addEntry(new MoodEntry(6, "Fine"));
        Thread.sleep(5); // ensure unique timestamps
        journal.addEntry(new MoodEntry(8, "Good"));
        Thread.sleep(5);
        journal.addEntry(new MoodEntry(4, "Meh"));
        // (6+8+4)/3 = 6.0
        assertEquals(6.0, journal.getAverageMood(), 0.01);
    }
    @Test
    void testJournalManager_concerningEntriesFiltered() throws InvalidMoodException, InterruptedException {
        JournalManager journal = new JournalManager();
        journal.addEntry(new MoodEntry(2, "Very low"));
        Thread.sleep(5);
        journal.addEntry(new MoodEntry(8, "High"));
        Thread.sleep(5);
        journal.addEntry(new MoodEntry(1, "Crisis"));
        assertEquals(2, journal.getConcerningEntries().size());
    }
    @Test
    void testJournalManager_noConcerningEntries() throws InvalidMoodException, InterruptedException {
        JournalManager journal = new JournalManager();
        journal.addEntry(new MoodEntry(5, "Fine"));
        Thread.sleep(5);
        journal.addEntry(new MoodEntry(7, "Good"));
        assertTrue(journal.getConcerningEntries().isEmpty());
    }
    @Test
    void testJournalManager_isEmptyInitially() {
        JournalManager journal = new JournalManager();
        assertTrue(journal.isEmpty());
    }
    @Test
    void testJournalManager_generateLogSummary_empty() {
        JournalManager journal = new JournalManager();
        String summary = journal.generateLogSummary();
        assertTrue(summary.contains("No journal entries"));
    }
    @Test
    void testJournalManager_generateLogSummary_withEntries() throws InvalidMoodException {
        JournalManager journal = new JournalManager();
        journal.addEntry(new MoodEntry(6, "Normal day"));
        String summary = journal.generateLogSummary();
        assertTrue(summary.contains("Total Entries"));
        assertTrue(summary.contains("Average Mood"));
    }
    //authservice tests
    @Test
    void testAuthService_loginSuccess() {
        assertNotNull(authService.login("P001"));
    }
    @Test
    void testAuthService_loginFails_badId() {
        assertNull(authService.login("FAKE999"));
    }
    @Test
    void testAuthService_loginFails_nullId() {
        assertNull(authService.login(null));
    }
    @Test
    void testAuthService_duplicateRegistrationThrows() {
        // P001 was already registered in setUp()
        assertThrows(DuplicateUserException.class,
                () -> authService.registerUser(new Patient("Duplicate", "P001")));
    }
    @Test
    void testAuthService_getUserById() {
        assertNotNull(authService.getUserById("T101"));
        assertEquals("Arfaoui", authService.getUserById("T101").getName());
    }
    @Test
    void testAuthService_userCount() {
        assertEquals(2, authService.getUserCount());
    }
    // tests for patient
    @Test
    void testPatient_addEntryValidScore() {
        patient.addNewJournalEntry(7, "Feeling okay");
        assertEquals(1, patient.getJournal().size());
    }
    @Test
    void testPatient_addEntryInvalidScore_doesNotCrash() {
        // Score 15 is invalid — should be caught internally, not throw to caller
        assertDoesNotThrow(() -> patient.addNewJournalEntry(15, "Bad input"));
        assertEquals(0, patient.getJournal().size(), "Invalid entry should not be stored");
    }
    @Test
    void testPatient_journalStartsEmpty() {
        assertTrue(patient.getJournal().isEmpty());
    }
    // therapist tests
    @Test
    void testMockAI_emptyEntries_returnsNoDataMessage() {
        JournalManager journal = new JournalManager();
        String result = mockAI.generateSummary(journal.getAllEntries().values());
        assertTrue(result.contains("No data"));
    }

    @Test
    void testMockAI_withEntries_containsAverageMood() throws InvalidMoodException {
        patient.addNewJournalEntry(7, "Good");
        String result = mockAI.generateSummary(patient.getJournal().getAllEntries().values());
        assertTrue(result.contains("7.0"));
    }
    @Test
    void testMockAI_withConcerningEntries_flagsInSummary() throws InvalidMoodException, InterruptedException {
        patient.addNewJournalEntry(2, "Terrible");
        Thread.sleep(5);
        patient.addNewJournalEntry(1, "Worst day");
        String result = mockAI.generateSummary(patient.getJournal().getAllEntries().values());
        assertTrue(result.contains("flagged") || result.contains("⚠"));
    }
    @Test
    void testTherapist_reviewDoesNotThrow() {
        // Therapist reviewing an empty patient journal should not crash
        assertDoesNotThrow(() -> therapist.reviewPatient(patient, mockAI));
    }
    // edge case tests
    @Test
    void testUser_blankNameThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> new Patient("  ", "P999"));
    }
    @Test
    void testUser_blankIdThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> new Patient("Name", ""));
    }
    @Test
    void testTherapist_blankSpecializationThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> new Therapist("Dr. X", "T999", "  "));
    }
}
