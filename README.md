# TheraCycle AI 
**Bridging the Gap in Mental Health via Longitudinal Data & AI Distillation**

TheraCycle AI is a Java-based healthcare solution designed to enhance the connection between therapists and patients. [cite_start]The platform targets critical challenges such as **recall bias**—where patients struggle to accurately report emotional states during weekly sessions—and the general difficulty patients face when opening up. 

[cite_start]By enabling consistent, granular daily updates, TheraCycle AI provides therapists with an objective, thematic view of a patient's week, ensuring more effective support and personalized care.

---

## Key Features

- [cite_start]**Daily Emotional Logging:** Patients can submit `MoodEntry` and `JournalNotes` to capture real-time emotional states.
- [cite_start]**AI-Powered "Weekly Therapist Brief":** Utilizes the **Google Gemini API** to distill raw journal text into structured clinical insights and thematic summaries.
- [cite_start]**Sentiment Monitoring:** Automatically flags concerning notes or abnormalities in sentiment for immediate therapist review.
- [cite_start]**Self-Reflection Portal:** Patients have access to their own logs and emotional trends to encourage self-awareness.

---

## Technical Design & Architecture

[cite_start]TheraCycle AI is built on a foundation of robust **Object-Oriented Design (OOD)** to demonstrate technical maturity:

- [cite_start]**Class Hierarchy:** Utilizes inheritance where `Patient` and `Therapist` extend a base `User` class.
- [cite_start]**Modular Interface Design:** Incorporates a `Loggable` interface to ensure a scalable and maintainable codebase.
- [cite_start]**Efficient Data Management:** Implements **Java Collections** (specifically Maps) to maintain a chronological record of patient entries.
- [cite_start]**Encapsulated AI Logic:** The `AISummary` class handles external API orchestration, hiding complexity from the core application while validating all AI-generated sentiment scores against expected parameters.

---

## Reliability & Integrity

- [cite_start]**Data Validation:** Rigorous input validation ensures data integrity, such as maintaining mood ratings strictly within a 1-10 scale.
- [cite_start]**Exception Handling:** Custom exception handling manages potential runtime errors gracefully.
- [cite_start]**Comprehensive Testing:** Unit tests cover critical components, including data validation logic and edge cases like zero-entry weeks or high-stress journal lengths.

---

## Tech Stack

- [cite_start]**Language:** Java 
- [cite_start]**AI Integration:** Google Gemini API 
- [cite_start]**Data Structures:** Java Collections (Maps, Lists) 
- [cite_start]**Architecture:** Object-Oriented (Inheritance, Interfaces, Encapsulation) 

---

## Project Roadmap

- [x] [cite_start]**Milestone 1:** Establish core Java classes and CRUD functionality for logs.
- [ ] [cite_start]**Milestone 2:** Integrate AI encapsulation logic via Gemini API.
- [ ] [cite_start]**Milestone 3:** Finalize testing suite and automated report generation.

---

---

### Project Status: Preliminary / Active Prototype 
**Current Stage:** Architecture Design & Core Logic Implementation.
*Note: This repository is currently being transitioned from a Java-based proposal to a live AI-integrated prototype for a May 2nd delivery.*

---
