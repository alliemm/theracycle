# TheraCycle AI
**CS-UY 3913 Applied Java Programming — Final Project**
Alma Marcu | NYU Tandon School of Engineering | Spring 2026

---

## Overview

TheraCycle AI is a Java-based mental health support application designed to improve the
continuity of care between therapists and their patients. It allows patients to submit daily
mood logs and journal notes, which are aggregated into a Weekly Therapist Brief. A Gemini
AI integration distills raw journal text into clinical-language summaries, while all core logic
— validation, flagging, storage, and report generation — is handled by the Java backend.

**Three core problems addressed:**
1. **Therapist-patient disconnect** — daily logs supplement the once-weekly session
2. **Recall bias** — notes are timestamped and stored at the moment of submission
3. **Comfort gap** — incremental sharing builds familiarity over time

---

## Package Structure

```
src/
└── com/theracycle/
    ├── Main.java                        # Entry point and application loop
    ├── models/
    │   ├── User.java                    # Abstract base class (encapsulation + polymorphism)
    │   ├── Patient.java                 # Extends User; owns a JournalManager
    │   ├── Therapist.java               # Extends User; reviews patients via AIService
    │   └── MoodEntry.java               # Single mood log (score + note + timestamp)
    ├── services/
    │   ├── AIService.java               # Interface decoupling AI from core logic
    │   ├── GeminiService.java           # Production AI via Gemini REST API
    │   ├── MockAIService.java           # Deterministic stand-in used in tests
    │   ├── JournalManager.java          # TreeMap-based journal store (implements Loggable)
    │   ├── Loggable.java                # Interface for log-summary-capable classes
    │   ├── AuthService.java             # HashMap-based user registry + login
    │   └── FileService.java             # CSV persistence (per-patient files)
    └── exceptions/
        ├── InvalidMoodException.java    # Thrown when mood score is outside 1–10
        └── DuplicateUserException.java  # Thrown on duplicate user registration
```

---

## Object-Oriented Design Decisions

| Principle | Where Applied |
|---|---|
| **Encapsulation** | All fields private; accessed via getters. `JournalManager` returns unmodifiable map. |
| **Inheritance** | `Patient` and `Therapist` both extend `User`, sharing name/ID logic. |
| **Polymorphism** | `displayDashboard()` is abstract in `User`; each subclass renders its own view. `AIService` interface lets `GeminiService` and `MockAIService` be swapped freely. |
| **Interfaces** | `AIService` decouples the AI vendor from all consuming classes. `Loggable` enforces a `generateLogSummary()` contract on any data source used for reports. |
| **Custom Exceptions** | `InvalidMoodException` and `DuplicateUserException` give domain-specific error context vs. generic Java exceptions. |

---

## AI Integration

**Library:** Google Gemini API (`gemini-pro` model via REST)
**Encapsulated in:** `GeminiService` (implements `AIService`)

**Why AI is used:** Raw journal text from patients can be emotionally inconsistent and verbose. The AI distills this into concise clinical language that a therapist can review quickly, identifying emotional themes across the week.

**What Java handles (not AI):**
- All data storage, sorting, and retrieval (`JournalManager`, `FileService`)
- Score validation and input sanitization (`MoodEntry`, `Patient`)
- Concerning-entry flagging logic (`isConcerning()` — score ≤ 3)
- User authentication and registration (`AuthService`)
- Weekly report structure (`generateLogSummary()` via `Loggable`)
- All error handling and fallback behavior

**If the API is unavailable:** `GeminiService` catches all exceptions and returns a plain-text fallback. The Java-generated report (above the AI section) is always available regardless of API status.

---

## Running the Application

### Prerequisites
- Java 17+
- JUnit 5 (for tests)

### Setup
1. Open `GeminiService.java`
2. Replace `YOUR_GEMINI_API_KEY` with your actual Gemini API key
3. Compile from the `src/` directory:

```bash
javac -d out $(find . -name "*.java")
java -cp out com.theracycle.Main
```

### Demo Credentials
| Role | ID |
|---|---|
| Patient (Alma) | `P001` |
| Therapist (Dr. Arfaoui) | `T101` |

---

## Testing

Tests are in `com.theracycle.tests.TheraCycleTest` and use **JUnit 5**.

**Coverage includes:**
- `MoodEntry` validation: valid scores, boundary values (1, 10), invalid scores (0, 11, -5), null/blank notes
- `JournalManager`: average mood calculation, concerning-entry filtering, empty journal edge case, `generateLogSummary()`
- `AuthService`: login success/failure, duplicate registration, null ID handling
- `Patient`: valid and invalid entry flow, journal-starts-empty assertion
- `Therapist + MockAIService`: AI output with/without concerning entries, no-crash guarantee on empty journal
- `User` validation: blank name/ID, blank specialization

**Run with Maven:**
```bash
mvn test
```

**All tests use `MockAIService`** — they pass regardless of Gemini API availability.

---

## External Libraries & Disclosure

| Library | Purpose |
|---|---|
| Google Gemini API (REST) | AI-generated clinical summaries |
| `java.net.http.HttpClient` | Built-in Java HTTP client (Java 11+) |
| JUnit 5 | Unit testing framework |

No third-party JSON parsing library is used. The Gemini response is parsed using
standard `String` methods to minimize external dependencies.

---

## Known Limitations (Intentionally Out of Scope)
- No real-time messaging or video conferencing
- No multi-therapist assignment per patient
- No clinic-wide database (file-per-patient CSV only)
- Console-based UI (visual complexity not required per rubric)
